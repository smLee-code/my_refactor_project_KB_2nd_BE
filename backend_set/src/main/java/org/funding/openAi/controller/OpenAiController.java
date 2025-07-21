package org.funding.openAi.controller;

import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.funding.openAi.dto.ChatRequestDTO;
import org.funding.openAi.dto.ChatResponseDTO;
import org.funding.openAi.dto.SummaryFundRequestDTO;
import org.funding.openAi.dto.SummaryFundResponseDTO;
import org.funding.openAi.service.ChatService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ai")
@RequiredArgsConstructor
public class OpenAiController {

    private final ChatService chatService;

    @PostMapping(value = "/ask", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ChatResponseDTO ask(@RequestBody ChatRequestDTO chatRequestDTO) {
        return chatService.ask(chatRequestDTO);
    }

    // 펀딩 ai 요약하기
    @PostMapping(value = "/fund", consumes = MediaType.APPLICATION_JSON_VALUE )
    public SummaryFundResponseDTO summaryFund(@RequestBody SummaryFundRequestDTO summaryFundRequestDTO) {
        return chatService.summaryFund(summaryFundRequestDTO);
    }
}
