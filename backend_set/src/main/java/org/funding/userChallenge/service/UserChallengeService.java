package org.funding.userChallenge.service;

import lombok.RequiredArgsConstructor;
import org.funding.badge.service.BadgeService;
import org.funding.challengeLog.dao.ChallengeLogDAO;
import org.funding.challengeLog.vo.ChallengeLogVO;
import org.funding.challengeLog.vo.enumType.VerifyType;
import org.funding.financialProduct.dao.ChallengeDAO;
import org.funding.financialProduct.dao.FinancialProductDAO;
import org.funding.financialProduct.vo.ChallengeVO;
import org.funding.financialProduct.vo.FinancialProductVO;
import org.funding.fund.dao.FundDAO;
import org.funding.fund.vo.FundVO;
import org.funding.fund.vo.enumType.FundType;
import org.funding.fund.vo.enumType.ProgressType;
import org.funding.global.error.ErrorCode;
import org.funding.global.error.exception.UserChallengeException;
import org.funding.openAi.client.OpenAIVisionClient;
import org.funding.openAi.dto.VisionResponseDTO;
import org.funding.user.dao.MemberDAO;
import org.funding.user.vo.MemberVO;
import org.funding.userChallenge.dao.UserChallengeDAO;
import org.funding.userChallenge.dto.*;
import org.funding.userChallenge.vo.UserChallengeVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
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
            throw new UserChallengeException(ErrorCode.FUNDING_NOT_FOUND);
        }

        // 진행중인 펀딩 예외처리
        ProgressType type = fund.getProgress();
        if (type != ProgressType.Launch) {
            throw new UserChallengeException(ErrorCode.END_FUND);
        }

        // 유저 예외처리
        MemberVO memberVO = memberDAO.findById(userId);
        if (memberVO == null) {
            throw new UserChallengeException(ErrorCode.MEMBER_NOT_FOUND);
        }


        // 중복가입 예외처리
        boolean isVerify = userChallengeDAO.existsByIdAndUserId(fundId, userId);
        if (isVerify) {
            throw new UserChallengeException(ErrorCode.ALREADY_JOIN_CHALLENGE);
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

        LocalDate startDate = challenge.getChallengeStartDate();
        LocalDate endDate = challenge.getChallengeEndDate();

        // 2. 날짜 예외처리 (로그 날짜 기준으로)
        if (logDate.isBefore(startDate) || logDate.isAfter(endDate)) {
            throw new UserChallengeException(ErrorCode.MISS_DATE_CHALLENGE);
        }

        // 3. 중복 인증 예외처리
        ChallengeLogVO existing = challengeLogDAO.selectLogByUserAndDate(userChallengeId, logDate);
        if (existing != null) {
            throw new UserChallengeException(ErrorCode.ALREADY_VERIFIED);
        }

        System.out.println("유저유저" + userId);
        System.out.println("유저 챌린지" + userChallengeId);
        // 4. 챌린지 가입 여부 확인
        boolean isVerify = userChallengeDAO.existsByIdAndUserId(userChallengeId, userId);
        if (!isVerify) {
            throw new UserChallengeException(ErrorCode.NOT_CHALLENGE_MEMBER);
        }

        // 5. 펀딩 상태 및 상품 유효성 체크
        ProgressType type = fund.getProgress();
        FinancialProductVO product = financialProductDAO.selectById(fund.getProductId());

        if (product == null) {
            throw new UserChallengeException(ErrorCode.NOT_FOUND_PRODUCT);
        }

        if (type != ProgressType.Launch) {
            throw new UserChallengeException(ErrorCode.END_FUND);
        }

        if (product.getFundType() != FundType.Challenge) {
            throw new UserChallengeException(ErrorCode.NO_CHALLENGE);
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
                    log.setVerified(VerifyType.UnVerified);
                    log.setVerifiedResult("미인증");
                    log.setImageUrl(null);
                    challengeLogDAO.insertChallengeLog(log);
                }
            }
        }

        // 7. 이미지 분석 및 인증 검증
        String verify = challenge.getVerifyStandard();
        VisionResponseDTO visionResponse = openAIVisionClient.analyzeImageWithPrompt(imageUrl, verify);

        int score = visionResponse.getScore();
        String reason = visionResponse.getReason();

        VerifyType verifyType;
        boolean isSuccess = false;

        if (score > 70) {
            verifyType = VerifyType.Verified;
            isSuccess = true;
        } else if (score >= 20) {
            verifyType = VerifyType.HumanVerify;
        } else {
            verifyType = VerifyType.UnVerified;
        }

        // 8. 인증 성공 로그 저장
        ChallengeLogVO log = new ChallengeLogVO();
        log.setUserChallengeId(userChallengeId);
        log.setUserId(userId);
        log.setLogDate(logDate);
        log.setImageUrl(imageUrl);
        log.setVerified(verifyType);
        log.setVerifiedResult(String.format("[점수: %d] %s", score, reason));
        challengeLogDAO.insertChallengeLog(log);

        //9. 유저 챌린지 상태 업데이트
        if (verifyType == VerifyType.Verified) {
            // 인증 성공 시, 성공 카운트 증가
            userChallengeDAO.updateUserChallengeSuccess(userChallengeId);
        } else {
            // 수동 검증 또는 미검증 시, 실패 카운트 증가
            userChallengeDAO.updateUserChallengeFail(userChallengeId);
        }
    }



    // 챌린지 취소 로직
    public void deleteChallenge(Long userChallengeId, Long userId) {
        MemberVO member = memberDAO.findById(userId);
        if (member == null) {
            throw new UserChallengeException(ErrorCode.MEMBER_NOT_FOUND);
        }

        UserChallengeVO userChallenge = userChallengeDAO.findById(userChallengeId);
        if (userChallenge == null) {
            throw new UserChallengeException(ErrorCode.NOT_CHALLENGE_MEMBER);
        }

        userChallengeDAO.deleteUserChallenge(userChallengeId);
    }

    // 유저가 참여한 모든 챌린지 조회
    public List<UserChallengeDetailDTO> findMyChallenges(Long userId) {
        // 특별한 비즈니스 로직 없이 DAO를 호출하여 결과를 바로 반환
        return userChallengeDAO.findAllChallengesByUserId(userId);
    }

    // 챌린지 상세보기
    public ChallengeDetailResponseDTO getChallengeDetails(Long userChallengeId) {
        // 1. 챌린지 기본 정보 조회
        UserChallengeDetailDTO challengeInfo = userChallengeDAO.findChallengeDetailById(userChallengeId);

        // 만약 존재하지 않는 챌린지라면 null 반환 (컨트롤러에서 예외 처리)
        if (challengeInfo == null) {
            return null;
        }

        // 2. 해당 챌린지의 모든 인증 기록 조회
        List<ChallengeLogVO> dailyLogs = challengeLogDAO.selectAllLogsByUserChallengeId(userChallengeId);

        // 3. 두 종류의 데이터를 하나의 응답 DTO에 담아서 반환
        ChallengeDetailResponseDTO responseDTO = new ChallengeDetailResponseDTO();
        responseDTO.setChallengeInfo(challengeInfo);
        responseDTO.setDailyLogs(dailyLogs);

        return responseDTO;
    }


    // 챌린지 참여자 조회
    public List<ChallengeParticipantDTO> getChallengeParticipants(Long fundId, Long creatorId) {
        verifyChallengeCreator(fundId, creatorId);

        return userChallengeDAO.findParticipantsByFundId(fundId);
    }

    // (공통 로직) 챌린지 생성자가 맞는지 확인하는 헬퍼 메서드
    private void verifyChallengeCreator(Long fundId, Long creatorId) {
        FundVO fund = fundDAO.selectById(fundId);
        if (fund == null) {
            throw new UserChallengeException(ErrorCode.FUNDING_NOT_FOUND);
        }
        if (!fund.getUploadUserId().equals(creatorId)) {
            throw new UserChallengeException(ErrorCode.AUTHENTICATION_FAILED);
        }
    }

    public List<ChallengeLogVO> getParticipantLogs(Long userChallengeId, String status, Long creatorId) {
        // 해당 로그를 볼 권한이 있는지 보안 검증
        UserChallengeVO userChallenge = userChallengeDAO.findById(userChallengeId);
        if (userChallenge == null) {
            throw new UserChallengeException(ErrorCode.NOT_CHALLENGE_MEMBER);
        }
        verifyChallengeCreator(userChallenge.getFundId(), creatorId);

        // DAO에 파라미터를 Map으로 전달하여 로그 조회
        Map<String, Object> params = new HashMap<>();
        params.put("userChallengeId", userChallengeId);
        if (status != null && !status.equalsIgnoreCase("ALL")) {
            params.put("status", status);
        }

        return challengeLogDAO.findLogsByUserChallengeId(params);
    }

    // 수동 챌린지 검증
    @Transactional
    public void manuallyVerifyLog(Long logId, Long creatorId, boolean isApproved) {
        // 로그 조회 및 상태 확인
        ChallengeLogVO log = challengeLogDAO.selectLogById(logId);
        if (log == null) {
            throw new UserChallengeException(ErrorCode.NOT_FOUND_CHALLENGE);
        }
        if (log.getVerified() != VerifyType.HumanVerify) {
            throw new UserChallengeException(ErrorCode.NOT_HUMAN_VERIFY_TARGET);
        }

        // 해당 로그를 수정할 권한이 있는지 보안 검증 (생성자 확인)
        UserChallengeVO userChallenge = userChallengeDAO.findById(log.getUserChallengeId());
        verifyChallengeCreator(userChallenge.getFundId(), creatorId);

        // 로그 상태 변경
        if (isApproved) {
            log.setVerified(VerifyType.Verified);
            log.setVerifiedResult("[수동인증] " + log.getVerifiedResult());
            challengeLogDAO.updateChallengeLog(log);
            // 성공 카운트 증가
            userChallengeDAO.incrementSuccessCount(log.getUserChallengeId());
        } else {
            log.setVerified(VerifyType.UnVerified);
            log.setVerifiedResult("[수동반려] " + log.getVerifiedResult());
            challengeLogDAO.updateChallengeLog(log);
            // 실패 카운트 증가
            userChallengeDAO.incrementFailCount(log.getUserChallengeId());
        }
    }
}
