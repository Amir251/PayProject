package com.snap.wallet.demo.wallet_demo.model;


import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.*;
import org.apache.commons.codec.digest.DigestUtils;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "wallet")
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class Wallet extends Auditable {
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    @JsonProperty("user_id")
    private UserEntity user;
    private BigDecimal balance;
    private String status;
    private String accountNumber;

    public Wallet(UserEntity userEntity) {
        this.accountNumber = generateAccountNumber(userEntity);
        this.user = userEntity;
    }

    private String generateAccountNumber(UserEntity userEntity) {
        String input = userEntity.getId().toString() + System.currentTimeMillis();
        return DigestUtils.sha256Hex(input).substring(0, 12).toUpperCase();
    }
}
