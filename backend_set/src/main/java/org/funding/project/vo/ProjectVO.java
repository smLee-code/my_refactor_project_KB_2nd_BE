package org.funding.project.vo;

import lombok.*;
import org.funding.project.vo.enumType.ProjectProgress;
import org.funding.project.vo.enumType.ProjectType;

import java.time.LocalDateTime;

import lombok.Data;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectVO {

    // Project 공통 칼럼
    private Long projectId; // 프로젝트 id
    private String title; // 프로젝트 제목
    private String promotion; // 프로젝트 홍보글
    private ProjectType projectType; // 프로젝트 타입
    private ProjectProgress progress; // 프로젝트 진행도
    private LocalDateTime deadline; // 마감일
    private LocalDateTime createAt; // 생성일 (프로젝트는 수정 못함)

    // 연관 관계
    private Long userId; // 제안자 id

    private String nickname;
    private String email;

    private Long badgeId;
    private String badgeName;

    private Long voteCount;


}
