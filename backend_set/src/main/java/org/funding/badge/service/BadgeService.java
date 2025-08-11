package org.funding.badge.service;

import lombok.RequiredArgsConstructor;
import org.funding.badge.dao.BadgeDAO;
import org.funding.badge.dto.BadgeResponseDTO;
import org.funding.badge.dto.CreateBadgeDTO;
import org.funding.badge.dto.UpdateBadgeDTO;
import org.funding.badge.vo.BadgeVO;
import org.funding.global.error.ErrorCode;
import org.funding.global.error.exception.BadgeException;
import org.funding.global.error.exception.MemberException;
import org.funding.mapping.UserBadgeVO;
import org.funding.user.dao.MemberDAO;
import org.funding.user.vo.MemberVO;
import org.funding.user.vo.enumType.Role;
import org.funding.votes.dao.VotesDAO;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BadgeService {

    private final BadgeDAO badgeDAO;
    private final MemberDAO memberDAO;

    // 뱃지 생성
    public void createBadge(CreateBadgeDTO createBadgeDTO, Long userId) {
        MemberVO member = memberDAO.findById(userId);
        if (member.getRole() != Role.ROLE_ADMIN) {
            throw new BadgeException(ErrorCode.ACCESS_DENIED);
        }

        BadgeVO badgeVO = new BadgeVO();
        badgeVO.setName(createBadgeDTO.getName());
        badgeVO.setDescription(createBadgeDTO.getDescription());
        badgeVO.setAutoGrantCondition(createBadgeDTO.getAutoGrantCondition());
        badgeDAO.insertBadge(badgeVO);
    }

    // 뱃지 수정
    public void updateBadge(UpdateBadgeDTO updateBadgeDTO, Long id, Long userId) {
        MemberVO member = memberDAO.findById(userId);
        if (member.getRole() != Role.ROLE_ADMIN) {
            throw new BadgeException(ErrorCode.ACCESS_DENIED);
        }

        BadgeVO badgeVO = new BadgeVO();
        badgeVO.setBadgeId(id);
        badgeVO.setName(updateBadgeDTO.getName());
        badgeVO.setDescription(updateBadgeDTO.getDescription());
        badgeVO.setAutoGrantCondition(updateBadgeDTO.getAutoGrantCondition());
        badgeDAO.updateBadge(badgeVO);
    }

    // 뱃지 삭제
    public void deleteBadge(Long badgeId, Long userId) {
        MemberVO member = memberDAO.findById(userId);
        if (member.getRole() != Role.ROLE_ADMIN) {
            throw new BadgeException(ErrorCode.ACCESS_DENIED);
        }

        badgeDAO.deleteBadge(badgeId);
    }

    // 뱃지 단건 조회
    public BadgeResponseDTO getBadge(Long badgeId) {
        return badgeDAO.selectBadgeById(badgeId);
    }

    // 뱃지 전체 조회
    public List<BadgeResponseDTO> getAllBadges() {
        return badgeDAO.selectAllBadges();
    }

    // 뱃지 자동 부여 기능
    public void checkAndGrantBadges(Long userId) {
        List<BadgeVO> badgeList = badgeDAO.selectAutoGrantBadges();

        for (BadgeVO badge : badgeList) {
            if (badgeDAO.hasUserBadge(userId, badge.getBadgeId())) {
                continue;
            }

            boolean isEligible = switch (badge.getAutoGrantCondition()) {

                case "COMPLETED_FUNDED_PROJECT" -> badgeDAO.hasCompletedFundedProject(userId);
                case "ROLE_ADMIN" -> badgeDAO.isAdmin(userId);
                case "DONATED" -> badgeDAO.hasDonated(userId);
                case "CHALLENGE_PARTICIPANT" -> badgeDAO.hasJoinedChallenge(userId);
                case "FINANCIAL_SUBSCRIBER" -> badgeDAO.hasSubscribedFinancialProduct(userId);
                case "INFLUENCER" -> badgeDAO.hasProjectWithTenOrMoreComments(userId);
                case "COMMENT_MASTER" -> badgeDAO.hasPostedTenComments(userId);
                case "HIT_PROJECT_MAKER" -> badgeDAO.hasProjectWithTenOrMoreLikes(userId);
                default -> false;
            };

            if (isEligible) {
                UserBadgeVO userBadge = new UserBadgeVO();
                userBadge.setUserId(userId);
                userBadge.setBadgeId(badge.getBadgeId());
                userBadge.setGrantedAt(LocalDateTime.now());

                badgeDAO.insertUserBadge(userBadge);
            }
        }
    }

    // 유저가 가진 전체 뱃지 조회
    public List<BadgeResponseDTO> getUserBadges(Long userId) {
        MemberVO member = memberDAO.findById(userId);
        if (member.getRole() != Role.ROLE_ADMIN) {
            throw new BadgeException(ErrorCode.MEMBER_NOT_FOUND);
        }

        return badgeDAO.findBadgesByUserId(userId);
    }
}
