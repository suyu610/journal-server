package com.uuorb.journal.model;

import cn.hutool.core.bean.BeanUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AIConfig {

    String openingStatement;

    // 称呼
    String salutation;

    // 关系
    String relationship;

    // 性格
    String personality;

    String lastLoginTime;

    public static AIConfig of(User user) {
        return BeanUtil.copyProperties(user, AIConfig.class);
    }

}
