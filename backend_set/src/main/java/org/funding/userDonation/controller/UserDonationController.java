package org.funding.userDonation.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.funding.global.error.ErrorCode;
import org.funding.global.error.exception.UserDonationException;
import org.funding.security.util.Auth;
import org.funding.userDonation.dto.DonateRequestDTO;
import org.funding.userDonation.dto.DonateResponseDTO;
import org.funding.userDonation.dto.UserDonationDetailDTO;
import org.funding.userDonation.service.UserDonationService;
import org.funding.userDonation.vo.UserDonationVO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Api(tags = "기부 참여 API (사용자용)")
@RestController
@RequestMapping("/api/user-donation")
@RequiredArgsConstructor
public class UserDonationController {

    private final UserDonationService userDonationService;

    @ApiOperation(value = "기부하기", notes = "특정 펀딩(fundId)에 기부합니다.")
    @Auth
    @PostMapping("/donation")
    public ResponseEntity<DonateResponseDTO> donate(@RequestBody DonateRequestDTO donateRequestDTO,
                                                    HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return ResponseEntity.ok(userDonationService.donate(donateRequestDTO, userId));
    }

    @ApiOperation(value = "단일 기부 내역 상세 조회", notes = "특정 기부 내역(userDonationId)을 상세 조회합니다.")
    @Auth
    @GetMapping("/donation/{id}")
    public ResponseEntity<UserDonationVO> getDonation(
            @ApiParam(value = "조회할 사용자 기부 ID", required = true, example = "1") @PathVariable("id") Long id,
            HttpServletRequest request) {
        return ResponseEntity.ok(userDonationService.getDonation(id));
    }

    @ApiOperation(value = "나의 전체 기부 내역 조회", notes = "현재 로그인한 사용자의 모든 기부 내역을 조회합니다.")
    @Auth
    @GetMapping("/donation-history")
    public ResponseEntity<List<UserDonationVO>> getAllDonations(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return ResponseEntity.ok(userDonationService.getAllDonations(userId));
    }

    @ApiOperation(value = "기부 내역 수정", notes = "특정 기부 내역(userDonationId)의 금액 또는 익명 여부를 수정합니다. 본인의 기부 내역만 수정 가능합니다.")
    @Auth
    @PatchMapping("/{id}")
    public ResponseEntity<String> updateDonation(
            @ApiParam(value = "수정할 사용자 기부 ID", required = true, example = "1") @PathVariable("id") Long id,
            @RequestBody DonateRequestDTO donateRequestDTO,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return ResponseEntity.ok(userDonationService.updateDonation(id, donateRequestDTO, userId));
    }

    @ApiOperation(value = "기부 내역 삭제", notes = "특정 기부 내역(userDonationId)을 삭제합니다. 본인의 기부 내역만 삭제 가능합니다.")
    @Auth
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteDonation(
            @ApiParam(value = "삭제할 사용자 기부 ID", required = true, example = "1") @PathVariable("id") Long id,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return ResponseEntity.ok(userDonationService.deleteDonation(id, userId));
    }

    @ApiOperation(value = "내가 참여한 모든 기부 펀딩 조회", notes = "현재 로그인한 사용자가 참여한 모든 기부 펀딩의 상세 목록을 조회합니다.")
    @Auth
    @GetMapping("/user/all/v2")
    public ResponseEntity<?> getMyAllDonations(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            throw new UserDonationException(ErrorCode.MEMBER_NOT_FOUND);
        }
        List<UserDonationDetailDTO> myDonations = userDonationService.findMyDonations(userId);
        return ResponseEntity.ok(myDonations);
    }
}