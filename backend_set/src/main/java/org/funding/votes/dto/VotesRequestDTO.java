package org.funding.votes.dto;

import lombok.Data;
import org.funding.votes.vo.VotesVO;

@Data
public class VotesRequestDTO {
    private Long userId;
    private Long projectId;

    public VotesVO toVO() {
        return VotesVO.builder()
                .userId(getUserId())
                .projectId(getProjectId())
                .build();

    }
}
