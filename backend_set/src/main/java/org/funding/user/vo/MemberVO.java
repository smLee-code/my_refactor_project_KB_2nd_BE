package org.funding.user.vo;

import lombok.Data;
import lombok.Generated;
import org.funding.InterestingKeyword.vo.InterestingKeywordVO;
import org.funding.mapping.UserBadgeVO;
import org.funding.user.vo.enumType.Role;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class MemberVO {

    private Long userId; // 유저 id
    private String password; // 유저 비번
    private String username; // 유저 이름
    private Role role; // 접근 권한
    private String email; // 유저 이메일
    private String nickname; // 유저 닉네임
    private LocalDateTime createAt; // 유저 생성일
    private LocalDateTime updateAt;

    private List<UserBadgeVO> userBadges; // 유저 뱃지 연관관계
    private List<InterestingKeywordVO> keywords; // 유저가 가진 키워드 id들 (1대다)
}
