package org.funding.retryVotes.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.funding.fund.vo.enumType.ProgressType;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MyVotedFundDTO {
    private Long fundId;
    private String productName;
    private ProgressType progressType;
    private LocalDateTime endAt;
    private String productImage;
}
