package org.funding.fund.controller;

import lombok.RequiredArgsConstructor;
import org.funding.fund.dto.FundProductRequestDTO;
import org.funding.fund.dto.FundListResponseDTO;
import org.funding.fund.dto.FundDetailResponseDTO;
import org.funding.fund.dto.FundUpdateRequestDTO;
import org.funding.fund.service.FundService;
import org.funding.fund.vo.FundVO;
import org.funding.fund.vo.enumType.FundType;
import org.funding.fund.vo.enumType.ProgressType;
import org.funding.security.util.Auth;
import org.funding.user.vo.MemberVO;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/fund")
@RequiredArgsConstructor
public class FundController {

    private final FundService fundService;

    //summary = "펀딩 상품 개설",
    //description = "요청된 정보를 기반으로 새로운 펀딩 상품을 생성합니다."
    @Auth
    @PostMapping(value = "/create/savings", consumes = "multipart/form-data")
    public ResponseEntity<?> createSavingsFund(
            @RequestPart("savingInfo") FundProductRequestDTO.SavingsRequest request,
            @RequestPart(value = "images", required = false) List<MultipartFile> images,
            HttpServletRequest servletRequest) {
        Long userId = (Long) servletRequest.getAttribute("userId");
        fundService.createSavingsFund(request, images, userId);
        return ResponseEntity.ok("Savings fund successfully created.");
    }

