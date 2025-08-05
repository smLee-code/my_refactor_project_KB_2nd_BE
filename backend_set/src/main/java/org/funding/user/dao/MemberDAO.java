package org.funding.user.dao;

import org.apache.ibatis.annotations.Mapper;
import org.funding.user.vo.MemberVO;

@Mapper
public interface MemberDAO {
    // 멤버 회원가입
    void insertMember(MemberVO member);
    MemberVO findByEmail(String email);

    MemberVO findByUsername(String username);

    MemberVO findById(Long userId);
    
    // 마이페이지 관련 메서드들
    void updateMember(MemberVO member); // 유저 정보 수정
}
