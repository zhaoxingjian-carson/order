package com.carson.utils;
public interface RateLimiter {

    boolean isOverLimit();

    long currentQPS();

    boolean visit();
}