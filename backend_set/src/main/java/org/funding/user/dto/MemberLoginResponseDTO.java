package org.funding.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.funding.user.vo.enumType.Role;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberLoginResponseDTO {
    private String token;
    private Role userRole;
}