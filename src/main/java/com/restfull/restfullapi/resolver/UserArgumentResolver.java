package com.restfull.restfullapi.resolver;

import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.server.ResponseStatusException;

import com.restfull.restfullapi.entities.User;
import com.restfull.restfullapi.repositories.UserRepository;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Digunakan untuk melakukan injection pada method yang parameter nya User
 */
@Component @AllArgsConstructor @Slf4j
public class UserArgumentResolver implements HandlerMethodArgumentResolver {

    private final UserRepository userRepository;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
       return User.class.equals(parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
         HttpServletRequest servletRequest = ((HttpServletRequest)webRequest.getNativeRequest());
         String token = servletRequest.getHeader("X-API-TOKEN");
         if(token == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unautorized");
        
         User user = this.userRepository.findFirstByToken(token).orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unautorized"));

         if(user.getTokenExpiredAt() < System.currentTimeMillis()) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unautorized");

        log.info("TOKEN : " + token);
        log.info("USERNAME : " + user.getUsername());
         return user;
    }
    
}
