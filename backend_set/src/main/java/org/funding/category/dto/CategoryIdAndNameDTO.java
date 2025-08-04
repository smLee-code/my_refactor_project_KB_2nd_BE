package org.funding.category.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.funding.category.vo.CategoryVO;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CategoryIdAndNameDTO {

    private Long id;
    private String name;

    public static CategoryIdAndNameDTO fromVO(CategoryVO vo) {
        return CategoryIdAndNameDTO.builder()
                .id(vo.getCategoryId())
                .name(vo.getName())
                .build();
    }
}
