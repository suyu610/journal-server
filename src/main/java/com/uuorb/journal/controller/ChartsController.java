package com.uuorb.journal.controller;

import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageInfo;
import com.uuorb.journal.annotation.Authorization;
import com.uuorb.journal.annotation.Log;
import com.uuorb.journal.annotation.UserId;
import com.uuorb.journal.controller.vo.ChartsDataNode;
import com.uuorb.journal.controller.vo.Result;
import com.uuorb.journal.mapper.ChartsMapper;
import com.uuorb.journal.model.Expense;
import com.uuorb.journal.service.ExpenseService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/charts")
public class ChartsController {

    @Resource
    ChartsMapper chartsMapper;
    @Resource
    ExpenseService expenseService;

    public static List<String> daysOfWeek() {
        LocalDate today = LocalDate.now();
        DayOfWeek currentDay = today.getDayOfWeek();
        List<String> daysOfWeek = new ArrayList<>(Arrays.asList("周一", "周二", "周三", "周四", "周五", "周六", "周日"));

        // 找到今天所在的星期索引
        int todayIndex = currentDay.getValue() - 1; // DayOfWeek的索引从1开始，且周日为1

        // 生成从“周二”到“周日”，然后回到“周一”的数组
        List<String> nextDays = new ArrayList<>();
        for (int i = 0; i < daysOfWeek.size(); i++) {
            int dayIndex = (todayIndex + i + 1) % daysOfWeek.size(); // 加1是因为我们要从“明天”开始
            nextDays.add(daysOfWeek.get(dayIndex));
        }

        // 输出生成的数组
        return nextDays;
    }
    @Log

    @Authorization
    @GetMapping("/weekly/{activityId}")
    Result getWeeklyCharts(@PathVariable("activityId") String activityId) {
        getInfo(activityId);
        List<String> DAYS_OF_WEEK = daysOfWeek();

        List<ChartsDataNode> chartsDataNodes = chartsMapper.queryWeekly(activityId);

        if (chartsDataNodes.size() == 0) {
            return Result.ok();
        }

        // 检查并添加缺失的星期几
        for (String day : DAYS_OF_WEEK) {
            if (!chartsDataNodes.stream().anyMatch(e -> day.equals(e.getName()))) {
                ChartsDataNode newDay = new ChartsDataNode();
                newDay.setName(day);
                newDay.setValue(BigDecimal.ZERO);
                chartsDataNodes.add(newDay);
            }
        }

        // 确保结果按周一到周日的顺序排列
        chartsDataNodes.sort(Comparator.comparing(e -> DAYS_OF_WEEK.indexOf(e.getName())));
        return Result.ok(chartsDataNodes);
    }

    private static void getInfo(String activityId) {
        log.info("查看可视化：activity:{}", activityId);
    }
    @Log

    @Authorization
    @GetMapping("/weekly/income/{activityId}")
    Result getWeeklyChartsIncome(@PathVariable("activityId") String activityId) {
        getInfo(activityId);
        List<String> DAYS_OF_WEEK = daysOfWeek();

        List<ChartsDataNode> chartsDataNodes = chartsMapper.queryWeeklyIncome(activityId);
        if (chartsDataNodes.isEmpty()) {
            return Result.ok();
        }
        // 检查并添加缺失的星期几
        for (String day : DAYS_OF_WEEK) {
            if (!chartsDataNodes.stream().anyMatch(e -> day.equals(e.getName()))) {
                ChartsDataNode newDay = new ChartsDataNode();
                newDay.setName(day);
                newDay.setValue(BigDecimal.ZERO);
                chartsDataNodes.add(newDay);
            }
        }

        // 确保结果按周一到周日的顺序排列
        chartsDataNodes.sort(Comparator.comparing(e -> DAYS_OF_WEEK.indexOf(e.getName())));
        return Result.ok(chartsDataNodes);
    }

    @Log

    @Authorization
    @GetMapping("/weekly/type/{activityId}")
    Result getWeeklyChartsGroupByType(@PathVariable("activityId") String activityId) {
        getInfo(activityId);

        List<ChartsDataNode> chartsDataNodes = chartsMapper.queryGroupByType(activityId);

        return Result.ok(chartsDataNodes);
    }

    @Log

    @Authorization
    @GetMapping("/export/{activityId}")
    public void exportExcel(HttpServletResponse response, @PathVariable("activityId") String activityId) throws IOException {//, @UserId String userId) {
        List list = expenseService.queryListUnlimited(activityId);
        // 这里注意 有同学反应使用swagger 会导致各种问题，请直接用浏览器或者用postman
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        // 这里URLEncoder.encode可以防止中文乱码 当然和easyexcel没有关系
        String fileName = URLEncoder.encode("测试", StandardCharsets.UTF_8).replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
        EasyExcel.write(response.getOutputStream(), Expense.class).sheet("模板").doWrite(list);
    }

    @Log


    @GetMapping("/test")
    public String test() {
        List list = expenseService.queryListUnlimited("aca120b534f3b04eb8");
        return JSON.toJSONString(list);
    }


}
