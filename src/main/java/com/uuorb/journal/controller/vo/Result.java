package com.uuorb.journal.controller.vo;

import cn.hutool.json.JSONObject;
import com.uuorb.journal.constant.ResultStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * @ClassName Result
 * @Description 统一返回值
 * @Author uuorb
 * @Date 2021/5/18 12:54 下午
 * @Version 0.1
 **/

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Result<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = -2289349812892813717L;

    int code;

    String msg;

    T data;

    public static <T> Result<T> ok(T data) {
        return new Result<T>(0, "success", data);
    }

    public static <T> Result ok() {
        return new Result(0, "success", null);
    }

    public static <T> Result error() {
        JSONObject resultDate = new JSONObject();
        resultDate.putOnce("message", "出现未知错误，请稍后再试");
        return new Result(-1, "fail", resultDate);
    }

    public static <T> Result<T> error(int errcode, String errmsg) {
        return new Result<T>(errcode, errmsg, null);
    }

    public static <T> Result<T> error(ResultStatus resultStatusEnum) {
        return new Result<T>(resultStatusEnum.getCode(), resultStatusEnum.getMsg(), null);
    }

    // 处理参数校验规则的错误
    public static <T> Result paramValidError(String errmsg) {
        return new Result(-400, errmsg, null);
    }

}
