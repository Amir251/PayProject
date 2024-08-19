package com.snap.wallet.demo.wallet_demo.exception;

public class ApiException extends RuntimeException {
    public ApiException(String message){
        super(message);
    }

    public ApiException(){
        super("An Error Occurred");
    }
}
