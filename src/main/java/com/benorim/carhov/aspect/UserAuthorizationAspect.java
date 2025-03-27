package com.benorim.carhov.aspect;

import com.benorim.carhov.security.services.UserDetailsImpl;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

@Aspect
@Component
@Slf4j
public class UserAuthorizationAspect {

    @Around("@annotation(com.benorim.carhov.aspect.RequireUserOwnership)")
    public Object checkUserOwnership(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Object[] args = joinPoint.getArgs();

        // Get the current authentication
        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            log.error("No authentication found");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        // Get the principal
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        
        // Check if principal is UserDetailsImpl
        if (!(principal instanceof UserDetailsImpl)) {
            log.error("Principal is not a UserDetailsImpl: {}", principal.getClass().getName());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        UserDetailsImpl currentUser = (UserDetailsImpl) principal;
        
        // Find the userId parameter
        Long userId = null;
        Parameter[] parameters = method.getParameters();
        for (int i = 0; i < parameters.length; i++) {
            if (parameters[i].isAnnotationPresent(PathVariable.class) && 
                parameters[i].getAnnotation(PathVariable.class).value().equals("userId")) {
                userId = (Long) args[i];
                break;
            }
        }

        if (userId == null) {
            log.error("No userId parameter found in method: {}", method.getName());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        // Check if user is trying to modify their own profile
        if (!currentUser.getId().equals(userId)) {
            log.warn("User {} attempted to modify user {}'s profile", currentUser.getId(), userId);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return joinPoint.proceed();
    }
} 