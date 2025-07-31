package org.funding.user.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.funding.user.vo.MemberVO;
import org.funding.InterestingKeyword.vo.InterestingKeywordVO;

import java.util.List;

@Mapper
public interface MemberDAO {
    // 멤버 회원가입
    void insertMember(MemberVO member);
    MemberVO findByEmail(String username);

    MemberVO findById(Long userId);
    
    // 마이페이지 관련 메서드들
    void updateMember(MemberVO member); // 유저 정보 수정

    // 키워드 관련 메서드들
    List<InterestingKeywordVO> findKeywordsByUserId(Long userId); // 키워드 조회
    void deleteKeywordsByUserId(Long userId); // 키워드 삭제
    void insertKeyword(@Param("userId") Long userId, @Param("keyword") String keyword); // 키워드 추가
}
