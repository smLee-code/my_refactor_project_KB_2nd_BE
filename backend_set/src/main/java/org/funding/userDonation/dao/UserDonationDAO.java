package org.funding.userDonation.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.funding.userDonation.dto.UserDonationDetailDTO;
import org.funding.userDonation.vo.UserDonationVO;

import java.util.List;

@Mapper
public interface UserDonationDAO {
    // 기부 내역 생성
    void insertUserDonation(UserDonationVO userDonationVO);

    // 단건 조회
    UserDonationVO findById(@Param("userDonationId") Long userDonationId);

    // 유저 id로 조회
    List<UserDonationVO> findByUserId(@Param("userId") Long userId);

    // 내역 업데이트
    void updateUserDonation(UserDonationVO userDonationVO);

    // 기부 내역 삭제
    void deleteUserDonation(@Param("userDonationId") Long userDonationId);

    // 유저 참여했는지 판별
    boolean existsByUserIdAndFundId(@Param("userId") Long userId, @Param("fundId") Long fundId);

    List<UserDonationDetailDTO> findAllDonationDetailsByUserId(@Param("userId") Long userId);
}
