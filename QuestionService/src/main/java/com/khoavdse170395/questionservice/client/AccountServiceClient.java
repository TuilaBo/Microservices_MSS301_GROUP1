package com.khoavdse170395.questionservice.client;

import com.khoavdse170395.questionservice.client.dto.AccountDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "account-service", url = "${services.account.base-url}")
public interface AccountServiceClient {

    // Expected endpoint in AccountService to fetch account by username
    @GetMapping("/api/auth/get-my-infor")
    AccountDTO getByUsername();
}

