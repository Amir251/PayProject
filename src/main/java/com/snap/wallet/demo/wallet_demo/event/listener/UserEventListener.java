package com.snap.wallet.demo.wallet_demo.event.listener;

import com.snap.wallet.demo.wallet_demo.enumeration.EventType;
import com.snap.wallet.demo.wallet_demo.event.UserEvent;
import com.snap.wallet.demo.wallet_demo.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserEventListener {
    private final EmailService emailService;

    @EventListener
    public void onUserEvent(UserEvent event) {
        if (event.getType() == EventType.REGISTRATION) {
            emailService.sendNewAccountEmail(event.getUser().getFirstName(), event.getUser().getEmail(), (String) event.getData().get("key"));
        }
    }
}
