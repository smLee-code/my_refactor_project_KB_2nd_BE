package org.funding.chatting.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.funding.chatting.dao.ChattingDAO;
import org.funding.chatting.dto.ChattingMessage;
import org.funding.chatting.dto.ChattingMessageResponseDTO;
import org.funding.chatting.vo.ChattingMessageVO;
import org.funding.user.dao.MemberDAO;
import org.funding.user.vo.MemberVO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChattingService {

    private final MemberDAO memberDAO;
    private final ChattingDAO chattingDAO;

    public void saveMessage(ChattingMessage message) {
        log.info("메시지 저장 요청: {}", message);
        chattingDAO.saveMessage(message);

    }

    public List<ChattingMessageResponseDTO> getMessages(Long projectId, Long userId) {
        List<ChattingMessageVO> chattingList = chattingDAO.findMessagesByRoomId(projectId);

        return chattingList.stream()
                .map(chattingVO -> {

                    MemberVO memberVO = memberDAO.findById(chattingVO.getUserId());

                    return ChattingMessageResponseDTO.builder()
                            .sender(memberVO.getUsername())
                            .isSelf(Objects.equals(userId, chattingVO.getUserId()))
                            .content(chattingVO.getContent())
                            .timestamp(chattingVO.getTimestamp())
                            .build();
                })
                .toList();

    }
}