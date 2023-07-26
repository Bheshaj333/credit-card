package com.example.creditcardoffers.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "Account")
@ToString
@Getter
@Setter
public class Account {
    @Id
    @GeneratedValue
    UUID accountId;

    @Column
    UUID customerId;

    @Column
    Long accountLimit;

    @Column
    Long perTransactionLimit;

    @Column
    Long lastAccountLimit;

    @Column
    Long lastPerTransactionLimit;

    @Column
    LocalDateTime accountLimitUpdateTime;

    @Column
    LocalDateTime perTransactionLimitUpdateTime;
}
