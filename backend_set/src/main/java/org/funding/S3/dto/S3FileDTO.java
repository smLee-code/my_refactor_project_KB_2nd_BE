package org.funding.S3.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class S3FileDTO {
    private Long id;
    private String originalFileName;
    private String s3Url;
}
