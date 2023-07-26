package com.example.creditcardoffers.controller;

import com.example.creditcardoffers.constants.enums.LimitType;
import com.example.creditcardoffers.constants.enums.Status;
import com.example.creditcardoffers.model.Account;
import com.example.creditcardoffers.model.LimitOffer;
import com.example.creditcardoffers.service.CreditCardLimitService;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/credit/limit")
public class CreditCardLimitController {
    @Autowired
    CreditCardLimitService creditCardLimitService;

    @PostMapping("/createAccount")
    ResponseEntity<String> createAccount(@RequestBody Account account) {
        try {
            creditCardLimitService.createAccount(account);
            return ResponseEntity.status(HttpStatus.CREATED).body("Account created successfully");
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred");
        }
    }

    @GetMapping("/getAccount")
    ResponseEntity<Account> getAccount(@RequestParam UUID accountId) {
        Optional<Account> account = creditCardLimitService.getAccount(accountId);
        if (account.isPresent()) {
            return ResponseEntity.ok(account.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/createOffer")
    ResponseEntity<Void> createLimitOffer(@RequestParam @NonNull UUID accountId,
                          @RequestParam LimitType limitType,
                          @RequestParam Long newLimit,
                          @RequestParam LocalDateTime offerActivationTime,
                          @RequestParam LocalDateTime offerExpiryTime) {
        try{
            creditCardLimitService.createLimitOffer(accountId, limitType, newLimit, offerActivationTime, offerExpiryTime);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/activeLimitOffers")
    public ResponseEntity<List<LimitOffer>> activeLimitOffers(@RequestParam @NonNull UUID accountId,
                                       @RequestParam(required = false) LocalDateTime activeDate) {
        try {
            List<LimitOffer> limitOffers = creditCardLimitService.activeLimitOffers(accountId, activeDate);
            return ResponseEntity.ok(limitOffers);
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/updateLimitOfferStatus")
    public ResponseEntity<Void> updateLimitOfferStatus(@RequestParam @NonNull UUID limitOfferId,
                                            @RequestParam Status status) {
        try {
            creditCardLimitService.updateLimitOfferStatus(limitOfferId, status);
            return ResponseEntity.ok().build();
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
