import http from 'k6/http';
import { check, sleep } from 'k6';
import { Rate, Trend } from 'k6/metrics';

// 커스텀 메트릭 정의
const errorRate = new Rate('errors');
const latencyTrend = new Trend('latency');

// 테스트 설정
export const options = {
    scenarios: {
        constant_load: {
            executor: 'constant-arrival-rate',
            rate: 100,              // 초당 100개 요청
            timeUnit: '1s',         // 1초 단위
            duration: '30s',        // 30초 동안
            preAllocatedVUs: 20,    // 미리 할당할 VU 수
            maxVUs: 100,            // 최대 VU 수
        },
    },
    thresholds: {
        'http_req_duration': ['p(95)<500'],  // 95%의 요청이 500ms 이내 완료
        'errors': ['rate<0.1'],              // 에러율 10% 미만
    },
};

const TEAMS = ['TeamA', 'TeamB', 'TeamC', 'TeamD'];
const PLAYERS = ['Player1', 'Player2', 'Player3', 'Player4'];

// 테스트 실행 전 한 번 실행
export function setup() {
    console.log('부하 테스트 시작: 초당 100개 이벤트 전송 (30초)');
}

// 메인 테스트 함수
export default function() {
    const teamId = TEAMS[Math.floor(Math.random() * TEAMS.length)];
    const playerId = PLAYERS[Math.floor(Math.random() * PLAYERS.length)];
    
    const payload = JSON.stringify({
        matchId: 'match123',
        eventType: 'GOAL',
        team: teamId,
        player: playerId,
        timestamp: new Date().toISOString()
    });

    const params = {
        headers: {
            'Content-Type': 'application/json',
        },
    };

    const response = http.post('http://localhost:8080/api/test/events', payload, params);
    
    // 응답 검증
    const checkResult = check(response, {
        'status is 200': (r) => r.status === 200,
        'response has eventId': (r) => r.body.length > 0,
    });

    // 메트릭 기록
    errorRate.add(!checkResult);
    latencyTrend.add(response.timings.duration);

    // 요청 간 약간의 간격
    sleep(0.01);
}

// 테스트 완료 후 실행
export function handleSummary(data) {
  console.log('전체 메트릭 데이터:', JSON.stringify(data, null, 2));

  // 안전한 메트릭 접근
  const p95ResponseTime = data.metrics?.http_req_duration?.values?.p95 || 'N/A';
  const errorRate = data.metrics?.errors?.values?.rate 
    ? (data.metrics.errors.values.rate * 100).toFixed(2) 
    : 'N/A';

  console.log(`95퍼센타일 응답 시간: ${
    typeof p95ResponseTime === 'number' 
      ? p95ResponseTime.toFixed(1) + 'ms' 
      : p95ResponseTime
  }`);
  console.log(`에러율: ${errorRate}%`);

  return {
    'stdout': `
    =========================
    🚀 부하 테스트 결과 요약 🚀
    =========================
    총 요청 수: ${data.metrics?.http_reqs?.values?.count || 'N/A'}
    평균 응답 시간: ${
      data.metrics?.http_req_duration?.values?.avg 
        ? (data.metrics.http_req_duration.values.avg * 1000).toFixed(2) + 'ms'
        : 'N/A'
    }
    에러율: ${errorRate}%
    95% 응답 시간: ${
      typeof p95ResponseTime === 'number' 
        ? p95ResponseTime.toFixed(1) + 'ms' 
        : p95ResponseTime
    }
    =========================
    `,
  };
} 