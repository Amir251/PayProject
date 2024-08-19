package com.snap.wallet.demo.wallet_demo.dtorequest;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserRequest {
    @NotEmpty(message = "first name can not be empty")
    private String firstName;
    @NotEmpty(message = "last name can not be empty")
    private String lastName;
    @NotEmpty(message = "email can not be empty")
    @Email(message = "Email is invalid")
    private String email;
    @NotEmpty(message = "password can not be empty")
    private String password;
    private String address;
    private String phone;
}
