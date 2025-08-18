package org.funding.fund.controller;

import lombok.RequiredArgsConstructor;
import org.funding.fund.dto.*;
import org.funding.fund.service.FundService;
import org.funding.fund.vo.FundVO;
import org.funding.fund.vo.enumType.FundType;
import org.funding.fund.vo.enumType.ProgressType;
import org.funding.global.error.ErrorCode;
import org.funding.global.error.exception.FundException;
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
        return ResponseEntity.ok("펀딩이 성공적으로 생성되었습니다.");
    }

    @Auth
    @PostMapping(value = "/create/loan", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createLoanFund(
            @RequestPart("loanInfo") FundProductRequestDTO.LoanRequest request,
            @RequestPart(value = "images", required = false) List<MultipartFile> images,
            HttpServletRequest servletRequest) {
        Long userId = (Long) servletRequest.getAttribute("userId");
        fundService.createLoanFund(request, images, userId);
        return ResponseEntity.ok("펀딩이 성공적으로 생성되었습니다.");
    }

    @Auth
    @PostMapping(value = "/create/challenge", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createChallengeFund(
            @RequestPart("challengeInfo") FundProductRequestDTO.ChallengeRequest request,
            @RequestPart(value = "images", required = false) List<MultipartFile> images,
            HttpServletRequest servletRequest) {
        Long userId = (Long) servletRequest.getAttribute("userId");
        fundService.createChallengeFund(request, images, userId);
        return ResponseEntity.ok("펀딩이 성공적으로 생성되었습니다.");
    }

    @Auth
    @PostMapping(value = "/create/donation", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createDonationFund(
            @RequestPart("donationInfo") FundProductRequestDTO.DonationRequest request,
            @RequestPart(value = "images", required = false) List<MultipartFile> images,
            HttpServletRequest servletRequest) {
        Long userId = (Long) servletRequest.getAttribute("userId");
        fundService.createDonationFund(request, images, userId);
        return ResponseEntity.ok("펀딩이 성공적으로 생성되었습니다.");
    }

    // 펀딩 목록 조회 API
    
    /**
     * 진행상태별 펀딩 목록 조회 (펀드타입 필터 옵션)
     * GET /api/fund/list?progress=Launch                        (진행중인 모든 펀딩)
     * GET /api/fund/list?progress=End                           (종료된 모든 펀딩)
     * GET /api/fund/list?progress=Launch&fundType=Savings       (진행중인 저축 펀딩)
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
     * 로그인 시 펀딩 참여여부 정보 추가 제공
     */
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
     *
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


    // 유저가 생성한 프로젝트 조회
    @Auth
    @GetMapping("/my/fund/all")
    public ResponseEntity<?> getMyAllCreatedFunds(
            @RequestParam(value = "fundType", required = false) String fundType,
            HttpServletRequest request) {

        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            throw new FundException(ErrorCode.MEMBER_NOT_FOUND);
        }
        // 서비스 호출 시 fundType 전달
        List<MyFundDetailDTO> myFunds = fundService.findMyCreatedFunds(userId, fundType);

        return ResponseEntity.ok(myFunds);
    }


}
