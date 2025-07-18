package org.funding.badge.service;

import lombok.RequiredArgsConstructor;
import org.funding.badge.dao.BadgeDAO;
import org.funding.badge.dto.BadgeResponseDTO;
import org.funding.badge.dto.CreateBadgeDTO;
import org.funding.badge.dto.UpdateBadgeDTO;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BadgeService {

    private final BadgeDAO badgeDAO;

    // 뱃지 생성
    public void createBadge(CreateBadgeDTO createBadgeDTO) {
        badgeDAO.insertBadge(createBadgeDTO);
    }

    // 뱃지 수정
    public void updateBadge(UpdateBadgeDTO updateBadgeDTO) {
        badgeDAO.updateBadge(updateBadgeDTO);
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
