package com.snap.wallet.demo.wallet_demo.repository;

import com.snap.wallet.demo.wallet_demo.model.TransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<TransactionEntity,Long> {
}
