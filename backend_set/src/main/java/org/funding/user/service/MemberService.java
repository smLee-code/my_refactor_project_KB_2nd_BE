package org.funding.user.service;

import lombok.RequiredArgsConstructor;
import org.funding.badge.service.BadgeService;
import org.funding.global.error.ErrorCode;
import org.funding.global.error.exception.MemberException;
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
            throw new MemberException(ErrorCode.MEMBER_NOT_FOUND);
        }

        // 뱃지 검증
        badgeService.checkAndGrantBadges(member.getUserId());

        String token = jwtProcessor.generateTokenWithUserIdAndRole(
                member.getUsername(),
                member.getUserId(),
                String.valueOf(member.getRole())
        );
        
        return new MemberLoginResponseDTO(token, member.getUserId(), member.getRole());
    }

    public Boolean checkEmailDuplicate(String email) {
        MemberVO member = memberDAO.findByEmail(email);

        System.out.println("Service member: " + member);

        if (member == null) {
            return true;
        }

        return false;
    }

    public Boolean checkNicknameDuplicate(String nickname) {
        MemberVO member = memberDAO.findByNickname(nickname);

        if (member == null) {
            return true;
        }

        return false;
    }
}
