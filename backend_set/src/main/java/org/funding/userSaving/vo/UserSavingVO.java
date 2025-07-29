package org.funding.userSaving.vo;

import lombok.Data;

@Data
public class UserSavingVO {

    private Long userSavingId;
    private Long fundId;
    private Long userId;
    private Integer savingAmount;
}
