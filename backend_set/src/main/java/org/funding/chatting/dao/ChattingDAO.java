package org.funding.chatting.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.funding.chatting.dto.ChattingMessage;
import org.funding.chatting.dto.ChattingMessageResponseDTO;
import org.funding.chatting.dto.RealtimeChatRequestDTO;
import org.funding.chatting.vo.ChattingMessageVO;

import java.util.List;

@Mapper
public interface ChattingDAO {

    // 메세지 저장
    void saveMessage(RealtimeChatRequestDTO message);

    // id로 메세지 조회
    ChattingMessageVO findMessageById(@Param("id") long id);

    // 방 id로 전체 메세지 조회
    List<ChattingMessageVO> findMessagesByRoomId(@Param("projectId") Long projectId);

}