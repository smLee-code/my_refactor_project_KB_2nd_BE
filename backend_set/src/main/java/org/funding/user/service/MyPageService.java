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

    // 현재 로그인한 사용자의 ID를 가져옴
    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        // 임시 테스트용: 인증이 없어도 테스트 사용자 ID 반환
        if (authentication == null || authentication.getName() == null || "anonymousUser".equals(authentication.getName())) {
            System.out.println("DEBUG: No authentication found, using test user ID: 1");
            return 1L; // 테스트용 사용자 ID // 추후 삭제
        }
        
        String username = authentication.getName();
        System.out.println("DEBUG: Authentication username = " + username);
        
        // username으로 먼저 시도
        MemberVO member = memberDAO.findByUsername(username);
        
        // username으로 찾지 못하면 email로 시도 // 추후 삭제
        if (member == null) {
            member = memberDAO.findByEmail(username);
            System.out.println("DEBUG: Trying email lookup, found member = " + (member != null ? member.getUsername() : "null"));
        } else {
            System.out.println("DEBUG: Found member by username = " + member.getUsername());
        }
        
        if (member == null) {
            System.out.println("DEBUG: Member not found, using test user ID: 1");
            return 1L; // 테스트용 사용자 ID
        }
        
        return member.getUserId();
    }

    // 4.1 마이페이지 조회
    public MyPageResponseDTO getMyPageInfo() {
        Long userId = getCurrentUserId();
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
    public List<KeywordResponseDTO> getMyKeywords() {
        Long userId = getCurrentUserId();

        List<Long> keywordIds = userKeywordDAO.selectKeywordIdsByUserId(userId);

        return keywordIds.stream()
                .map(keywordDAO::selectKeywordById)
                .map(KeywordResponseDTO::fromVO)
                .collect(Collectors.toList());
    }

    // 4.2.3 키워드 수정
    @Transactional
    public void updateMyKeywords(List<String> newKeywords) {
        Long userId = getCurrentUserId();
        
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
    public void updateAccountInfo(UpdateAccountRequestDTO request) {
        Long userId = getCurrentUserId();
        MemberVO member = memberDAO.findById(userId);
        
        member.setUsername(request.getUsername());
        member.setNickname(request.getNickname());
        member.setPhoneNumber(request.getPhoneNumber());
        
        memberDAO.updateMember(member);
    }

    // 4.4 내 투표 조회
    public List<VotesResponseDTO> getMyVotes() {
        Long userId = getCurrentUserId();
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
    public List<ProjectListDTO> getMyProjects() {
        Long userId = getCurrentUserId();
        List<ProjectVO> projects = projectDAO.findByUserId(userId);
        
        return projects.stream()
                .map(ProjectListDTO::fromVO)
                .collect(Collectors.toList());
    }
} 