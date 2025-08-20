package org.funding.S3.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.funding.S3.controller.S3Controller;
import org.funding.S3.service.S3ImageService;
import org.funding.S3.vo.enumType.ImageType;
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
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class S3ControllerTest {

    private MockMvc mockMvc;

    @Mock
    private S3ImageService imageService;

    @InjectMocks
    private S3Controller s3Controller;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Long MOCK_USER_ID = 1L;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(s3Controller)
                .setMessageConverters(new MappingJackson2HttpMessageConverter(), new StringHttpMessageConverter(StandardCharsets.UTF_8))
                .build();
    }

    @Test
    @DisplayName("이미지 업로드 API - 성공")
    void uploadImage_success() throws Exception {
        // given
        Long postId = 10L;
        ImageType imageType = ImageType.Funding;

        // MockMultipartFile 테스트용 가짜 파일 생성
        MockMultipartFile imageFile1 = new MockMultipartFile(
                "files",
                "test1.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "test image 1".getBytes(StandardCharsets.UTF_8)
        );
        MockMultipartFile imageFile2 = new MockMultipartFile(
                "files",
                "test2.png",
                MediaType.IMAGE_PNG_VALUE,
                "test image 2".getBytes(StandardCharsets.UTF_8)
        );

        // void를 반환하는 서비스 메서드는 doNothing()으로 Mocking
        doNothing().when(imageService).uploadImagesForPost(any(ImageType.class), anyLong(), any(List.class));

        // when & then
        mockMvc.perform(multipart("/api/s3/images")
                        .file(imageFile1)
                        .file(imageFile2)
                        .param("postId", String.valueOf(postId))
                        .param("imageType", imageType.name())
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaType.TEXT_PLAIN)
                        .requestAttr("userId", MOCK_USER_ID))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("성공적으로 업로드 되었습니다."));
    }

    @Test
    @DisplayName("이미지 업로드 API - 실패 (IOException 발생)")
    void uploadImage_failure() throws Exception {
        // given
        Long postId = 10L;
        ImageType imageType = ImageType.Project;
        String errorMessage = "S3-Error";

        MockMultipartFile imageFile = new MockMultipartFile("files", "test.jpg", MediaType.IMAGE_JPEG_VALUE, "image".getBytes());

        // 서비스 메서드가 IOException을 던지도록 설정
        doThrow(new IOException(errorMessage)).when(imageService).uploadImagesForPost(any(ImageType.class), anyLong(), any(List.class));

        // when & then
        mockMvc.perform(multipart("/api/s3/images")
                        .file(imageFile)
                        .param("postId", String.valueOf(postId))
                        .param("imageType", imageType.name())
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaType.TEXT_PLAIN)
                        .requestAttr("userId", MOCK_USER_ID))
                .andDo(print())
                .andExpect(status().isNotFound()) // 컨트롤러에서 404를 반환하므로 isNotFound()로 확인
                .andExpect(content().string("업로드가 실패하였습니다: " + errorMessage));
    }
}