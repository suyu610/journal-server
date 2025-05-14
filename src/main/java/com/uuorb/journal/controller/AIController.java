package com.uuorb.journal.controller;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.thread.ThreadUtil;
import com.huaban.analysis.jieba.JiebaSegmenter;
import com.huaban.analysis.jieba.SegToken;
import com.uuorb.journal.annotation.Authorization;
import com.uuorb.journal.annotation.Log;
import com.uuorb.journal.annotation.UserId;
import com.uuorb.journal.controller.vo.Result;
import com.uuorb.journal.mapper.ExpenseMapper;
import com.uuorb.journal.model.AIConfig;
import com.uuorb.journal.model.EngelExpense;
import com.uuorb.journal.model.Expense;
import com.uuorb.journal.model.User;
import com.uuorb.journal.service.AiService;
import com.uuorb.journal.service.ExpenseService;
import com.uuorb.journal.service.UserService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.util.List;

import static com.uuorb.journal.constant.ResultStatus.AI_FORMAT_ERROR;

@Slf4j
@RestController
@RequestMapping("/ai")
public class AIController {

    @Resource(name = "kimi")
    AiService kimiService;

    @Resource(name = "coze")
    AiService cozeService;

    @Resource
    ExpenseService expenseService;

    @Resource
    UserService userService;

    @Resource
    ExpenseMapper expenseMapper;

    /**
     * @param sentence
     * @param activityId
     * @param userId
     * @return
     */
    @Log
    @Authorization
    @GetMapping("/format")
    Result<Expense> format(@RequestParam String sentence, @RequestParam String activityId, @UserId String userId) {
        log.info("==> 结构化:{} ", sentence);

        // todo: 校验用户和活动是否归属
        Expense expense = kimiService.formatExpense(sentence);

        if (expense == null) {
            log.error("结构化失败：{}", sentence);
            return Result.error(AI_FORMAT_ERROR);
        }

        // 填充前端所需的参数
        expense.setActivityId(activityId);
        expense.setUserId(userId);
        expense.setCreateTime(new DateTime());
        expense.setUpdateTime(new DateTime());

        // 插入DB
        ThreadUtil.execAsync(() -> {
            expenseService.insertExpenseAndCalcRemainingBudget(expense);
        });

        return Result.ok(expense);
    }

    @Log
    @GetMapping("/praise")
    Result<String> praise(@RequestParam String sentence) {
        // 找到自己的AI关系
        String response = kimiService.praise(sentence);
        return Result.ok(response);
    }

    @Log
    @Authorization
    @GetMapping("/praise/advance")
    Result<String> praiseAdvance(@RequestParam String sentence, @RequestParam String activityId,
        @UserId String userId) {
        // 找到自己的AI关系
        User userProfile = userService.getUserByUserId(userId);
        AIConfig aiConfig = AIConfig.of(userProfile);
        // todo: 关系，称呼，性格
        String response = kimiService.praise(sentence, aiConfig);
        return Result.ok(response);
    }

    /**
     * 流式返回
     *
     * @param sentence
     * @param userId
     * @return
     */
    @Log
    @Authorization
    @GetMapping("/praise/stream")
    ResponseEntity<StreamingResponseBody> praiseStream(@RequestParam String sentence, @UserId String userId) {
        // 找到自己的AI关系
        User userProfile = userService.getUserByUserId(userId);
        AIConfig aiConfig = AIConfig.of(userProfile);
        return ResponseEntity.ok()
            .header("content-type", MediaType.TEXT_EVENT_STREAM_VALUE + ";charset=UTF-8")
            .body(outputStream -> {
                try {
                    kimiService.praiseStream(sentence, aiConfig, outputStream);
                } catch (Exception e) {
                    // 处理异常
                    log.error("夸夸失败:{},{}", sentence, e.getMessage());
                }
            });
    }

    @Log
    @GetMapping("/engel/{activityId}")
    ResponseEntity<StreamingResponseBody> engel(@PathVariable String activityId) {
        List<EngelExpense> expenseList = expenseMapper.queryListBrief(activityId);
        return ResponseEntity.ok()
            .header("content-type", MediaType.TEXT_EVENT_STREAM_VALUE + ";charset=UTF-8")
            .body(outputStream -> {
                try {
                    kimiService.engelExplain(expenseList, outputStream);
                } catch (Exception e) {
                    // todo: 处理异常
                    log.error(e.getMessage());
                }
            });
    }

    @Log
    @GetMapping("/tts")
    Result<String> tts(@RequestParam String sentence, @RequestParam String activityId, @UserId String userId) {
        // 找到自己的AI关系
        User userProfile = userService.getUserByUserId(userId);
        AIConfig aiConfig = AIConfig.of(userProfile);

        return Result.ok(cozeService.tts(sentence, aiConfig));
    }

    @Log
    @Authorization
    @GetMapping("/image")
    Result<String> generateImage(@RequestParam String model, @RequestParam String description,
        @RequestParam String role) {
        String result = cozeService.generateImage(model, description, role);
        if (result.startsWith("http")) {
            return Result.ok(result);
        } else {
            return Result.error(805, result);
        }
    }

    @Log
    @Authorization
    @GetMapping("/greeting")
    Result<String> praiseAdvance(@UserId String userId) {
        // 找到自己的AI关系
        User userProfile = userService.getUserByUserId(userId);
        AIConfig aiConfig = AIConfig.of(userProfile);

        // todo: 关系，称呼，性格
        String response = kimiService.greet(aiConfig);
        return Result.ok(response);
    }

    @GetMapping("/format/local")
    Result<List<SegToken>> formatLocal(@RequestParam("sentence") String sentence) {
        List<SegToken> process = new JiebaSegmenter().process(sentence, JiebaSegmenter.SegMode.INDEX);
        return Result.ok(process);
    }
}
