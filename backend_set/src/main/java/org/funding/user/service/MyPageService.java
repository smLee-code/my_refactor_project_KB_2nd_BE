package org.funding.user.service;

import lombok.RequiredArgsConstructor;
import org.funding.fund.dao.FundDAO;
import org.funding.keyword.dao.KeywordDAO;
import org.funding.keyword.dto.KeywordResponseDTO;
import org.funding.keyword.vo.KeywordVO;
import org.funding.project.dao.ProjectDAO;
import org.funding.project.dto.response.ProjectListDTO;
import org.funding.project.vo.ProjectVO;
import org.funding.user.dao.MemberDAO;
import org.funding.user.dto.*;
import org.funding.user.vo.MemberVO;
import org.funding.userKeyword.dao.UserKeywordDAO;
import org.funding.userKeyword.dto.UserKeywordRequestDTO;
import org.funding.votes.dto.VotesResponseDTO;
import org.funding.votes.service.VotesService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MyPageService {

    private final MemberDAO memberDAO;
    private final ProjectDAO projectDAO;
    private final FundDAO fundDAO;
    private final KeywordDAO keywordDAO;
    private final UserKeywordDAO userKeywordDAO;
    private final VotesService votesService;

    // 4.1 마이페이지 조회
    public MyPageResponseDTO getMyPageInfo(Long userId) {

        MemberVO member = memberDAO.findById(userId);
        
        // 키워드 조회 - KeywordDAO 사용
        List<Long> keywordIds = userKeywordDAO.selectKeywordIdsByUserId(userId);
        List<String> keywordList = keywordIds.stream()
                .map(keywordDAO::selectKeywordById)
                .map(KeywordVO::getName)
                .collect(Collectors.toList());
        
        // 통계 정보 조회
        int totalProjects = projectDAO.countByUserId(userId);
        int totalVotes = votesService.findVotedProjects(userId).size();
        
        return MyPageResponseDTO.builder()
                .userId(member.getUserId())
                .username(member.getUsername())
                .email(member.getEmail())
                .nickname(member.getNickname())
                .phoneNumber(member.getPhoneNumber())
                .role(member.getRole())
                .createAt(member.getCreateAt())
                .totalVotes(totalVotes)
                .totalProjects(totalProjects)
                .keywords(keywordList)
                .build();
    }

    // 4.2.2 키워드 조회
    public List<KeywordResponseDTO> getMyKeywords(Long userId) {

        List<Long> keywordIds = userKeywordDAO.selectKeywordIdsByUserId(userId);

        return keywordIds.stream()
                .map(keywordDAO::selectKeywordById)
                .map(KeywordResponseDTO::fromVO)
                .collect(Collectors.toList());
    }

    // 4.2.3 키워드 수정
    @Transactional
    public void updateMyKeywords(List<String> newKeywords, Long userId) {
        
        // 기존 키워드 삭제
        userKeywordDAO.deleteKeywordsByUserId(userId);
        
        // 새로운 키워드 추가
        for (String keywordName : newKeywords) {
            KeywordVO keywordVO = keywordDAO.selectKeywordByName(keywordName);
            if (keywordVO != null) {
                userKeywordDAO.insertUserKeyword(new UserKeywordRequestDTO(userId, keywordVO.getKeywordId()));
            }
        }
    }

    // 4.3 개인정보 수정
    @Transactional
    public void updateAccountInfo(UpdateAccountRequestDTO request, Long userId) {
        MemberVO member = memberDAO.findById(userId);
        
        member.setUsername(request.getUsername());
        member.setNickname(request.getNickname());
        member.setPhoneNumber(request.getPhoneNumber());
        
        memberDAO.updateMember(member);
    }

    // 4.4 내 투표 조회
    public List<VotesResponseDTO> getMyVotes(Long userId) {
        List<Long> votedProjectIds = votesService.findVotedProjects(userId);
        
        // 투표한 프로젝트 ID 목록을 VotesResponseDTO로 변환
        return votedProjectIds.stream()
                .map(projectId -> {
                    VotesResponseDTO dto = new VotesResponseDTO();
                    dto.setUserId(userId);
                    dto.setProjectId(projectId);
                    // 투표 시간은 현재 시간으로 설정 (실제로는 DB에서 조회해야 함)
                    dto.setVoteTime(java.time.LocalDateTime.now());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    // 4.5.1 작성한 프로젝트 조회 - ProjectListDTO 사용
    public List<ProjectListDTO> getMyProjects(Long userId) {
        List<ProjectVO> projects = projectDAO.findByUserId(userId);
        
        return projects.stream()
                .map(ProjectListDTO::fromVO)
                .collect(Collectors.toList());
    }
} 