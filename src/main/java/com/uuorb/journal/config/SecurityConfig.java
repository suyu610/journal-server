package com.uuorb.journal.config;

import com.uuorb.journal.annotation.UserId;
import com.uuorb.journal.interceptor.AdminInterceptor;
import com.uuorb.journal.interceptor.AuthInterceptor;
import com.uuorb.journal.interceptor.UserIdResolver;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration

public class SecurityConfig implements WebMvcConfigurer {

    @Resource
    private AuthInterceptor authInterceptor;

    @Resource
    private AdminInterceptor adminInterceptor;

    @Resource
    private UserIdResolver userIdResolver;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor).addPathPatterns("/**").excludePathPatterns("/token").excludePathPatterns("/user/login");
        registry.addInterceptor(adminInterceptor).addPathPatterns("/**");
    }


    @Override
    public void addArgumentResolvers(final List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(userIdResolver);
    }
}
