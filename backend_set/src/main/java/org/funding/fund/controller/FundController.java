package org.funding.fund.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
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

@Api(tags = "펀딩 API")
@RestController
@RequestMapping("/api/fund")
@RequiredArgsConstructor
public class FundController {

    private final FundService fundService;

    @ApiOperation(value = "저축(Savings) 펀딩 생성", notes = "새로운 저축 타입의 펀딩을 생성합니다. 펀딩 정보(savingInfo)와 이미지 파일(images)을 multipart/form-data 형식으로 전송해야 합니다.")
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

    @ApiOperation(value = "대출(Loan) 펀딩 생성", notes = "새로운 대출 타입의 펀딩을 생성합니다. 펀딩 정보(loanInfo)와 이미지 파일(images)을 multipart/form-data 형식으로 전송해야 합니다.")
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

    @ApiOperation(value = "챌린지(Challenge) 펀딩 생성", notes = "새로운 챌린지 타입의 펀딩을 생성합니다. 펀딩 정보(challengeInfo)와 이미지 파일(images)을 multipart/form-data 형식으로 전송해야 합니다.")
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

    @ApiOperation(value = "기부(Donation) 펀딩 생성", notes = "새로운 기부 타입의 펀딩을 생성합니다. 펀딩 정보(donationInfo)와 이미지 파일(images)을 multipart/form-data 형식으로 전송해야 합니다.")
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

    @ApiOperation(value = "펀딩 목록 조회", notes = "진행 상태(progress)와 펀딩 타입(fundType)을 기준으로 펀딩 목록을 필터링하여 조회합니다.")
    @GetMapping("/list")
    public ResponseEntity<List<FundListResponseDTO>> getFundsList(
            @ApiParam(value = "펀딩 진행 상태", required = true, example = "Launch") @RequestParam ProgressType progress,
            @ApiParam(value = "펀딩 타입 (선택)", example = "Challenge") @RequestParam(required = false) FundType fundType) {
        List<FundListResponseDTO> funds = fundService.getFundsByProgressAndType(progress, fundType);
        return ResponseEntity.ok(funds);
    }

    @ApiOperation(value = "펀딩 상세 조회", notes = "특정 펀딩(fundId)의 모든 상세 정보를 조회합니다.")
    @GetMapping("/{fundId}")
    public ResponseEntity<FundDetailResponseDTO> getFundDetail(
            @ApiParam(value = "조회할 펀딩 ID", required = true, example = "1") @PathVariable Long fundId,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        FundDetailResponseDTO fundDetail = fundService.getFundDetail(fundId, userId);
        return ResponseEntity.ok(fundDetail);
    }

    @ApiOperation(value = "펀딩 수정 (생성자용)", notes = "특정 펀딩의 정보를 수정합니다. 펀딩 생성자만 수정할 수 있습니다.")
    @Auth
    @PutMapping("/{fundId}")
    public ResponseEntity<?> updateFund(
            @ApiParam(value = "수정할 펀딩 ID", required = true, example = "1") @PathVariable Long fundId,
            @RequestBody FundUpdateRequestDTO request,
            HttpServletRequest servletRequest) {
        Long userId = (Long) servletRequest.getAttribute("userId");
        String result = fundService.updateFund(fundId, request, userId);
        return ResponseEntity.ok(Map.of("message", result));
    }

    @ApiOperation(value = "펀딩 삭제 (생성자용)", notes = "특정 펀딩을 삭제합니다. 펀딩 생성자만 삭제할 수 있습니다.")
    @Auth
    @DeleteMapping("/{fundId}")
    public ResponseEntity<?> deleteFund(
            @ApiParam(value = "삭제할 펀딩 ID", required = true, example = "1") @PathVariable Long fundId,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        String result = fundService.deleteFund(fundId, userId);
        return ResponseEntity.ok(Map.of("message", result));
    }

    @ApiOperation(value = "내가 생성한 펀딩 목록 조회", notes = "현재 로그인한 사용자가 생성한 모든 펀딩 목록을 조회합니다. 펀딩 타입으로 필터링할 수 있습니다.")
    @Auth
    @GetMapping("/my/fund/all")
    public ResponseEntity<?> getMyAllCreatedFunds(
            @ApiParam(value = "조회할 펀딩 타입 (선택)", example = "Loan") @RequestParam(value = "fundType", required = false) String fundType,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            throw new FundException(ErrorCode.MEMBER_NOT_FOUND);
        }
        List<MyFundDetailDTO> myFunds = fundService.findMyCreatedFunds(userId, fundType);
        return ResponseEntity.ok(myFunds);
    }
}
