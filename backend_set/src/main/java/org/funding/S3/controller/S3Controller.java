package org.funding.S3.controller;

import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.funding.S3.service.S3ImageService;
import org.funding.S3.vo.enumType.ImageType;
import org.funding.security.util.Auth;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/s3")
@RequiredArgsConstructor
public class S3Controller {

    private final S3ImageService imageService;

    @Auth
    @PostMapping(value = "/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadImage(
            @RequestParam("postId") Long postId,
            @RequestParam("imageType") ImageType imageType,
            @RequestParam("files") List<MultipartFile> files,
            HttpServletRequest request) {
        try {
            imageService.uploadImagesForPost(imageType, postId, files);
            return ResponseEntity.ok("성공적으로 업로드 되었습니다.");
        } catch (IOException e) {
            return ResponseEntity.status(404).body("업로드가 실패하였습니다: " + e.getMessage());
        }
    }


}
