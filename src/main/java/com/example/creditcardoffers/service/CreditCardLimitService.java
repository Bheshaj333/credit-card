package com.example.creditcardoffers.service;

import com.example.creditcardoffers.constants.enums.LimitType;
import com.example.creditcardoffers.constants.enums.LimitOfferStatus;
import com.example.creditcardoffers.constants.enums.Status;
import com.example.creditcardoffers.model.Account;
import com.example.creditcardoffers.model.LimitOffer;
import com.example.creditcardoffers.repository.AccountRepository;
import com.example.creditcardoffers.repository.LimitOfferRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class CreditCardLimitService {
    @Autowired
    AccountRepository accountRepository;
    @Autowired
    LimitOfferRepository limitOfferRepository;

    public Account createAccount(Account account) {
        return accountRepository.save(account);
    }

    public Optional<Account> getAccount(UUID accountId) {
        return accountRepository.findById(accountId);
    }

    public void createLimitOffer(UUID accountId,
                                 LimitType limitType,
                                 Long newLimit,
                                 LocalDateTime offerActivationTime,
                                 LocalDateTime offerExpiryTime) {
        if (!offerExpiryTime.isAfter(LocalDateTime.now()) && !offerExpiryTime.isAfter(offerActivationTime)) {
            throw new IllegalArgumentException("Offer expiry time should be after current time and activation time.");
        }
        Optional<Account> account = accountRepository.findById(accountId);
        if (account.isPresent()) {
            if ((limitType == LimitType.ACCOUNT_LIMIT && account.get().getAccountLimit() >= newLimit) ||
                    (limitType == LimitType.PER_TRANSACTION_LIMIT && account.get().getPerTransactionLimit() >= newLimit)) {
                throw new IllegalArgumentException("New limit should be greater than the current limit.");
            }
            LimitOffer limitOffer = new LimitOffer();
            limitOffer.setAccountId(accountId);
            limitOffer.setLimitType(limitType);
            limitOffer.setNewLimit(newLimit);
            limitOffer.setOfferActivationTime(offerActivationTime);
            limitOffer.setOfferExpiryTime(offerExpiryTime);
            limitOffer.setStatus(LimitOfferStatus.PENDING);
            limitOfferRepository.save(limitOffer);
        } else {
            throw new NoSuchElementException("Account not found with the provided accountId.");
        }
    }

    public List<LimitOffer> activeLimitOffers(UUID accountId, LocalDateTime activeDate) {
        List<LimitOffer> limitOffers = new ArrayList<>();
        if (activeDate == null) {
            limitOffers = limitOfferRepository.findAllByAccountIdAndStatus(accountId, LimitOfferStatus.PENDING);
        } else {
            limitOffers = limitOfferRepository.findAllByAccountIdAndStatusAndActiveDate(accountId, LimitOfferStatus.PENDING, activeDate);
        }
        if (limitOffers.isEmpty()) {
            throw new NoSuchElementException("No active limit offers found for the provided accountId and activeDate.");
        }
        return limitOffers;
    }

    public void updateLimitOfferStatus(UUID limitOfferId, Status status) {
        Optional<LimitOffer> limitOfferOpt = limitOfferRepository.findById(limitOfferId);
        if (limitOfferOpt.isPresent()) {
            LimitOffer limitOffer = limitOfferOpt.get();
            if (status == Status.ACCEPTED) {
                LocalDateTime now = LocalDateTime.now();
                if (limitOffer.getOfferExpiryTime().isBefore(now)) {
                    throw new IllegalArgumentException("Offer has expired and cannot be accepted.");
                }
                limitOffer.setStatus(LimitOfferStatus.ACTIVE);
                limitOffer.setActiveDate(LocalDateTime.now());
                limitOfferRepository.save(limitOffer);

                Optional<Account> accountOpt = accountRepository.findById(limitOffer.getAccountId());
                if (accountOpt.isPresent()) {
                    Account account = accountOpt.get();
                    if (limitOffer.getLimitType() == LimitType.ACCOUNT_LIMIT) {
                        account.setLastAccountLimit(account.getAccountLimit());
                        account.setAccountLimit(limitOffer.getNewLimit());
                        account.setAccountLimitUpdateTime(LocalDateTime.now());
                    } else {
                        account.setLastPerTransactionLimit(account.getPerTransactionLimit());
                        account.setPerTransactionLimit(limitOffer.getNewLimit());
                        account.setPerTransactionLimitUpdateTime(LocalDateTime.now());
                    }
                    accountRepository.save(account);
                } else {
                    throw new NoSuchElementException("No account associated with the offer found.");
                }
            } else {
                limitOffer.setStatus(LimitOfferStatus.REJECTED);
                limitOfferRepository.save(limitOffer);
            }
        } else {
            throw new NoSuchElementException("No limit offer found for the provided limitOfferId.");
        }
    }
}
