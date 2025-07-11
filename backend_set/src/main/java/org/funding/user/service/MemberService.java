package org.funding.user.service;

import lombok.RequiredArgsConstructor;
import org.funding.security.util.JwtProcessor;
import org.funding.user.dao.MemberDAO;
import org.funding.user.vo.MemberVO;
import org.funding.user.vo.enumType.Role;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberDAO memberDAO;
    private final PasswordEncoder passwordEncoder;
    private final JwtProcessor jwtProcessor;

    public void signup(MemberVO memberVO) {
        LocalDateTime now = LocalDateTime.now();

        memberVO.setPassword(passwordEncoder.encode(memberVO.getPassword())); // 비밀번호 암호화
        memberVO.setRole(Role.valueOf("ROLE_NORMAL"));
        memberVO.setCreateAt(now);
        memberVO.setUpdateAt(now);
        memberDAO.insertMember(memberVO);
    }

    public String login(String username, String password) {
        MemberVO member = memberDAO.findByUsername(username);
        if (member == null || !passwordEncoder.matches(password, member.getPassword())) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다, 회원이 없습니다"); // 임시 에러처리
        }

        return jwtProcessor.generateTokenWithRole(member.getUsername(), String.valueOf(member.getRole()));
    }
}
