package org.funding.openAi.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.funding.fund.dao.FundDAO;
import org.funding.interestingKeyword.dao.InterestingKeywordDAO;
import org.funding.interestingKeyword.vo.InterestingKeywordVO;
import org.funding.fund.vo.FundVO;
import org.funding.fund.vo.enumType.FundType;
import org.funding.global.error.ErrorCode;
import org.funding.global.error.exception.OpenAiException;
import org.funding.openAi.client.OpenAIClient;
import org.funding.openAi.dao.FundAIDAO;
import org.funding.project.dao.ProjectDAO;
import org.funding.project.vo.ProjectVO;
import org.json.JSONArray;
import org.json.JSONException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
public class FundAIService {

    private final FundAIDAO fundAIDAO;
    private final OpenAIClient openAIClient;
    private final ProjectDAO projectDAO;
    private final InterestingKeywordDAO interestingKeywordDAO;
    private final FundDAO fundDAO;

    // id로 펀딩 상품 추출
    public FundVO findFundById(Long fundId) {
        return fundAIDAO.findFundById(fundId);
    }

    public List<FundVO> getSimilarFunds(Long fundId) {
        FundVO fund = fundAIDAO.findFundById(fundId);
        if (fund == null) {
            throw new OpenAiException(ErrorCode.FUNDING_NOT_FOUND);
        }

        FundType fundType = fundAIDAO.findFundTypeByProductId(fund.getProductId());
        if (fundType == null) {
            throw new OpenAiException(ErrorCode.NOT_FUND_TYPE);
        }

        return fundAIDAO.findFundsByFundTypeExcludeSelf(fundType.toString(), fundId);
    }


    public List<FundVO> aiSimilar(Long fundId) {
        FundVO targetFund = findFundById(fundId);

        if (targetFund == null) {
            throw new OpenAiException(ErrorCode.FUNDING_NOT_FOUND);
        }

        List<FundVO> candidates = getSimilarFunds(fundId);
        if (candidates.isEmpty()) {
            return Collections.emptyList();
        }

        StringBuilder prompt = new StringBuilder();
        prompt.append("다음은 기준 펀딩과 동일한 유형의 펀딩입니다. ")
                .append("기준 펀딩과 가장 유사한 10개 펀딩의 ID만 JSON 배열로 반환해주세요. 예: [1, 2, 3]\n\n");

        prompt.append("기준 펀딩 \n").append(describeFund(targetFund)).append("\n\n");

        prompt.append("비교 대상: \n");
        for (FundVO fund : candidates) {
            prompt.append("ID :").append(fund.getFundId())
                    .append(" - ").append(describeFund(fund)).append("\n");
        }

        String aiResponse = openAIClient.askOpenAI(prompt.toString());
        List<Long> recommendIds = parseFundIdsFromAIResponse(aiResponse);

        return candidates.stream()
                .filter(fund -> recommendIds.contains(fund.getFundId()))
                .sorted(Comparator.comparingInt(f -> recommendIds.indexOf(f.getFundId())))
                .collect(Collectors.toList());
    }


    private List<Long> parseFundIdsFromAIResponse(String response) {
        try {
            JSONArray array = new JSONArray(response);
            List<Long> ids = new ArrayList<>();
            for (int i =0; i< array.length(); i++) {
                ids.add(array.getLong(i));
            }
            return ids;
        } catch (JSONException e) {
            log.error("Ai 응답 파싱 실패: {}", response);
            return Collections.emptyList();
        }
    }

    // 사용자 맞춤형 펀딩 추천
    public List<FundVO> recommendForUser(Long userId) {
        // 사용자의 관심 키워드 조회
        List<InterestingKeywordVO> userKeywordsVO = interestingKeywordDAO.findByUserId(userId);
        if (userKeywordsVO.isEmpty()) {
            // 관심 키워드가 없으면 추천 불가, 빈 리스트 반환
            return Collections.emptyList();
        }
        List<String> userKeywords = userKeywordsVO.stream()
                .map(InterestingKeywordVO::getKeyword)
                .collect(Collectors.toList());

        // 키워드와 일치하는 프로젝트를 가진 펀딩 목록 1차 선별
        List<FundVO> candidates = fundDAO.findFundsByKeywordNames(userKeywords);
        if (candidates.isEmpty()) {
            return Collections.emptyList();
        }

        // AI에게 전달할 프롬프트 생성
        StringBuilder prompt = new StringBuilder();
        prompt.append("사용자의 관심사는 '").append(String.join(", ", userKeywords)).append("' 입니다.\n")
                .append("아래 펀딩 목록 중에서 사용자가 가장 흥미를 느낄만한 순서대로 펀딩 10개의 ID만 JSON 배열로 반환해주세요. 예: [10, 5, 22]\n\n")
                .append("펀딩 목록:\n");

        for (FundVO fund : candidates) {
            prompt.append("ID: ").append(fund.getFundId())
                    .append(" - ").append(describeFund_ai(fund)).append("\n");
        }

        // OpenAI API 호출 및 결과 파싱
        String aiResponse = openAIClient.askOpenAI(prompt.toString());
        List<Long> recommendIds = parseFundIdsFromAIResponse(aiResponse);

        // AI가 추천한 순서대로 펀딩 리스트 정렬 및 반환
        return candidates.stream()
                .filter(fund -> recommendIds.contains(fund.getFundId()))
                .sorted(Comparator.comparingInt(f -> recommendIds.indexOf(f.getFundId())))
                .collect(Collectors.toList());
    }


    private String describeFund_ai(FundVO fund) {
        ProjectVO project = projectDAO.findById(fund.getProjectId()); // 프로젝트 정보 조회
        if (project == null) {
            return String.format("펀딩명: %s, 금융사: %s", "알 수 없는 펀딩", fund.getFinancialInstitution());
        }
        return String.format("프로젝트명: %s, 프로젝트 설명: %s, 금융사: %s, 프로젝트 타입: %s",
                project.getTitle(),
                project.getPromotion(),
                fund.getFinancialInstitution(),
                project.getProjectType());
    }

    private String describeFund(FundVO fund) {
        return "금융사: " + fund.getFinancialInstitution() +
                ", 시작일: " + fund.getLaunchAt() +
                ", 종료일: " + fund.getEndAt();
    }
}
