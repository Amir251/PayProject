package com.snap.wallet.demo.wallet_demo.repository;

import com.snap.wallet.demo.wallet_demo.model.TransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TransactionRepository extends JpaRepository<TransactionEntity,Long> {
    @Query("SELECT t FROM TransactionEntity t WHERE t.sourceWallet.user.id = :userId OR t.destinationWallet.user.id = :userId")
    List<TransactionEntity> findTransactionsByUserId(@Param("userId") Long userId);
}
