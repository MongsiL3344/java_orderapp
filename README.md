# OrderApp

Java Swing 클라이언트와 Spring Boot 백엔드로 구성한 배달 주문 프로그램입니다.

## 프로젝트 구조

```text
orderapp/
├── backend/       # Spring Boot 백엔드
├── swing-client/  # Java Swing GUI 클라이언트
└── settings.gradle
```

## 실행 순서

먼저 백엔드를 실행합니다.

```bash
cd backend
./gradlew bootRun
```

다른 터미널에서 Swing 클라이언트를 실행합니다.

```bash
cd /Users/wkdgustjr/Dev/orderapp
./backend/gradlew -p . :swing-client:run
```

## 주요 기능

- 백엔드가 H2 데이터베이스를 JDBC로 관리합니다.
- 서버 시작 시 `backend/src/main/resources/data/menus.json`의 메뉴 데이터를 DB에 저장합니다.
- Swing 화면은 `GET /api/menus`로 메뉴 목록을 불러옵니다.
- 주문하기 버튼을 누르면 주문서 JSON을 `POST /api/orders`로 전송합니다.
- 백엔드는 주문 JSON을 DB에 저장합니다.

## API

```text
GET  /api/menus
POST /api/orders
GET  /api/orders
```

## H2 콘솔

백엔드 실행 후 아래 주소에서 H2 콘솔을 확인할 수 있습니다.

```text
http://localhost:8080/h2-console
```

접속 정보는 다음과 같습니다.

```text
JDBC URL: jdbc:h2:file:./data/orderapp-db
User Name: sa
Password:
```

## 메뉴 데이터 수정

기본 메뉴는 아래 파일에서 관리합니다.

```text
backend/src/main/resources/data/menus.json
```

이미 DB에 메뉴가 들어간 상태에서는 JSON을 수정해도 자동으로 덮어쓰지 않습니다. 개발 중 메뉴 데이터를 다시 반영하려면 백엔드를 종료한 뒤 `backend/data` 폴더를 삭제하고 다시 실행하면 됩니다.

## 검증

```bash
./backend/gradlew -p . :backend:test :swing-client:compileJava
```
