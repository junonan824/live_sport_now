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

1. **실시간 스코어보드**  
   - SSE를 통한 실시간 점수 업데이트  
   - Redis를 사용한 빠른 점수 조회  
   - 자동 재연결 및 에러 처리  

2. **이벤트 처리**  
   - Kafka를 통한 이벤트 스트리밍  
   - 이벤트 영구 저장소로 Elasticsearch 사용  
   - 비동기 이벤트 처리  

3. **모니터링**  
   - Spring Boot Actuator  
   - Prometheus 메트릭  
   - 커스텀 모니터링 스크립트  

## 실행 방법

### 사전 요구사항
- Docker & Docker Compose  
- Java 17  
- Node.js & npm  

### Docker & Docker Compose 설치

#### Windows
1. [Docker Desktop](https://www.docker.com/products/docker-desktop) 설치
   - Docker Compose는 Docker Desktop에 포함되어 있음

#### Mac
1. [Docker Desktop](https://www.docker.com/products/docker-desktop) 설치
   - Docker Compose는 Docker Desktop에 포함되어 있음

#### Linux (Ubuntu)
```bash
# Docker 설치
sudo apt-get update
sudo apt-get install docker.io

# Docker Compose 설치
sudo curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose

# Docker 서비스 시작
sudo systemctl start docker
sudo systemctl enable docker

# 현재 사용자를 docker 그룹에 추가 (sudo 없이 실행하기 위함)
sudo usermod -aG docker $USER
```

### 1. 백엔드 인프라 실행

```bash
# 인프라 컴포넌트 시작
docker-compose up -d

# 상태 확인
docker-compose ps
# 프로젝트 루트 디렉토리에서 빌드 및 백엔드 서버 실행
./gradlew build
./gradlew bootRun

### 2. 프론트엔드 실행
```bash
cd frontend
npm install
npm start
```

### 3. 테스트
```bash
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
# 이벤트 발생 테스트2
curl -X POST "http://localhost:8080/api/test/events" \
-H "Content-Type: application/json" \
-d '{
  "matchId": "match123",
  "eventType": "GOAL",
  "team": "TeamB",
  "player": "Player2",
  "timestamp": "2024-03-15T14:30:00Z"
}'
# 시스템 상태 확인
sh ./monitor.sh

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
1. Docker 관련 문제
   - 권한 문제: `sudo usermod -aG docker $USER` 실행 후 재로그인
   - 포트 충돌: `docker ps` 로 실행 중인 컨테이너 확인
   - 컨테이너 충돌: `docker-compose down -v` 로 기존 컨테이너 제거

2. Redis 연결 실패
   - Redis 서버 실행 확인
   - 포트 충돌 확인

3. Kafka 연결 오류
   - Zookeeper 실행 확인
   - 토픽 존재 여부 확인

4. SSE 연결 끊김
   - 네트워크 상태 확인
   - 브라우저 캐시 삭제

### 4. 시뮬레이션 테스트
```bash
# 1. 새로운 가상 경기 시작
curl -X POST "http://localhost:8080/api/simulation/matches" \
-H "Content-Type: application/json" \
-d '{
    "homeTeam": "Tottenham",
    "awayTeam": "Manchester City"
}'

# 2. 실시간 로그 모니터링
tail -f logs/application.log

# 3. 특정 경기의 골 이벤트 조회
curl "http://localhost:8080/api/events/match/{matchId}/goals"

# 4. 현재 스코어보드 조회
curl "http://localhost:8080/api/scoreboard/top/10"
```

#### 시뮬레이션 동작 방식
- 30초마다 새로운 이벤트가 자동 생성됩니다 (골, 슈팅, 경고 등)
- 각 이벤트는 실제 선수 데이터를 기반으로 생성됩니다
- 90분이 지나면 경기가 자동으로 종료됩니다

#### 프론트엔드에서 실시간 경기 보기
1. 브라우저에서 프론트엔드 앱 접속
2. SSE를 통해 실시간으로 업데이트되는 경기 정보 확인
3. 개발자 도구의 Network 탭에서 SSE 연결 상태 모니터링

#### 문제 해결
1. SSE 연결 문제
```bash
# Redis 서버 상태 확인
docker ps | grep redis
```

2. 이벤트가 생성되지 않는 경우
```bash
# 애플리케이션 로그 확인
tail -f logs/application.log | grep "Generated event"
```

3. 데이터베이스 연결 문제
```bash
# Docker 컨테이너 상태 확인
docker-compose ps
```

### 시뮬레이션 테스트 가이드

#### 1. 서버 실행 확인

#### 1-1. 백엔드 서버 실행
```bash
docker-compose up -d
```

#### 1-2. 백엔드 서버가 정상적으로 실행 중인지 확인
```bash
curl http://localhost:8080/actuator/health
```

#### 2. 시뮬레이션 실행
```bash
# 시뮬레이션 서버 실행
curl -X POST "http://localhost:8080/api/simulation/matches" \
-H "Content-Type: application/json" \
-d '{
    "homeTeam": "Tottenham",
    "awayTeam": "Manchester City"
}'
```
응답 예시
```json
{"matchId":"c333fc7b-1ae0-4e22-9839-d17667019c97","message":"Started simulation: Tottenham vs Manchester City"}
```

#### 3. 프론트엔드에서 확인
#### 3-1. 브라우저에서 프론트엔드 앱 접속
   ```bash
   cd frontend
   npm start
   ```
   - 브라우저가 자동으로 http://localhost:3000 열림

#### 3-2. 실시간 경기 정보 확인
   - 메인 페이지에서 진행 중인 경기 목록 확인
   - 시뮬레이션 경기 클릭하여 상세 페이지로 이동

#### 3-3. 개발자 도구로 모니터링
   - F12 또는 우클릭 > 검사 클릭
   - Network 탭 선택
   - Filter: "EventSource" 입력
   - SSE 연결 상태 및 이벤트 수신 확인

#### 4. 실시간 업데이트 확인
#### 4-1. 실시간 로그 모니터링
   ```bash
   tail -f logs/application.log | grep "Generated event"
   ```
#### 4-2. 현재 스코어 확인
   ```bash
   curl "http://localhost:8080/api/scoreboard/top/10"
   ```
#### 4-3. 특정 경기의 골 이벤트 조회
   ```bash
   curl "http://localhost:8080/api/events/match/{matchId}/goals"
   ```

#### 5. 문제 해결
#### 5-1. SSE 연결이 보이지 않는 경우:
  # Redis 상태 확인
  ```bash
  docker ps | grep redis
  ```

#### 5-2. 경기 정보가 업데이트되지 않는 경우:
  # 애플리케이션 로그 확인
  ```bash
  	tail -f logs/application.log
  ```

#### 5-3. 프론트엔드에서 데이터를 받지 못하는 경우:
  1. 브라우저 콘솔 로그 확인
  2. CORS 설정 확인
  3. 네트워크 요청/응답 확인