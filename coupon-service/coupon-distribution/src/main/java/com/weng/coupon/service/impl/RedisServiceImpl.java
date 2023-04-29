package com.weng.coupon.service.impl;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.weng.coupon.constant.Constant;
import com.weng.coupon.constants.CouponStatus;
import com.weng.coupon.deserializer.CouponDeserializer;
import com.weng.coupon.entity.Coupon;
import com.weng.coupon.exception.CouponException;
import com.weng.coupon.serializer.CouponSerializer;
import com.weng.coupon.service.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Slf4j
public class RedisServiceImpl implements RedisService {
    @Autowired
    private StringRedisTemplate redisTemplate;

    private Gson gson = new GsonBuilder().registerTypeAdapter(new TypeToken<Coupon>(){}.getType(), new CouponSerializer())
            .registerTypeAdapter(new TypeToken<Coupon>(){}.getType(), new CouponDeserializer())
            .create();

    @Override
    public List<Coupon> getCachedCoupons(Long userId, Integer status) {
        log.info("Get Coupons From Cache: {}, {}", userId, status);
        String redisKey = status2RedisKey(status, userId);
        List<String> couponStrs = redisTemplate.opsForHash().values(redisKey).stream()
                .map(o -> Objects.toString(o, null))
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(couponStrs)) {
            saveEmptyCouponListToCache(userId, Collections.singletonList(status));
            return Collections.emptyList();
        }
        return couponStrs.stream().map(s -> gson.fromJson(s, Coupon.class)).collect(Collectors.toList());
    }

    @Override
    public void saveEmptyCouponListToCache(Long userId, List<Integer> status) {
        log.info("Save Empty List To Cache For User: {}, Status: {}",
                userId, gson.toJson(status));

        // key:coupon_id value:coupon的序列化
        Map<String, String> invalidMap = new HashMap<>();
        invalidMap.put("-1", gson.toJson(Coupon.invalidCoupon()));

        // 一次性发送多条redis命令，只是一次请求发送多条命令还是类似lua脚本一次执行多条命令？
        SessionCallback<Object> sessionCallback = new SessionCallback<Object>() {
            @Override
            public Object execute(RedisOperations redisOperations) throws DataAccessException {
                status.forEach(s -> {
                    String redisKey = status2RedisKey(s, userId);
                    redisOperations.opsForHash().putAll(redisKey, invalidMap);
                });
                return null;
            }
        };

        log.info("Pipeline Exe Result: {}",
                gson.toJson(redisTemplate.executePipelined(sessionCallback)));
    }

    @Override
    public String tryToAcquireCouponCodeFromCache(Integer templateId) {
        String redisKey = String.format("%s%s", Constant.RedisPrefix.COUPON_TEMPLATE, templateId);
        String couponCode = redisTemplate.opsForList().leftPop(redisKey);
        log.info("Acquire Coupon Code: {}, {}, {}",
                templateId, redisKey, couponCode);
        return couponCode;
    }

    @Override
    public Integer addCouponToCache(Long userId, List<Coupon> coupons, Integer status) throws CouponException {
        log.info("Add Coupon To Cache: {}, {}, {}",
                userId, new Gson().toJson(coupons), status);
        CouponStatus couponStatus = CouponStatus.of(status);
        Integer result = -1;
        switch (couponStatus) {
            case USABLE:
                result = addCouponToCacheForUsable(userId, coupons);
                break;
            case USED:
                result = addCouponToCacheForUsed(userId, coupons);
                break;
            case EXPIRED:
                result = addCouponToCacheForExpiration(userId, coupons);
        }
        return result;
    }

    private Integer addCouponToCacheForUsable(Long userId, List<Coupon> coupons) {
        String redisKey = status2RedisKey(CouponStatus.USABLE.getCode(), userId);
        Map<String, String> map = new HashMap<>();
        coupons.forEach(e -> {
            map.put(e.getCouponCode(), gson.toJson(e));
        });
        redisTemplate.opsForHash().putAll(redisKey, map);
        log.info("Add {} coupons to cache: {} {}", map.size(), userId, redisKey);
        redisTemplate.expire(redisKey, getRandomExpirationTime(1, 2), TimeUnit.SECONDS);
        return map.size();
    }

    private Integer addCouponToCacheForUsed(Long userId, List<Coupon> coupons) throws CouponException {
        log.debug("Add Coupon To Cache For Used.");
        String redisKeyForUsable = status2RedisKey(CouponStatus.USABLE.getCode(), userId);
        String redisKeyForUsed = status2RedisKey(CouponStatus.USED.getCode(), userId);

        List<Coupon> cachedUsableCoupons = getCachedCoupons(userId, CouponStatus.USABLE.getCode());
        List<Integer> paramIds = coupons.stream().map(Coupon::getId).collect(Collectors.toList());
        List<Integer> usableIds = cachedUsableCoupons.stream().map(Coupon::getId).collect(Collectors.toList());
        if (!org.apache.commons.collections4.CollectionUtils.isSubCollection(paramIds, usableIds)) {
            log.error("CurCoupons Is Not Equal ToCache: {}, {}, {}",
                    userId, gson.toJson(usableIds),
                    gson.toJson(paramIds));
            throw new CouponException("CurCoupons Is Not Equal To Cache!");
        }

        Map<String, String> map = new HashMap<>();
        coupons.forEach(c -> {
            c.setStatus(CouponStatus.USED);
            map.put(c.getId().toString(), gson.toJson(c));
        });
        SessionCallback<Object> sessionCallback = new SessionCallback<Object>() {
            @Override
            public Object execute(RedisOperations redisOperations) throws DataAccessException {
                redisOperations.opsForHash().delete(redisKeyForUsable, paramIds.stream().map(i -> i.toString()).collect(Collectors.toList()).toArray());
                redisOperations.opsForHash().putAll(redisKeyForUsed, map);
                redisOperations.expire(redisKeyForUsable, getRandomExpirationTime(1, 2), TimeUnit.SECONDS);
                redisOperations.expire(redisKeyForUsed, getRandomExpirationTime(1, 2), TimeUnit.SECONDS);
                return null;
            }
        };
        log.info("Pipeline Exe Result: {}",
                gson.toJson(
                        redisTemplate.executePipelined(sessionCallback)));

        return coupons.size();
    }

    private Integer addCouponToCacheForExpiration(Long userId, List<Coupon> coupons) throws CouponException {
        log.debug("Add Coupon To Cache For Expiration.");
        String redisKeyForUsable = status2RedisKey(CouponStatus.USABLE.getCode(), userId);
        String redisKeyForExpired = status2RedisKey(CouponStatus.EXPIRED.getCode(), userId);

        List<Coupon> cachedUsableCoupons = getCachedCoupons(userId, CouponStatus.USABLE.getCode());
        List<Integer> paramIds = coupons.stream().map(Coupon::getId).collect(Collectors.toList());
        List<Integer> usableIds = cachedUsableCoupons.stream().map(Coupon::getId).collect(Collectors.toList());
        if (!org.apache.commons.collections4.CollectionUtils.isSubCollection(paramIds, usableIds)) {
            log.error("CurCoupons Is Not Equal ToCache: {}, {}, {}",
                    userId, gson.toJson(usableIds),
                    gson.toJson(paramIds));
            throw new CouponException("CurCoupons Is Not Equal To Cache!");
        }

        Map<String, String> map = new HashMap<>();
        coupons.forEach(c -> {
            c.setStatus(CouponStatus.EXPIRED);
            map.put(c.getId().toString(), gson.toJson(c));
        });
        SessionCallback<Object> sessionCallback = new SessionCallback<Object>() {
            @Override
            public Object execute(RedisOperations redisOperations) throws DataAccessException {
                redisOperations.opsForHash().delete(redisKeyForUsable, paramIds.stream().map(i -> i.toString()).collect(Collectors.toList()).toArray());
                redisOperations.opsForHash().putAll(redisKeyForExpired, map);
                redisOperations.expire(redisKeyForUsable, getRandomExpirationTime(1, 2), TimeUnit.SECONDS);
                redisOperations.expire(redisKeyForExpired, getRandomExpirationTime(1, 2), TimeUnit.SECONDS);
                return null;
            }
        };
        log.info("Pipeline Exe Result: {}",
                gson.toJson(
                        redisTemplate.executePipelined(sessionCallback)));

        return coupons.size();
    }

    private String status2RedisKey(Integer status, Long userId) {
        CouponStatus couponStatus = CouponStatus.of(status);
        String redisKey = null;
        switch (couponStatus) {
            case USED:
                redisKey = String.format("%s%s", Constant.RedisPrefix.USER_COUPON_USED, userId);
                break;
            case USABLE:
                redisKey = String.format("%s%s", Constant.RedisPrefix.USER_COUPON_USABLE, userId);
                break;
            case EXPIRED:
                redisKey = String.format("%S%s", Constant.RedisPrefix.USER_COUPON_EXPIRED, userId);
                break;
        }
        return redisKey;
    }

    private Long getRandomExpirationTime(Integer min, Integer max) {
        return RandomUtils.nextLong(min * 60 * 60, max * 60 * 60);
    }
}
