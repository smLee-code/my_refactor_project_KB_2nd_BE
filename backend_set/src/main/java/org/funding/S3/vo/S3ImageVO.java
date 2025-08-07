package org.funding.S3.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.funding.S3.vo.enumType.ImageType;
import java.time.LocalDateTime;

@Data
public class S3ImageVO {
    private ImageType imageType;
    private Long postId;
    private String imageUrl;
    private LocalDateTime createdAt;
}
