package org.funding.chatting.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ChattingMessageVO {
    private Long id;
    private Long projectId;
    private Long userId;
    private String content;
    private LocalDateTime timestamp;
}