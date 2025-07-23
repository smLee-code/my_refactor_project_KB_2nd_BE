package org.funding.openAi.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.funding.fund.vo.FundVO;
import org.funding.fund.vo.enumType.FundType;
import org.funding.openAi.client.OpenAIClient;
import org.funding.openAi.dao.FundAIDAO;
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

    // id로 펀딩 상품 추출
    public FundVO findFundById(Long fundId) {
        return fundAIDAO.findFundById(fundId);
    }

    public List<FundVO> getSimilarFunds(Long fundId) {
        FundVO fund = fundAIDAO.findFundById(fundId);
        if (fund == null) {
            throw new RuntimeException("해당 펀딩이 존재하지 않습니다");
        }

        FundType fundType = fundAIDAO.findFundTypeByProductId(fund.getProductId());
        if (fundType == null) {
            throw new RuntimeException("펀딩 상품의 타입 정보가 없습니다.");
        }

        return fundAIDAO.findFundsByFundTypeExcludeSelf(fundType.toString(), fundId);
    }


    public List<FundVO> aiSimilar(Long fundId) {
        FundVO targetFund = findFundById(fundId);

        if (targetFund == null) {
            throw new RuntimeException("해당 펀딩이 존재하지 않습니다.");
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

    private String describeFund(FundVO fund) {
        return "금융사: " + fund.getFinancialInstitution() +
                ", 시작일: " + fund.getLaunchAt() +
                ", 종료일: " + fund.getEndAt();
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
}
