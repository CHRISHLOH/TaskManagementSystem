package com.task.management.system.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "t_deactivated_token", schema = "management_system")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DeactivatedToken {
    @Id
    private UUID id;

    @Column(name = "c_keep_until")
    private Instant keepUntil;
}