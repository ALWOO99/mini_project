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

    public boolean register(String username, String password) {
        // Check if the username already exists in Redis
        if (redisTemplate.hasKey(username)) { // If the key exists, return false
            return false; 
        }
    
        // Store the username and password in Redis with a 1-hour expiry
        redisTemplate.opsForValue().set(username, password, 3600, TimeUnit.SECONDS);
        return true; // Return true for successful registration
    }
    
    public boolean authenticate(String username, String password) {
        String storedPassword = redisTemplate.opsForValue().get(username);
        System.out.println("Stored password for " + username + ": " + storedPassword);
        return storedPassword != null && storedPassword.equals(password);
    }
}