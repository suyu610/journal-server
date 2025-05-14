package com.uuorb.journal.mapper;

import com.uuorb.journal.model.Config;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SystemMapper {
    @Select("SELECT * FROM system_config WHERE  user_id = 'system'")
    List<Config> getAllSystemConfig();
}
