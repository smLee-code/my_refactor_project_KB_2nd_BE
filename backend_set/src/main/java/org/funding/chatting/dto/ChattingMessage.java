package org.funding.chatting.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChattingMessage {
    private Long id;
    private Long projectId;     // 추가
    private Long userId;
    private String content;

    private String timestamp;
}