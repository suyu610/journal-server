package com.uuorb.journal.controller;

import cn.hutool.core.collection.CollectionUtil;
import com.uuorb.journal.annotation.Authorization;
import com.uuorb.journal.annotation.UserId;
import com.uuorb.journal.constant.ResultStatus;
import com.uuorb.journal.controller.vo.Result;
import com.uuorb.journal.model.Activity;
import com.uuorb.journal.model.ActivityUserRel;
import com.uuorb.journal.model.User;
import com.uuorb.journal.service.ActivityService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("activity")
public class ActivityController {

    @Resource
    ActivityService service;


    /**
     * 删除活动
     *
     * @param userId
     * @return
     */
    @Authorization
    @DeleteMapping("/{activityId}")
    Result deleteActivity(@UserId String userId, @PathVariable("activityId") String activityId) {

        Activity activityQuery = Activity.builder().activityId(activityId).userId(userId).build();
        boolean isOwnerActivity = service.isOwnerActivity(activityQuery);
        if (!isOwnerActivity) {
            return Result.error(ResultStatus.NOT_OWN_RESOURCE);
        }


        return Result.ok(service.deleteActivity(userId, activityId));
    }

    /**
     * 创建的活动
     */
    @Authorization
    @GetMapping("/list")
    Result getActivityList(@UserId String userId) {
        Activity query = Activity.builder().userId(userId).build();
        return Result.ok(service.querySelfActivityList(query));
    }


    @Authorization
    @GetMapping("/search/{activityId}")
    Result searchActivity(@PathVariable("activityId") String activityId) {
        Activity activity = service.searchActivity(activityId);
        if (activity == null) {
            return Result.error(ResultStatus.RESOURCE_NOT_FOUND);
        }
        activity.setExpenseList(null);

        return Result.ok(activity);

    }

    /**
     * 当前的活动
     *
     * @return
     */
    @Authorization
    @GetMapping("/current")
    Result getCurrentActivity(@UserId String userId) {
        Activity activity = service.getCurrentActivity(userId);
        return Result.ok(activity);
    }

    /**
     * 加入的活动
     */
    @Authorization
    @GetMapping("/list/joined")
    Result getJoinedActivityList(@UserId String userId) {
        Activity query = Activity.builder().userId(userId).build();
        return Result.ok(service.queryJoinedActivityList(query));
    }

    @Authorization
    @PostMapping
    Result insert(@RequestBody Activity activity, @UserId String userId) {
        activity.setUserId(userId);
        Activity resp = service.insert(activity);
        return Result.ok(resp);
    }


    @Authorization
    @PatchMapping
    Result update(@RequestBody Activity activity, @UserId String userId) {
        activity.setUserId(userId);
        if (activity.getActivityId() == null) {
            return Result.error(ResultStatus.PRIMARY_ID_MISS);
        }

        boolean isOwnerActivity = service.isOwnerActivity(activity);

        if (!isOwnerActivity) {
            return Result.error(ResultStatus.NOT_OWN_RESOURCE);
        }

        return Result.ok(service.update(activity));
    }

    @Authorization
    @PostMapping("/exit/{activityId}")
    Result exitActivity(@PathVariable("activityId") String activityId, @UserId String userId) {
        // 如果这个账本是自己的，则不能退
        Activity query = Activity.builder().activityId(activityId).userId(userId).build();

        boolean ownerActivity = service.isOwnerActivity(query);
        if (ownerActivity) {
            return Result.error(ResultStatus.OWNER_CANT_EXIT);
        }

        service.exitActivity(activityId, userId);
        return Result.ok();
    }

    @Authorization
    @PostMapping("/join/{activityId}")
    Result joinActivity(@PathVariable("activityId") String activityId, @UserId String userId) {
        log.info("==> 【加入活动】 用户: {}，活动id: {}", userId, activityId);
        //  =  validate =
        // 1. 找不找得到这个活动
        Activity activity = service.queryActivityByActivityId(activityId);
        if (activity == null) {
            return Result.error(ResultStatus.RESOURCE_NOT_FOUND);
        }
        // 2. 是否为本人的活动
        if (activity.getUserId().equalsIgnoreCase(userId)) {
            return Result.error(ResultStatus.FORBID_JOIN_SELF_ACTIVITY);
        }

        // 3. 是否已经加入了
        List<User> joinedUserList = activity.getUserList()
                .stream()
                .filter(user -> user.getUserId().equalsIgnoreCase(userId))
                .toList();

        if (CollectionUtil.isNotEmpty(joinedUserList)) {
            return Result.error(ResultStatus.JOIN_REPEAT);
        }

        ActivityUserRel joinQuery = ActivityUserRel.builder().userId(userId).activityId(activityId).build();

        service.joinActivity(joinQuery);

        return Result.ok();
    }

    @Authorization
    @GetMapping("/isOwner")
    Result isOwner(@RequestParam("activityId") String activityId, @UserId String userId) {
        Activity query = Activity.builder().activityId(activityId).userId(userId).build();
        return Result.ok(service.isOwnerActivity(query));
    }
}
