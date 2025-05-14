package com.uuorb.journal.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
public class LogBean {

    Integer id;

    String userID;

    Date createTime;

    Long duration;

    String httpMethod;

    String params;

    String functionName;

    String url;

    String ip;

}
