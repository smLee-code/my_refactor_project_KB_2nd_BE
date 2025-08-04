package org.funding.fundKeyword.service;

import lombok.RequiredArgsConstructor;
import org.funding.keyword.dao.KeywordDAO;
import org.funding.keyword.vo.KeywordVO;
import org.funding.fund.dao.FundDAO;
import org.funding.fundKeyword.dao.FundKeywordDAO;
import org.funding.fundKeyword.dto.FundKeywordRequestDTO;
import org.funding.fundKeyword.vo.FundKeywordVO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FundKeywordService {

    private final FundKeywordDAO fundKeywordDAO;
    private final KeywordDAO keywordDAO;
    private final FundDAO fundDAO;

    public List<KeywordVO> findKeywordIdsByFundId(Long fundId) {
        List<Long> keywordIdList = fundKeywordDAO.selectKeywordIdsByFundId(fundId);

        return keywordIdList.stream().map(keywordDAO::selectKeywordById).toList();
    }

    public void mapFundKeyword(FundKeywordRequestDTO requestDTO) {
        FundKeywordVO fundKeywordVO = fundKeywordDAO.findFundKeywordMapping(requestDTO);

        if (fundKeywordVO != null) {
            // 이미 매핑되어 있음
            return;
        }

        fundKeywordDAO.insertFundKeyword(requestDTO);
    }

    public void unmapFundKeyword(FundKeywordRequestDTO requestDTO) {
        FundKeywordVO fundKeywordVO = fundKeywordDAO.findFundKeywordMapping(requestDTO);

        if (fundKeywordVO == null) {
            // 이미 매핑되어 있지 않음
            return;
        }

        fundKeywordDAO.deleteFundKeyword(requestDTO);
    }
}