package org.funding.fund.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.funding.financialProduct.dao.*;
import org.funding.financialProduct.dto.*;
import org.funding.financialProduct.vo.*;
import org.funding.fund.dao.FundDAO;
import org.funding.fund.dto.FundCreateDTO;
import org.funding.fund.dto.FundProductRequestDTO;
import org.funding.fund.vo.FundVO;
import org.funding.fund.vo.enumType.FundType;
import org.funding.fund.vo.enumType.ProgressType;
import org.springframework.stereotype.Service;
import java.util.Map;

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
            log.info("Savings fund created successfully with product ID: {}", product.getProductId());
            return "Savings fund created successfully";
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
            log.info("Loan fund created successfully with product ID: {}", product.getProductId());
            return "Loan fund created successfully";
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
            log.info("Challenge fund created successfully with product ID: {}", product.getProductId());
            return "Challenge fund created successfully";
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
            log.info("Donation fund created successfully with product ID: {}", product.getProductId());
            return "Donation fund created successfully";
        } catch (Exception e) {
            log.error("Error creating donation fund: ", e);
            throw new RuntimeException("Failed to create donation fund", e);
        }
    }
    

}

