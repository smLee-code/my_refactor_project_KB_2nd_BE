package org.funding.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController // Spring MVC 컨트롤러로 등록
@Log4j2
public class HomeController {


    @GetMapping("/")
    public String home() {
        log.info("================> HomeController /");
        return "index"; // View의 이름 (ServletConfig의 ViewResolver에 의해 /WEB-INF/views/index.jsp로 변환)
    }
}
