package org.funding.user.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "개인정보 수정 요청")
public class UpdateAccountRequestDTO { // 개인정보 수정
    @ApiModelProperty(value = "사용자 이름", example = "홍길동", required = true)
    private String username;
    
    @ApiModelProperty(value = "닉네임", example = "길동이", required = true)
    private String nickname;
    
    @ApiModelProperty(value = "전화번호", example = "010-1234-5678", required = true)
    private String phoneNumber;
} 