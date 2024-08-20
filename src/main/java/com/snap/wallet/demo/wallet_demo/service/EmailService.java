package com.snap.wallet.demo.wallet_demo.service;

public interface EmailService {
    void sendNewAccountEmail(String name,String email,String token);
    void sendReportPurchase(String email,String message);
    void sendTransferReport(String email,String message);
}
