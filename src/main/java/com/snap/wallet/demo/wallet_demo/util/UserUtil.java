package com.snap.wallet.demo.wallet_demo.util;


import com.snap.wallet.demo.wallet_demo.domain.RequestContext;
import com.snap.wallet.demo.wallet_demo.dto.User;
import com.snap.wallet.demo.wallet_demo.dtorequest.UserRequest;
import com.snap.wallet.demo.wallet_demo.model.CredentialEntity;
import com.snap.wallet.demo.wallet_demo.model.RoleEntity;
import com.snap.wallet.demo.wallet_demo.model.UserEntity;
import org.springframework.beans.BeanUtils;

import java.util.UUID;

import static java.time.LocalDateTime.now;
import static org.apache.logging.log4j.util.Strings.EMPTY;

public class UserUtil {
    public static UserEntity createUserEntity(UserRequest userRequest, RoleEntity roleByName) {
        RequestContext.start();
        RequestContext.setUserId(0L);
        return UserEntity.builder()
                .userId(UUID.randomUUID().toString())
                .firstName(userRequest.getFirstName())
                .lastName(userRequest.getLastName())
                .email(userRequest.getEmail())
                .accountNonExpire(true)
                .accountNonLocked(true)
                .enabled(false)
                .phone(EMPTY)
                .role(roleByName)
                .phone(userRequest.getPhone())
                .address(userRequest.getAddress())
                .build();
    }

    public static User fromUserEntity(UserEntity userEntity, RoleEntity role, CredentialEntity credentialEntity) {
        User user = new User();
        BeanUtils.copyProperties(userEntity, user);
        user.setCredentialNonExpire(isCredentialNonExpire(credentialEntity));
        user.setCreatedAt(userEntity.getCreatedAt().toString());
        user.setUpdatedAt(userEntity.getUpdatedAt().toString());
        user.setRole(role.getName());
        user.setEnabled(userEntity.isEnabled());
        user.setAccountNonLocked(userEntity.isAccountNonLocked());
        user.setAccountNonExpire(userEntity.isAccountNonExpire());
        return user;
    }

    public static boolean isCredentialNonExpire(CredentialEntity credentialEntity) {
        return credentialEntity.getUpdatedAt().plusDays(90).isAfter(now());
    }
}
