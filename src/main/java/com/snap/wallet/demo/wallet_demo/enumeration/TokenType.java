package com.snap.wallet.demo.wallet_demo.enumeration;

public enum TokenType {
    ACCESS("access-token");

    private final String value;

    TokenType(String value) {
        this.value = value;
    }

    public String getValue(){return this.value;}
}
