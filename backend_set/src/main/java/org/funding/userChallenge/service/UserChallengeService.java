package org.funding.userChallenge.service;

import lombok.RequiredArgsConstructor;
import org.funding.challengeLog.dao.ChallengeLogDAO;
import org.funding.challengeLog.vo.ChallengeLogVO;
import org.funding.financialProduct.dao.FinancialProductDAO;
import org.funding.financialProduct.vo.FinancialProductVO;
import org.funding.openAi.client.OpenAIVisionClient;
import org.funding.user.dao.MemberDAO;
import org.funding.user.vo.MemberVO;
import org.funding.userChallenge.dao.UserChallengeDAO;
import org.funding.userChallenge.dto.ApplyChallengeRequestDTO;
import org.funding.userChallenge.vo.UserChallengeVO;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class UserChallengeService {

    private final UserChallengeDAO userChallengeDAO;
    private final OpenAIVisionClient openAIVisionClient;
    private final ChallengeLogDAO challengeLogDAO;
    private final FinancialProductDAO financialProductDAO;
    private final MemberDAO memberDAO;

    // 첼린지 가입 로직 (가입 전에 결제 로직 추가해줘야함)
    public void applyChallenge(Long productId, ApplyChallengeRequestDTO challengeRequestDTO) {
        FinancialProductVO financialProductVO = financialProductDAO.selectById(productId);
        // 상품 예외처리
        if (financialProductVO == null) {
            throw new RuntimeException("존재하지 않는 상품 id 입니다.");
        }

        // 유저 예외처리
        MemberVO memberVO = memberDAO.findById(challengeRequestDTO.getUserId());
        if (memberVO == null) {
            throw new RuntimeException("존재하지 않는 유저입니다.");
        }

        UserChallengeVO userChallengeVO = new UserChallengeVO();
        userChallengeVO.setUserId(challengeRequestDTO.getUserId());
        userChallengeVO.setProductId(productId);
        userChallengeDAO.insertUserChallenge(userChallengeVO);
    }


    // 첼린지 인증 로직
    public void verifyDailyChallenge(Long userChallengeId, String imageUrl, LocalDate logDate) {

        String result = openAIVisionClient.analyzeImageWithPrompt(imageUrl, "해당 검증 입력값");
        boolean isVerified = result.contains("검증 물체") && result.contains("검증 행위");

        ChallengeLogVO existing = challengeLogDAO.selectLogByUserAndDate(userChallengeId, logDate);
        if (existing != null) {
            throw new RuntimeException("이미 인증 되었습니다");
        }

        ChallengeLogVO log = new ChallengeLogVO();
        log.setUserChallengeId(userChallengeId);
        log.setLogDate(logDate);
        log.setImageUrl(imageUrl);
        log.setVerified(isVerified);
        log.setVerifiedResult(result);
        challengeLogDAO.insertChallengeLog(log);

        if (isVerified) {
            userChallengeDAO.updateUserChallengeSuccess(userChallengeId);
        } else {
            userChallengeDAO.updateUserChallengeFail(userChallengeId);
        }
    }
}
