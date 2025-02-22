package com.example.livesportsnow;

import com.example.livesportsnow.config.TestConfig;
import com.example.livesportsnow.service.LiveMatchService;
import com.example.livesportsnow.service.RedisService;
import com.example.livesportsnow.service.EsService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.context.ApplicationEventPublisher;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(
    properties = {
        "spring.main.allow-bean-definition-overriding=true"
    }
)
@Import(TestConfig.class)
@TestPropertySource(properties = {
    "spring.kafka.bootstrap-servers=localhost:9092",
    "spring.redis.host=localhost",
    "spring.redis.port=6379",
    "spring.elasticsearch.uris=http://localhost:9200"
})
class LiveSportsNowApplicationTests {

    @MockBean
    private KafkaTemplate<String, String> kafkaTemplate;

    @MockBean
    private RedisTemplate<String, Object> redisTemplate;

    @MockBean
    private StringRedisTemplate stringRedisTemplate;

    @MockBean
    private ElasticsearchClient elasticsearchClient;

    @MockBean
    private ElasticsearchTemplate elasticsearchTemplate;

    @MockBean
    private LiveMatchService liveMatchService;

    @MockBean
    private RedisService redisService;

    @MockBean
    private EsService esService;

    @MockBean
    private ApplicationEventPublisher eventPublisher;

    @MockBean
    private ZSetOperations<String, String> zSetOperations;

    @Test
    void contextLoads() {
    }
} 