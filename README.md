# CMS REST API

## 프로젝트 개요
- CMS(Content Management System) REST API 구현 과제
- Java 25, Spring Boot 4, Spring Security, JPA, H2 Database, Lombok 사용
- Postman으로 API 테스트 및 문서화

## 실행 방법
1. JDK 25 설치
2. H2 콘솔: http://localhost:8080/h2-console
 - Driver Class: org.h2.Driver
 - JDBC URL: jdbc:h2:file:./data/cmsdb
 - User Name: admin
 - Password: 1234
 3. DB 초기 세팅 구문 입력 필요 (DB 파일은 backend/data/cmsdb.mv.db 에 저장될 예정이고, 혹시 몰라 github 에 해당 파일 따로 첨부 했습니다. DB 입력 구문은 밑의 DB 스키마 아래에 있습니다.)

## H2 DB 파일 위치
- DB 파일 경로: `backend/data/cmsdb.mv.db`
  - application.yml의 설정(`jdbc:h2:file:./data/cmsdb`)
- 파일 기반 DB, 서버를 재시작해도 데이터가 유지


## 주요 기능
- 콘텐츠 CRUD (생성, 목록 조회(페이징 처리), 상세, 수정, 삭제)
- Spring Security, JWT 기반 로그인 (ADMIN/USER)
- 작성자 본인만 수정/삭제, ADMIN은 전체 수정/삭제 가능
- 예외 처리 및 표준 응답 구조

## DB 스키마 (Contents)
| 컬럼명             | 설명         | 타입                |
|--------------------|--------------|---------------------|
| id                 | 고유 아이디  | bigint, PK, not null|
| title              | 제목         | varchar(100), not null|
| description        | 내용         | text                |
| view_count         | 조회수       | bigint, not null    |
| created_date       | 생성일       | timestamp           |
| created_by         | 생성자       | varchar(50), not null|
| last_modified_date | 수정일       | timestamp           |
| last_modified_by   | 수정자       | varchar(50)         |

## DB 스키마 (Users)
| 컬럼명     | 설명       | 타입                        |
|------------|------------|-----------------------------|
| uid        | 고유 아이디| bigint, PK, not null, auto_increment |
| id         | 로그인 ID  | varchar(50), not null, unique|
| password   | 비밀번호   | varchar(255), not null      |
| nickname   | 닉네임     | varchar(50), not null       |
| role       | 권한       | varchar(20), not null       |
| create_at  | 가입일시   | timestamp, default current_timestamp |
| update_at  | 수정일시   | timestamp, default current_timestamp, on update current_timestamp |

## DB 초기 세팅 필요 구문
```sql
CREATE TABLE IF NOT EXISTS users (
    uid BIGINT AUTO_INCREMENT PRIMARY KEY,
    id VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    nickname VARCHAR(50) NOT NULL,
    role VARCHAR(20) NOT NULL,
    create_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,      -- 가입일시: 자동 입력
    update_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP -- 수정일시: 자동 입력 및 수정 시 갱신
);

CREATE TABLE IF NOT EXISTS contents (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(100) NOT NULL,
    description TEXT,
    view_count BIGINT NOT NULL DEFAULT 0,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,   -- 생성일시: 자동 입력
    created_by VARCHAR(50) NOT NULL,
    last_modified_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, -- 수정일시: 자동 입력 및 수정 시 갱신
    last_modified_by VARCHAR(50)
);

INSERT INTO users (id, password, nickname, role)
VALUES
  ('admin',  '$2a$10$lDKSQKEXy9QXV1I5mnAr1uPQBwSQ94asIoYR7OjdnMMYl9T21OnaC', '관리자', 'ADMIN'),
  ('user01', '$2a$10$lDKSQKEXy9QXV1I5mnAr1uPQBwSQ94asIoYR7OjdnMMYl9T21OnaC', '유저01', 'USER'),
  ('user02', '$2a$10$lDKSQKEXy9QXV1I5mnAr1uPQBwSQ94asIoYR7OjdnMMYl9T21OnaC', '유저02', 'USER');
```

### 1. 콘텐츠 생성
- `POST /api/v1/contents/write`
- RequestBody: { title, description }
- 인증 필요

### 2. 콘텐츠 목록 (페이징)
- `GET /api/v1/contents/list?page=0&size=20&sort=createdDate,desc`
- Response: page, size, totalElements, totalPages, content([...])

### 3. 콘텐츠 상세
- `GET /api/v1/contents/{id}`

### 4. 콘텐츠 수정
- `PATCH /api/v1/contents/{id}`
- 본인/ADMIN만 가능

### 5. 콘텐츠 삭제
- `DELETE /api/v1/contents/{id}`
- 본인/ADMIN만 가능

### 6. 로그인
- Spring Security 기반, JWT

## 사용한 도구 및 참고 자료
- Postman: API 테스트 및 문서화
- GitHub Copilot: jwt 및 spring security, application.yml 초기 세팅
- 기존 국비 학원에서 했던 팀프로젝트 자료 참고

## 추가 구현/특이사항
- 예외 처리: GlobalExceptionHandler로 일관된 에러 응답
- 페이징: Spring Data JPA Pageable, PageResponseDTO 사용
- API 를 ApiResponse 로 감싸 공통적인 형태 유지.
    {
        "status": 200,
        "date": "2026-03-19T07:52:18.592377900Z",
        "content": {
            
        }
    }

## REST API 문서 (Postman)
- https://documenter.getpostman.com/view/48175457/2sBXihqsmS
