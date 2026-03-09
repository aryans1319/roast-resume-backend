package com.roastmyresume.backend.service;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RateLimiterService {

    // One bucket per IP address
    private final ConcurrentHashMap<String, Bucket> buckets = new ConcurrentHashMap<>();

    private Bucket createNewBucket() {
        // Allow 5 requests per IP per hour
        Refill refill = Refill.intervally(5, Duration.ofHours(1));
        Bandwidth limit = Bandwidth.classic(5, refill);
        return Bucket.builder().addLimit(limit).build();
    }

    public boolean isAllowed(String ipAddress) {
        Bucket bucket = buckets.computeIfAbsent(ipAddress, k -> createNewBucket());
        return bucket.tryConsume(1);
    }

    public long getRemainingTokens(String ipAddress) {
        Bucket bucket = buckets.computeIfAbsent(ipAddress, k -> createNewBucket());
        return bucket.getAvailableTokens();
    }
}