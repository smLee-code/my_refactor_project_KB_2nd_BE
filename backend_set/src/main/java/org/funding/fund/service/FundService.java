package org.funding.fund.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.funding.financialProduct.dao.*;
import org.funding.financialProduct.dto.*;
import org.funding.financialProduct.vo.*;
import org.funding.fund.dao.FundDAO;
import org.funding.fund.dto.FundCreateDTO;
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

    public String createFund(Map<String, Object> requestMap) {
        try {
            String fundTypeStr = (String) requestMap.get("fundType");
            FundType fundType = FundType.fromString(fundTypeStr);
            
            log.info("Creating fund with type: {}", fundType);
            
            switch (fundType) {
                case Savings:
                    return createSavingsFund(requestMap);
                case Loan:
                    return createLoanFund(requestMap);
                case Challenge:
                    return createChallengeFund(requestMap);
                case Donation:
                    return createDonationFund(requestMap);
                default:
                    throw new IllegalArgumentException("Unsupported fund type: " + fundType);
            }
        } catch (Exception e) {
            log.error("Error creating fund: ", e);
            return "Error: " + e.getMessage();
        }
    }
    
    private String createSavingsFund(Map<String, Object> requestMap) {
        try {
            FinancialProductVO product = createBaseFinancialProduct(requestMap, FundType.Savings);
            FinancialProductVO savedProduct = financialProductDAO.insert(product);
            
            SavingsVO savings = SavingsVO.builder()
                .productId(savedProduct.getProductId())
                .interestRate(((Number) requestMap.get("interestRate")).doubleValue())
                .periodDays(((Number) requestMap.get("periodDays")).intValue())
                .successCondition((String) requestMap.get("successCondition"))
                .build();
            
            savingsDAO.insertSavings(savings);
            log.info("Savings fund created successfully with product ID: {}", savedProduct.getProductId());
            return "Savings fund created successfully";
        } catch (Exception e) {
            log.error("Error creating savings fund: ", e);
            throw new RuntimeException("Failed to create savings fund", e);
        }
    }
    
    private String createLoanFund(Map<String, Object> requestMap) {
        try {
            FinancialProductVO product = createBaseFinancialProduct(requestMap, FundType.Loan);
            FinancialProductVO savedProduct = financialProductDAO.insert(product);
            
            LoanVO loan = LoanVO.builder()
                .productId(savedProduct.getProductId())
                .loanLimit(((Number) requestMap.get("loanLimit")).longValue())
                .minInterestRate(((Number) requestMap.get("minInterestRate")).doubleValue())
                .maxInterestRate(((Number) requestMap.get("maxInterestRate")).doubleValue())
                .reward((String) requestMap.get("reward"))
                .rewardCondition((String) requestMap.get("rewardCondition"))
                .build();
            
            loanDAO.insertLoan(loan);
            log.info("Loan fund created successfully with product ID: {}", savedProduct.getProductId());
            return "Loan fund created successfully";
        } catch (Exception e) {
            log.error("Error creating loan fund: ", e);
            throw new RuntimeException("Failed to create loan fund", e);
        }
    }
    
    private String createChallengeFund(Map<String, Object> requestMap) {
        try {
            FinancialProductVO product = createBaseFinancialProduct(requestMap, FundType.Challenge);
            FinancialProductVO savedProduct = financialProductDAO.insert(product);
            
            ChallengeVO challenge = ChallengeVO.builder()
                .productId(savedProduct.getProductId())
                .challengePeriodDays(((Number) requestMap.get("challengePeriodDays")).intValue())
                .reward((String) requestMap.get("reward"))
                .rewardCondition((String) requestMap.get("rewardCondition"))
                .build();
            
            challengeDAO.insertChallenge(challenge);
            log.info("Challenge fund created successfully with product ID: {}", savedProduct.getProductId());
            return "Challenge fund created successfully";
        } catch (Exception e) {
            log.error("Error creating challenge fund: ", e);
            throw new RuntimeException("Failed to create challenge fund", e);
        }
    }
    
    private String createDonationFund(Map<String, Object> requestMap) {
        try {
            FinancialProductVO product = createBaseFinancialProduct(requestMap, FundType.Donation);
            FinancialProductVO savedProduct = financialProductDAO.insert(product);
            
            DonationVO donation = DonationVO.builder()
                .productId(savedProduct.getProductId())
                .recipient((String) requestMap.get("recipient"))
                .usagePlan((String) requestMap.get("usagePlan"))
                .minDonationAmount(((Number) requestMap.get("minDonationAmount")).intValue())
                .maxDonationAmount(requestMap.get("maxDonationAmount") != null ? ((Number) requestMap.get("maxDonationAmount")).intValue() : null)
                .targetAmount(((Number) requestMap.get("targetAmount")).longValue())
                .build();
            
            donationDAO.insertDonation(donation);
            log.info("Donation fund created successfully with product ID: {}", savedProduct.getProductId());
            return "Donation fund created successfully";
        } catch (Exception e) {
            log.error("Error creating donation fund: ", e);
            throw new RuntimeException("Failed to create donation fund", e);
        }
    }
    
    private FinancialProductVO createBaseFinancialProduct(Map<String, Object> requestMap, FundType fundType) {
        return FinancialProductVO.builder()
            .name((String) requestMap.get("name"))
            .detail((String) requestMap.get("detail"))
            .fundType(fundType)
            .thumbnail((String) requestMap.get("thumbnail"))
            .joinCondition((String) requestMap.get("joinCondition"))
            .build();
    }

}

