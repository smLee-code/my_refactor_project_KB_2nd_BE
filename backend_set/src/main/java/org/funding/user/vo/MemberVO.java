package org.funding.user.vo;

import lombok.Data;
import lombok.Generated;
import org.funding.user.vo.enumType.Role;

import java.time.LocalDateTime;

@Data
public class MemberVO {

    private Long userId; // 유저 id
    private String password; // 유저 비번
    private String username; // 유저 이름
    private Role role;
    private String email;
    private String nickname;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;
}
