package org.funding.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberSignupDTO {
    private String username;
    private String password;
    private String email;
    private String phoneNumber;
    private String nickname;
}
