package com.uuorb.journal.interceptor;

import com.uuorb.journal.annotation.UserId;
import com.uuorb.journal.util.TokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
@Slf4j
public class UserIdResolver implements HandlerMethodArgumentResolver {
    public UserIdResolver() {
    }

    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(UserId.class);
    }

    public String resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        if (parameter.getParameterAnnotation(UserId.class) != null) {
            String token = webRequest.getHeader("Authorization");
            return TokenUtil.getUserId(token);
        }
        return null;
    }


}
