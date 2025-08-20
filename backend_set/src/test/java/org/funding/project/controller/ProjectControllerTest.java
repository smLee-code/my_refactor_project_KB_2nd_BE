package org.funding.project.controller;

import org.funding.projectKeyword.dto.ProjectKeywordRequestDTO;
import org.junit.jupiter.api.Test;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.funding.project.dto.request.CreateProjectRequestDTO;
import org.funding.project.dto.request.CreateSavingsProjectRequestDTO;

import org.funding.project.dto.response.ProjectListDTO;
import org.funding.project.dto.response.ProjectResponseDTO;
import org.funding.project.dto.response.TopProjectDTO;
import org.funding.project.service.ProjectService;
import org.funding.project.vo.ProjectVO;
import org.funding.project.vo.enumType.ProjectType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ProjectControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ProjectService projectService;

    @InjectMocks
    private ProjectController projectController;

    private ObjectMapper objectMapper;
    private final Long MOCK_USER_ID = 1L;

    @BeforeEach
    void setUp() {
        // LocalDate, LocalDateTime 등 Java 8 날짜/시간 타입 직렬화를 위해 JavaTimeModule 추가
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        mockMvc = MockMvcBuilders.standaloneSetup(projectController)
                .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper), new StringHttpMessageConverter(StandardCharsets.UTF_8))
                .build();
    }

    @Test
    @DisplayName("인기 프로젝트 목록 조회 API")
    void getTopProject() throws Exception {
        // given
        TopProjectDTO topProject = new TopProjectDTO();
        topProject.setTitle("인기 프로젝트 1");
        List<TopProjectDTO> responseList = Collections.singletonList(topProject);
        given(projectService.getTopProjects()).willReturn(responseList);

        // when & then
        mockMvc.perform(get("/api/project/top")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("인기 프로젝트 1"));
    }

    @Test
    @DisplayName("사용자 맞춤 프로젝트 추천 API")
    void getRecommendedProjects() throws Exception {
        // given
        ProjectListDTO projectListDTO = ProjectListDTO.builder().title("맞춤 추천 프로젝트").build();
        List<ProjectListDTO> responseList = Collections.singletonList(projectListDTO);
        given(projectService.recommendProjectsByUserKeywords(MOCK_USER_ID)).willReturn(responseList);

        // when & then
        mockMvc.perform(get("/api/project/list/keyword")
                        .requestAttr("userId", MOCK_USER_ID)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("맞춤 추천 프로젝트"));
    }

    @Test
    @DisplayName("프로젝트 생성 API - Multipart/Polymorphic DTO")
    void createProject() throws Exception {
        // given
        // @JsonTypeInfo를 사용하는 복잡한 DTO 생성 (자식 클래스 사용)
        CreateSavingsProjectRequestDTO savingsRequest = new CreateSavingsProjectRequestDTO();
        savingsRequest.setProjectType(ProjectType.Savings);
        savingsRequest.setTitle("새로운 저축 프로젝트");
        savingsRequest.setDeadline(LocalDate.now().plusDays(30));
        savingsRequest.setKeywordIds(Collections.singletonList(1L));

        String projectInfoJson = objectMapper.writeValueAsString(savingsRequest);

        // JSON 데이터 파트를 위한 MockMultipartFile 생성 (Content-Type 명시 필수)
        MockMultipartFile projectInfoPart = new MockMultipartFile(
                "projectInfo",
                "",
                "application/json",
                projectInfoJson.getBytes(StandardCharsets.UTF_8)
        );

        // 이미지 파일 파트 생성
        MockMultipartFile imageFile = new MockMultipartFile("images", "image.jpg", MediaType.IMAGE_JPEG_VALUE, "image_content".getBytes());

        // 서비스 Mocking
        ProjectResponseDTO responseDTO = new ProjectResponseDTO();
        ProjectVO projectVO = new ProjectVO();
        projectVO.setProjectId(99L);
        projectVO.setTitle("새로운 저축 프로젝트");
        responseDTO.setBasicInfo(projectVO);
        given(projectService.createProject(any(CreateProjectRequestDTO.class), any(), anyLong())).willReturn(responseDTO);

        // when & then
        mockMvc.perform(multipart("/api/project")
                        .file(imageFile)
                        .file(projectInfoPart)
                        .requestAttr("userId", MOCK_USER_ID)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.basicInfo.title").value("새로운 저축 프로젝트"));
    }

    @Test
    @DisplayName("프로젝트 삭제 API")
    void deleteProject() throws Exception {
        // given
        Long projectId = 1L;
        // void를 반환하는 서비스 메서드는 doNothing()으로 Mocking
        doNothing().when(projectService).deleteProject(projectId, MOCK_USER_ID);

        // when & then
        mockMvc.perform(delete("/api/project/delete/{id}", projectId)
                        .requestAttr("userId", MOCK_USER_ID))
                .andDo(print())
                .andExpect(status().isOk());

        // deleteProject 메서드가 올바른 인자와 함께 호출되었는지 검증
        verify(projectService).deleteProject(projectId, MOCK_USER_ID);
    }


}