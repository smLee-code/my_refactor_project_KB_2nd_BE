package org.funding.userChallenge.service;

import lombok.RequiredArgsConstructor;
import org.funding.challengeLog.dao.ChallengeLogDAO;
import org.funding.challengeLog.vo.ChallengeLogVO;
import org.funding.financialProduct.dao.ChallengeDAO;
import org.funding.financialProduct.dao.FinancialProductDAO;
import org.funding.financialProduct.vo.ChallengeVO;
import org.funding.financialProduct.vo.FinancialProductVO;
import org.funding.fund.dao.FundDAO;
import org.funding.fund.vo.FundVO;
import org.funding.fund.vo.enumType.FundType;
import org.funding.fund.vo.enumType.ProgressType;
import org.funding.openAi.client.OpenAIVisionClient;
import org.funding.user.dao.MemberDAO;
import org.funding.user.vo.MemberVO;
import org.funding.userChallenge.dao.UserChallengeDAO;
import org.funding.userChallenge.dto.ApplyChallengeRequestDTO;
import org.funding.userChallenge.dto.DeleteChallengeRequestDTO;
import org.funding.userChallenge.vo.UserChallengeVO;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class UserChallengeService {

    private final UserChallengeDAO userChallengeDAO;
    private final OpenAIVisionClient openAIVisionClient;
    private final ChallengeLogDAO challengeLogDAO;
    private final MemberDAO memberDAO;
    private final FundDAO fundDAO;
    private final FinancialProductDAO financialProductDAO;
    private final ChallengeDAO challengeDAO;

    // 첼린지 가입 로직 (가입 전에 결제 로직 추가해줘야함)
    public void applyChallenge(Long fundId, ApplyChallengeRequestDTO challengeRequestDTO) {
        FundVO fund = fundDAO.selectById(fundId);

        // 상품 예외처리
        if (fund == null) {
            throw new RuntimeException("존재하지 않는 펀딩 입니다.");
        }

        // 진행중인 펀딩 예외처리
        ProgressType type = fund.getProgress();
        if (type != ProgressType.Launch) {
            throw new RuntimeException("해당 펀딩은 종료되었습니다.");
        }

        // 유저 예외처리
        MemberVO memberVO = memberDAO.findById(challengeRequestDTO.getUserId());
        if (memberVO == null) {
            throw new RuntimeException("존재하지 않는 유저입니다.");
        }


        // 중복가입 예외처리
        boolean isVerify = userChallengeDAO.existsByIdAndUserId(fundId, challengeRequestDTO.getUserId());
        if (isVerify) {
            throw new RuntimeException("이미 첼린지에 가입된 회원입니다");
        }


        UserChallengeVO userChallengeVO = new UserChallengeVO();
        userChallengeVO.setUserId(challengeRequestDTO.getUserId());
        userChallengeVO.setFundId(fundId);
        userChallengeDAO.insertUserChallenge(userChallengeVO);
    }


    // 첼린지 인증 로직
    public void verifyDailyChallenge(Long userChallengeId, Long userId, String imageUrl, LocalDate logDate) {

        // 펀딩 진행 예외처리
        UserChallengeVO userChallenge = userChallengeDAO.findById(userChallengeId);
        Long fundId = userChallenge.getFundId();
        FundVO fund = fundDAO.selectById(fundId);
        ProgressType type = fund.getProgress();
        Long productId = fund.getProductId();
        FinancialProductVO product = financialProductDAO.selectById(productId);

        // 상품 예외처리
        if (product == null) {
            throw new RuntimeException("해당 상품은 존재하지 않습니다.");
        }

        // 펀딩 예외처리
        if (type != ProgressType.Launch) {
            throw new RuntimeException("해당 펀딩은 종료된 펀딩입니다");
        }

        // 유형 예외처리
        FundType fundType = product.getFundType();
        if (fundType != FundType.Challenge) {
            throw new RuntimeException("해당 상품은 첼린지 상품이 아닙니다.");
        }

        // 중복 예외처리
        ChallengeLogVO existing = challengeLogDAO.selectLogByUserAndDate(userChallengeId, logDate);
        if (existing != null) {
            throw new RuntimeException("금일은 이미 인증 되었습니다");
        }

        ChallengeVO challenge = challengeDAO.selectByProductId(product.getProductId());
        // 검증 기준
        String rewardCondition = challenge.getRewardCondition();

        // 해당 사용자가 첼린지에 가입이 되어있는지
        boolean isVerify = userChallengeDAO.existsByIdAndUserId(userChallengeId, userId);
        if (!isVerify) {
            throw new RuntimeException("해당 유저는 첼린지에 가입되어있지 않습니다.");
        }

        String result = openAIVisionClient.analyzeImageWithPrompt(imageUrl, rewardCondition);
        boolean isVerified = result.contains("확인되었습니다.");

        // 인증 예외처리
        if (!isVerified) {
            throw new RuntimeException("해당 사진이 리워드 조건 기준에 도달하지 못하였습니다.");
        }

        ChallengeLogVO log = new ChallengeLogVO();
        log.setUserChallengeId(userChallengeId);
        log.setLogDate(logDate);
        log.setImageUrl(imageUrl);
        log.setVerified(true);
        log.setVerifiedResult(result);
        challengeLogDAO.insertChallengeLog(log);

        userChallengeDAO.updateUserChallengeSuccess(userChallengeId);
    }

    // 챌린지 취소 로직
    public void deleteChallenge(Long userChallengeId, DeleteChallengeRequestDTO deleteChallengeRequestDTO) {
        MemberVO member = memberDAO.findById(deleteChallengeRequestDTO.getUserId());
        if (member == null) {
            throw new RuntimeException("해당 유저는 존재하지 않는 유저입니다.");
        }

        UserChallengeVO userChallenge = userChallengeDAO.findById(userChallengeId);
        if (userChallenge == null) {
            throw new RuntimeException("첼린지에 참여히시지 않으셨습니다.");
        }

        userChallengeDAO.deleteUserChallenge(userChallengeId);
    }
}
