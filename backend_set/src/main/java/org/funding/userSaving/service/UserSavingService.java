package org.funding.userSaving.service;

import lombok.RequiredArgsConstructor;
import org.funding.user.dao.MemberDAO;
import org.funding.user.vo.MemberVO;
import org.funding.userSaving.dao.UserSavingDAO;
import org.funding.userSaving.dto.UserSavingRequestDTO;
import org.funding.userSaving.vo.UserSavingVO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserSavingService {

    private final MemberDAO memberDAO;
    private final UserSavingDAO userSavingDAO;

    // 저축 가입
    public String applySaving(UserSavingRequestDTO userSavingRequestDTO) {
        MemberVO member = memberDAO.findById(userSavingRequestDTO.getUserId());
        if (member == null) {
            throw new RuntimeException("해당 멤버는 존재하지 않습니다.");
        }


        UserSavingVO userSaving = new UserSavingVO();
        userSaving.setUserId(userSaving.getUserId());
        userSaving.setFundId(userSaving.getFundId());
        userSaving.setSavingAmount(userSaving.getSavingAmount());


        userSavingDAO.insertUserSaving(userSaving);
        return "정상적으로 저축에 가입하셨습니다.";
    }

    // 저축 상품 해지
    public String cancelSaving(Long userSavingId) {
        UserSavingVO userSaving = userSavingDAO.findById(userSavingId);
        if (userSaving == null) {
            throw new RuntimeException("해당 저축에 신청되어있지 않습니다.");
        }

        userSavingDAO.deleteUserSaving(userSavingId);
        return "정상적으로 해지되었습니다.";
    }

    // id로 단건 조회
    public UserSavingVO findById(Long userSavingId) {
        UserSavingVO userSaving = userSavingDAO.findById(userSavingId);
        if (userSaving == null) {
            throw new RuntimeException("해당 저축 상품이 존재하지 않습니다.");
        }
        return userSaving;
    }

    // 유저가 가입한 저축 상품 전체 보기
    public List<UserSavingVO> getAllUserSaving(Long userId) {
        return userSavingDAO.findByUserId(userId);
    }
}
