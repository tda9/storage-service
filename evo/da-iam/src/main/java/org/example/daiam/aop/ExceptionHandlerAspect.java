package org.example.daiam.aop;

import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.InternalServerErrorException;
import jakarta.ws.rs.NotFoundException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ExceptionHandlerAspect {
    @Pointcut("execution(* org.example.daiam.service.impl.AuthenticationService.*(..)) || " +
            "execution(* org.example.daiam.service.impl.KeycloakAuthenticationService.*(..)) ||" +
            "execution(* org.example.daiam.service.impl.KeycloakUserService.*(..)) " )
    public void authenticationHandler() {}

    @Around("authenticationHandler()")
    public Object handlePublicMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            return joinPoint.proceed();
        }catch (BadRequestException
                | NotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
