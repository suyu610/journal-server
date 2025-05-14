package com.uuorb.journal.service;

import com.uuorb.journal.mapper.ActivityMapper;
import com.uuorb.journal.mapper.UserMapper;
import com.uuorb.journal.model.Activity;
import com.uuorb.journal.model.ActivityUserRel;
import com.uuorb.journal.model.User;
import com.uuorb.journal.util.IDUtil;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ActivityService {

    @Resource
    ActivityMapper mapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserService userService;

    public Activity insert(Activity activity) {
        String activityId = IDUtil.activityId();
        String userId = activity.getUserId();
        activity.setActivityId(activityId);

        // 判断默认
        if (activity.getActivated()) {
            mapper.updateAllInActive(activityId, userId);
            // 更新user中的activityId
            User user = User.builder().userId(userId).currentActivityId(activityId).build();
            userMapper.updateUser(user);
        }

        activity.setRemainingBudget(activity.getBudget());
        // 插入一条引用
        ActivityUserRel joinQuery = ActivityUserRel.builder().userId(userId).activityId(activityId).build();
        mapper.joinActivity(joinQuery);
        mapper.insert(activity);
        return activity;
    }

    public boolean isOwnerActivity(Activity activity) {
        return mapper.isOwner(activity);
    }

    public boolean isJoinedActivity(Activity activity) {
        return mapper.isJoinedOwner(activity);
    }

    public boolean hasQueryPermission(Activity activity) {
        return isOwnerActivity(activity) || isJoinedActivity(activity);
    }

    public List<Activity> querySelfActivityList(Activity activity) {
        List<Activity> activityList = mapper.querySelfActivityList(activity);

        for (Activity activityItem : activityList) {
            String activityId = activityItem.getActivityId();


        }

        return activityList;
    }

    public Activity queryActivityByActivityId(String activityId) {
        return mapper.queryActivityByActivityId(activityId);
    }

    public List<Activity> queryJoinedActivityList(Activity activity) {
        return mapper.queryJoinedActivityList(activity);
    }

    public Integer update(Activity activity) {
        String activityId = activity.getActivityId();
        String userId = activity.getUserId();
        // 如果设置为默认
        if (activity.getActivated()) {
            mapper.updateAllInActive(activityId, userId);
            // 更新user中的activityId
            User user = User.builder().userId(userId).currentActivityId(activityId).build();
            userMapper.updateUser(user);
        }

        // 如果取消设置默认，则需要看一下user表里的currentActivityId，是不是当前的
        if (!activity.getActivated()) {
            User user = userService.getUserByUserId(userId);
            if (activityId.equalsIgnoreCase(user.getCurrentActivityId())) {
                userMapper.setCurrentActivityNull(userId);
            }
        }
        Integer i = mapper.updateActivity(activity);
        // 需要重新算一遍activity的余额
        refreshActivityRemainingBudget(activityId);
        return i;
    }

    public void joinActivity(ActivityUserRel joinQuery) {
        mapper.joinActivity(joinQuery);
    }

    public void refreshActivityRemainingBudget(String activityId) {
        mapper.refreshActivityRemainingBudget(activityId);
    }

    public Activity getCurrentActivity(String userId) {
        return mapper.getCurrentActivity(userId);
    }

    /**
     * 保留expense，用于恢复
     * @param userId
     * @param activityId
     * @return
     */
    public boolean deleteActivity(String userId, String activityId) {
        // 删除所有引用
        mapper.deleteAllRef(activityId);
        // 删除账本
        mapper.deleteActivity(activityId);
        // 删除所有用户当前的账本
        mapper.deleteAllUserActivityRef(activityId);
        return true;
    }

    public Activity searchActivity(String activityId) {
        return mapper.queryActivityByActivityId(activityId);
    }

    public void exitActivity(String activityId, String userId) {
        mapper.exitActivity(activityId,userId);
    }
}
