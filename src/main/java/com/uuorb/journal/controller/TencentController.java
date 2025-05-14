package com.uuorb.journal.controller;

import com.uuorb.journal.annotation.Authorization;
import com.uuorb.journal.annotation.Log;
import com.uuorb.journal.controller.vo.CosCredential;
import com.uuorb.journal.controller.vo.Result;
import com.uuorb.journal.service.CosService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/tencent")
public class TencentController {


    @Resource
    private CosService cosService;
    @Log

    @Authorization
    @GetMapping("/cos/credential")
    public Result<CosCredential> getCredential() {
        CosCredential credential = cosService.getCredential();
        log.info("获取文件上传密钥:{}", credential.toString());
        return Result.ok(credential);
    }

}
