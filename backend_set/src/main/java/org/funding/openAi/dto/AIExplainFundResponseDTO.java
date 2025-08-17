package org.funding.openAi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AIExplainFundResponseDTO {
    private String introduction;
    private List<String> features;
    private List<String> advantages;
    private List<String> disadvantages;
}