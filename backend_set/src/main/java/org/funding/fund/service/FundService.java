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

    public String createSavingsFund(FundProductRequestDTO.SavingsRequest request) {
        try {
            log.info("Creating savings fund with name: {}", request.getName());
            
            FinancialProductVO product = FinancialProductVO.builder()
                .name(request.getName())
                .detail(request.getDetail())
                .fundType(FundType.Savings)
                .thumbnail(request.getThumbnail())
                .joinCondition(request.getJoinCondition())
                .build();
            
            financialProductDAO.insert(product);
            
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
    
    public String createLoanFund(FundProductRequestDTO.LoanRequest request) {
        try {
            log.info("Creating loan fund with name: {}", request.getName());
            
            FinancialProductVO product = FinancialProductVO.builder()
                .name(request.getName())
                .detail(request.getDetail())
                .fundType(FundType.Loan)
                .thumbnail(request.getThumbnail())
                .joinCondition(request.getJoinCondition())
                .build();
            
            financialProductDAO.insert(product);
            
            LoanVO loan = LoanVO.builder()
                .productId(product.getProductId())
                .loanLimit(request.getLoanLimit())
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
    
    public String createChallengeFund(FundProductRequestDTO.ChallengeRequest request) {
        try {
            log.info("Creating challenge fund with name: {}", request.getName());
            
            FinancialProductVO product = FinancialProductVO.builder()
                .name(request.getName())
                .detail(request.getDetail())
                .fundType(FundType.Challenge)
                .thumbnail(request.getThumbnail())
                .joinCondition(request.getJoinCondition())
                .build();
            
            financialProductDAO.insert(product);
            
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
    
    public String createDonationFund(FundProductRequestDTO.DonationRequest request) {
        try {
            log.info("Creating donation fund with name: {}", request.getName());
            
            FinancialProductVO product = FinancialProductVO.builder()
                .name(request.getName())
                .detail(request.getDetail())
                .fundType(FundType.Donation)
                .thumbnail(request.getThumbnail())
                .joinCondition(request.getJoinCondition())
                .build();
            
            financialProductDAO.insert(product);
            
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

