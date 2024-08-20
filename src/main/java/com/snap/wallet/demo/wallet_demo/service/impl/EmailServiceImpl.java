package com.snap.wallet.demo.wallet_demo.service.impl;

import com.snap.wallet.demo.wallet_demo.exception.ApiException;
import com.snap.wallet.demo.wallet_demo.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import static com.snap.wallet.demo.wallet_demo.util.EmailUtils.getEmailMessage;


@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {
    public static final String NEW_USER_ACCOUNT_VERIFICATION = "New User Account Verification";
    public static final String PURCHASE_REPORT = "Purchase Report";
    public static final String TRANSFER_REPORT = "Transfer Report";
    private final JavaMailSender sender;
    @Value("${spring.mail.verify.host}")
    private String host;
    @Value("${spring.mail.username}")
    private String fromEmail;

    @Override
    @Async
    public void sendNewAccountEmail(String name, String email, String token) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setSubject(NEW_USER_ACCOUNT_VERIFICATION);
            message.setFrom(fromEmail);
            message.setTo(email);
            message.setText(getEmailMessage(name, host, token));
            sender.send(message);
        } catch (Exception e) {
            log.error(e.getLocalizedMessage());
            throw new ApiException("Unable To Send Email");
        }
    }

    @Override
    public void sendReportPurchase(String email, String message) {
        try {
            SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
            simpleMailMessage.setSubject(PURCHASE_REPORT);
            simpleMailMessage.setFrom(fromEmail);
            simpleMailMessage.setTo(email);
            simpleMailMessage.setText(message);
            sender.send(simpleMailMessage);
        } catch (Exception e) {
            log.error(e.getLocalizedMessage());
            throw new ApiException("Unable To Send Email");
        }
    }

    @Override
    public void sendTransferReport(String email, String message) {
        try {
            SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
            simpleMailMessage.setSubject(TRANSFER_REPORT);
            simpleMailMessage.setFrom(fromEmail);
            simpleMailMessage.setTo(email);
            simpleMailMessage.setText(message);
            sender.send(simpleMailMessage);
        } catch (Exception e) {
            log.error(e.getLocalizedMessage());
            throw new ApiException("Unable To Send Email");
        }
    }
}
