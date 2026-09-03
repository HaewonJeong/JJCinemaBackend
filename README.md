## JJCinemaBackend
> 영화 예매 서비스의 핵심 동선을 직접 설계하고 해결하는 데 초점을 둔 프로젝트 입니다.

## 프로젝트 목표
- 백엔드 기본기 학습
- Restful API 설계 경험
- 상용 서비스의 도메인 분석 및 구현 경험
- 계층형 아키텍처, 인증/인가, 동시성 제어, 결제 흐름, 예외 처리, Role 구분을 직접 만들면서 체화

## Member

| 이름 | 담당 | 
| --- | --- | 
| 정해원 | Back Developer |

## 기술 스택

<table>
<tr>
<td valign="top" width="50%">

**Backend**

<img src="https://img.shields.io/badge/Java-21-007396?style=for-the-badge&logo=openjdk&logoColor=white"/>
<img src="https://img.shields.io/badge/Spring%20Boot-4.1.0-6DB33F?style=for-the-badge&logo=springboot&logoColor=white"/>
<img src="https://img.shields.io/badge/Spring%20Data%20JPA-6DB33F?style=for-the-badge&logo=spring&logoColor=white"/>
<img src="https://img.shields.io/badge/Gradle-02303A?style=for-the-badge&logo=gradle&logoColor=white"/> 

**Database**

<img src="https://img.shields.io/badge/PostgreSQL-4169E1?style=for-the-badge&logo=postgresql&logoColor=white"/>
<img src="https://img.shields.io/badge/AWS%20RDS-527FFF?style=for-the-badge&logo=amazonrds&logoColor=white"/>

</td>
<td valign="top" width="50%">

**Infra / DevOps (배포 예정)**

<img src="https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white"/>
<img src="https://img.shields.io/badge/AWS%20EC2-FF9900?style=for-the-badge&logo=amazonec2&logoColor=white"/>
<img src="https://img.shields.io/badge/GitHub%20Actions-2088FF?style=for-the-badge&logo=githubactions&logoColor=white"/>

**Frontend (연동)**

<img src="https://img.shields.io/badge/Next.js-000000?style=for-the-badge&logo=nextdotjs&logoColor=white"/>
<img src="https://img.shields.io/badge/Vercel-000000?style=for-the-badge&logo=vercel&logoColor=white"/>

</td>
</tr>
</table>
<br>

## 문서/협업
> GIT/GITHUB/NOTION/POSTMAN
## 주요 기능

**일반 사용자**

- 회원가입(닉네임/이메일/비밀번호 형식 검증, 이메일 실시간 중복확인), 로그인/로그아웃(서버 세션 기반, 새로고침 시 세션 복원)
- 영화 목록 조회(상영중/상영예정 탭, 제목·장르 검색), 상영시간표 조회
- 좌석 선택 및 임시선점(HOLD, 5분), 모의 결제(성공/실패 시뮬레이션), 예매 확정
- 내 예매 조회, 예매 취소(결제완료 건은 환불 처리)

**관리자**

- 대시보드: 오늘/누적 매출·예매 통계, 상영별 좌석 점유율
- 영화 관리: 등록/수정(포스터 업로드, 장르·등급 DB 연동 드롭다운)
- 상영 관리: 다중 슬롯 일괄 등록, 개별 수정, 여러 회차 선택 후 일괄 수정(상영관/가격)
- 회원 관리: 역할(일반/관리자) 변경, 계정 활성/비활성 (본인 계정은 변경 불가)

## 서비스 소개
- 영화 목록/상세, 상영 회차, 좌석 배치도를 조회하고 좌석을 선택해 예매합니다.
- 좌석은 선택 즉시 확정되는 것이 아니라 **5분간 임시 선점(HOLD)** 되며, 그 안에 결제해야 예매가 확정(CONFIRMED)됩니다.
- 관리자 전용 API는 컨트롤러를 `admin` 패키지·`/api/admin/**` 경로로 분리하고, `SecurityConfig`에서 `.requestMatchers("/api/admin/**").hasRole("ADMIN")` 로 권한을 관리합니다.

**좌석 구성**
| 구분 | 내용 |
| --- | --- |
| 배치 | 5행(A-E) × 8열(1-8), 총 40석 |
| 상영관당 좌석 | 상영 회차 등록 시 40석 자동 생성 |
| 임시 선점 시간 | 5분 (초과 시 자동으로 예매 가능 상태로 취급) |

**예매 정책**
- 로그인한 회원만 예매 가능 (세션 기반 인증)
- 한 번에 여러 좌석 동시 선점 가능, 중복 좌석 선택 불가
- 임시 선점(HOLD) 상태에서만 결제 가능, 5분 초과 시 재선택 필요
- 예매 취소 시 좌석은 즉시 반납되어 다른 사용자가 선택 가능

