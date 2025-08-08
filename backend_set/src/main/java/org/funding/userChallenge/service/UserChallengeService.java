package org.funding.userChallenge.service;

import lombok.RequiredArgsConstructor;
import org.funding.badge.service.BadgeService;
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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
    private final BadgeService badgeService;

    // 첼린지 가입 로직 (가입 전에 결제 로직 추가해줘야함)
    public void applyChallenge(Long fundId, Long userId) {
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
        MemberVO memberVO = memberDAO.findById(userId);
        if (memberVO == null) {
            throw new RuntimeException("존재하지 않는 유저입니다.");
        }


        // 중복가입 예외처리
        boolean isVerify = userChallengeDAO.existsByIdAndUserId(fundId, userId);
        if (isVerify) {
            throw new RuntimeException("이미 첼린지에 가입된 회원입니다");
        }


        UserChallengeVO userChallengeVO = new UserChallengeVO();
        userChallengeVO.setUserId(userId);
        userChallengeVO.setFundId(fundId);
        userChallengeDAO.insertUserChallenge(userChallengeVO);

        // 뱃지 권한 부여 검증
        badgeService.checkAndGrantBadges(userId);
    }


    // 첼린지 인증 로직
    public void verifyDailyChallenge(Long userChallengeId, Long userId, String imageUrl, LocalDate logDate) {

        // 1. 챌린지, 펀딩, 상품 조회
        UserChallengeVO userChallenge = userChallengeDAO.findById(userChallengeId);
        FundVO fund = fundDAO.selectById(userChallenge.getFundId());
        ChallengeVO challenge = challengeDAO.selectByProductId(fund.getProductId());

        System.out.println("sdsds" + challenge);

        LocalDate startDate = challenge.getChallengeStartDate();
        LocalDate endDate = challenge.getChallengeEndDate();

        // 2. 날짜 예외처리 (로그 날짜 기준으로)
        if (logDate.isBefore(startDate) || logDate.isAfter(endDate)) {
            throw new RuntimeException("챌린지 참여 가능 날짜가 아닙니다.");
        }

        // 3. 중복 인증 예외처리
        ChallengeLogVO existing = challengeLogDAO.selectLogByUserAndDate(userChallengeId, logDate);
        if (existing != null) {
            throw new RuntimeException("해당 날짜는 이미 인증되었습니다.");
        }

        System.out.println("유저유저" + userId);
        System.out.println("유저 챌린지" + userChallengeId);
        // 4. 챌린지 가입 여부 확인
        boolean isVerify = userChallengeDAO.existsByIdAndUserId(userChallengeId, userId);
        if (!isVerify) {
            throw new RuntimeException("해당 유저는 첼린지에 가입되어 있지 않습니다.");
        }

        // 5. 펀딩 상태 및 상품 유효성 체크
        ProgressType type = fund.getProgress();
        FinancialProductVO product = financialProductDAO.selectById(fund.getProductId());

        if (product == null) {
            throw new RuntimeException("해당 상품은 존재하지 않습니다.");
        }

        if (type != ProgressType.Launch) {
            throw new RuntimeException("해당 펀딩은 종료된 상태입니다.");
        }

        if (product.getFundType() != FundType.Challenge) {
            throw new RuntimeException("해당 상품은 첼린지 상품이 아닙니다.");
        }

        // 6. 미인증 로그 자동 생성 (오늘 이전까지)
        LocalDate today = LocalDate.now();
        LocalDate lastDate = today.isBefore(endDate) ? today : endDate;

        List<LocalDate> datesToBackfill = startDate.datesUntil(logDate).collect(Collectors.toList());
        List<LocalDate> verifyDates = challengeLogDAO.selectAllLogDatesByUserChallengeId(userChallengeId);
        Set<LocalDate> verifiedSet = new HashSet<>(verifyDates);

        for (LocalDate date : datesToBackfill) {
            if (!verifiedSet.contains(date)) {
                boolean exists = challengeLogDAO.existsByUserChallengeIdAndLogDate(userChallengeId, date);
                if (!exists) {
                    ChallengeLogVO log = new ChallengeLogVO();
                    log.setUserChallengeId(userChallengeId);
                    log.setUserId(userId);
                    log.setLogDate(date);
                    log.setVerified(false);
                    log.setVerifiedResult("미인증");
                    log.setImageUrl(null);
                    challengeLogDAO.insertChallengeLog(log);
                }
            }
        }

        // 7. 이미지 분석 및 인증 검증
        String rewardCondition = challenge.getRewardCondition();
        String result = openAIVisionClient.analyzeImageWithPrompt(imageUrl, rewardCondition);
        boolean isVerified = result.contains("확인되었습니다.");

        if (!isVerified) {
            throw new RuntimeException("해당 사진이 리워드 조건을 만족하지 못했습니다.");
        }

        // 8. 인증 성공 로그 저장
        ChallengeLogVO log = new ChallengeLogVO();
        log.setUserChallengeId(userChallengeId);
        log.setUserId(userId);
        log.setLogDate(logDate);
        log.setImageUrl(imageUrl);
        log.setVerified(true);
        log.setVerifiedResult(result);
        challengeLogDAO.insertChallengeLog(log);

        // 9. 유저 챌린지 상태 업데이트
        userChallengeDAO.updateUserChallengeSuccess(userChallengeId);
    }



    // 챌린지 취소 로직
    public void deleteChallenge(Long userChallengeId, Long userId) {
        MemberVO member = memberDAO.findById(userId);
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
