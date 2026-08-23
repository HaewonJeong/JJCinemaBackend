## JJCinemaBackend
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

| 이름 | 담당 | 
| --- | --- | 
| 정해원 | Back Developer |

## 기술 스택
> JAVA 21/ SPRING BOOT 4.1.0 / PostgresSQL/GRADLE/SPRING DATA JPA/ Spring Security (추후 수정)

## 문서/협업
> GIT/GITHUB/NOTION/POSTMAIN (추후 수정)

## ERD
<img width="1708" height="1029" alt="image" src="https://github.com/user-attachments/assets/284f8d62-d4b3-4d4a-bfac-bda1aa60766e" />
[ERD 링크](https://www.erdcloud.com/d/XnissnvXBK8n68nhE)

## 엔티티 설계 컨벤션
- 엔티티 생성은 Lombok `@Builder` 대신 **정적 팩토리 메서드**(`static create(...)`)를 사용


## Convention
> 필요할까요? 
## 브랜치 구조

```
main
 └── develop
       ├── feature/BE-login
       ├── feature/FE-fridge-list
       ├── fix/BE-recipe-null
       └── hotfix/FE-token-expire
```

| 브랜치 | 용도 | 병합 대상 |
| --- | --- | --- |
| `main` | 최종 배포 브랜치 | — |
| `develop` | 통합 개발 브랜치 | `main` |
| `feature/*` | 기능 개발 | `develop` |
| `fix/*` | 버그 수정 | `develop` |
| `hotfix/*` | 긴급 수정 (main 직접) | `main`, `develop` |
| `docs/*` | 문서 작업 | `develop` |
| `refactor/*` | 리팩토링 | `develop` |
## 브랜치 명명 규칙
> [타입]/[BE 또는 FE]-[기능명]
- 소문자 + 하이픈(`-`) 사용
- BE/FE 접두사로 파트 구분 필수
- 기능명은 영어, 간결하게
## Commit Convention
## 커밋 타입

| 타입 | 설명 | 예시 |
| --- | --- | --- |
| `feat` | 새 기능 추가 | `feat: 장보기 목록 추가 기능` |
| `fix` | 버그 수정 | `fix: 로그인 실패 시 메시지 미표시 수정` |
| `refactor` | 기능 변경 없는 코드 개선 | `refactor: UserService 메서드 분리` |
| `docs` | 문서 수정 | `docs: README 환경변수 설명 추가` |
| `test` | 테스트 코드 | `test: 재료 등록 서비스 단위 테스트` |
| `chore` | 빌드 설정, 의존성 변경 | `chore: QueryDSL 의존성 추가` |
| `design` | UI/디자인 변경 | `design: 홈 화면 냉파 점수 UI 수정` |
| `style` | 포맷, 세미콜론 등 (로직 무관) | `style: import 정렬` |


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
## 파일/폴더 명명 규칙

## 공통

| 항목 | 규칙 | 예시 |
| --- | --- | --- |
| 패키지/디렉토리 | 소문자, 단수 | `ingredient`, `recipe` |
| 환경설정 파일 | 소문자 + 하이픈 | `application-dev.yml` |
| SQL 파일 | 대문자 + 언더스코어 | `V1__CREATE_INGREDIENT_TABLE.sql` |

---

## Java / Spring Boot 코드 컨벤션

## 명명 규칙

| 대상 | 규칙 | 예시 |
| --- | --- | --- |
| 클래스 | PascalCase | `IngredientService`, `RecipeController` |
| 메서드/변수 | camelCase | `findByExpiryDate()`, `userId` |
| 상수 | UPPER_SNAKE_CASE | `MAX_EXPIRY_DAY` |
| 패키지 | 소문자, 단수 | `com.naengpa.ingredient` |
| DB 엔티티 | PascalCase + `Entity` 제거 가능 | `Ingredient`, `Recipe` |

---

## 예외 처리

- `RuntimeException` 직접 사용 금지 → 커스텀 예외 클래스 사용
- `@RestControllerAdvice`로 전역 예외 처리 통일
- 예외 메시지는 한국어 허용 (서비스 특성상)

```java
// 커스텀 예외 예시
public class IngredientNotFoundException extends RuntimeException {
    public IngredientNotFoundException(Long id) {
        super("재료를 찾을 수 없습니다. id=" + id);
    }
}
```

# 보안 파일 관리 규칙
- `.env`, `application-secret.yml`, 개인 API 키는 **절대 커밋 금지**
- `.gitignore`에 반드시 추가:

```
# 백엔드
application-secret.yml
application-local.yml
*.key

# 프론트엔드
.env
.env.local
.env.development.local
.env.production.local
```

- 환경변수 예시 파일(`application-secret.yml.example`, `.env.example`)을 커밋하여 팀원 공유
- 실수로 민감 정보가 커밋된 경우: **즉시 팀장에게 알리고 키 폐기**


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

