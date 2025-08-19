package org.funding.chatting.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.funding.chatting.dto.*;
import org.funding.chatting.service.ChattingService;
import org.funding.security.util.Auth;
import org.funding.user.dao.MemberDAO;
import org.funding.user.vo.MemberVO;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.List;

@Api(tags = "실시간 채팅 API", description = "HTTP를 이용한 채팅 내역 조회 및 WebSocket을 이용한 실시간 메시징을 처리합니다.")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ChattingController {

    private final ChattingService chattingService;
    private final MemberDAO memberDAO;

    // WebSocket 엔드포인트
    @Auth
    @MessageMapping("/hello")
    @SendTo("/topic/greetings")
    public GreetingMessage greeting(HttpServletRequest request) throws Exception {
        Long userId = (Long) request.getAttribute("userId");
        MemberVO member = memberDAO.findById(userId);
        return new GreetingMessage("hello," + member.getUsername());
    }

    // WebSocket 엔드포인트
    @MessageMapping("/chat/{projectId}")
    @SendTo("/topic/chat/{projectId}")
    public RealtimeChatResponseDTO chat(@DestinationVariable Long projectId, String content, Principal principal) {
        Long userId = Long.parseLong(principal.getName());
        RealtimeChatRequestDTO requestDTO =
                RealtimeChatRequestDTO.builder()
                        .projectId(projectId)
                        .userId(userId)
                        .content(content)
                        .build();
        return chattingService.saveMessage(requestDTO);
    }

    // HTTP 엔드포인트
    @ApiOperation(value = "채팅 내역 조회", notes = "특정 프로젝트의 이전 채팅 내역을 모두 조회합니다.")
    @GetMapping("/chat/history/{projectId}")
    public List<RealtimeChatResponseDTO> getChatHistory(
            @ApiParam(value = "프로젝트 ID", required = true, example = "1") @PathVariable Long projectId,
            HttpServletRequest request
    ) throws Exception {
        Long userId = (Long) request.getAttribute("userId");
        List<RealtimeChatResponseDTO> messages = chattingService.getMessages(projectId, userId);
        return messages;
    }
}