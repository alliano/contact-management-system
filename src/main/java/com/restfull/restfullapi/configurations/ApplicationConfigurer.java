package com.restfull.restfullapi.configurations;

import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.restfull.restfullapi.resolver.UserArgumentResolver;

import lombok.AllArgsConstructor;

@Configuration @AllArgsConstructor
public class ApplicationConfigurer implements WebMvcConfigurer {

    private final UserArgumentResolver userArgumentResolver;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
       WebMvcConfigurer.super.addArgumentResolvers(resolvers);
        resolvers.add(this.userArgumentResolver);
    }
    
}
