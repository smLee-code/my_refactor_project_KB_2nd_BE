package org.funding.badge.vo;

import lombok.Data;
import org.funding.mapping.UserBadgeVO;

import java.util.List;

@Data
public class BadgeVO {
    private Long badgeId; // 뱃지 id
    private String name; // 뱃지 이름
    private String description; // 뱃지 설명
    private String autoGrantCondition; // 자동 부여 조건

    private List<UserBadgeVO> badgeUsers;  // 유저 뱃지 연관관계 매핑
}
