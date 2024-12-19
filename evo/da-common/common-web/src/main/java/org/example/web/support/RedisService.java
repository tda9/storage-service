//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.example.web.support;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.springframework.context.annotation.DependsOn;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@DependsOn({"redisTemplate"})
public class RedisService {
    private static final String HASH_KEY = "BLACKLIST_TOKEN";
    private final RedisTemplate<String, Object> redisTemplate;
    private final HashOperations<String, String, String> hashOperations;

    public RedisService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.hashOperations = redisTemplate.opsForHash();
    }

    public void save(String token) {
        this.hashOperations.put(HASH_KEY,token,null);
        redisTemplate.expire(HASH_KEY, 7, TimeUnit.DAYS);
    }

    public Map<String, String> findAll() {
        return this.hashOperations.entries(HASH_KEY);
    }

    public String findByToken(String token) {
        return this.hashOperations.get(HASH_KEY, token);
    }

    public boolean isEntryExist(String token) {
        return this.hashOperations.hasKey(HASH_KEY, token);
    }

//    public void update(String token, String userId) {
//        this.save(token);
//    }

    public void delete(String token) {
        this.hashOperations.delete(HASH_KEY, token);
    }
}
