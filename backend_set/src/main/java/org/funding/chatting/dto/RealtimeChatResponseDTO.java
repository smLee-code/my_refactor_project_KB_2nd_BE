package org.funding.chatting.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RealtimeChatResponseDTO {

    private Long id;
    private Long projectId;
    private Long userId;
    private String username;
    private String nickname;
    private String content;
    private LocalDateTime timestamp;
}
