package org.funding.votes.service;

import lombok.RequiredArgsConstructor;
import org.funding.badge.service.BadgeService;
import org.funding.votes.dao.VotesDAO;
import org.funding.votes.vo.VotesVO;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VotesService {
    private final VotesDAO votesDAO;
    private final BadgeService badgeService;

    // 투표시 뱃지 조건 검사하는 기능
    public void voteOnPost(VotesVO vo) {
        // 투표시 투표 저장 기능
        votesDAO.insertVote(vo);

        badgeService.checkAndGrantBadges(vo.getUserId());
    }
}
