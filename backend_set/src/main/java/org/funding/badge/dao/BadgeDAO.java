package org.funding.badge.dao;

import org.apache.ibatis.annotations.Mapper;
import org.funding.badge.dto.BadgeResponseDTO;
import org.funding.badge.dto.CreateBadgeDTO;
import org.funding.badge.dto.UpdateBadgeDTO;

import java.util.List;

@Mapper
public interface BadgeDAO {

    // 뱃지 생성
    void insertBadge(CreateBadgeDTO createBadgeDTO);

    // 뱃지 업데이트
    void updateBadge(UpdateBadgeDTO updateBadgeDTO);

    // 뱃지 삭제
    void deleteBadge(Long id);

    // 뱃지 단건 조회
    BadgeResponseDTO selectBadgeById(Long badgeId);

    // 뱃지 전체 조회
    List<BadgeResponseDTO> selectAllBadges();
}
