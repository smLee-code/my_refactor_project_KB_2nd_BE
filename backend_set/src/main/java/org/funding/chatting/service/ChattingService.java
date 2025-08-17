package org.funding.chatting.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.funding.chatting.dao.ChattingDAO;
import org.funding.chatting.dto.ChattingMessage;
import org.funding.chatting.dto.ChattingMessageResponseDTO;
import org.funding.chatting.dto.RealtimeChatRequestDTO;
import org.funding.chatting.dto.RealtimeChatResponseDTO;
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

    public RealtimeChatResponseDTO saveMessage(RealtimeChatRequestDTO requestDTO) {

        chattingDAO.saveMessage(requestDTO);
        Long id = requestDTO.getId();
        ChattingMessageVO chattingVO = chattingDAO.findMessageById(id);

        System.out.println(chattingVO);

        return RealtimeChatResponseDTO.builder()
                .id(chattingVO.getId())
                .projectId(chattingVO.getProjectId())
                .userId(chattingVO.getUserId())
                .username(memberDAO.findById(chattingVO.getUserId()).getUsername())
                .content(chattingVO.getContent())
                .timestamp(chattingVO.getTimestamp())
                .build();
    }

    public List<RealtimeChatResponseDTO> getMessages(Long projectId, Long userId) {
        List<ChattingMessageVO> chattingList = chattingDAO.findMessagesByRoomId(projectId);

        return chattingList.stream()
                .map(chattingVO -> {

                    MemberVO memberVO = memberDAO.findById(chattingVO.getUserId());

                    return RealtimeChatResponseDTO.builder()
                            .id(chattingVO.getId())
                            .projectId(chattingVO.getProjectId())
                            .userId(chattingVO.getUserId())
                            .username(memberVO.getUsername())
                            .nickname(memberVO.getNickname())
                            .content(chattingVO.getContent())
                            .timestamp(chattingVO.getTimestamp())
                            .build();
                })
                .toList();

    }


}