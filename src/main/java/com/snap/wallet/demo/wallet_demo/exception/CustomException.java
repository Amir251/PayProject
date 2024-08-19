package com.snap.wallet.demo.wallet_demo.exception;

public class CustomException extends RuntimeException {
    private final String messageCode;
    private final Object[] params;

    public CustomException(String messageCode, Object... params) {
        super(messageCode);
        this.messageCode = messageCode;
        this.params = params;
    }

    public String getMessageCode() {
        return messageCode;
    }

    public Object[] getParams() {
        return params;
    }
}
