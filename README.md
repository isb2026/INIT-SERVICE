# INIT-SERVICE

품목 및 마스터 데이터 초기화 서비스입니다.

## 기술 스택

- **Spring Boot**: 3.4.1
- **Java**: 17
- **Database**: MySQL (MariaDB)
- **Message Queue**: Kafka
- **Query Builder**: QueryDSL
- **API Documentation**: Swagger (SpringDoc OpenAPI)

## 주요 기능

- 품목(Item) 관리
- 루트 제품(RootProduct) 관리
- 터미널(Terminal) 관리
- 다국어 번역(Language) 관리
- Kafka를 통한 비동기 메시지 처리
- 멀티 테넌트 지원

## 실행 방법

### 로컬 환경
```bash
./gradlew bootRun --args='--spring.profiles.active=local'
```

### 포트
- Local: `8082`
- Dev/Prod: `8080`

### Context Path
`/init`

## API 문서

- Swagger UI: `http://localhost:8082/init/swagger-ui`
- API Docs: `http://localhost:8082/init/v3/api-docs`