package org.funding.badge.service;

import lombok.RequiredArgsConstructor;
import org.funding.badge.dao.BadgeDAO;
import org.funding.badge.dto.BadgeResponseDTO;
import org.funding.badge.dto.CreateBadgeDTO;
import org.funding.badge.dto.UpdateBadgeDTO;
import org.funding.badge.vo.BadgeVO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BadgeService {

    private final BadgeDAO badgeDAO;

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
}
