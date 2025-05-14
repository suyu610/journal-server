package com.uuorb.journal.model.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
@Data
public class WechatLoginResp implements Serializable {

    @Serial
    private static final long serialVersionUID = 3799784124974560066L;

    private String openid;
    private String session_key;
    private String unionid;
    private String errcode;
    private String errmsg;
}
