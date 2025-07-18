package org.funding.financialProduct.dao;

import org.funding.financialProduct.vo.ChallengeVO;
import org.mapstruct.Mapper;

@Mapper
public interface ChallengeDAO {
    void insertChallenge(ChallengeVO vo);
}
