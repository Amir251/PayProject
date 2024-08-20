package com.snap.wallet.demo.wallet_demo.event;

import com.snap.wallet.demo.wallet_demo.enumeration.EventType;
import com.snap.wallet.demo.wallet_demo.model.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
public class UserEvent {
    private UserEntity user;
    private EventType type;
    private Map<?, ?> data;
    String transactionMessage;
}
