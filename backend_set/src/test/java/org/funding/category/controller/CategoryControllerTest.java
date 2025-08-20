package org.funding.category.controller;

import org.funding.category.dto.CategoryWithKeywordsResponseDTO;
import org.junit.jupiter.api.Test;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.funding.category.dto.CategoryRequestDTO;
import org.funding.category.service.CategoryService;
import org.funding.category.vo.CategoryVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class CategoryControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CategoryService categoryService;

    @InjectMocks
    private CategoryController categoryController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        // JSON과 String 응답을 모두 처리할 수 있도록 컨버터를 설정
        mockMvc = MockMvcBuilders.standaloneSetup(categoryController)
                .setMessageConverters(new MappingJackson2HttpMessageConverter(), new StringHttpMessageConverter(StandardCharsets.UTF_8))
                .build();
    }

    @Test
    @DisplayName("전체 카테고리 목록 조회 API")
    void getAllCategories() throws Exception {
        // given
        CategoryVO category = CategoryVO.builder().categoryId(1L).name("IT").build();
        List<CategoryVO> responseList = Collections.singletonList(category);
        given(categoryService.getAllCategories()).willReturn(responseList);

        // when & then
        mockMvc.perform(get("/api/category")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("IT"));
    }

    @Test
    @DisplayName("키워드를 포함한 전체 카테고리 목록 조회 API")
    void getAllCategoriesWithKeywords() throws Exception {
        // given
        CategoryWithKeywordsResponseDTO responseDTO = CategoryWithKeywordsResponseDTO.builder().build();
        List<CategoryWithKeywordsResponseDTO> responseList = Collections.singletonList(responseDTO);
        given(categoryService.getAllWithKeywords()).willReturn(responseList);

        // when & then
        mockMvc.perform(get("/api/category/all")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("카테고리 생성 API")
    void createCategory() throws Exception {
        // given
        CategoryRequestDTO requestDTO = new CategoryRequestDTO("새 카테고리", 10);
        doNothing().when(categoryService).addCategory(any(CategoryRequestDTO.class));

        // when & then
        mockMvc.perform(post("/api/category")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO))
                        .accept(MediaType.TEXT_PLAIN))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("카테고리 생성 완료"));
    }

    @Test
    @DisplayName("카테고리 삭제 API")
    void deleteCategory() throws Exception {
        // given
        String categoryName = "IT";
        doNothing().when(categoryService).deleteCategory(categoryName);

        // when & then
        mockMvc.perform(delete("/api/category")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("\"" + categoryName + "\"")
                        .accept(MediaType.TEXT_PLAIN))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("카테고리 삭제 완료"));
    }
}