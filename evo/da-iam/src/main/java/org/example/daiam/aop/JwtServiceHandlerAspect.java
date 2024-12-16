//package org.example.daiam.aop;
//
//import org.aspectj.lang.ProceedingJoinPoint;
//import org.aspectj.lang.annotation.Around;
//import org.aspectj.lang.annotation.Aspect;
//import org.aspectj.lang.annotation.Pointcut;
//import org.springframework.stereotype.Component;
//
//@Aspect
//@Component
//public class JwtServiceHandlerAspect {
//    @Pointcut("execution(* org.example.daiam.service.JWTService.*(..))")
//    public void jwtHandler() {}
//
//    @Around("jwtHandler()")
//    public Object handleExceptions(ProceedingJoinPoint joinPoint) throws Throwable {
//        try {
//            return joinPoint.proceed();
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }
//}
