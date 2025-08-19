package org.funding.openAi.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.funding.fund.dto.FundDetailResponseDTO;
import org.funding.fund.service.FundService;
import org.funding.fund.vo.enumType.FundType;
import org.funding.openAi.client.OpenAIClient;
import org.funding.openAi.dao.ChatDAO;
import org.funding.openAi.dto.*;
import org.funding.openAi.vo.ChatLogVO;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final OpenAIClient openAIClient;

    private final ChatDAO chatDAO;
    private final FundService fundService;


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


     // FundDetailResponseDTO 객체를 AI 프롬프트에 넣기 좋은 문자열로 변환하는 헬퍼 메서드
    private String convertFundDetailsToString(FundDetailResponseDTO details) {
        StringBuilder sb = new StringBuilder();
        sb.append("상품명: ").append(details.getName()).append("\n");
        sb.append("펀딩 종류: ").append(details.getFundType()).append("\n");
        sb.append("상세 설명: ").append(details.getDetail()).append("\n");
        sb.append("금융사: ").append(details.getFinancialInstitution()).append("\n");
        sb.append("진행 상태: ").append(details.getProgress()).append("\n");
        sb.append("가입 조건: ").append(details.getProductCondition()).append("\n");

        if (details.getFundType() == FundType.Savings) {
            sb.append("연이율: ").append(details.getInterestRate()).append("%\n");
            sb.append("저축 기간: ").append(details.getPeriodDays()).append("일\n");
        }
        if (details.getFundType() == FundType.Loan) {
            sb.append("대출 한도: ").append(details.getLoanLimit()).append("원\n");
            sb.append("금리: ").append(details.getMinInterestRate()).append("~").append(details.getMaxInterestRate()).append("%\n");
        }
        if (details.getFundType() == FundType.Challenge) {
            sb.append("챌린지 기간: ").append(details.getChallengePeriodDays()).append("일\n");
            sb.append("챌린지 보상: ").append(details.getChallengeReward()).append("\n");
        }
        if (details.getFundType() == FundType.Donation) {
            sb.append("기부처: ").append(details.getRecipient()).append("\n");
            sb.append("목표 금액: ").append(details.getTargetAmount()).append("원\n");
        }
        return sb.toString();
    }

    public AIExplainFundResponseDTO explainFund(Long fundId, Long userId) throws JsonProcessingException {
        // fundId로 모든 상세 정보 조회
        FundDetailResponseDTO fundDetails = fundService.getFundDetail(fundId, userId);

        // 조회된 상세 정보를 AI가 이해하기 쉬운 텍스트로 변환
        String fundInfoAsText = convertFundDetailsToString(fundDetails);

        // AI에게 보낼 프롬프트 생성
        String prompt = String.format(
                """
                아래 펀딩 상품의 상세 정보를 보고, 금융 상품을 잘 모르는 사용자에게 친구처럼 설명해줘.
                반드시 아래 4가지 항목에 맞춰서, key가 introduction, features, advantages, disadvantages 인 JSON 객체 형식으로만 답변해줘.
                
                - introduction: "이건 OOO하는 서비스야~" 같이 쉽고 간단한 한 문장 소개
                - features: "이건 이런 특징이 있어~" 같이 상품의 핵심 특징 2~3가지
                - advantages: "이건 이런 장점이 있어~" 같이 사용자가 얻을 수 있는 긍정적인 점 2~3가지
                - disadvantages: "이건 이런 단점이 있어~" 같이 사용자가 유의해야 할 점이나 잠재적 단점 2~3가지
                
                ---
                [펀딩 상품 정보]
                %s
                ---
                """,
                fundInfoAsText
        );

        // OpenAI API 호출
        String aiJsonResponse = openAIClient.askOpenAI(prompt);
        ObjectMapper objectMapper = new ObjectMapper();
        // AI가 생성한 JSON 응답을 DTO 객체로 파싱
        AIExplainFundResponseDTO responseDTO = objectMapper.readValue(aiJsonResponse, AIExplainFundResponseDTO.class);


        return responseDTO;
    }
}
