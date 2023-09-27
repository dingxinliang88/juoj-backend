package com.juzi.oj.manager;

import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RRateLimiter;
import org.redisson.api.RateIntervalUnit;
import org.redisson.api.RateType;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 使用Redisson实现限流
 *
 * @author codejuzi
 */
@Component
@Slf4j
public class RedisLimiterManager {

    @Resource
    private RedissonClient redissonClient;

    /**
     * 执行限流，设定每秒最多访问一次
     *
     * @param key 限流key
     * @return true - 可以操作
     */
    public boolean doRateLimit(String key) {
        RRateLimiter rateLimiter = redissonClient.getRateLimiter(key);

        boolean rateRes = rateLimiter.trySetRate(RateType.OVERALL, 1, 1, RateIntervalUnit.SECONDS);
        if (rateRes) {
            log.info("init rate: {}, interval: {}", rateLimiter.getConfig().getRate(), rateLimiter.getConfig().getRateInterval());
        }

        // 每一个操作来，申请一个令牌
        return rateLimiter.tryAcquire(1);
    }
}
