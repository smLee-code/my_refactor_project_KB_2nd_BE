package org.funding.interestingKeyword.dao;

import org.apache.ibatis.annotations.Mapper;
import org.funding.interestingKeyword.vo.InterestingKeywordVO;

import java.util.List;

@Mapper
public interface InterestingKeywordDAO {
    List<InterestingKeywordVO> findByUserId(Long userId);
}