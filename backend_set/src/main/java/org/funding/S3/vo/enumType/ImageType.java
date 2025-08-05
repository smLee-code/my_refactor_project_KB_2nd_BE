package org.funding.S3.vo.enumType;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum ImageType {
    Funding, Project;

    public static ImageType fromString(String value) {
        for (ImageType type : ImageType.values()) {
            if (type.name().equals(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("지정되지 않은 이미지 타입:" + value);
    }
}
