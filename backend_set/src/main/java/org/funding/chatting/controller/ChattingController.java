package org.funding.chatting.controller;

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

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ChattingController {

    private final ChattingService chattingService;
    private final MemberDAO memberDAO;

    @Auth
    @MessageMapping("/hello")
    @SendTo("/topic/greetings")
    public GreetingMessage greeting(HttpServletRequest request) throws Exception {
        Long userId = (Long) request.getAttribute("userId");
        MemberVO member = memberDAO.findById(userId);
        return new GreetingMessage("hello," + member.getUsername());
    }

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

    @GetMapping("/chat/history/{projectId}")
    public List<RealtimeChatResponseDTO> getChatHistory(
            @PathVariable Long projectId,
            HttpServletRequest request
    ) throws Exception {
        Long userId = (Long) request.getAttribute("userId");

        return chattingService.getMessages(projectId, userId);
    }
}