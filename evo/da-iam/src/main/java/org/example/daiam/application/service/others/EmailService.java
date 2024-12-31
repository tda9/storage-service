package org.example.daiam.application.service.others;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.ws.rs.InternalServerErrorException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.logging.Level;
import java.util.logging.Logger;


@RequiredArgsConstructor
@Service
public class EmailService {
    private final Logger logger = Logger.getLogger(EmailService.class.getName());
    @Value("${spring.mail.username}")
    private String from;
    @Value("${confirmation.registration.url}")
    private String verifyEmailUrl;

    @Value("${confirmation.registration.url}")
    private String forgotPasswordUrl;

    private final TemplateEngine templateEngine;
    private final JavaMailSender javaMailSender;
    @Value("${spring.mail.registrationTemplateName}")
    private String verifyEmailTemplateName;
    @Value("${spring.mail.passwordResetTemplateName}")
    private String passwordResetTemplateName;

    public void sendEmail(String to, String subject, String body) {
        try {
            SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
            simpleMailMessage.setFrom(from);
            simpleMailMessage.setTo(to);
            simpleMailMessage.setSubject(subject);
            simpleMailMessage.setText(body);
            javaMailSender.send(simpleMailMessage);
        } catch (Exception e) {
            //logger.log(Level.SEVERE, "Error while sending mail:"+ e.getMessage());
            throw new InternalServerErrorException(e.getMessage());
        }
    }

    @Async
    public void verifyEmail(String to, String token) {
        sendEmailWithHtmlTemplate(to, "Confirm Registration IAM Service", verifyEmailTemplateName, verifyEmailUrl + "?email=" + to + "&token=" + token);
    }

    public void sendForgotPasswordEmail(String to, String token) {
        sendEmailWithHtmlTemplate(to, "Confirm Registration IAM Service", verifyEmailTemplateName, verifyEmailUrl + "email=" + to + "&token=" + token);
    }

    public void sendEmailWithHtmlTemplate(String to, String subject, String templateName, String link) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "UTF-8");
        Context context = new Context();
        context.setVariable("link", link);
        try {
            helper.setTo(to);
            helper.setSubject(subject);
            String htmlContent = templateEngine.process(templateName, context);
            helper.setText(htmlContent, true);
            javaMailSender.send(mimeMessage);
        } catch (MessagingException e) {
            logger.log(Level.SEVERE, e.getMessage());
            throw new InternalServerErrorException(e.getMessage());
        }
    }

    public void sendResetPassword(String to, String subject, String templateName, String content) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "UTF-8");
        Context context = new Context();
        context.setVariable("content", content);
        try {
            helper.setTo(to);
            helper.setSubject(subject);
            String htmlContent = templateEngine.process(templateName, context);
            helper.setText(htmlContent, true);
            javaMailSender.send(mimeMessage);
        } catch (MessagingException e) {
            logger.log(Level.SEVERE, "Error while sending mail");
        }
    }
}
