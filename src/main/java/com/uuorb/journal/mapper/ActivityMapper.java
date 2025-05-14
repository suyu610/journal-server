package com.uuorb.journal.mapper;

import com.uuorb.journal.model.Activity;
import com.uuorb.journal.model.ActivityUserRel;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface ActivityMapper {
    List<Activity> querySelfActivityList(Activity activity);
    List<Activity> queryJoinedActivityList(Activity activity);

    void insert(Activity activity);

    void updateAllInActive(@Param("activityId") String id, @Param("userId") String userId);

    Integer relCount(ActivityUserRel query);

    Boolean isOwner(Activity query);

    Integer updateActivity(Activity activity);

    Activity queryActivityByActivityId(String activityId);

    void joinActivity(ActivityUserRel joinQuery);

    boolean isJoinedOwner(Activity activity);

    void refreshActivityRemainingBudget(String activityId);

    Activity getCurrentActivity(@Param("userId") String userId);

    @Delete("DELETE FROM activity_user_rel WHERE activity_id = #{activityId}")
    void deleteAllRef(String activityId);

    @Delete("DELETE FROM activity WHERE activity_id = #{activityId}")
    void deleteActivity(String activityId);

    @Update("UPDATE  users SET current_activity_id = null WHERE current_activity_id = #{activityId}")
    void deleteAllUserActivityRef(String activityId);


    @Delete("DELETE FROM activity_user_rel WHERE activity_id = #{activityId} AND user_id = #{userId}")
    void exitActivity(String activityId, String userId);
}
