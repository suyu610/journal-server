package com.uuorb.journal.mapper;

import com.uuorb.journal.model.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface UserMapper {

    @Select("SELECT * FROM users where user_id = #{userId}")
    User getUserByUserId(String userId);

    /**
     * 慎用！！！ 没有判空，可能会全表查
     *
     * @param telephone
     * @return
     */
    List<User> selectUser(User query);

    void createUser(User user);

    void updateUser(User user);
    @Update("UPDATE users SET current_activity_id = null WHERE user_id = #{userId}")
    void setCurrentActivityNull(String userId);
}
