package org.funding.user.dao;

import org.apache.ibatis.annotations.Mapper;
import org.funding.user.vo.MemberVO;

@Mapper
public interface MemberDAO {
    // 멤버 회원가입
    void insertMember(MemberVO member);
    MemberVO findByEmail(String username);
}
