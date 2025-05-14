package com.uuorb.journal.interceptor;

import com.alibaba.fastjson.JSON;
import com.uuorb.journal.annotation.Admin;
import com.uuorb.journal.controller.vo.Result;
import com.uuorb.journal.util.TokenUtil;
import io.netty.util.internal.StringUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

/**
 * 必须是管理员才能访问的接口
 */

@Service
public class AdminInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {
            HandlerMethod hm = (HandlerMethod) handler;
            Admin signature = hm.getMethodAnnotation(Admin.class);
            if (signature == null) {
                return true;
            }

            // 验证签名的方法
            String token = request.getHeader("Authorization");

            // token存在且 role=admin
            if(!StringUtil.isNullOrEmpty(token) && TokenUtil.validateAdmin(token)){
                request.setAttribute("openid",TokenUtil.getClaimsFromToken(token).get("openid"));
                return true;
            }

            // token无效
            Result result = new Result(-1,"此接口为管理员接口",null);
            String strResponseJson = JSON.toJSONString(result);
            response.setContentType("application/json;charset=UTF-8");
            try (OutputStream out = response.getOutputStream()) {
                out.write(strResponseJson.getBytes(StandardCharsets.UTF_8));
                out.flush();
            }
            return false;
        }

        return true;
    }
}

