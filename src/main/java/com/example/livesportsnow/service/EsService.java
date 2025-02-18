package com.example.livesportsnow.service;

import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class EsService {

    private final RestHighLevelClient esClient;
    private final String indexName;

    public EsService(
            RestHighLevelClient esClient,
            @Value("${elasticsearch.index.events}") String indexName) {
        this.esClient = esClient;
        this.indexName = indexName;
    }

    /**
     * 여러 이벤트를 벌크로 저장합니다.
     */
    public BulkResponse bulkInsertEvents(List<Map<String, Object>> events) throws IOException {
        BulkRequest bulkRequest = new BulkRequest();
        
        for (Map<String, Object> event : events) {
            IndexRequest indexRequest = new IndexRequest(indexName)
                    .source(event);
            bulkRequest.add(indexRequest);
        }
        
        try {
            BulkResponse response = esClient.bulk(bulkRequest, RequestOptions.DEFAULT);
            if (response.hasFailures()) {
                log.error("Bulk insert has failures: {}", response.buildFailureMessage());
            } else {
                log.info("Successfully inserted {} events", events.size());
            }
            return response;
        } catch (IOException e) {
            log.error("Failed to perform bulk insert", e);
            throw e;
        }
    }

    /**
     * 특정 매치의 골 이벤트를 검색합니다.
     */
    public List<Map<String, Object>> searchGoalEvents(String matchId) throws IOException {
        SearchRequest searchRequest = new SearchRequest(indexName);
        
        // 쿼리 생성
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery()
                .must(QueryBuilders.matchQuery("matchId", matchId))
                .must(QueryBuilders.matchQuery("eventType", "GOAL"));
        
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder()
                .query(boolQuery)
                .size(100);  // 최대 100개 결과
        
        searchRequest.source(searchSourceBuilder);
        
        try {
            SearchResponse response = esClient.search(searchRequest, RequestOptions.DEFAULT);
            List<Map<String, Object>> results = new ArrayList<>();
            
            for (SearchHit hit : response.getHits().getHits()) {
                results.add(hit.getSourceAsMap());
            }
            
            log.info("Found {} goal events for match {}", results.size(), matchId);
            return results;
        } catch (IOException e) {
            log.error("Failed to search goal events for match {}", matchId, e);
            throw e;
        }
    }
} 