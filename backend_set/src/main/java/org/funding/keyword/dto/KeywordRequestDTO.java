package org.funding.keyword.dto;

import lombok.Data;
import org.funding.keyword.vo.KeywordVO;

@Data
public class KeywordRequestDTO {
    private Long categoryId;
    private String name;

    public static KeywordVO toVO(KeywordRequestDTO dto) {
        return KeywordVO.builder()
                .categoryId(dto.getCategoryId())
                .name(dto.getName())
                .build();
    }
}
