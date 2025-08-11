package org.funding.keyword.service;

import lombok.RequiredArgsConstructor;
import org.funding.global.error.ErrorCode;
import org.funding.global.error.exception.KeywordException;
import org.funding.keyword.dao.KeywordDAO;
import org.funding.keyword.dto.KeywordRequestDTO;
import org.funding.keyword.vo.KeywordVO;
import org.funding.user.dao.MemberDAO;
import org.funding.user.vo.MemberVO;
import org.funding.user.vo.enumType.Role;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class KeywordService {
    private final MemberDAO memberDAO;

    private final KeywordDAO keywordDAO;

    public KeywordVO findKeyword(String name) {
        return keywordDAO.selectKeywordByName(name);
    }

    public List<KeywordVO> findAllKeywords() {
        return keywordDAO.selectAllKeywords();
    }

    public List<KeywordVO> findAllKeywordsByCategoryName(String categoryName) {
        return keywordDAO.selectKeywordsByCategoryName(categoryName);
    }

    public List<KeywordVO> findAllKeywordsByCategoryId(Long categoryId) {
        return keywordDAO.selectKeywordsByCategoryId(categoryId);
    }

    public void addKeyword(KeywordRequestDTO requestDTO, Long userId) {
        MemberVO member = memberDAO.findById(userId);
        if (member == null) {
            throw new KeywordException(ErrorCode.MEMBER_NOT_FOUND);
        }
        if (member.getRole() != Role.ROLE_ADMIN) {
            throw new KeywordException(ErrorCode.MEMBER_NOT_ADMIN);
        }
        keywordDAO.insertKeyword(requestDTO);
    }

    public void deleteKeyword(String name, Long userId) {
        MemberVO member = memberDAO.findById(userId);
        if (member == null) {
            throw new KeywordException(ErrorCode.MEMBER_NOT_FOUND);
        }
        if (member.getRole() != Role.ROLE_ADMIN) {
            throw new KeywordException(ErrorCode.MEMBER_NOT_ADMIN);
        }
        keywordDAO.deleteKeyword(name);
    }
}
