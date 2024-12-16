//package org.example.daiam.aop;
//
//import jakarta.ws.rs.InternalServerErrorException;
//import org.aspectj.lang.ProceedingJoinPoint;
//import org.aspectj.lang.annotation.Around;
//import org.aspectj.lang.annotation.Aspect;
//import org.aspectj.lang.annotation.Pointcut;
//import org.springframework.stereotype.Component;
//
//@Aspect
//@Component
//public class BaseKeycloakServiceAspect {
//    @Pointcut("execution(* org.example.daiam.service.BaseKeycloakService.*(..))")
//    public void authenticationHandler() {
//    }
//
//    @Around("authenticationHandler()")
//    public Object handleExceptions(ProceedingJoinPoint joinPoint) throws Throwable {
//        try {
//            return joinPoint.proceed();
//        } catch (Exception e) {
//            throw new InternalServerErrorException("Change keycloak password failed: " + e.getMessage());
//        }
//    }
//}
