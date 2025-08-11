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

    void saveMessage(RealtimeChatRequestDTO message);

    ChattingMessageVO findMessageById(@Param("id") long id);

    List<ChattingMessageVO> findMessagesByRoomId(@Param("projectId") Long projectId);

}