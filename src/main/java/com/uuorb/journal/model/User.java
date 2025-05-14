package com.uuorb.journal.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {

    Integer id;

    String userId;

    String nickname;

    String avatarUrl;

    String openid;

    String unionId;
    String appleId;

    Boolean vip;

    String telephone;

    String currentActivityId;

    // 开场白
    String openingStatement;


    String aiAvatarUrl;

    // 称呼
    String salutation;

    // 关系
    String relationship;

    // 性格
    String personality;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    Date createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    Date updateTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    Date vipExpireTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    Date lastLoginTime;
}
