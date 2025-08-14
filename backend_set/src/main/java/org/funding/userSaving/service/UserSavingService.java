package org.funding.userSaving.service;

import lombok.RequiredArgsConstructor;
import org.funding.S3.dao.S3ImageDAO;
import org.funding.S3.vo.S3ImageVO;
import org.funding.S3.vo.enumType.ImageType;
import org.funding.badge.service.BadgeService;
import org.funding.global.error.ErrorCode;
import org.funding.global.error.exception.UserSavingException;
import org.funding.user.dao.MemberDAO;
import org.funding.user.vo.MemberVO;
import org.funding.userSaving.dao.UserSavingDAO;
import org.funding.userSaving.dto.UserSavingDetailDTO;
import org.funding.userSaving.dto.UserSavingRequestDTO;
import org.funding.userSaving.vo.UserSavingVO;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserSavingService {

    private final MemberDAO memberDAO;
    private final UserSavingDAO userSavingDAO;
    private final BadgeService badgeService;
    private final S3ImageDAO s3ImageDAO;

    // 저축 가입
    public String applySaving(Long fundId, UserSavingRequestDTO userSavingRequestDTO, Long userId) {
        MemberVO member = memberDAO.findById(userId);
        if (member == null) {
            throw new UserSavingException(ErrorCode.MEMBER_NOT_FOUND);
        }

        UserSavingVO userSaving = new UserSavingVO();
        userSaving.setUserId(userId);
        userSaving.setFundId(fundId);
        userSaving.setSavingAmount(userSavingRequestDTO.getSavingAmount());

        userSavingDAO.insertUserSaving(userSaving);

        // 뱃지 권한 부여
        badgeService.checkAndGrantBadges(userId);

        return "정상적으로 저축에 가입하셨습니다.";
    }

    // 저축 상품 해지
    public String cancelSaving(Long userSavingId, Long userId) {
        UserSavingVO userSaving = userSavingDAO.findById(userSavingId);
        if (userSaving == null) {
            throw new UserSavingException(ErrorCode.NOT_FOUND_SAVING);
        }
        if (!Objects.equals(userSaving.getUserId(), userId)) {
            throw new UserSavingException(ErrorCode.NO_CANCEL_SAVING);
        }

        userSavingDAO.deleteUserSaving(userSavingId);
        return "정상적으로 해지되었습니다.";
    }

    // id로 단건 조회
    public UserSavingVO findById(Long userSavingId) {
        UserSavingVO userSaving = userSavingDAO.findById(userSavingId);
        if (userSaving == null) {
            throw new UserSavingException(ErrorCode.NOT_FOUND_SAVING);
        }
        return userSaving;
    }

    // 유저가 가입한 저축 상품 전체 보기
    public List<UserSavingVO> getAllUserSaving(Long userId) {
        return userSavingDAO.findByUserId(userId);
    }

    // 유저가 가입한 저축 상품 모아보기(디테일)
    public List<UserSavingDetailDTO> findMySavings(Long userId) {
        List<UserSavingDetailDTO> mySavings = userSavingDAO.findAllSavingDetailsByUserId(userId);

        if (mySavings == null || mySavings.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> productIds = mySavings.stream()
                .map(UserSavingDetailDTO::getProductId)
                .collect(Collectors.toList());

        List<S3ImageVO> allImages = s3ImageDAO.findImagesForPostIds(ImageType.Funding, productIds);

        Map<Long, List<S3ImageVO>> imagesByProductId = allImages.stream()
                .collect(Collectors.groupingBy(S3ImageVO::getPostId));

        mySavings.forEach(saving ->
                saving.setImages(imagesByProductId.getOrDefault(saving.getProductId(), Collections.emptyList()))
        );

        return mySavings;
    }
}