    @Auth
    @PostMapping(value = "/create/loan", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createLoanFund(
            @RequestPart("loanInfo") FundProductRequestDTO.LoanRequest request,
            @RequestPart(value = "images", required = false) List<MultipartFile> images,
            HttpServletRequest servletRequest) {
        Long userId = (Long) servletRequest.getAttribute("userId");
        fundService.createLoanFund(request, images, userId);
        return ResponseEntity.ok("Loan fund successfully created.");
    }

    @Auth
    @PostMapping(value = "/create/challenge", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createChallengeFund(
            @RequestPart("challengeInfo") FundProductRequestDTO.ChallengeRequest request,
            @RequestPart(value = "images", required = false) List<MultipartFile> images,
            HttpServletRequest servletRequest) {
        Long userId = (Long) servletRequest.getAttribute("userId");
        fundService.createChallengeFund(request, images, userId);
        return ResponseEntity.ok("Challenge fund successfully created.");
    }

    @Auth
    @PostMapping(value = "/create/donation", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createDonationFund(
            @RequestPart("donationInfo") FundProductRequestDTO.DonationRequest request,
            @RequestPart(value = "images", required = false) List<MultipartFile> images,
            HttpServletRequest servletRequest) {
        Long userId = (Long) servletRequest.getAttribute("userId");
        fundService.createDonationFund(request, images, userId);
        return ResponseEntity.ok("Donation fund successfully created.");
    }

    //summary = "백엔드 개발용 펀딩 상품 입력 템플릿(프론트 연결X, 프로젝트 연결 후 삭제 예정API)",
    //description = "입력한 상품 타입에 따라 해당하는 펀딩 상품 입력 폼 데이터를 반환합니다."
    // 예시) http://localhost:8080/api/fund/template?fundType=saving
    @GetMapping("/template")
    public ResponseEntity<?> getFundInputTemplate(@RequestParam String fundType) {
        String lowerType = fundType.toLowerCase();
        Map<String, Object> example = new LinkedHashMap<>();

        switch (lowerType) {
            case "savings" -> {
                example.put("name", "청년 희망 적금");
                example.put("detail", "청년을 위한 특별 금리 적금 상품");
                example.put("joinCondition", "만 19-34세, 소득 증빙 필요");
                example.put("interestRate", 3.5);
                example.put("periodDays", 365);
                example.put("successCondition", "매월 10만원 이상 납입");
                example.put("projectId", 1);
                example.put("progress", "Launch");
                example.put("launchDate", "2024-01-01");
                example.put("endDate", "2024-12-31");
                example.put("financialInstitution", "국민은행");
                example.put("keywordIds", List.of(8L, 9L, 10L));
            }
            case "loan" -> {
                example.put("name", "청년 창업 대출");
                example.put("detail", "청년 창업자를 위한 저금리 대출");
                example.put("joinCondition", "만 20-39세, 사업자등록증 필요");
                example.put("loanLimit", 50000000);
                example.put("minInterestRate", 2.5);
                example.put("maxInterestRate", 4.5);
                example.put("reward", "창업 성공시 금리 우대");
                example.put("rewardCondition", "매출 목표 달성시");
                example.put("projectId", 1);
                example.put("progress", "Launch");
                example.put("launchDate", "2024-01-01");
                example.put("endDate", "2024-12-31");
                example.put("financialInstitution", "신한은행");
                example.put("keywordIds", List.of(11L, 12L, 13L));
            }
            case "challenge" -> {
                example.put("name", "30일 절약 챌린지");
                example.put("detail", "30일간 절약 목표 달성 챌린지");
                example.put("joinCondition", "누구나 참여 가능");
                example.put("challengePeriodDays", 30);
                example.put("reward", "스타벅스 기프티콘");
                example.put("rewardCondition", "목표 금액 절약 달성시");
                example.put("verifyStandard", "영수증 인증 또는 계좌 내역 확인");
                example.put("projectId", 1);
                example.put("progress", "Launch");
                example.put("launchDate", "2024-01-01");
                example.put("endDate", "2024-01-31");
                example.put("financialInstitution", "우리은행");
                example.put("keywordIds", List.of(14L, 15L));
            }
            case "donation" -> {
                example.put("name", "아동 교육 지원 기부");
                example.put("detail", "소외계층 아동들의 교육을 지원하는 기부");
                example.put("joinCondition", "누구나 참여 가능");
                example.put("recipient", "사회복지법인 아이들의꿈");
                example.put("usagePlan", "교육용품 구입 및 장학금 지원");
                example.put("minDonationAmount", 1000);
                example.put("maxDonationAmount", 1000000);
                example.put("targetAmount", 10000000);
                example.put("projectId", 1);
                example.put("progress", "Launch");
                example.put("launchDate", "2024-01-01");
                example.put("endDate", "2024-12-31");
                example.put("financialInstitution", "하나은행");
                example.put("keywordIds", List.of(16L, 17L));
            }
            default -> {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Unsupported fundType: " + fundType));
            }
        }

        return ResponseEntity.ok(example);
    }

    // 펀딩 목록 조회 API
    
    /**
     * 진행상태별 펀딩 목록 조회 (펀드타입 필터 옵션)
     * GET /api/fund/list?progress=Launch                        (진행중인 모든 펀딩)
     * GET /api/fund/list?progress=End                           (종료된 모든 펀딩)
     * GET /api/fund/list?progress=Launch&fundType=Savings       (진행중인 적금 펀딩)
     * GET /api/fund/list?progress=End&fundType=Donation         (종료된 기부 펀딩)
     */
    @GetMapping("/list")
    public ResponseEntity<List<FundListResponseDTO>> getFundsList(
            @RequestParam ProgressType progress,
            @RequestParam(required = false) FundType fundType) {
        List<FundListResponseDTO> funds = fundService.getFundsByProgressAndType(progress, fundType);
        return ResponseEntity.ok(funds);
    }
    
    /**
     * 펀딩 상세 조회
     * GET /api/fund/{fundId}
     * fund_id로 펀딩 정보와 연관된 모든 상세 정보를 조회
     * (fund + financial_product + 타입별 상세 테이블)
     */
    @Auth
    @GetMapping("/{fundId}")
    public ResponseEntity<FundDetailResponseDTO> getFundDetail(@PathVariable Long fundId,
                                                               HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        FundDetailResponseDTO fundDetail = fundService.getFundDetail(fundId, userId);
        return ResponseEntity.ok(fundDetail);
    }
    
    /**
     * 펀딩 수정
     * PUT /api/fund/{fundId}
     * fund_id로 펀딩과 연관된 모든 정보를 수정
     * 요청 바디에 수정하고자 하는 필드만 포함시키면 해당 필드만 업데이트됨
     */
    @Auth
    @PutMapping("/{fundId}")
    public ResponseEntity<?> updateFund(
            @PathVariable Long fundId, 
            @RequestBody FundUpdateRequestDTO request,
            HttpServletRequest servletRequest) {
        Long userId = (Long) servletRequest.getAttribute("userId");
        String result = fundService.updateFund(fundId, request, userId);
        return ResponseEntity.ok(Map.of("message", result));
    }
    
    /**
     * 펀딩 삭제
     * DELETE /api/fund/{fundId}
     * fund_id로 펀딩과 관련된 모든 데이터를 삭제
     * (fund -> 타입별 테이블 -> financial_product 순서로 삭제)
     */
    @Auth
    @DeleteMapping("/{fundId}")
    public ResponseEntity<?> deleteFund(@PathVariable Long fundId,
                                        HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        String result = fundService.deleteFund(fundId, userId);
        return ResponseEntity.ok(Map.of("message", result));
    }
}
