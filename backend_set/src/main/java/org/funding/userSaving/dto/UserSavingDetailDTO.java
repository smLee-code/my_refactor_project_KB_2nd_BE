package org.funding.userSaving.dto;

import lombok.Data;
import org.funding.S3.vo.S3ImageVO;

import java.util.List;

@Data
public class UserSavingDetailDTO {

    private Long userSavingId;
    private Integer savingAmount;

    private String savingName;
    private Integer periodDays;
    private Double interestRate;

    private Long productId;
    private List<S3ImageVO> images;
}
