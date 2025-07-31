package org.funding.payment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.funding.payment.dto.PaymentCompleteRequestDTO;
import org.funding.payment.dto.PaymentCreateRequestDTO;
import org.funding.payment.dto.FundingInfoDTO;
import org.funding.payment.dao.FundingInfoDAO;
import org.funding.payment.dao.PaymentDAO;
import org.funding.payment.vo.PaymentVO;
import org.funding.security.account.domain.CustomUser;
import org.funding.user.dao.MemberDAO;
import org.funding.userChallenge.dto.ApplyChallengeRequestDTO;
import org.funding.userChallenge.service.UserChallengeService;
import org.funding.userDonation.dto.DonateRequestDTO;
import org.funding.userDonation.service.UserDonationService;
import org.funding.userChallenge.dao.UserChallengeDAO;
import org.funding.userDonation.dao.UserDonationDAO;
import org.funding.userChallenge.vo.UserChallengeVO;
import org.funding.userDonation.vo.UserDonationVO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class PaymentService {
    
    private final RestTemplate restTemplate = new RestTemplate();
    
    // 결제별 metadata 임시 저장소
    private final Map<String, Map<String, Object>> paymentMetadata = new ConcurrentHashMap<>();

    // TODO: Service 의존성 문제 해결 후 사용
    // private final UserDonationService userDonationService;
    // private final UserChallengeService userChallengeService;
    
    private final PaymentDAO paymentDAO;
    private final MemberDAO memberDAO;
    private final FundingInfoDAO fundingInfoDAO;
    private final UserChallengeDAO userChallengeDAO;
    private final UserDonationDAO userDonationDAO;
    
    @Value("${imp.api.key}")
    private String apiKey;
    
    @Value("${imp.api.secret}")
    private String apiSecret;
    
    // 사용자 ID 추출
    public Long getUserIdFromAuthentication(Authentication authentication) {
        CustomUser customUser = (CustomUser) authentication.getPrincipal();
        String username = customUser.getUsername();
        return memberDAO.findByEmail(username).getUserId();
    }
    
    // 결제 정보 생성
    public Map<String, Object> createPaymentOrder(PaymentCreateRequestDTO request, Long userId) {
        try {
            // 1. fund_id로 펀딩 정보 조회
            FundingInfoDTO fundingInfo = fundingInfoDAO.selectFundingInfoById(request.getFundId());
            if (fundingInfo == null) {
                throw new RuntimeException("존재하지 않는 펀딩 ID입니다: " + request.getFundId());
            }
            
            // 2. merchant_uid 생성
            String merchantUid = "payment_" + System.currentTimeMillis() + "_" + userId;
            
            // 3. 금액 결정
            Integer amount = getAmountByFundingType(fundingInfo.getFundType(), request.getAmount());
            
            // 4. 결제 정보 DB 저장
            PaymentVO payment = PaymentVO.builder()
                .merchantUid(merchantUid)
                .userId(userId)
                .fundId(request.getFundId())
                .fundingType(fundingInfo.getFundType())
                .amount(amount)
                .status("PENDING")
                .build();
                
            paymentDAO.insertPayment(payment);
            
            // 5. metadata 저장 (있는 경우)
            if (request.getMetadata() != null && !request.getMetadata().isEmpty()) {
                paymentMetadata.put(merchantUid, request.getMetadata());
                log.info("Metadata 저장 - merchantUid: {}, metadata: {}", merchantUid, request.getMetadata());
            }
            
            return Map.of(
                "merchant_uid", merchantUid,
                "amount", amount
            );
            
        } catch (Exception e) {
            log.error("결제 정보 생성 실패", e);
            throw new RuntimeException("결제 정보 생성에 실패했습니다: " + e.getMessage());
        }
    }
    
    // 펀딩 타입별 금액 결정
    private Integer getAmountByFundingType(String fundingType, Integer requestAmount) {
        if ("challenge".equalsIgnoreCase(fundingType)) {
            return 3000; // 챌린지는 3000원 고정
        } else if ("donation".equalsIgnoreCase(fundingType)) {
            if (requestAmount == null || requestAmount <= 0) {
                throw new IllegalArgumentException("기부 금액을 입력해주세요.");
            }
            return requestAmount;
        }
        throw new IllegalArgumentException("지원하지 않는 펀딩 타입입니다: " + fundingType);
    }
    
    // 결제 완료 처리
    public Map<String, Object> completePayment(PaymentCompleteRequestDTO request) {
        try {
            // 1. DB에서 결제 정보 조회
            PaymentVO payment = paymentDAO.selectPaymentByMerchantUid(request.getMerchantUid());
            if (payment == null) {
                throw new RuntimeException("결제 정보를 찾을 수 없습니다.");
            }
            
            // 2. 포트원 결제 검증
            boolean isValid = verifyPaymentWithPortone(request.getImpUid(), payment.getAmount());
            if (!isValid) {
                payment.setStatus("FAILED");
                paymentDAO.updatePaymentStatus(payment);
                throw new RuntimeException("결제 검증에 실패했습니다.");
            }
            
            // 3. 결제 정보 업데이트
            Map<String, Object> paymentInfo = getPaymentInfoFromPortone(request.getImpUid());
            payment.setImpUid(request.getImpUid());
            payment.setStatus("PAID");
            payment.setPayMethod((String) paymentInfo.get("pay_method"));
            payment.setPgProvider((String) paymentInfo.get("pg_provider"));
            paymentDAO.updatePaymentComplete(payment);
            
            // 4. 펀딩 가입 처리
            String result = processFundingJoin(payment);
            
            return Map.of(
                "success", true,
                "message", "결제 및 펀딩 참여가 완료되었습니다.",
                "result", result
            );
            
        } catch (Exception e) {
            log.error("결제 완료 처리 실패", e);
            throw new RuntimeException(e.getMessage());
        }
    }
    
    // 포트원 결제 검증
    private boolean verifyPaymentWithPortone(String impUid, Integer expectedAmount) {
        try {
            // 1. 포트원 액세스 토큰 획득
            String accessToken = getPortoneAccessToken();
            
            // 2. 결제 정보 조회
            String url = "https://api.iamport.kr/payments/" + impUid;
            
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + accessToken);
            
            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<Map> response = restTemplate.exchange(
                url, HttpMethod.GET, entity, Map.class
            );
            
            Map<String, Object> body = response.getBody();
            if (body != null && body.get("response") != null) {
                Map<String, Object> paymentInfo = (Map<String, Object>) body.get("response");
                
                // 3. 결제 금액 검증
                Object amountObj = paymentInfo.get("amount");
                Integer paidAmount = null;
                if (amountObj instanceof Integer) {
                    paidAmount = (Integer) amountObj;
                } else if (amountObj instanceof Long) {
                    paidAmount = ((Long) amountObj).intValue();
                } else if (amountObj instanceof Double) {
                    paidAmount = ((Double) amountObj).intValue();
                }
                
                if (paidAmount == null || !paidAmount.equals(expectedAmount)) {
                    log.error("결제 금액 불일치 - 실제: {}, 예상: {}", paidAmount, expectedAmount);
                    return false;
                }
                
                // 4. 결제 상태 확인
                String status = (String) paymentInfo.get("status");
                if (!"paid".equals(status)) {
                    log.error("결제 상태 오류: {}", status);
                    return false;
                }
                
                return true;
            }
            
            return false;
        } catch (Exception e) {
            log.error("결제 검증 실패", e);
            return false;
        }
    }
    
    // 포트원에서 결제 정보 조회
    private Map<String, Object> getPaymentInfoFromPortone(String impUid) {
        try {
            String accessToken = getPortoneAccessToken();
            String url = "https://api.iamport.kr/payments/" + impUid;
            
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + accessToken);
            
            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<Map> response = restTemplate.exchange(
                url, HttpMethod.GET, entity, Map.class
            );
            
            Map<String, Object> body = response.getBody();
            if (body != null && body.get("response") != null) {
                return (Map<String, Object>) body.get("response");
            }
            
            throw new RuntimeException("결제 정보 조회 실패");
        } catch (Exception e) {
            log.error("포트원 결제 정보 조회 실패", e);
            throw new RuntimeException("결제 정보 조회 실패", e);
        }
    }
    
    // 펀딩 가입 처리
    private String processFundingJoin(PaymentVO payment) {
        try {
            String result = null;
            
            switch (payment.getFundingType().toLowerCase()) {
                case "donation":
                    result = processDonationJoin(payment);
                    break;
                case "challenge":
                    result = processChallengeJoin(payment);
                    break;
                default:
                    throw new IllegalArgumentException("결제가 필요한 펀딩 타입이 아닙니다: " + payment.getFundingType());
            }
            
            return result;
            
        } catch (Exception e) {
            log.error("펀딩 가입 처리 실패", e);
            throw new RuntimeException("펀딩 가입 처리 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
    
    // 기부형 펀딩 가입
    private String processDonationJoin(PaymentVO payment) {
        // metadata에서 익명여부 가져오기
        Map<String, Object> metadata = paymentMetadata.get(payment.getMerchantUid());
        Boolean anonymous = false;
        
        if (metadata != null && metadata.containsKey("anonymous")) {
            anonymous = (Boolean) metadata.get("anonymous");
            log.info("익명여부 설정 - merchantUid: {}, anonymous: {}", payment.getMerchantUid(), anonymous);
            // 사용 후 metadata 제거 (메모리 관리)
            paymentMetadata.remove(payment.getMerchantUid());
        }
        
        // DAO를 직접 사용하여 기부 정보 저장
        UserDonationVO userDonation = new UserDonationVO();
        userDonation.setFundId(payment.getFundId());
        userDonation.setUserId(payment.getUserId());
        userDonation.setDonationAmount(payment.getAmount());
        userDonation.setAnonymous(anonymous);
        
        userDonationDAO.insertUserDonation(userDonation);
        log.info("기부 정보 저장 완료 - userId: {}, fundId: {}, amount: {}, anonymous: {}", 
            payment.getUserId(), payment.getFundId(), payment.getAmount(), anonymous);
        
        return "기부가 완료되었습니다.";
    }
    
    // 챌린지형 펀딩 가입
    private String processChallengeJoin(PaymentVO payment) {
        // DAO를 직접 사용하여 챌린지 가입 정보 저장
        UserChallengeVO userChallenge = new UserChallengeVO();
        userChallenge.setUserId(payment.getUserId());
        userChallenge.setFundId(payment.getFundId());
        
        userChallengeDAO.insertUserChallenge(userChallenge);
        log.info("챌린지 가입 정보 저장 완료 - userId: {}, fundId: {}", 
            payment.getUserId(), payment.getFundId());
        
        return "챌린지 가입이 완료되었습니다.";
    }
    
    // 포트원 액세스 토큰 획득
    private String getPortoneAccessToken() {
        try {
            String url = "https://api.iamport.kr/users/getToken";
            
            Map<String, String> request = Map.of(
                "imp_key", apiKey,
                "imp_secret", apiSecret
            );
            
            ResponseEntity<Map> response = restTemplate.postForEntity(
                url, request, Map.class
            );
            
            Map<String, Object> body = response.getBody();
            if (body != null && body.get("response") != null) {
                Map<String, Object> tokenInfo = (Map<String, Object>) body.get("response");
                return (String) tokenInfo.get("access_token");
            }
            
            throw new RuntimeException("포트원 액세스 토큰 획득 실패");
        } catch (Exception e) {
            log.error("포트원 액세스 토큰 획득 실패", e);
            throw new RuntimeException("포트원 액세스 토큰 획득 실패", e);
        }
    }
    
    
}