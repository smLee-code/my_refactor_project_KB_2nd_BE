package org.funding.chatting.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChattingMessage {
    private Long projectId;
    private Long userId;
    private String username;
    private String content;
    private LocalDateTime timestamp;
}