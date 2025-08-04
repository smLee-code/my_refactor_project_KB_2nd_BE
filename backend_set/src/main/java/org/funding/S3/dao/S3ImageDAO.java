package org.funding.S3.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.funding.S3.dto.S3FileDTO;
import org.funding.S3.vo.S3ImageVO;
import org.funding.S3.vo.enumType.ImageType;

import java.util.List;

@Mapper
public interface S3ImageDAO {
    // 이미지 저장
    void insertImage(S3ImageVO s3ImageVO);

    // 타입에 따른 이미지 리스트 추출
    List<S3ImageVO> findImagesByPost(@Param("imageType") ImageType imageType, @Param("postId") Long postId);

}
