package com.example.creditcardoffers.repository;

import com.example.creditcardoffers.constants.enums.LimitOfferStatus;
import com.example.creditcardoffers.model.LimitOffer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface LimitOfferRepository extends JpaRepository<LimitOffer, UUID> {
    List<LimitOffer> findAllByAccountIdAndStatus(UUID accoundId, LimitOfferStatus status);

    List<LimitOffer> findAllByAccountIdAndStatusAndActiveDate(UUID accoundId, LimitOfferStatus status, LocalDateTime activeDate);
}
