package org.funding.fund.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.funding.financialProduct.dao.*;
import org.funding.financialProduct.dto.*;
import org.funding.financialProduct.vo.*;
import org.funding.fund.dao.FundDAO;
import org.funding.fund.dto.FundProductRequestDTO;
import org.funding.fund.vo.FundVO;
import org.funding.fund.vo.enumType.FundType;
import org.funding.fund.vo.enumType.ProgressType;
import org.funding.fund.dto.FundListResponseDTO;
import org.funding.fund.dto.FundDetailResponseDTO;
import org.funding.fund.dto.FundUpdateRequestDTO;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.math.BigDecimal;

/**
 * 펀딩 생성 서비스
 * 각 펀딩 타입별로 FinancialProduct를 먼저 생성하고,
 * 해당 product_id를 외래키로 사용하여 타입별 엔티티에 데이터를 저장한다.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class FundService {

    private final FundDAO fundDAO;
    private final FinancialProductDAO financialProductDAO;
    private final SavingsDAO savingsDAO;
    private final LoanDAO loanDAO;
    private final ChallengeDAO challengeDAO;
    private final DonationDAO donationDAO;

    /**
     * 적금 펀딩 생성
     * 1. FinancialProduct 생성 (fund_type=Savings)
     * 2. 생성된 product_id를 사용하여 Savings 엔티티 생성
     * 
     * @param request 적금 생성 요청 데이터
     * @return 성공 메시지
     */
    public String createSavingsFund(FundProductRequestDTO.SavingsRequest request) {
        try {
            log.info("Creating savings fund with name: {}", request.getName());
            
            // 1. 공통 금융상품 정보 생성
            FinancialProductVO product = FinancialProductVO.builder()
                .name(request.getName())
                .detail(request.getDetail())
                .fundType(FundType.Savings)
                .thumbnail(request.getThumbnail())
                .joinCondition(request.getJoinCondition())
                .build();
            
            // 2. 금융상품 DB 저장 (자동 생성된 product_id가 product 객체에 설정됨)
            financialProductDAO.insert(product);
            
            // 3. 생성된 product_id를 외래키로 사용하여 적금 정보 생성
            SavingsVO savings = SavingsVO.builder()
                .productId(product.getProductId())
                .interestRate(request.getInterestRate())
                .periodDays(request.getPeriodDays())
                .successCondition(request.getSuccessCondition())
                .build();
            
            savingsDAO.insertSavings(savings);
            
            // 4. 생성된 product_id를 외래키로 사용하여 Fund 생성
            FundVO fund = FundVO.builder()
                .productId(product.getProductId())
                .projectId(request.getProjectId())
                .progress(request.getProgress())
                .launchAt(request.getLaunchDate().atStartOfDay()) // 00:00:00
                .endAt(request.getEndDate().atTime(23, 59, 59)) // 23:59:59
                .financialInstitution(request.getFinancialInstitution())
                .build();
            
            fundDAO.insert(fund);
            log.info("Savings fund and Fund created successfully with product ID: {}", product.getProductId());
            return "Savings fund and Fund created successfully";
        } catch (Exception e) {
            log.error("Error creating savings fund: ", e);
            throw new RuntimeException("Failed to create savings fund", e);
        }
    }
    
    /**
     * 대출 펀딩 생성
     * 1. FinancialProduct 생성 (fund_type=Loan)
     * 2. 생성된 product_id를 사용하여 Loan 엔티티 생성
     * 
     * @param request 대출 생성 요청 데이터
     * @return 성공 메시지
     */
    public String createLoanFund(FundProductRequestDTO.LoanRequest request) {
        try {
            log.info("Creating loan fund with name: {}", request.getName());
            
            // 1. 공통 금융상품 정보 생성
            FinancialProductVO product = FinancialProductVO.builder()
                .name(request.getName())
                .detail(request.getDetail())
                .fundType(FundType.Loan)
                .thumbnail(request.getThumbnail())
                .joinCondition(request.getJoinCondition())
                .build();
            
            // 2. 금융상품 DB 저장 (자동 생성된 product_id가 product 객체에 설정됨)
            financialProductDAO.insert(product);
            
            // 3. 생성된 product_id를 외래키로 사용하여 대출 정보 생성 (상환 기간 기본 1년)
            LoanVO loan = LoanVO.builder()
                .productId(product.getProductId())
                .loanLimit(request.getLoanLimit())
                .repaymentStartDate(java.time.LocalDateTime.now())
                .repaymentEndDate(java.time.LocalDateTime.now().plusYears(1))
                .minInterestRate(request.getMinInterestRate())
                .maxInterestRate(request.getMaxInterestRate())
                .reward(request.getReward())
                .rewardCondition(request.getRewardCondition())
                .build();
            
            loanDAO.insertLoan(loan);
            
            // 4. 생성된 product_id를 외래키로 사용하여 Fund 생성
            FundVO fund = FundVO.builder()
                .productId(product.getProductId())
                .projectId(request.getProjectId())
                .progress(request.getProgress())
                .launchAt(request.getLaunchDate().atStartOfDay()) // 00:00:00
                .endAt(request.getEndDate().atTime(23, 59, 59)) // 23:59:59
                .financialInstitution(request.getFinancialInstitution())
                .build();
            
            fundDAO.insert(fund);
            log.info("Loan fund and Fund created successfully with product ID: {}", product.getProductId());
            return "Loan fund and Fund created successfully";
        } catch (Exception e) {
            log.error("Error creating loan fund: ", e);
            throw new RuntimeException("Failed to create loan fund", e);
        }
    }
    
    /**
     * 챌린지 펀딩 생성
     * 1. FinancialProduct 생성 (fund_type=Challenge)
     * 2. 생성된 product_id를 사용하여 Challenge 엔티티 생성
     * 
     * @param request 챌린지 생성 요청 데이터
     * @return 성공 메시지
     */
    public String createChallengeFund(FundProductRequestDTO.ChallengeRequest request) {
        try {
            log.info("Creating challenge fund with name: {}", request.getName());
            
            // 1. 공통 금융상품 정보 생성
            FinancialProductVO product = FinancialProductVO.builder()
                .name(request.getName())
                .detail(request.getDetail())
                .fundType(FundType.Challenge)
                .thumbnail(request.getThumbnail())
                .joinCondition(request.getJoinCondition())
                .build();
            
            // 2. 금융상품 DB 저장 (자동 생성된 product_id가 product 객체에 설정됨)
            financialProductDAO.insert(product);
            
            // 3. 생성된 product_id를 외래키로 사용하여 챌린지 정보 생성
            ChallengeVO challenge = ChallengeVO.builder()
                .productId(product.getProductId())
                .challengePeriodDays(request.getChallengePeriodDays())
                .reward(request.getReward())
                .rewardCondition(request.getRewardCondition())
                    .verifyStandard(request.getVerifyStandard()) // 검증 기준 추가
                .build();
            
            challengeDAO.insertChallenge(challenge);
            
            // 4. 생성된 product_id를 외래키로 사용하여 Fund 생성
            FundVO fund = FundVO.builder()
                .productId(product.getProductId())
                .projectId(request.getProjectId())
                .progress(request.getProgress())
                .launchAt(request.getLaunchDate().atStartOfDay()) // 00:00:00
                .endAt(request.getEndDate().atTime(23, 59, 59)) // 23:59:59
                .financialInstitution(request.getFinancialInstitution())
                .build();
            
            fundDAO.insert(fund);
            log.info("Challenge fund and Fund created successfully with product ID: {}", product.getProductId());
            return "Challenge fund and Fund created successfully";
        } catch (Exception e) {
            log.error("Error creating challenge fund: ", e);
            throw new RuntimeException("Failed to create challenge fund", e);
        }
    }
    
    /**
     * 기부 펀딩 생성
     * 1. FinancialProduct 생성 (fund_type=Donation)
     * 2. 생성된 product_id를 사용하여 Donation 엔티티 생성
     * 
     * @param request 기부 생성 요청 데이터
     * @return 성공 메시지
     */
    public String createDonationFund(FundProductRequestDTO.DonationRequest request) {
        try {
            log.info("Creating donation fund with name: {}", request.getName());
            
            // 1. 공통 금융상품 정보 생성
            FinancialProductVO product = FinancialProductVO.builder()
                .name(request.getName())
                .detail(request.getDetail())
                .fundType(FundType.Donation)
                .thumbnail(request.getThumbnail())
                .joinCondition(request.getJoinCondition())
                .build();
            
            // 2. 금융상품 DB 저장 (자동 생성된 product_id가 product 객체에 설정됨)
            financialProductDAO.insert(product);
            
            // 3. 생성된 product_id를 외래키로 사용하여 기부 정보 생성
            DonationVO donation = DonationVO.builder()
                .productId(product.getProductId())
                .recipient(request.getRecipient())
                .usagePlan(request.getUsagePlan())
                .minDonationAmount(request.getMinDonationAmount())
                .maxDonationAmount(request.getMaxDonationAmount())
                .targetAmount(request.getTargetAmount())
                .build();
            
            donationDAO.insertDonation(donation);
            
            // 4. 생성된 product_id를 외래키로 사용하여 Fund 생성
            FundVO fund = FundVO.builder()
                .productId(product.getProductId())
                .projectId(request.getProjectId())
                .progress(request.getProgress())
                .launchAt(request.getLaunchDate().atStartOfDay()) // 00:00:00
                .endAt(request.getEndDate().atTime(23, 59, 59)) // 23:59:59
                .financialInstitution(request.getFinancialInstitution())
                .build();
            
            fundDAO.insert(fund);
            log.info("Donation fund and Fund created successfully with product ID: {}", product.getProductId());
            return "Donation fund and Fund created successfully";
        } catch (Exception e) {
            log.error("Error creating donation fund: ", e);
            throw new RuntimeException("Failed to create donation fund", e);
        }
    }

    // 펀딩 종료 서비스 로직
    public void closeExpiredFunds() {
        List<FundVO> fundList = fundDAO.selectAll();

        LocalDateTime now = LocalDateTime.now();
        for (FundVO fund : fundList) {
            if (fund.getEndAt().isBefore(now) && fund.getProgress() != ProgressType.End) {
                fund.setProgress(ProgressType.End);
                fundDAO.update(fund);
            }
        }
    }
    
    /**
     * 진행상태 + 펀드타입별 펀딩 목록 조회 (펀드타입은 선택사항)
     * 
     * @param progress 진행상태 (Launch, End)
     * @param fundType 펀드타입 (Savings, Loan, Challenge, Donation) - null이면 모든 타입
     * @return 조건에 맞는 펀딩 목록 (상품명과 썸네일 포함)
     */
    public List<FundListResponseDTO> getFundsByProgressAndType(ProgressType progress, FundType fundType) {
        return fundDAO.selectByProgressAndFundType(progress, fundType);
    }
    
    /**
     * 펀딩 상세 조회
     * fund_id로 펀딩 정보와 연관된 금융상품, 그리고 상품 타입별 상세 정보를 조회
     * 
     * @param fundId 펀딩 ID
     * @return 펀딩 상세 정보 (fund + financial_product + 타입별 상세)
     */
    public FundDetailResponseDTO getFundDetail(Long fundId) {
        FundDetailResponseDTO fundDetail = fundDAO.selectDetailById(fundId);
        if (fundDetail == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 펀딩입니다. fundId: " + fundId);
        }
        return fundDetail;
    }
    
    /**
     * 펀딩 수정
     * fund_id로 펀딩과 연관된 모든 정보를 수정
     * 
     * @param fundId 펀딩 ID
     * @param request 수정할 펀딩 정보
     * @return 성공 메시지
     */
    public String updateFund(Long fundId, FundUpdateRequestDTO request) {
        try {
            // 1. 펀딩 존재 여부 확인
            FundDetailResponseDTO existingFund = getFundDetail(fundId);
            
            // 2. Fund 테이블 업데이트
            FundVO fundVO = fundDAO.selectById(fundId);
            if (request.getProgress() != null) fundVO.setProgress(request.getProgress());
            if (request.getLaunchAt() != null) fundVO.setLaunchAt(request.getLaunchAt());
            if (request.getEndAt() != null) fundVO.setEndAt(request.getEndAt());
            if (request.getFinancialInstitution() != null) fundVO.setFinancialInstitution(request.getFinancialInstitution());
            fundDAO.update(fundVO);
            
            // 3. Financial Product 테이블 업데이트
            FinancialProductVO productVO = financialProductDAO.selectById(existingFund.getProductId());
            if (request.getName() != null) productVO.setName(request.getName());
            if (request.getDetail() != null) productVO.setDetail(request.getDetail());
            if (request.getIconUrl() != null) productVO.setThumbnail(request.getIconUrl());
            if (request.getProductCondition() != null) productVO.setJoinCondition(request.getProductCondition());
            financialProductDAO.update(productVO);
            
            // 4. 타입별 상세 테이블 업데이트
            if (request.getProductDetails() != null) {
                updateProductDetails(existingFund.getFundType(), existingFund.getProductId(), request.getProductDetails());
            }
            
            log.info("Fund updated successfully with id: {}", fundId);
            return "펀딩이 성공적으로 수정되었습니다.";
            
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error updating fund: ", e);
            throw new RuntimeException("펀딩 수정 중 오류가 발생했습니다.", e);
        }
    }
    
    /**
     * 타입별 상세 정보 업데이트 헬퍼 메서드
     */
    @SuppressWarnings("unchecked")
    private void updateProductDetails(FundType fundType, Long productId, Object details) {
        // Object를 Map으로 캐스팅 (Jackson이 JSON을 LinkedHashMap으로 변환)
        Map<String, Object> detailsMap = (Map<String, Object>) details;
        switch (fundType) {
            case Savings:
                SavingsVO savingsVO = savingsDAO.selectByProductId(productId);
                if (savingsVO != null) {
                    if (detailsMap.containsKey("periodDays")) {
                        savingsVO.setPeriodDays(((Number) detailsMap.get("periodDays")).intValue());
                    }
                    if (detailsMap.containsKey("interestRate")) {
                        savingsVO.setInterestRate(((Number) detailsMap.get("interestRate")).doubleValue());
                    }
                    if (detailsMap.containsKey("successCondition")) {
                        savingsVO.setSuccessCondition((String) detailsMap.get("successCondition"));
                    }
                    savingsDAO.update(savingsVO);
                }
                break;
                
            case Donation:
                DonationVO donationVO = donationDAO.selectByProductId(productId);
                if (donationVO != null) {
                    if (detailsMap.containsKey("recipient")) {
                        donationVO.setRecipient((String) detailsMap.get("recipient"));
                    }
                    if (detailsMap.containsKey("usagePlan")) {
                        donationVO.setUsagePlan((String) detailsMap.get("usagePlan"));
                    }
                    if (detailsMap.containsKey("minDonationAmount")) {
                        donationVO.setMinDonationAmount(((Number) detailsMap.get("minDonationAmount")).intValue());
                    }
                    if (detailsMap.containsKey("maxDonationAmount")) {
                        donationVO.setMaxDonationAmount(((Number) detailsMap.get("maxDonationAmount")).intValue());
                    }
                    if (detailsMap.containsKey("targetAmount")) {
                        donationVO.setTargetAmount(((Number) detailsMap.get("targetAmount")).longValue());
                    }
                    donationDAO.update(donationVO);
                }
                break;
                
            case Loan:
                LoanVO loanVO = loanDAO.selectByProductId(productId);
                if (loanVO != null) {
                    if (detailsMap.containsKey("loanLimit")) {
                        loanVO.setLoanLimit(((Number) detailsMap.get("loanLimit")).longValue());
                    }
                    if (detailsMap.containsKey("repaymentStartDate")) {
                        String dateStr = (String) detailsMap.get("repaymentStartDate");
                        loanVO.setRepaymentStartDate(LocalDate.parse(dateStr).atStartOfDay());
                    }
                    if (detailsMap.containsKey("repaymentEndDate")) {
                        String dateStr = (String) detailsMap.get("repaymentEndDate");
                        loanVO.setRepaymentEndDate(LocalDate.parse(dateStr).atStartOfDay());
                    }
                    if (detailsMap.containsKey("minInterestRate")) {
                        loanVO.setMinInterestRate(((Number) detailsMap.get("minInterestRate")).doubleValue());
                    }
                    if (detailsMap.containsKey("maxInterestRate")) {
                        loanVO.setMaxInterestRate(((Number) detailsMap.get("maxInterestRate")).doubleValue());
                    }
                    if (detailsMap.containsKey("reward")) {
                        loanVO.setReward((String) detailsMap.get("reward"));
                    }
                    if (detailsMap.containsKey("rewardCondition")) {
                        loanVO.setRewardCondition((String) detailsMap.get("rewardCondition"));
                    }
                    loanDAO.update(loanVO);
                }
                break;
                
            case Challenge:
                ChallengeVO challengeVO = challengeDAO.selectByProductId(productId);
                if (challengeVO != null) {
                    if (detailsMap.containsKey("challengePeriodDays")) {
                        challengeVO.setChallengePeriodDays(((Number) detailsMap.get("challengePeriodDays")).intValue());
                    }
                    if (detailsMap.containsKey("reward")) {
                        challengeVO.setReward((String) detailsMap.get("reward"));
                    }
                    if (detailsMap.containsKey("rewardCondition")) {
                        challengeVO.setRewardCondition((String) detailsMap.get("rewardCondition"));
                    }
                    challengeDAO.update(challengeVO);
                }
                break;
        }
    }
    
    /**
     * 펀딩 삭제
     * fund_id로 펀딩과 관련된 모든 데이터를 삭제
     * 외래키 제약 조건 때문에 삭제 순서 중요:
     * 1. fund 테이블 삭제
     * 2. 타입별 상세 테이블 삭제 (savings/donation/loan/challenge)
     * 3. financial_product 테이블 삭제
     * 
     * @param fundId 삭제할 펀딩 ID
     * @return 성공 메시지
     */
    public String deleteFund(Long fundId) {
        try {
            // 1. 펀딩 존재 여부 확인
            FundDetailResponseDTO existingFund = getFundDetail(fundId);
            Long productId = existingFund.getProductId();
            FundType fundType = existingFund.getFundType();
            
            // 2. fund 테이블에서 삭제 (외래키 제약 때문에 가장 먼저)
            fundDAO.delete(fundId);
            log.info("Fund deleted with id: {}", fundId);
            
            // 3. 타입별 상세 테이블에서 삭제
            switch (fundType) {
                case Savings:
                    savingsDAO.deleteByProductId(productId);
                    log.info("Savings details deleted for productId: {}", productId);
                    break;
                case Donation:
                    donationDAO.deleteByProductId(productId);
                    log.info("Donation details deleted for productId: {}", productId);
                    break;
                case Loan:
                    loanDAO.deleteByProductId(productId);
                    log.info("Loan details deleted for productId: {}", productId);
                    break;
                case Challenge:
                    challengeDAO.deleteByProductId(productId);
                    log.info("Challenge details deleted for productId: {}", productId);
                    break;
            }
            
            // 4. financial_product 테이블에서 삭제 (외래키 참조가 없어진 후)
            financialProductDAO.delete(productId);
            log.info("Financial product deleted with id: {}", productId);
            
            return "펀딩이 성공적으로 삭제되었습니다.";
            
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error deleting fund: ", e);
            throw new RuntimeException("펀딩 삭제 중 오류가 발생했습니다.", e);
        }
    }

}

