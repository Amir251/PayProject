package com.snap.wallet.demo.wallet_demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private Long id;
    private Long createdBy;
    private Long updatedBy;
    private String userId;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String lastLogin;
    private String createdAt;
    private String updatedAt;
    private String role;
    private boolean accountNonExpire;
    private boolean accountNonLocked;
    private boolean enabled;
    private boolean credentialNonExpire;
}
