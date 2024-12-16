//package org.example.daiam.aop;
//
//import jakarta.ws.rs.InternalServerErrorException;
//import org.aspectj.lang.ProceedingJoinPoint;
//import org.aspectj.lang.annotation.AfterThrowing;
//import org.aspectj.lang.annotation.Around;
//import org.aspectj.lang.annotation.Aspect;
//import org.aspectj.lang.annotation.Pointcut;
//import org.springframework.stereotype.Component;
//
//import java.io.IOException;
//import java.security.NoSuchAlgorithmException;
//import java.security.spec.InvalidKeySpecException;
//
//@Aspect
//@Component
//public class RSAKeyHandlerAspect {
//    @Pointcut("execution(* org.example.daiam.utils.RSAKeyUtil.jwkSet())")
//    public void rsaHandler() {}
//
//    @Around("rsaHandler()")
//    public Object handleKey(ProceedingJoinPoint joinPoint) throws Throwable {
//        try{
//            return joinPoint.proceed();
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }
//}
