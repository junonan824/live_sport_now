# Live Sports Now

실시간 스포츠 스코어보드 시스템입니다. 이벤트 기반 아키텍처를 사용하여 실시간 점수 업데이트를 처리하고, 사용자에게 Server-Sent Events(SSE)를 통해 실시간으로 전달합니다.

## 기술 스택

### 백엔드
- Java 17  
- Spring Boot 3.2.3  
- Kafka  
- Redis  
- Elasticsearch  
- WebSocket/SSE  

### 프론트엔드
- React  
- Server-Sent Events (SSE)  
- CSS3  

### 인프라
- Docker  
- Docker Compose  
- GitHub Actions  

## 시스템 아키텍처


## 주요 기능

1. **회원 관리**
   - 회원가입 및 로그인
   - JWT 기반 인증
   - 회원 정보 관리

2. **실시간 스코어보드**  
   - SSE를 통한 실시간 점수 업데이트  
   - Redis를 사용한 빠른 점수 조회  
   - 자동 재연결 및 에러 처리  

3. **이벤트 처리**  
   - Kafka를 통한 이벤트 스트리밍  
   - 이벤트 영구 저장소로 Elasticsearch 사용  
   - 비동기 이벤트 처리  

4. **모니터링**  
   - Spring Boot Actuator  
   - Prometheus 메트릭  
   - 커스텀 모니터링 스크립트  

## 실행 방법

### 사전 요구사항
- Docker & Docker Compose  
- Java 17  
- Node.js & npm  

### 1. 백엔드 인프라 실행

```bash
# 인프라 컴포넌트 시작
docker-compose up -d

# 상태 확인
docker-compose ps

3. 프론트엔드 실행
```bash
cd frontend
npm install
npm start
```
4. 테스트
# 이벤트 발생 테스트
curl -X POST "http://localhost:8080/api/test/events" \
-H "Content-Type: application/json" \
-d '{
  "matchId": "match123",
  "eventType": "GOAL",
  "team": "TeamA",
  "player": "Player1",
  "timestamp": "2024-03-15T14:30:00Z"
}'

# 시스템 상태 확인
./monitor.sh

# 부하 테스트
k6 run load-test.js

API 엔드포인트

스코어보드 API
	•	GET /api/scoreboard/top/{n} - 상위 N개 팀 스코어 조회
	•	POST /api/scoreboard/teams/{teamId}/increment - 팀 스코어 증가

이벤트 API
	•	POST /api/test/events - 테스트 이벤트 발생
	•	GET /stream/scores - SSE 스트림 연결

모니터링
메트릭 확인

# 애플리케이션 메트릭
curl http://localhost:8080/actuator/metrics

# 프로메테우스 메트릭
curl http://localhost:8080/actuator/prometheus

로그 확인

# 애플리케이션 로그
tail -f logs/application.log

# 도커 컨테이너 로그
docker-compose logs -f

배포

GitHub Actions를 통해 자동 배포가 구성되어 있습니다:
	1.	main 브랜치 푸시 시 자동 빌드
	2.	Docker 이미지 생성 및 푸시
	3.	배포 자동화

개발 가이드

브랜치 전략
	•	main: 프로덕션 브랜치
	•	develop: 개발 브랜치
	•	feature/*: 기능 개발
	•	bugfix/*: 버그 수정

커밋 컨벤션
	•	feat: 새로운 기능
	•	fix: 버그 수정
	•	docs: 문서 수정
	•	style: 코드 포맷팅
	•	refactor: 코드 리팩토링
	•	test: 테스트 코드
	•	chore: 빌드 업무 수정

문제 해결

일반적인 문제
	1.	Redis 연결 실패
	•	Redis 서버 실행 확인
	•	포트 충돌 확인
	2.	Kafka 연결 오류
	•	Zookeeper 실행 확인
	•	토픽 존재 여부 확인
	3.	SSE 연결 끊김
	•	네트워크 상태 확인
	•	브라우저 캐시 삭제