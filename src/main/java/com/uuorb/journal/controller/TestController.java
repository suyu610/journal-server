package com.uuorb.journal.controller;

import com.uuorb.journal.controller.vo.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/system")
public class TestController {
    @GetMapping("/version")
    Result getVersion() {
        return Result.ok("0.0.1");
    }
}
