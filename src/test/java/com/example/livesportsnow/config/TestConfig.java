package com.example.livesportsnow.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.mockito.Mockito;

@TestConfiguration
public class TestConfig {
    
    @Bean
    @Primary
    public ElasticsearchTemplate elasticsearchTemplate() {
        return Mockito.mock(ElasticsearchTemplate.class);
    }
} 