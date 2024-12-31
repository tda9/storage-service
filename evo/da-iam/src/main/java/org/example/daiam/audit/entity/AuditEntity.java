package org.example.daiam.audit.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@MappedSuperclass
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
public class AuditEntity {
    @CreatedBy
    @Column(name = "created_by",length = 1000)
    protected String createdBy;
    @CreatedDate
    @Column(name = "created_date")
    protected LocalDateTime createdDate;
    @LastModifiedBy
    @Column(name = "last_modified_by",length = 1000)
    protected String lastModifiedBy;
    @LastModifiedDate
    @Column(name = "last_modified_date")
    protected LocalDateTime lastModifiedDate;
}
