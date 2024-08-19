package com.snap.wallet.demo.wallet_demo.dtorequest;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class LoginRequest {
    @NotEmpty(message = "email can not be empty")
    @Email(message = "Email is invalid")
    private String email;
    @NotEmpty(message = "password can not be empty")
    private String password;
}
