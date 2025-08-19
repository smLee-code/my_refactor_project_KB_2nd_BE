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

@Api(tags = "S3 이미지 업로드 API")
@RestController
@RequestMapping("/api/s3")
@RequiredArgsConstructor
public class S3Controller {

    private final S3ImageService imageService;

    @ApiOperation(value = "이미지 업로드", notes = "게시물(펀딩, 프로젝트 등)에 관련된 이미지 파일들을 S3에 업로드합니다.")
    @Auth
    @PostMapping(value = "/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadImage(
            @ApiParam(value = "이미지가 연결될 게시물 ID", required = true, example = "1") @RequestParam("postId") Long postId,
            @ApiParam(value = "이미지 타입 (e.g., Funding, Project)", required = true, example = "Funding") @RequestParam("imageType") ImageType imageType,
            @ApiParam(value = "업로드할 이미지 파일 목록", required = true) @RequestParam("files") List<MultipartFile> files,
            HttpServletRequest request) {
        try {
            imageService.uploadImagesForPost(imageType, postId, files);
            return ResponseEntity.ok("성공적으로 업로드 되었습니다.");
        } catch (IOException e) {
            return ResponseEntity.status(404).body("업로드가 실패하였습니다: " + e.getMessage());
        }
    }
}