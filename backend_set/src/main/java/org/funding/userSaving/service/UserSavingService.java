package org.funding.userSaving.service;

import lombok.RequiredArgsConstructor;
import org.funding.S3.dao.S3ImageDAO;
import org.funding.S3.vo.S3ImageVO;
import org.funding.S3.vo.enumType.ImageType;
import org.funding.badge.service.BadgeService;
import org.funding.user.dao.MemberDAO;
import org.funding.user.vo.MemberVO;
import org.funding.userSaving.dao.UserSavingDAO;
import org.funding.userSaving.dto.UserSavingDetailDTO;
import org.funding.userSaving.dto.UserSavingRequestDTO;
import org.funding.userSaving.vo.UserSavingVO;
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
    public String applySaving(UserSavingRequestDTO userSavingRequestDTO, Long userId) {
        MemberVO member = memberDAO.findById(userId);
        if (member == null) {
            throw new RuntimeException("해당 멤버는 존재하지 않습니다.");
        }

        UserSavingVO userSaving = new UserSavingVO();
        userSaving.setUserId(userId);
        userSaving.setFundId(userSavingRequestDTO.getFundId());
        userSaving.setSavingAmount(userSaving.getSavingAmount());

        userSavingDAO.insertUserSaving(userSaving);

        // 뱃지 권한 부여
        badgeService.checkAndGrantBadges(userId);

        return "정상적으로 저축에 가입하셨습니다.";
    }

    // 저축 상품 해지
    public String cancelSaving(Long userSavingId, Long userId) {
        UserSavingVO userSaving = userSavingDAO.findById(userSavingId);
        if (userSaving == null) {
            throw new RuntimeException("해당 저축에 신청되어있지 않습니다.");
        }
        if (!Objects.equals(userSaving.getUserId(), userId)) {
            throw new RuntimeException("해지 권한이 없습니다. (본인만 가능)");
        }

        userSavingDAO.deleteUserSaving(userSavingId);
        return "정상적으로 해지되었습니다.";
    }

    // id로 단건 조회
    public UserSavingVO findById(Long userSavingId) {
        UserSavingVO userSaving = userSavingDAO.findById(userSavingId);
        if (userSaving == null) {
            throw new RuntimeException("해당 저축 상품이 존재하지 않습니다.");
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