## ERD
**[ERD 링크](https://www.erdcloud.com/d/XnissnvXBK8n68nhE)**
<img width="2390" height="1204" alt="image" src="https://github.com/user-attachments/assets/d3e5de36-3c42-4a8b-99ef-ba0c2ddcdd65" />

**엔티티 설계 컨벤션**
- 엔티티 생성은 Lombok `@Builder` 대신 **정적 팩토리 메서드**(`static create(...)`)를 사용

**DB 제약조건 / 인덱스**
- `seats`, `booking_seats`: `UNIQUE(showtime_id, seat_code)` — 같은 상영 회차의 같은 좌석이 중복 저장되지 않도록 DB 레벨에서 강제
- `payments.booking_id`: `UNIQUE` — 예매 1건당 결제 1건만 허용
- `showtimes`: `UNIQUE(theater, date, time)` — 같은 상영관·같은 시간대 중복 등록 방지
- 조회 빈도가 높은 컬럼(`showtime_id`, `movie_id`, `user_id`, `status` 등)에 인덱스 적용

## ⚙️ 동시성 설계

같은 좌석을 여러 사용자가 동시에 예매 요청해도 한 명만 성공하도록, **좌석 단위 비관적 락(`PESSIMISTIC_WRITE`)** 으로 처리합니다.
<img width="894" height="612" alt="image" src="https://github.com/user-attachments/assets/39b51bb4-fcc5-4219-97fc-14135812cae9" />

**설계 포인트**

- **좌석 단위 락**: 상영 회차 전체가 아니라 좌석 하나하나에 락을 걸어, 서로 다른 좌석을 고른 사용자끼리는 대기 없이 동시에 처리됩니다. (상영 회차 단위로 잠그면 무관한 좌석끼리도 서로 기다리게 되어 실용성이 떨어진다고 판단해 좌석 단위로 재설계했습니다.)
- **데드락 방지**: 한 번에 여러 좌석을 예매할 때는 좌석 코드를 정렬한 뒤 항상 같은 순서로 락을 겁니다. 두 트랜잭션이 서로 다른 순서로 락을 시도하면 서로의 락을 기다리는 데드락이 발생할 수 있기 때문입니다.
- **임시 선점 만료(Lazy Expiration)**: 별도 스케줄러 없이, 좌석 상태를 조회하는 시점에 `held_at + 5분`이 지났는지 계산해 만료 여부를 판단합니다.
- **최후 방어선**: `booking_seats`, `seats`에 걸린 `UNIQUE(showtime_id, seat_code)` 제약이 락으로도 못 막은 예외 상황을 DB 레벨에서 한 번 더 방어합니다.

## 배포 아키텍쳐

## API 설계

| 메서드 | 경로 | 설명 | 권한 |
| --- | --- | --- | --- |
| POST | `/api/auth/signup` | 회원가입 | 공개 |
| POST | `/api/auth/login` | 로그인 | 공개 |
| GET | `/api/auth/me` | 로그인 상태 확인 | 로그인 필요 |
| POST | `/api/auth/logout` | 로그아웃 | 로그인 필요 |
| GET | `/api/auth/check-email` | 이메일 중복확인 | 공개 |
| GET | `/api/movies`, `/api/movies/{id}` | 영화 조회 | 공개 |
| GET | `/api/showtimes`, `/api/showtimes/{id}` | 상영 조회 | 공개 |
| GET | `/api/showtimes/{id}/seats` | 좌석 배치도 조회 | 공개 |
| POST | `/api/bookings` | 좌석 선점(예매 생성) | 로그인 필요 |
| GET | `/api/bookings/me`, `/api/bookings/{id}` | 내 예매 조회 | 로그인 필요(본인만) |
| DELETE | `/api/bookings/{id}` | 예매 취소 | 로그인 필요(본인만) |
| POST | `/api/payments` | 모의 결제 | 로그인 필요 |
| GET/POST/PATCH/DELETE | `/api/admin/**` | 영화·상영·회원 관리, 통계 | `ROLE_ADMIN` |

**응답 형식**

모든 API는 아래 공통 포맷으로 응답합니다.

```json
{
  "success": true,
  "message": "설명 메시지",
  "data": {}
}
```

**오류 코드**

| HTTP 상태 | 사용 시점 |
| --- | --- |
| 400 Bad Request | 입력값 검증 실패 (IllegalArgumentException) — 중복 좌석, 존재하지 않는 좌석 등 |
| 401 Unauthorized | 로그인하지 않은 상태로 인증 필요한 API 접근 |
| 403 Forbidden | 본인 소유가 아닌 리소스 접근, 또는 관리자 권한 필요 |
| 404 Not Found | 리소스를 찾을 수 없음 (IllegalStateException) — 존재하지 않는 예매/영화/회차 등 |
| 409 Conflict | 좌석 중복 선점, DB 제약 위반 (DataIntegrityViolationException) |
| 500 Internal Server Error | 그 외 예상하지 못한 서버 오류 |


**프로젝트 구조**
- 프로젝트의 규모를 고려하여 `계층형(layered Architecture)` 구조를 사용 하였습니다.
```
src/main/java/com/jjcompany/jjcinemabackend/
├── JJCinemaBackendApplication.java
├── admin/             # 관리자 컨트롤러
├── config/            # SecurityConfig, PasswordConfig 등 설정 클래스
├── controller/        # REST 컨트롤러
├── domain/            # JPA 엔티티 (Genre, Rating, Booking, Movie, Payment, Showtime, User 등)
├── dto/
│   ├── request/       # 요청 DTO
│   └── response/      # 응답 DTO
├── repository/        # Spring Data JPA 리포지토리
├── security/          # CustomUserDetailsService 등 인증 관련
└── service/           # 비즈니스 로직
```

---

## 예외 처리
- GlobalExceptionHandler 를 적용하여, 각 컨트롤러마다 try-catch 넣을 필요 없이 자동으로 이 핸들러가 예외를 낚아 채서 예외 코드 + ApiResponse.fail(메시지) 형식으로 변환
- `@RestControllerAdvice`로 전역 예외 처리 통일

```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiResponse<Void>> handleNotFound(IllegalStateException e){
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.fail(e.getMessage()));
    }
...
```

# 보안 파일 관리 규칙
- `.env`와 같은 개인 API 키는 **절대 커밋 금지**
- `.gitignore`에 반드시 추가

## 실행 방법
1. Git Clone 하기
2. `sql/schema.sql`을 원하는 PostgreSQL DB에 직접 실행해서 테이블을 생성
3. 백엔드 서버 및 프론트 실행

기본 포트는 `8080`입니다.

## 시연

