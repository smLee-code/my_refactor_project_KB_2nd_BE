package org.funding.userSaving.dao;

import org.apache.ibatis.annotations.Mapper;
import org.funding.userSaving.vo.UserSavingVO;

import java.util.List;

@Mapper
public interface UserSavingDAO {
    // 저축 가입
    void insertUserSaving(UserSavingVO userSavingVO);

    // id로 단건 조회
    UserSavingVO findById(Long userSavingId);

    // 유저 id로 전체 조회
    List<UserSavingVO> findByUserId(Long userId);

    // 저축 정보 수정(관리자용)
    void updateUserSaving(UserSavingVO userSavingVO);

    // 저축 가입 삭제
    void deleteUserSaving(Long userSavingId);
}
