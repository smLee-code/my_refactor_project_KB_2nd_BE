package org.funding.votes.dto;

import lombok.Data;
import org.funding.votes.vo.VotesVO;

import java.time.LocalDateTime;

@Data
public class VotesResponseDTO {
    private Long voteId; // 투표 id
    private Long userId; // 투표한 사용자 id
    private Long projectId; // 투표된 프로젝트 id
    private LocalDateTime voteTime; // 투표 날짜

    public static VotesResponseDTO fromVO(VotesVO selectedVotesVO) {
        VotesResponseDTO dto = new VotesResponseDTO();
        dto.setVoteId(selectedVotesVO.getVoteId());
        dto.setUserId(selectedVotesVO.getUserId());
        dto.setProjectId(selectedVotesVO.getProjectId());
        dto.setVoteTime(selectedVotesVO.getVoteTime());
        return dto;
    }
}
