//package org.example.daiam.aop;
//import org.aspectj.lang.annotation.*;
//import org.aspectj.lang.ProceedingJoinPoint;
//import org.springframework.retry.annotation.Backoff;
//import org.springframework.retry.annotation.Retryable;
//import org.springframework.stereotype.Component;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//@Aspect
//@Component
//public class DatabaseOperationRetryAspect {
//    private static final Logger logger = LoggerFactory.getLogger(RetryAndTransactionAspect.class);
//
//    // Define a pointcut for methods in specific packages or classes
//    @Pointcut("execution(* com.example.service..*(..))")
//    public void serviceMethods() {}
//
//    // Wrap service methods with retry and transaction logic
//    @Around("serviceMethods()")
//    public Object retryAndLog(ProceedingJoinPoint joinPoint) throws Throwable {
//        try {
//            // Retry logic
//            return retryOperation(joinPoint);
//        } catch (Exception e) {
//            // Log error and rethrow
//            logger.error("Error occurred in method: {}", joinPoint.getSignature(), e);
//            throw e;
//        }
//    }
//
//    // Retry wrapper
//    private Object retryOperation(ProceedingJoinPoint joinPoint) throws Throwable {
//        int maxAttempts = 3;
//        int attempt = 0;
//        long delay = 2000; // 2 seconds backoff
//
//        while (true) {
//            try {
//                attempt++;
//                logger.info("Attempt {} for method {}", attempt, joinPoint.getSignature());
//                return joinPoint.proceed();
//            } catch (Exception e) {
//                if (attempt >= maxAttempts) {
//                    logger.error("Max retry attempts reached for method: {}", joinPoint.getSignature());
//                    throw e;
//                }
//                logger.warn("Retrying method {} after error: {}", joinPoint.getSignature(), e.getMessage());
//                Thread.sleep(delay);
//            }
//        }
//    }
//}
