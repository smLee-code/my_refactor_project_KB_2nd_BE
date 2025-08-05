package org.funding.openAi.vo;

import lombok.Data;

import java.util.Date;

@Data
public class ChatLogVO {
    private Long id;
    private String prompt;
    private String response;
    private Date createdAt;
}
