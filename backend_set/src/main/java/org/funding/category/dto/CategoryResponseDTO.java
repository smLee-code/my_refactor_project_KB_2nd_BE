package org.funding.category.dto;

import lombok.Data;

@Data
public class CategoryResponseDTO {

    private Long categoryId;
    private String name;
    private Integer weight;

}
