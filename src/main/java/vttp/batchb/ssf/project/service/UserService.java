package vttp.batchb.ssf.project.service;

import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final RedisTemplate<String, String> redisTemplate;
    public UserService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void register(String username, String password) {
        redisTemplate.opsForValue().set(username, password, 3600, TimeUnit.SECONDS); // Expires in an hour for simplicity
    }

    public boolean authenticate(String username, String password) {
        String storedPassword = redisTemplate.opsForValue().get(username);
        System.out.println("Stored password for " + username + ": " + storedPassword);
        return storedPassword != null && storedPassword.equals(password);
    }
}