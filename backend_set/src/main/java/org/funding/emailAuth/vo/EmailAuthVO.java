package org.funding.emailAuth.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EmailAuthVO {
    private Long id;
    private String email;
    private String code;
    private boolean expired;
    private LocalDateTime createAt;
}
