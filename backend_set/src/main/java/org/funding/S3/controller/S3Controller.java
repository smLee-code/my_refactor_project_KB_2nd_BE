package org.funding.S3.controller;

import lombok.RequiredArgsConstructor;
import org.funding.S3.service.S3ImageService;
import org.funding.S3.vo.enumType.ImageType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class S3Controller {

    private final S3ImageService imageService;

    @PostMapping("/{postId}/images")
    @ResponseBody
    public ResponseEntity<String> uploadImage(
            @PathVariable Long postId,
            @RequestParam ImageType imageType,
            @RequestParam("files") List<MultipartFile> files) {
        try {
            imageService.uploadImagesForPost(imageType, postId, files);
            return ResponseEntity.ok("성공적으로 업로드 되었습니다.");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("업로드가 실패하였습니다.");
        }
    }
}
