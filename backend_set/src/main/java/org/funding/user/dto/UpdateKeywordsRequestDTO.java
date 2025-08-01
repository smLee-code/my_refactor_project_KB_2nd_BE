package org.funding.user.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "키워드 수정 요청")
public class UpdateKeywordsRequestDTO { // 관심 키워드 수정
    @ApiModelProperty(value = "관심 키워드 목록", example = "[\"저축\", \"투자\", \"대출\"]", required = true)
    private List<String> keywords;
} 