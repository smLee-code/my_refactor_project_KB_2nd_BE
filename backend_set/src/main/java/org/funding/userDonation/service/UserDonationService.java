package org.funding.userDonation.service;

import lombok.RequiredArgsConstructor;
import org.funding.S3.dao.S3ImageDAO;
import org.funding.S3.vo.S3ImageVO;
import org.funding.S3.vo.enumType.ImageType;
import org.funding.badge.service.BadgeService;
import org.funding.financialProduct.dao.DonationDAO;
import org.funding.financialProduct.vo.DonationVO;
import org.funding.fund.dao.FundDAO;
import org.funding.fund.vo.FundVO;
import org.funding.fund.vo.enumType.ProgressType;
import org.funding.global.error.ErrorCode;
import org.funding.global.error.exception.UserDonationException;
import org.funding.user.dao.MemberDAO;
import org.funding.user.vo.MemberVO;
import org.funding.user.vo.enumType.Role;
import org.funding.userDonation.dao.UserDonationDAO;
import org.funding.userDonation.dto.DonateRequestDTO;
import org.funding.userDonation.dto.DonateResponseDTO;
import org.funding.userDonation.dto.UserDonationDetailDTO;
import org.funding.userDonation.vo.UserDonationVO;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserDonationService {
    private final MemberDAO memberDAO;
    private final UserDonationDAO userDonationDAO;
    private final DonationDAO donationDAO;
    private final FundDAO fundDAO;
    private final BadgeService badgeService;
    private final S3ImageDAO s3ImageDAO;

    // 기부
    public DonateResponseDTO donate(DonateRequestDTO donateRequestDTO, Long userId) {
        Long fundId = donateRequestDTO.getFundId();

        MemberVO member = validateMember(userId);
        FundVO fund = validateFund(fundId);

        Long productId = fund.getProductId();

        // 기부 금액 검증
        DonationVO donation = donationDAO.selectByProductId(productId);
        Integer maxAmount = donation.getMaxDonationAmount();
        Integer minAmount = donation.getMinDonationAmount();

        if (donateRequestDTO.getDonateAmount() > maxAmount || donateRequestDTO.getDonateAmount() < minAmount) {
            throw new UserDonationException(ErrorCode.OVER_DONATE_AMOUNT);
        }

        UserDonationVO userDonationVO = new UserDonationVO();
        userDonationVO.setUserId(userId);
        userDonationVO.setDonationAmount(donateRequestDTO.getDonateAmount());
        userDonationVO.setAnonymous(donateRequestDTO.isAnonymous());
        userDonationDAO.insertUserDonation(userDonationVO);

        // 뱃지 권한 검증 (기부 참여)
        badgeService.checkAndGrantBadges(userId);

        DonateResponseDTO donateResponseDTO = new DonateResponseDTO();
        donateResponseDTO.setUserName(member.getUsername());
        donateResponseDTO.setDonateAmount(donateRequestDTO.getDonateAmount());
        return donateResponseDTO;
    }

    // 기부 내역 단건 조회
    public UserDonationVO getDonation(Long userDonationId) {
        UserDonationVO userDonation = userDonationDAO.findById(userDonationId);
        if (userDonation == null) {
            throw new UserDonationException(ErrorCode.NOT_FOUND_DONATE);
        }

        return userDonation;
    }

    // 유저의 기부 내역 전체 조회
    public List<UserDonationVO> getAllDonations(Long userId) {
        MemberVO member = memberDAO.findById(userId);
        if (member == null) {
            throw new UserDonationException(ErrorCode.MEMBER_NOT_FOUND);
        }

        return userDonationDAO.findByUserId(userId);
    }

    // 기부 내역 수정
    public String updateDonation(Long userDonationId, DonateRequestDTO donateRequestDTO, Long userId) {
        MemberVO member = memberDAO.findById(userId);
        if (member.getRole() != Role.ROLE_ADMIN) {
            throw new UserDonationException(ErrorCode.MEMBER_NOT_ADMIN);
        }

        UserDonationVO userDonation = userDonationDAO.findById(userDonationId);
        if (userDonation == null) {
            throw new UserDonationException(ErrorCode.NOT_FOUND_DONATE);
        }

        userDonation.setDonationAmount(donateRequestDTO.getDonateAmount());
        userDonation.setAnonymous(donateRequestDTO.isAnonymous());

        userDonationDAO.updateUserDonation(userDonation);

        return "기부 내역이 수정되었습니다.";
    }

    // 기부 내역 삭제
    public String deleteDonation(Long userDonationId, Long userId) {
        MemberVO member = memberDAO.findById(userId);
        if (member == null) {
            throw new UserDonationException(ErrorCode.MEMBER_NOT_FOUND);
        }

        UserDonationVO userDonationVO = userDonationDAO.findById(userDonationId);
        if (userDonationVO == null) {
            throw new UserDonationException(ErrorCode.NOT_FOUND_DONATE);
        }

        if (!Objects.equals(userDonationVO.getUserId(), member.getUserId())) {
            throw new UserDonationException(ErrorCode.CAN_NOT_DELETE);
        }

        userDonationDAO.deleteUserDonation(userDonationId);
        return "정상적으로 기부 내역이 삭제되었습니다.";
    }
    // 공통 검증 메서드
    private MemberVO validateMember(Long userId) {
        MemberVO member = memberDAO.findById(userId);
        if (member == null) {
            throw new UserDonationException(ErrorCode.MEMBER_NOT_FOUND);
        }
        return member;
    }

    private FundVO validateFund(Long fundId) {
        FundVO fund = fundDAO.selectById(fundId);
        if (fund == null) {
            throw new UserDonationException(ErrorCode.FUNDING_NOT_FOUND);
        }

        if (fund.getProgress() == ProgressType.End) {
            throw new UserDonationException(ErrorCode.DONE_DONATE);
        }
        return fund;
    }

    // 유저가 참여한 기부 전체 조회하기
    public List<UserDonationDetailDTO> findMyDonations(Long userId) {

        List<UserDonationDetailDTO> myDonations = userDonationDAO.findAllDonationDetailsByUserId(userId);

        if (myDonations == null || myDonations.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> productIds = myDonations.stream()
                .map(UserDonationDetailDTO::getProductId)
                .collect(Collectors.toList());

        List<S3ImageVO> allImages = s3ImageDAO.findImagesForPostIds(ImageType.Funding, productIds);

        Map<Long, List<S3ImageVO>> imagesByProductId = allImages.stream()
                .collect(Collectors.groupingBy(S3ImageVO::getPostId));
        myDonations.forEach(donation ->
                donation.setImages(imagesByProductId.getOrDefault(donation.getProductId(), Collections.emptyList()))
        );

        return myDonations;
    }

}
