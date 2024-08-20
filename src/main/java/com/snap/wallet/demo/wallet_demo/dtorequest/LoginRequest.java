package com.snap.wallet.demo.wallet_demo.dtorequest;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "Login request data transfer object")
public class LoginRequest {
    @NotEmpty(message = "email can not be empty")
    @Email(message = "Email is invalid")
    private String email;
    @NotEmpty(message = "password can not be empty")
    private String password;
}
