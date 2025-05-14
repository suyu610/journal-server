package com.uuorb.journal.mapper;

import com.uuorb.journal.controller.vo.ChartsDataNode;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ChartsMapper {

    List<ChartsDataNode> queryWeekly(String activityId);
    List<ChartsDataNode> queryWeeklyIncome(String activityId);

    List<ChartsDataNode> queryGroupByType(String activityId);

}
