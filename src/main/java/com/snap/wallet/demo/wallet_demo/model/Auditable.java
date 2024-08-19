package com.snap.wallet.demo.wallet_demo.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.snap.wallet.demo.wallet_demo.domain.RequestContext;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.util.AlternativeJdkIdGenerator;

import java.time.LocalDateTime;

@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@JsonIgnoreProperties(value = {"createAt", "updateAt"}, allowGetters = true)
public abstract class Auditable {
    @Id
    @SequenceGenerator(name = "Primary_Key_Sq", sequenceName = "Primary_Key_Sq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "Primary_Key_Sq")
    private Long id;
    @Column
    private String referenceId = new AlternativeJdkIdGenerator().generateId().toString();
    private Long createdBy = 0L;
    @CreatedDate
    @Column(updatable = false, nullable = false)
    private LocalDateTime createdAt;
    @NotNull
    private Long updatedBy = 0L;
    @CreatedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    @NotNull
    private Boolean active = true;


    @PrePersist
    public void beforePersist() {
        Long userId = RequestContext.getUserId() != null ? RequestContext.getUserId() : 0L;
        setCreatedAt(LocalDateTime.now());
        setUpdatedAt(LocalDateTime.now());
        setCreatedBy(userId);
        setUpdatedBy(userId);
    }

    @PreUpdate
    public void beforeUpdate() {
        Long userId = RequestContext.getUserId() != null ? RequestContext.getUserId() : 0L;
        setUpdatedAt(LocalDateTime.now());
        setUpdatedBy(userId);
    }
}
