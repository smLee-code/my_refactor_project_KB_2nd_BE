package org.funding.financialProduct.dao;

import org.funding.financialProduct.vo.ChallengeVO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ChallengeDAO {
    // 챌린지 삽입
    void insertChallenge(ChallengeVO vo);

    // 상품 id로 챌린지 조회
    ChallengeVO selectByProductId(Long productId);

    // 챌린지 업데이트
    void update(ChallengeVO vo);

    // 상품 id로 챌린지 삭제
    void deleteByProductId(Long productId);
}
