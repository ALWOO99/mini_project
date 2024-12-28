package vttp.batchb.ssf.project.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;
import vttp.batchb.ssf.project.model.ChatMessage;

@Configuration
public class RedisConfig {

    @Value("${spring.data.redis.host}")
    private String redisHost;

    @Value("${spring.data.redis.port}")
    private int redisPort;

    @Value("${spring.data.redis.database}")
    private int redisDatabase;

    @Value("${spring.data.redis.username}")
    private String redisUsername;

    @Value("${spring.data.redis.password}")
    private String redisPassword;

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate(); // Define the RestTemplate bean here
    }

    @Bean
    public JedisConnectionFactory jedisConnectionFactory() {
        RedisStandaloneConfiguration redisConfig = new RedisStandaloneConfiguration("localhost", 6379);
        JedisClientConfiguration jedisClientConfiguration = JedisClientConfiguration.builder().build();
        return new JedisConnectionFactory(redisConfig, jedisClientConfiguration);
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate() {

        // Create a database configuration
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(redisHost, redisPort);
        // Sets the database - select 0
        config.setDatabase(redisDatabase);
        // Set the username and password if they are set
        if (!redisUsername.trim().equals("")) {
            // logger.info("Setting Redis username and password");
            config.setUsername(redisUsername);
            config.setPassword(redisPassword);
        }

        // Create a connection to the database
        JedisClientConfiguration jedisClient = JedisClientConfiguration.builder().build();

        // Create a factory to connect to Redis
        JedisConnectionFactory jedisFac = new JedisConnectionFactory(config, jedisClient);
        jedisFac.afterPropertiesSet();

        // Create the RedisTemplate
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(jedisFac);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(new StringRedisSerializer());
        return redisTemplate;
    }

    @Bean
    public RedisTemplate<String, ChatMessage> redisTemplateChat(JedisConnectionFactory jedisConnectionFactory) {
        RedisTemplate<String, ChatMessage> template = new RedisTemplate<>();
        template.setConnectionFactory(jedisConnectionFactory);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule()); // Support for LocalDateTime

        Jackson2JsonRedisSerializer<ChatMessage> serializer = new Jackson2JsonRedisSerializer<>(ChatMessage.class);
        serializer.setObjectMapper(objectMapper);

        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(serializer); // Use the customized serializer
        return template;
    }

    @Bean
    public HttpSessionListener httpSessionListener() {
        return new HttpSessionListener() {
            @Override
            public void sessionCreated(HttpSessionEvent se) {
                System.out.println("Session Created: " + se.getSession().getId());
            }

            @Override
            public void sessionDestroyed(HttpSessionEvent se) {
                System.out.println("Session Destroyed: " + se.getSession().getId());
            }
        };
    }
}
