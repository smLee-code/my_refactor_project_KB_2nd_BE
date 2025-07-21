package org.funding.fund.scheduler;

import lombok.RequiredArgsConstructor;
import org.funding.fund.service.FundService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FundScheduler {

    private final FundService fundService;

    @Scheduled(cron = "0 0 * * * * ?") // 매시 정각마다
    public void updateExpiredFunds() {
        fundService.closeExpiredFunds();
    }
}
