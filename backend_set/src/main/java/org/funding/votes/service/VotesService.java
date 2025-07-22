package org.funding.votes.service;

import lombok.RequiredArgsConstructor;
import org.funding.project.dto.response.ProjectResponseDTO;
import org.funding.votes.dao.VotesDAO;
import org.funding.votes.dto.VotesRequestDTO;
import org.funding.votes.dto.VotesResponseDTO;
import org.funding.votes.vo.VotesVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class VotesService {

    private final VotesDAO votesDAO;

    public VotesResponseDTO createVotes(VotesRequestDTO requestDTO) {
        VotesVO votesVO = requestDTO.toVO();
        votesDAO.insertVotes(votesVO);
        Long voteId = votesVO.getVoteId();

        VotesVO selectedVotesVO = votesDAO.selectVotesById(voteId);
        return VotesResponseDTO.fromVO(selectedVotesVO);
    }

    @Transactional
    public void deleteVotes(VotesRequestDTO requestDTO) {
        VotesVO votesVO = votesDAO.selectVotes(requestDTO);

        if(votesVO == null) {
            // 삭제할 대상이 없음
            return;
        }

        Long voteId = votesVO.getVoteId();
        votesDAO.deleteVotes(voteId);
    }
}
