#!/bin/bash

echo "=== Kafka Status ==="
# Kafka 토픽 리스트
docker exec kafka kafka-topics --bootstrap-server localhost:9092 --list

# match_events 토픽 상세 정보
echo -e "\n=== match_events Topic Details ==="
docker exec kafka kafka-topics --bootstrap-server localhost:9092 --describe --topic match_events

# 컨슈머 그룹 상태
echo -e "\n=== Consumer Group Status ==="
docker exec kafka kafka-consumer-groups --bootstrap-server localhost:9092 --describe --group live-sports-group

echo -e "\n=== Redis Scoreboard ==="
redis-cli ZRANGE scoreboard 0 -1 WITHSCORES

echo -e "\n=== Elasticsearch Indices ==="
curl -s "localhost:9200/_cat/indices?v"

echo -e "\n=== Application Metrics ==="
curl -s "localhost:8080/actuator/metrics" | jq .

echo -e "\n=== Recent Logs ==="
tail -n 20 logs/application.log 