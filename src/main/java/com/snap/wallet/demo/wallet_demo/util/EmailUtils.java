package com.snap.wallet.demo.wallet_demo.util;

public class EmailUtils {
    public static String getEmailMessage(String name, String host, String key) {
        return "Hello " + name + ",\n\nYour new account has been created. Please click on the link to verify your account.\n\n" +
                getVerificationUrl(host, key) + "\n\nThe support Team";
    }

    private static String getVerificationUrl(String host, String key) {
        return host + "/users/verify/account?key=" + key;
    }
}
