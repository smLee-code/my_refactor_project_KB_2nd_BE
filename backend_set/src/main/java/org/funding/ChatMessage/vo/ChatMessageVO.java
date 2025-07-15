package org.funding.ChatMessage.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ChatMessageVO {
    private Long messageId; // 메세지 id
    private Long senderId; // 보낸 사람 id
    private Long projectId; // 프로젝트 id
    private String content; // 메세지 내용
    private LocalDateTime sendAt; // 보낸 시간
}
