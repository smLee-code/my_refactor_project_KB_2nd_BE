package org.funding.chatting.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RealtimeChatRequestDTO {

    private Long id;
    private Long projectId;
    private Long userId;
    private String content;
}
