package org.funding.user.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.funding.user.vo.enumType.Role;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "마이페이지 조회 응답")
public class MyPageResponseDTO { // 마이페이지 조회
    // 기본 정보 
    @ApiModelProperty(value = "사용자 ID", example = "1")
    private Long userId;
    
    @ApiModelProperty(value = "사용자 이름", example = "홍길동")
    private String username;
    
    @ApiModelProperty(value = "이메일", example = "user@example.com")
    private String email;
    
    @ApiModelProperty(value = "닉네임", example = "길동이")
    private String nickname;
    
    @ApiModelProperty(value = "전화번호", example = "010-1234-5678")
    private String phoneNumber;
    
    @ApiModelProperty(value = "사용자 권한", example = "ROLE_NORMAL")
    private Role role;
    
    @ApiModelProperty(value = "가입일시", example = "2024-01-01T00:00:00")
    private LocalDateTime createAt;
    
    // 요약 정보
    @ApiModelProperty(value = "총 투표 수", example = "5")
    private int totalVotes;
    
    @ApiModelProperty(value = "작성한 프로젝트 수", example = "3")
    private int totalProjects;
    
    @ApiModelProperty(value = "관심 키워드 목록", example = "[\"운동\", \"투자\", \"대출\"]")
    private List<String> keywords;
} 