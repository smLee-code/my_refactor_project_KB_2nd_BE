package org.funding.mapping;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserBadgeVO {
    private Long matchId;
    private Long userId; // 유저 id
    private Long badgeId; // 뱃지 id
    private LocalDateTime grantedAt; // 획득 날짜
}
