package com.snap.wallet.demo.wallet_demo.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionDto (String status, LocalDateTime dateTime, String type, String accountNo, BigDecimal amount) {
}
