package com.nowcoder.configuration;

import com.nowcoder.interceptor.LoginRequiredInterceptor;
import com.nowcoder.interceptor.PassportInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Component
public class WenDaWebConfiguration extends WebMvcConfigurerAdapter {
    @Autowired
    PassportInterceptor passportInterceptor;

    @Autowired
    LoginRequiredInterceptor loginRequiredInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // this is to add an interceptor when initialize the whole program
        // so it is now that register the interceptor on the linkage
        registry.addInterceptor(passportInterceptor);
        // this interceptor need to be add after passportInterceptor as the hostholder is set in the first interceptro
        // .addPathPatterns( the url of pages need to register the interceptor)
        registry.addInterceptor(loginRequiredInterceptor).addPathPatterns("/user/*");
        super.addInterceptors(registry);
    }
}