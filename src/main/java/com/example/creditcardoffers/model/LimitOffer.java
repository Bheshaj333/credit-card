package com.example.creditcardoffers.model;

import com.example.creditcardoffers.constants.enums.LimitType;
import com.example.creditcardoffers.constants.enums.LimitOfferStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "limit_offer")
@ToString
@Getter
@Setter
public class LimitOffer implements Serializable {
    @Id
    @GeneratedValue
    private UUID limitOfferId;

    @Column
    private UUID accountId;

    @Column
    @Enumerated(EnumType.STRING)
    private LimitType limitType;

    @Column
    private Long newLimit;

    @Column
    private LocalDateTime offerActivationTime;

    @Column
    private LocalDateTime offerExpiryTime;

    @Column
    @Enumerated(EnumType.STRING)
    private LimitOfferStatus status;

    @Column
    private LocalDateTime activeDate;
}
