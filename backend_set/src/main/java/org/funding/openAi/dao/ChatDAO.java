package org.funding.openAi.dao;

import org.apache.ibatis.annotations.Mapper;
import org.funding.openAi.vo.ChatLogVO;

@Mapper
public interface ChatDAO {
    // ai 채팅 기록 저장
    void insertChatLog(ChatLogVO chatLogVO);
}
