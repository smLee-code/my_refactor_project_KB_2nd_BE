package org.funding.chatting.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.funding.chatting.vo.ChattingMessageVO;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChattingMessageResponseDTO {
    private String sender; // 채팅을 보낸 유저 이름 (username)
    private Boolean isSelf; // 채팅을 보낸것이 로그인된 유저 자신인지
    private String content; // 메시지 내용
    private String timestamp;
}
