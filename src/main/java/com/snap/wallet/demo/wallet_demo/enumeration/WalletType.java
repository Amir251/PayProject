package com.snap.wallet.demo.wallet_demo.enumeration;

public enum WalletType {
    ACTIVE("active"), SUSPEND("suspend");

    private final String value;

    WalletType(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }
}
