package org.funding.S3.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import org.funding.S3.dao.S3ImageDAO;
import org.funding.S3.vo.S3ImageVO;
import org.funding.S3.vo.enumType.ImageType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3ImageService {

    private final AmazonS3 amazonS3;
    private final S3ImageDAO s3ImageDAO;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    // 이미지 등록
    public void uploadImagesForPost(ImageType imageType, Long postId, List<MultipartFile> files) throws IOException {
        for (MultipartFile file : files) {
            String folder = "";
            if (imageType == ImageType.Funding) {
                folder = "funding/";
            } else if (imageType == ImageType.Project) {
                folder = "project/";
            }

            String fileName = UUID.randomUUID() + "-" + file.getOriginalFilename();
            String key = folder + fileName;

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize());
            metadata.setContentType(file.getContentType());

            amazonS3.putObject(new PutObjectRequest(bucketName, key, file.getInputStream(), metadata));
                    // .withCannedAcl(CannedAccessControlList.PublicRead));

            String imageUrl = amazonS3.getUrl(bucketName, key).toString();

            S3ImageVO imageVO = new S3ImageVO(imageType, postId, imageUrl, LocalDateTime.now());
            s3ImageDAO.insertImage(imageVO);
        }
    }

    // 이미지 출력
    public List<S3ImageVO> getImagesForPost(ImageType imageType, Long postId) {
        return s3ImageDAO.findImagesByPost(imageType, postId);
    }

    // 썸네일 이미지 출력
    public S3ImageVO getFirstImageForPost(ImageType imageType, Long postId) {
        return s3ImageDAO.findFirstImageByPost(imageType, postId);
    }

    // 이미지 s3에서 삭제
    @Transactional
    public void deleteImagesForPost(ImageType imageType, Long postId) {
        List<S3ImageVO> imageList = s3ImageDAO.findImagesByPost(imageType, postId);

        for (S3ImageVO image : imageList) {
            String imageUrl = image.getImageUrl();
            String key = extractKeyFromUrl(imageUrl);

            amazonS3.deleteObject(bucketName, key);
        }

        // 데이터 베이스 에서 삭제
        s3ImageDAO.deleteImagesByPost(imageType, postId);
    }

    // s3 키 추출 메서드
    private String extractKeyFromUrl(String imageUrl) {
        return imageUrl.substring(imageUrl.indexOf(".com/") + 5);
    }


}
