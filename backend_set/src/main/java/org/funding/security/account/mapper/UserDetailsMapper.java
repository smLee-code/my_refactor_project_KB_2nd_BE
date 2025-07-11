package org.funding.security.account.mapper;

import org.funding.security.account.domain.MemberVO;

public interface UserDetailsMapper {

  public MemberVO get(String username);
}
