package org.funding.votes.service;

import lombok.RequiredArgsConstructor;
import org.funding.exception.DuplicateVoteException;
import org.funding.project.dto.response.ProjectResponseDTO;
import org.funding.votes.dao.VotesDAO;
import org.funding.votes.dto.VotesRequestDTO;
import org.funding.votes.dto.VotesResponseDTO;
import org.funding.votes.vo.VotesVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VotesService {

    private final VotesDAO votesDAO;

    @Transactional
    public VotesResponseDTO toggleVote(Long projectId, Long userId) {
        VotesRequestDTO requestDTO = new VotesRequestDTO();
        requestDTO.setProjectId(projectId);
        requestDTO.setUserId(userId);
        VotesVO existingVote = votesDAO.selectVotes(requestDTO);

        if (existingVote != null) {
            // 이미 투표한 상태 → 삭제
            votesDAO.deleteVotes(existingVote.getVoteId());
            return null; // 또는 삭제된 정보 리턴할 수도 있음
        }

        // 투표하지 않은 상태 → 추가
        VotesVO votesVO = requestDTO.toVO();
        votesDAO.insertVotes(votesVO);

        VotesVO selectedVotesVO = votesDAO.selectVotesById(votesVO.getVoteId());
        return VotesResponseDTO.fromVO(selectedVotesVO);
    }


    public VotesResponseDTO createVotes(VotesRequestDTO requestDTO) {


        //투표는 토글 기능이라서 중복 투표 개념이 없을거같음
//        if(votesDAO.selectVotes(requestDTO) != null) {
//            throw new DuplicateVoteException("이미 투표한 프로젝트입니다. (중복 투표 불가)");
//        }

        VotesVO votesVO = requestDTO.toVO();
        votesDAO.insertVotes(votesVO);
        Long voteId = votesVO.getVoteId();

        VotesVO selectedVotesVO = votesDAO.selectVotesById(voteId);
        return VotesResponseDTO.fromVO(selectedVotesVO);
    }

    @Transactional
    public void deleteVotes(Long projectId, Long userId) {
        VotesRequestDTO requestDTO = new VotesRequestDTO();
        requestDTO.setProjectId(projectId);
        requestDTO.setUserId(userId);
        VotesVO votesVO = votesDAO.selectVotes(requestDTO);

        if(votesVO == null) {
            // 삭제할 대상이 없음
            return;
        }

        Long voteId = votesVO.getVoteId();
        votesDAO.deleteVotes(voteId);
    }

    public List<Long> findVotedProjects(Long userId) {
        List<Long> votedProjects = votesDAO.selectVotedProjectsByUserId(userId);

        return votedProjects;
    }

    public Long countVotes(Long projectId) {

        Long voteCount = votesDAO.countVotes(projectId);

        return voteCount;
    }

    //어디에 쓰일지 잘 모르겠음
    public void deleteVotesDirect(Long userId, Long projectId) {
        votesDAO.deleteVotesByUserIdAndProjectId(userId, projectId);
    }


    public Boolean hasVoted(VotesRequestDTO requestDTO, Long userId) {
        VotesVO votesVO = votesDAO.selectVotes(requestDTO);

        if(votesVO == null) {
            return false;
        }
        else {
            return true;
        }
    }
}
