package org.funding.fund.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.funding.S3.vo.S3ImageVO;
import org.funding.fund.vo.enumType.ProgressType;
import org.funding.fund.vo.enumType.FundType;
import java.time.LocalDateTime;
import java.util.List;
import org.funding.keyword.vo.KeywordVO;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FundListResponseDTO {
    // Fund 정보
    private Long fundId;
    private Long productId;
    private Long projectId;
    private ProgressType progress;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime launchAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime endAt;
    private String financialInstitution;
    private int retryVotesCount;
    private S3ImageVO thumbnailImage;
    
    // FinancialProduct 정보
    private String name;
    private String thumbnail;
    private FundType fundType;
    
    // Keywords 정보
    private List<KeywordVO> keywords;
}