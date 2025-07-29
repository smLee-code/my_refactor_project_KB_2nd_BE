package org.funding.userDonation.controller;

import lombok.RequiredArgsConstructor;
import org.funding.userDonation.dto.DonateRequestDTO;
import org.funding.userDonation.dto.DonateResponseDTO;
import org.funding.userDonation.service.UserDonationService;
import org.funding.userDonation.vo.UserDonationVO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user-saving")
@RequiredArgsConstructor
public class UserDonationController {

    private final UserDonationService userDonationService;
    // 기부
    @PostMapping("/saving")
    public ResponseEntity<DonateResponseDTO> donate(@RequestBody DonateRequestDTO donateRequestDTO) {
        return ResponseEntity.ok(userDonationService.donate(donateRequestDTO));
    }


    // 기부 내역 상세 조회
    @GetMapping("/{id}")
    public ResponseEntity<UserDonationVO> getDonation(@PathVariable("id") Long id) {
        return ResponseEntity.ok(userDonationService.getDonation(id));
    }

    // 유저의 기부 내역 전체 조회
    @GetMapping("/{id}")
    public ResponseEntity<List<UserDonationVO>> getAllDonations(@PathVariable("id") Long id) {
        return ResponseEntity.ok(userDonationService.getAllDonations(id));
    }

    // 기부 내역 수정 (관리자용)
    @PatchMapping("/{id}")
    public ResponseEntity<String> updateDonation(@PathVariable("id") Long id, @RequestBody DonateRequestDTO donateRequestDTO) {
        return ResponseEntity.ok(userDonationService.updateDonation(id, donateRequestDTO));
    }

    // 기부 내역 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteDonation(@PathVariable("id") Long id) {
        return ResponseEntity.ok(userDonationService.deleteDonation(id));
    }
}
