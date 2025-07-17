package com.mukando.commons.jpa;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Version;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;

import java.io.Serializable;
import java.time.LocalDateTime;

@MappedSuperclass
@Data
public class BaseEntity implements Serializable {

    @CreatedBy
    @Column
    private String createdBy;

    @CreationTimestamp
    @Column
    @JsonFormat(shape = JsonFormat.Shape.STRING, locale = "en_ZW", pattern = "dd/MM/yyyy HH:mm:sss", timezone = "Africa/Harare")
    private LocalDateTime createdDate;

    @LastModifiedBy
    @Column
    private String lastModifiedBy;

    @UpdateTimestamp
    @Column
    @JsonFormat(shape = JsonFormat.Shape.STRING, locale = "en_ZW", pattern = "dd/MM/yyyy HH:mm:sss", timezone = "Africa/Harare")
    private LocalDateTime lastModifiedDate;

    @Version
    private Integer version;

    private boolean deleted;

    public void pseudoDelete() {
        this.deleted = true;
    }
}
