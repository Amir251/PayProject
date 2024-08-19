package com.snap.wallet.demo.wallet_demo.repository;

import com.snap.wallet.demo.wallet_demo.model.PurchaseEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PurchaseRepository extends JpaRepository<PurchaseEntity,Long> {
}
