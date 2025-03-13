package com.meetime.hubspot.infrastructure.config;

import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Component
public class HubspotRateLimiter {
    private final AtomicInteger remainingRequests = new AtomicInteger(100);
    private final AtomicLong nextResetTime = new AtomicLong(System.currentTimeMillis());
    private final Lock lock = new ReentrantLock();

    public void update(int remaining, long intervalMillis) {
        lock.lock();
        try {
            this.remainingRequests.set(remaining);
            this.nextResetTime.set(System.currentTimeMillis() + intervalMillis);
        } finally {
            lock.unlock();
        }
    }

    public void handleRateLimitExceeded(long retryAfterMillis) {
        lock.lock();
        try {
            this.nextResetTime.set(System.currentTimeMillis() + retryAfterMillis);
            this.remainingRequests.set(0);
        } finally {
            lock.unlock();
        }
    }

    public void acquire() throws InterruptedException {
        lock.lock();
        try {
            while (remainingRequests.get() <= 0) {
                long waitTime = nextResetTime.get() - System.currentTimeMillis();
                if (waitTime > 0) {
                    Thread.sleep(waitTime);
                }
                this.remainingRequests.set(100);
            }
            this.remainingRequests.decrementAndGet();
        } finally {
            lock.unlock();
        }
    }
}
