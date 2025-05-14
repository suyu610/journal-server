package com.uuorb.journal.mapper;

import com.uuorb.journal.model.LogBean;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface LogMapper {
    Integer insertLog(LogBean log);
}
