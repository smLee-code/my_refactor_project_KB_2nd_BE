package org.funding.badge.service;

import lombok.RequiredArgsConstructor;
import org.funding.badge.dao.BadgeDAO;
import org.funding.badge.dto.BadgeResponseDTO;
import org.funding.badge.dto.CreateBadgeDTO;
import org.funding.badge.dto.UpdateBadgeDTO;
import org.funding.badge.vo.BadgeVO;
import org.funding.mapping.UserBadgeVO;
import org.funding.votes.dao.VotesDAO;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BadgeService {

    private final BadgeDAO badgeDAO;
    private final VotesDAO votesDAO;

    // 뱃지 생성
    public void createBadge(CreateBadgeDTO createBadgeDTO) {
        BadgeVO badgeVO = new BadgeVO();
        badgeVO.setName(createBadgeDTO.getName());
        badgeVO.setDescription(createBadgeDTO.getDescription());
        badgeVO.setAutoGrantCondition(createBadgeDTO.getAutoGrantCondition());
        badgeDAO.insertBadge(badgeVO);
    }

    // 뱃지 수정
    public void updateBadge(UpdateBadgeDTO updateBadgeDTO, Long id) {
        BadgeVO badgeVO = new BadgeVO();
        badgeVO.setBadgeId(id);
        badgeVO.setName(updateBadgeDTO.getName());
        badgeVO.setDescription(updateBadgeDTO.getDescription());
        badgeVO.setAutoGrantCondition(updateBadgeDTO.getAutoGrantCondition());
        badgeDAO.updateBadge(badgeVO);
    }

    // 뱃지 삭제
    public void deleteBadge(Long badgeId) {
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
                // 예시: 투표를 3회 이상할시 뱃지 부여 (아직 기획 구체화 더 필요)
                case "VOTE_3_AND_POST_2" -> {
                    int voteCount = votesDAO.countVotesByUserId(userId);
                    yield voteCount >= 3;
                }
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
}
