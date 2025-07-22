package org.funding.comment.dto;

import lombok.Data;
import org.funding.comment.vo.enumType.TargetType;

@Data
public class findAllCommentsRequestDTO {

    private TargetType targetType;
    private Long targetId;

}
