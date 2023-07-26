package com.example.creditcardoffers;

import com.example.creditcardoffers.model.Account;
import com.example.creditcardoffers.repository.AccountRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Log4j2
@Component
public class Bootstrap implements ApplicationListener<ApplicationReadyEvent> {
    @Autowired
    AccountRepository accountRepository;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        log.info("Bootstrap Started");
        if(accountRepository.count() == 0){
            Account account = new Account();
            account.setCustomerId(UUID.randomUUID());
            account.setAccountLimit(30000L);
            account.setPerTransactionLimit(10000L);
            account.setLastAccountLimit(10000L);
            account.setLastPerTransactionLimit(8000L);
            account.setAccountLimitUpdateTime(LocalDateTime.now().minusDays(10));
            account.setPerTransactionLimitUpdateTime(LocalDateTime.now().minusDays(10));
            accountRepository.save(account);

            Account account2 = new Account();
            account2.setCustomerId(UUID.randomUUID());
            account2.setAccountLimit(70000L);
            account2.setPerTransactionLimit(10000L);
            accountRepository.save(account2);
        }
    }
}
