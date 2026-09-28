package com.example.pharmacyservice.config;

import com.example.pharmacyservice.model.dto.MedicineDto;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.JacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.Map;

/**
 * Redis làm Cache Provider: value lưu dưới dạng JSON (dễ đọc qua redis-cli),
 * key dạng String "medicines::&lt;id&gt;". TTL 60 phút: giá thuốc có thể đổi nên
 * không cache quá lâu; khi sửa giá thì @CacheEvict xóa key ngay lập tức.
 *
 * Cache "medicines" dùng typed serializer JacksonJsonRedisSerializer(MedicineDto)
 * nên JSON đọc/ghi đối xứng, không cần @class type info, không cần unsafe typing.
 */
@Configuration
@EnableCaching
public class RedisCacheConfig {

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        RedisCacheConfiguration defaults = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(60))
                .disableCachingNullValues()
                .serializeKeysWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()));

        RedisCacheConfiguration medicines = defaults.serializeValuesWith(
                RedisSerializationContext.SerializationPair.fromSerializer(
                        new JacksonJsonRedisSerializer<>(MedicineDto.class)));

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(defaults)
                .withInitialCacheConfigurations(Map.of("medicines", medicines))
                .build();
    }
}
