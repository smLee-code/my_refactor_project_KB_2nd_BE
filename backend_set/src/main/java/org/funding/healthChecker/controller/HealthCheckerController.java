package org.funding.healthChecker.controller;

import io.swagger.annotations.Api;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "헬스체커 API")
@RestController
public class HealthCheckerController {

    // 서버 상태검사용 헬스체커 api
    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("healthy");
    }
}
