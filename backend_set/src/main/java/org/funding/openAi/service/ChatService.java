package org.funding.openAi.service;

import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.funding.openAi.client.OpenAIClient;
import org.funding.openAi.dao.ChatDAO;
import org.funding.openAi.dto.ChatRequestDTO;
import org.funding.openAi.dto.ChatResponseDTO;
import org.funding.openAi.dto.SummaryFundRequestDTO;
import org.funding.openAi.dto.SummaryFundResponseDTO;
import org.funding.openAi.vo.ChatLogVO;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final OpenAIClient openAIClient;

    private final ChatDAO chatDAO;

    public ChatResponseDTO ask(ChatRequestDTO chatRequestDTO) {
        String prompt = chatRequestDTO.getPrompt();
        String answer = openAIClient.askOpenAI(prompt);

        ChatLogVO chatLog = new ChatLogVO();
        chatLog.setPrompt(prompt);
        chatLog.setResponse(answer);
        chatLog.setCreatedAt(new Date());

        chatDAO.insertChatLog(chatLog);

        return new ChatResponseDTO(answer);
    }

    public SummaryFundResponseDTO summaryFund(SummaryFundRequestDTO summaryFundRequestDTO) {
        String prompt = String.format(
                """
                아래는 펀딩 상품에 대한 설명입니다. 이 설명을 기반으로 다음 정보를 JSON 형식으로 정리해주세요:
                
                1. 연이율 (annual interest rate): %% 단위로 숫자만 추출
                2. 기업 개입 여부 (isCorporateInvolved): 설명에 기업(회사)이 개입되었는지 여부를 true/false로 판단
                3. 예상 수익 (expectedProfit): 500만원을 투자했을 때 얼마를 벌 수 있는지 '원' 단위로 계산하여 숫자만 표시
                4. 전체 요약(summaryContent): 펀딩 내용을 사용자가 알기쉽게 정리해서 요약
                상품 설명:
                펀딩 상품 이름: %s
                펀딩 타입: %s
                내용: %s
            
                JSON 형식:
                {
                  "annualInterestRate": number,
                  "isCorporateInvolved": boolean,
                  "expectedProfit": number,
                  "summaryContent": string,
                }
                """,
                summaryFundRequestDTO.getFundName(),
                summaryFundRequestDTO.getFundType(),
                summaryFundRequestDTO.getContent()
        );


        String answer = openAIClient.askOpenAI(prompt);

        ChatLogVO chatLog = new ChatLogVO();
        chatLog.setPrompt(prompt);
        chatLog.setResponse(answer);
        chatLog.setCreatedAt(new Date());

        chatDAO.insertChatLog(chatLog);

        return new SummaryFundResponseDTO(answer);

    }
}
