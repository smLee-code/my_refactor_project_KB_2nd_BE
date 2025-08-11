package org.funding.chatting.vo;

import lombok.Data;

@Data
public class ChattingMessageVO {
    private Long id;
    private Long roomId;
    private Long userId;
    private String content;
    private String timestamp;
}