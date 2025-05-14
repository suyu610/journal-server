package com.uuorb.journal.controller;

import com.uuorb.journal.controller.vo.Result;
import com.uuorb.journal.mapper.SystemMapper;
import com.uuorb.journal.model.Config;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/system")
public class SystemController {
    @Resource
    SystemMapper systemMapper;

    @GetMapping("/config/all")
    Result getAllConfig(){
        List<Config> allSystemConfig = systemMapper.getAllSystemConfig();
        return Result.ok(allSystemConfig);
    }

}
