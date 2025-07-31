package org.funding.user.service;

import lombok.RequiredArgsConstructor;
import org.funding.InterestingKeyword.vo.InterestingKeywordVO;
import org.funding.fund.dao.FundDAO;
import org.funding.fund.vo.FundVO;
import org.funding.project.dao.ProjectDAO;
import org.funding.project.vo.ProjectVO;
import org.funding.user.dao.MemberDAO;
import org.funding.user.dto.*;
import org.funding.user.vo.MemberVO;
import org.funding.votes.dao.VotesDAO;
import org.funding.votes.vo.VotesVO;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MyPageService {

    private final MemberDAO memberDAO;
    private final VotesDAO votesDAO;
    private final ProjectDAO projectDAO;
    private final FundDAO fundDAO;

    // 현재 로그인한 사용자의 ID를 가져옴
    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        MemberVO member = memberDAO.findByEmail(username);
        return member.getUserId();
    }

    // 4.1 마이페이지 조회
    public MyPageResponseDTO getMyPageInfo() {
        Long userId = getCurrentUserId();
        MemberVO member = memberDAO.findById(userId);
        
        // 키워드 조회
        List<InterestingKeywordVO> keywords = memberDAO.findKeywordsByUserId(userId);
        List<String> keywordList = keywords.stream()
                .map(InterestingKeywordVO::getKeyword)
                .collect(Collectors.toList());
        
        // 통계 정보 조회
        int totalVotes = votesDAO.countVotesByUserId(userId);
        int totalProjects = projectDAO.countByUserId(userId);
        
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
    public List<KeywordResponseDTO> getMyKeywords() {
        Long userId = getCurrentUserId();
        List<InterestingKeywordVO> keywords = memberDAO.findKeywordsByUserId(userId);
        
        return keywords.stream()
                .map(keyword -> KeywordResponseDTO.builder()
                        .interestId(keyword.getInterestId())
                        .keyword(keyword.getKeyword())
                        .build())
                .collect(Collectors.toList());
    }

    // 4.2.3 키워드 수정
    @Transactional
    public void updateMyKeywords(List<String> newKeywords) {
        Long userId = getCurrentUserId();
        
        // 기존 키워드 삭제
        memberDAO.deleteKeywordsByUserId(userId);
        
        // 새로운 키워드 추가
        for (String keyword : newKeywords) {
            memberDAO.insertKeyword(userId, keyword);
        }
    }

    // 4.3 개인정보 수정
    @Transactional
    public void updateAccountInfo(UpdateAccountRequestDTO request) {
        Long userId = getCurrentUserId();
        MemberVO member = memberDAO.findById(userId);
        
        member.setUsername(request.getUsername());
        member.setNickname(request.getNickname());
        member.setPhoneNumber(request.getPhoneNumber());
        
        memberDAO.updateMember(member);
    }

    // 4.4 내 투표 조회
    public List<MyVoteResponseDTO> getMyVotes() {
        Long userId = getCurrentUserId();
        List<VotesVO> votes = votesDAO.findByUserId(userId);
        
        return votes.stream()
                .map(vote -> MyVoteResponseDTO.builder()
                        .voteId(vote.getVoteId())
                        .projectId(vote.getProjectId())
                        .projectTitle(getProjectTitle(vote.getProjectId()))
                        .voteTime(vote.getVoteTime())
                        .build())
                .collect(Collectors.toList());
    }

    // 4.5.1 작성한 프로젝트 조회
    public List<MyProjectResponseDTO> getMyProjects() {
        Long userId = getCurrentUserId();
        List<ProjectVO> projects = projectDAO.findByUserId(userId);
        
        return projects.stream()
                .map(project -> MyProjectResponseDTO.builder()
                        .projectId(project.getProjectId())
                        .title(project.getTitle())
                        .promotion(project.getPromotion())
                        .projectType(project.getProjectType())
                        .progress(project.getProgress())
                        .deadline(project.getDeadline())
                        .createAt(project.getCreateAt())
                        .build())
                .collect(Collectors.toList());
    }



    // 헬퍼 메서드: 프로젝트 제목 조회
    private String getProjectTitle(Long projectId) {
        ProjectVO project = projectDAO.findById(projectId);
        return project != null ? project.getTitle() : "삭제된 프로젝트";
    }
} 