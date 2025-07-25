package org.funding.chatting.controller;

import lombok.RequiredArgsConstructor;
import org.funding.chatting.dto.ChattingMessage;
import org.funding.chatting.dto.GreetingMessage;
import org.funding.chatting.service.ChattingService;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ChattingController {

    private final ChattingService chattingService;

    @MessageMapping("/hello")
    @SendTo("/topic/greetings")
    public GreetingMessage greeting(GreetingMessage message) throws Exception {
        return new GreetingMessage("hello," + message.getName());
    }

    @MessageMapping("/chat/{roomId}")
    @SendTo("/topic/chat/{roomId}")
    public ChattingMessage chat(@DestinationVariable Long roomId, ChattingMessage message) throws Exception {
        message.setRoomId(roomId);
        chattingService.saveMessage(message); //db 저장

        return message;
    }

    @GetMapping("/chat/history/{roomId}")
    public List<ChattingMessage> getChatHistory(@PathVariable Long roomId) {
        System.out.println("🔥 채팅 내역 요청 roomId = " + roomId);
        return chattingService.getMessages(roomId);
    }
}