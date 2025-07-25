package org.funding.chatting.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.funding.chatting.dao.ChattingDAO;
import org.funding.chatting.dto.ChattingMessage;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChattingService {

    private final ChattingDAO chattingDAO;

    public void saveMessage(ChattingMessage message) {
        log.info("메시지 저장 요청: {}", message);
        chattingDAO.saveMessage(message);

    }

    public List<ChattingMessage> getMessages(Long roomId) {
        return chattingDAO.findMessagesByRoomId(roomId);
    }
}