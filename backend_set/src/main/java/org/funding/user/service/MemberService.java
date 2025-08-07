package org.funding.user.service;

import lombok.RequiredArgsConstructor;
import org.funding.badge.service.BadgeService;
import org.funding.security.util.JwtProcessor;
import org.funding.user.dao.MemberDAO;
import org.funding.user.dto.MemberSignupDTO;
import org.funding.user.dto.MemberLoginResponseDTO;
import org.funding.user.vo.MemberVO;
import org.funding.user.vo.enumType.Role;
import org.funding.userKeyword.dto.UserKeywordRequestDTO;
import org.funding.userKeyword.service.UserKeywordService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final UserKeywordService userKeywordService;

    private final MemberDAO memberDAO;
    private final PasswordEncoder passwordEncoder;
    private final JwtProcessor jwtProcessor;
    private final BadgeService badgeService;

    public void signup(MemberSignupDTO signupDTO) {

        // member 테이블
        MemberVO memberVO = new MemberVO();
        LocalDateTime now = LocalDateTime.now();

        memberVO.setPassword(passwordEncoder.encode(signupDTO.getPassword())); // 비밀번호 암호화
        memberVO.setEmail(signupDTO.getEmail());
        memberVO.setUsername(signupDTO.getUsername());
        memberVO.setNickname(signupDTO.getNickname());
        memberVO.setPhoneNumber(signupDTO.getPhoneNumber());

        memberVO.setRole(Role.valueOf("ROLE_NORMAL")); // 초기 회원가입은 노멀
        memberVO.setCreateAt(now);
        memberVO.setUpdateAt(now);
        memberDAO.insertMember(memberVO);

        Long userId = memberVO.getUserId(); // insert 이후 자동으로 채워짐

        // user_keyword 테이블
        signupDTO.getKeywordIds()
                .forEach(keywordId -> {
                    userKeywordService.mapUserKeyword(new UserKeywordRequestDTO(userId, keywordId));
                });
    }

    public MemberLoginResponseDTO login(String email, String password) {
        MemberVO member = memberDAO.findByEmail(email);
        if (member == null || !passwordEncoder.matches(password, member.getPassword())) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다, 회원이 없습니다");
        }

        // 뱃지 검증
        badgeService.checkAndGrantBadges(member.getUserId());

        String token = jwtProcessor.generateTokenWithUserIdAndRole(
                member.getUsername(),
                member.getUserId(),
                String.valueOf(member.getRole())
        );
        
        return new MemberLoginResponseDTO(token, member.getRole());
    }
}
