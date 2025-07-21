package org.funding.project.dto;

import lombok.Data;
import org.funding.fund.vo.enumType.FundType;
import org.funding.project.vo.ProjectVO;
import org.funding.project.vo.enumType.ProjectProgress;
import org.funding.project.vo.enumType.ProjectType;
//import org.funding.project.vo.enumType.ProjProgress;

import java.time.LocalDateTime;

@Data
public class ProjectDTO {

//    // Project 공통 칼럼
//    private Long projectId; // 프로젝트 id
//    private Long userId; // 제안자 id
//    private ProjectType projectType; // 프로젝트 타입
//    private String title; // 프로젝트 제목
//    private String promotion; // 프로젝트 홍보글
//    private ProjectProgress progress; // 프로젝트 진행도
//    private LocalDateTime deadline; // 마감일
//    private LocalDateTime createAt; // 생성일 (프로젝트는 수정 못함)

    private ProjectVO basicInfo;
    private Object detailInfo;

}