import http from 'k6/http';
import { Counter, Trend } from 'k6/metrics';

const BASE_URL = 'http://localhost:8080';
const EMAIL = 'tz_test_1788509794@example.com'; // 로그인 가능한 아무 계정으로 바꿔도 됨
const PASSWORD = 'abcd1234!';
const SHOWTIME_ID = 3;   // 테스트할 상영 회차 id
const SEAT_CODE = 'A1';  // 지금 "선택 가능" 상태인 좌석이어야 함

export const options = {
  scenarios: {
    seat_race: {
      executor: 'per-vu-iterations',
      vus: 100,
      iterations: 1,      // VU당 딱 1번, 100명이 동시에 A1 하나를 노림
      maxDuration: '30s',
    },
  },
};

const successCount = new Counter('booking_success');
const conflictCount = new Counter('booking_conflict_409');
const otherFailCount = new Counter('booking_other_fail');
const successDuration = new Trend('success_response_time');
const conflictDuration = new Trend('conflict_response_time'); // 락 대기 후 실패까지 걸린 시간

export function setup() {
  const res = http.post(
    `${BASE_URL}/api/auth/login`,
    JSON.stringify({ email: EMAIL, password: PASSWORD }),
    { headers: { 'Content-Type': 'application/json' } },
  );
  const setCookie = res.headers['Set-Cookie'] || '';
  const jsessionid = setCookie.split(';')[0]; // "JSESSIONID=xxxx"
  if (!jsessionid.startsWith('JSESSIONID')) {
    throw new Error('로그인 실패 — 이메일/비번 확인. 응답: ' + res.body);
  }
  return { cookie: jsessionid };
}

export default function (data) {
  const res = http.post(
    `${BASE_URL}/api/bookings`,
    JSON.stringify({ showtimeId: SHOWTIME_ID, seatCodes: [SEAT_CODE] }),
    { headers: { 'Content-Type': 'application/json', Cookie: data.cookie } },
  );

  if (res.status === 201) {
    successCount.add(1);
    successDuration.add(res.timings.duration);
  } else if (res.status === 409) {
    conflictCount.add(1);
    conflictDuration.add(res.timings.duration);
  } else {
    otherFailCount.add(1);
    console.log(`unexpected status ${res.status}: ${res.body}`);
  }
}
