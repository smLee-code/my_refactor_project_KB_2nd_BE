package org.funding.Image.vo;

import lombok.Data;
import org.funding.Image.vo.enumType.ImageType;

import java.time.LocalDateTime;

@Data
public class ImageVO {
    private Long imageId; // 이미지 id
    private ImageType imageType; // 이미지 타입
    private Long postId; // 해당 엔티티 id
    private String imageUrl; // 이미지 url
    private LocalDateTime createAt; // 생성 시간
}
