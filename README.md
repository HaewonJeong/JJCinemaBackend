# JJCinemaBackend
> 영화 예매 서비스 백엔드 프로젝트입니다.

## 프로젝트 목표
- 백엔드 기본기 학습
- Restful API 설계 경험
- 상용 서비스의 도메인 분석 및 구현 경험
- 동시성 이슈 해결 및 성능 최적화

## 프로젝트 문서
- 준비중

## Member
- 정해원

## 기술 스택
> JAVA 21/ SPRING BOOT 4.1.0 / PostgresSQL/GRADLE/SPRING DATA JPA (추후 수정)
- **Language**: Java 21, Kotlin (엔티티 일부)
- **Framework**: Spring Boot 4.1.0
  - Spring Web (MVC)
  - Spring Data JPA (Hibernate ORM)
  - Spring Security
- **DB**: PostgreSQL
- **Build**: Gradle (Groovy DSL)
- **기타**: Lombok, spring-dotenv(.env 파일 로딩)

## 문서/협업
> GIT/GITHUB/NOTION/POSTMAIN (추후 수정)
## ERD
> 추후 ERD Cloud 및 링크 업데이트

## 엔티티 설계 컨벤션
- 엔티티 생성은 Lombok `@Builder` 대신 **정적 팩토리 메서드**(`static create(...)`)를 사용


## Convention
> 필요할까요? 혼자 개발하는데..

## Commit Convention


## 프로젝트 구조
- 프로젝트의 규모가 작아 계층형(ayered Architecture) 구조를 사용 하였습니다.
```
src/main/java/com/jjcompany/jjcinemabackend/
├── JJCinemaBackendApplication.java
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



# 필요 X 지울 예정

## 로컬 실행 방법

### 1. 사전 준비

- JDK 21
- PostgreSQL (로컬 또는 원격)

### 2. DB 준비

`sql/schema.sql`을 원하는 PostgreSQL DB에 직접 실행해서 테이블을 생성합니다.
(Spring Boot가 자동으로 실행해주는 파일이 아니므로 `psql`, DBeaver 등으로 수동 실행 필요)

```
createdb jjcinema_db
psql -d jjcinema_db -f sql/schema.sql
```

### 3. 환경변수 설정

프로젝트 루트에 `.env` 파일을 만들고 아래 값을 채워줍니다. (`.env`는 git에 커밋되지 않습니다)

```
DB_USERNAME=your_db_username
DB_PASSWORD=your_db_password
```

접속 URL은 `src/main/resources/application.yaml`에 `jdbc:postgresql://localhost:5432/jjcinema_db`로 고정되어 있습니다. DB 이름/호스트가 다르면 해당 파일을 직접 수정하세요.

### 4. 서버 실행

```
./gradlew.bat bootRun
```

기본 포트는 `8080`입니다.

## API 구현 현황

| 기능 | 엔드포인트 | 비고 |
| --- | --- | --- |
| 회원가입 | `POST /api/auth/signup` | |
| 로그인 | `POST /api/auth/login` | 세션 기반 로그인 |
| 장르 CRUD | `/api/genres`, `/api/genres/{id}` | GET/POST/DELETE |
| 관람등급 CRUD | `/api/ratings`, `/api/ratings/{id}` | GET/POST/DELETE |

`/api/auth/**`를 제외한 모든 요청은 인증이 필요합니다(`SecurityConfig`).

`Booking`, `BookingSeat`, `Movie`, `Payment`, `Showtime`은 현재 엔티티만 정의되어 있고, Repository/Service/Controller는 아직 구현되지 않았습니다.
