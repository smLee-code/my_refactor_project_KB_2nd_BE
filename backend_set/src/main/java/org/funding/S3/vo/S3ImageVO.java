package org.funding.S3.vo;

import lombok.Data;
import org.funding.S3.vo.enumType.ImageType;

import java.time.LocalDateTime;

@Data
public class S3ImageVO {
    private ImageType ImageType;
    private Long postId;
    private String imageUrl;
    private LocalDateTime createdAt;
}
