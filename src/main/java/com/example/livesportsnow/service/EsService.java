package com.example.livesportsnow.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.BulkRequest;
import co.elastic.clients.elasticsearch.core.BulkResponse;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class EsService {

    private final ElasticsearchClient esClient;
    private final String indexName;

    public EsService(
            ElasticsearchClient esClient,
            @Value("${elasticsearch.index.events}") String indexName) {
        this.esClient = esClient;
        this.indexName = indexName;
    }

    /**
     * 여러 이벤트를 벌크로 저장합니다.
     */
    public void bulkInsertEvents(List<Map<String, Object>> events) throws IOException {
        var bulkRequest = new BulkRequest.Builder();
        
        for (Map<String, Object> event : events) {
            bulkRequest.operations(op -> op
                .index(idx -> idx
                    .index(indexName)
                    .document(event)
                )
            );
        }
        
        BulkResponse response = esClient.bulk(bulkRequest.build());
        
        if (response.errors()) {
            log.error("Bulk insert has failures");
        } else {
            log.info("Successfully inserted {} events", events.size());
        }
    }

    /**
     * 특정 매치의 골 이벤트를 검색합니다.
     */
    public List<Map<String, Object>> searchGoalEvents(String matchId) throws IOException {
        SearchResponse<Map> response = esClient.search(s -> s
            .index(indexName)
            .query(q -> q
                .bool(b -> b
                    .must(m -> m
                        .match(t -> t
                            .field("matchId")
                            .query(matchId)
                        )
                    )
                    .must(m -> m
                        .match(t -> t
                            .field("eventType")
                            .query("GOAL")
                        )
                    )
                )
            ),
            Map.class
        );

        List<Map<String, Object>> results = new ArrayList<>();
        for (Hit<Map> hit : response.hits().hits()) {
            results.add(hit.source());
        }
        
        log.info("Found {} goal events for match {}", results.size(), matchId);
        return results;
    }
} 