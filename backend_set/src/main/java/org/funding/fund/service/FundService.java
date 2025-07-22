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
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDate;
import java.util.List;

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

}

