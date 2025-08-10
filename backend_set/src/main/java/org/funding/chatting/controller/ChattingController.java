package org.funding.chatting.controller;

import lombok.RequiredArgsConstructor;
import org.funding.chatting.dto.ChattingMessage;
import org.funding.chatting.dto.GreetingMessage;
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

//    @Auth
//    @MessageMapping("/chat/{projectId}")
//    @SendTo("/topic/chat/{projectId}")
//    public ChattingMessage chat(@DestinationVariable Long projectId, ChattingMessage message, HttpServletRequest request) throws Exception {
//
//        System.out.println("✅ chat() called!");
//
//        Long userId = (Long) request.getAttribute("userId");
//        MemberVO member = memberDAO.findById(userId);
//        message.setProjectId(projectId);
//        message.setSender(member.getUsername());
//        chattingService.saveMessage(message); //db 저장
//
//        return message;
//    }

    @MessageMapping("/chat/{projectId}")
    @SendTo("/topic/chat/{projectId}")
    public ChattingMessage chat(@DestinationVariable Long projectId, ChattingMessage message, Principal principal) {
        Long userId = Long.parseLong(principal.getName());
        MemberVO member = memberDAO.findById(userId);
        message.setProjectId(projectId);
        message.setUserId(userId);
        chattingService.saveMessage(message);
        return message;
    }


    @GetMapping("/chat/history/{projectId}")
    public List<ChattingMessage> getChatHistory(@PathVariable Long projectId) throws Exception {
        return chattingService.getMessages(projectId);
    }
}