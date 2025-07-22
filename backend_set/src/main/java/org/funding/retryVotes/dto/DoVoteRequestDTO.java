package org.funding.retryVotes.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DoVoteRequestDTO {
    private Long userId; // 재출시 희망자 id
    private Long fundId; // 펀딩 id
}
