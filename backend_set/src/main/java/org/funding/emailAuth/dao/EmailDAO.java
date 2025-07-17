package org.funding.emailAuth.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.funding.emailAuth.vo.EmailAuthVO;

@Mapper
public interface EmailDAO {
    // 이메일 코드 삽입
    void insertAuthCode(@Param("email") String email, @Param("code") String code);
    // code로 이메일 판별
    EmailAuthVO findAuthCode(@Param("email") String email, @Param("code") String code);
    // code 만료 검증
    void expiredCode(@Param("email") String email, @Param("code") String code);
}
