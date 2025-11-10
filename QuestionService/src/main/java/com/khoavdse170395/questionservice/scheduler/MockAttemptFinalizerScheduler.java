package com.khoavdse170395.questionservice.scheduler;

import com.khoavdse170395.questionservice.service.MockAttemptService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class MockAttemptFinalizerScheduler {

    private final MockAttemptService mockAttemptService;

    @Scheduled(fixedDelayString = "${mock-attempt.auto-finalize-interval-ms:60000}")
    public void finalizeExpiredAttempts() {
        int processed = mockAttemptService.finalizeExpiredAttempts();
        if (processed > 0) {
            log.info("Auto-finalized {} mock attempts that exceeded their allotted time", processed);
        }
    }
}
