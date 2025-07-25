package org.funding.chatting.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChattingMessage {
    private Long id;
    private Long roomId;     // 추가
    private String sender;
    private String content;

    private String timestamp;
}