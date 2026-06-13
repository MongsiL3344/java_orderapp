# OrderApp

Java Swing 클라이언트와 Spring Boot 백엔드로 구성한 배달 주문 프로그램입니다.

앱을 실행하면 첫 화면에서 사용자 역할을 선택합니다.

- `손님이에요`: 메뉴를 보고 장바구니에 담아 주문하는 주문창으로 이동합니다.
- `사장님이에요`: 메뉴를 관리하고 들어온 주문을 확인하는 매장관리창으로 이동합니다.

## 프로젝트 구조

```text
orderapp/
├── backend/       # Spring Boot 백엔드
├── swing-client/  # Java Swing GUI 클라이언트
└── settings.gradle
```

## 주요 기능

### 손님 주문창

- `GET /api/menus`로 메뉴 목록을 불러옵니다.
- 메뉴를 장바구니에 담고 수량을 변경할 수 있습니다.
- 이름, 전화번호, 주소를 입력한 뒤 주문할 수 있습니다.
- 주문하기 버튼을 누르면 주문서 JSON을 `POST /api/orders`로 전송합니다.

### 사장님 매장관리창

- `메뉴 관리` 탭에서 메뉴 목록을 확인합니다.
- 메뉴를 추가, 수정, 삭제할 수 있습니다.
- `주문 내역` 탭에서 들어온 주문 목록을 확인합니다.
- 주문을 선택하면 주문자 정보, 총액, 접수 시각, 주문 메뉴를 확인할 수 있습니다.

### 백엔드

- H2 데이터베이스를 JDBC로 관리합니다.
- 처음 실행해서 메뉴가 없으면 빈 메뉴 목록으로 시작합니다.
- 메뉴 CRUD API와 주문 생성/조회 API를 제공합니다.
- 주문 원본 JSON을 DB에 저장해 사장님 화면에서 상세 내용을 확인할 수 있게 합니다.

## 실행 순서

먼저 백엔드를 실행합니다.

```bash
cd backend
./gradlew bootRun
```

다른 터미널에서 Swing 클라이언트를 실행합니다.

```bash
cd swing-client
./gradlew run
```

기본 서버 URL은 `http://localhost:8080`입니다. 손님 주문창과 사장님 매장관리창 모두 화면에서 서버 URL을 수정할 수 있습니다.

## API

```text
GET    /api/menus          # 메뉴 목록 조회
POST   /api/menus          # 메뉴 추가
PUT    /api/menus/{id}     # 메뉴 수정
DELETE /api/menus/{id}     # 메뉴 삭제

POST   /api/orders         # 주문 생성
GET    /api/orders         # 주문 목록 조회
```

메뉴 추가/수정 요청 본문 예시는 다음과 같습니다.

```json
{
  "name": "후라이드 치킨",
  "price": 18000,
  "description": "바삭한 기본 치킨"
}
```

주문 생성 요청 본문 예시는 다음과 같습니다.

```json
{
  "customerName": "홍길동",
  "phoneNumber": "010-0000-0000",
  "address": "서울시",
  "orderedAt": "2026-06-13T12:00:00Z",
  "totalPrice": 20000,
  "items": [
    {
      "menuName": "후라이드 치킨",
      "price": 18000,
      "quantity": 1
    },
    {
      "menuName": "콜라",
      "price": 2000,
      "quantity": 1
    }
  ]
}
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

## 메뉴 데이터 관리

메뉴는 사장님 매장관리창의 `메뉴 관리` 탭이나 메뉴 CRUD API로 등록, 수정, 삭제합니다. 처음 실행해서 DB에 메뉴가 없으면 손님 주문창과 사장님 메뉴 관리 화면에 `메뉴가 없어요` 문구가 표시됩니다.

메뉴 데이터는 H2 DB에 저장됩니다. 개발 중 메뉴와 주문 데이터를 모두 초기화하려면 백엔드를 종료한 뒤 `backend/data` 폴더를 삭제하고 다시 실행하면 됩니다.

## 오픈소스 사용 고지

이 프로젝트에서 직접 사용한 주요 오픈소스 라이브러리는 다음과 같습니다.

| 이름 | 버전 | 사용 위치 | 용도 | 라이선스 |
|---|---:|---|---|---|
| Spring Boot | 4.1.0 | backend | REST API, JDBC, JSON 처리, 서버 실행, H2 콘솔 | Apache License 2.0 |
| H2 Database | 2.4.240 | backend | 개발용 파일 기반 데이터베이스 | MPL 2.0 또는 EPL 1.0 |
| Jackson Databind | 2.21.2 | swing-client | 서버와 주고받는 JSON 직렬화/역직렬화 | Apache License 2.0 |
| JUnit Platform | 6.0.3 | backend test | 백엔드 테스트 실행 | Eclipse Public License 2.0 |

Java 표준 라이브러리, Gradle 빌드 도구, Spring Boot가 내부적으로 가져오는 전이 의존성은 별도 표기에서 제외했습니다.
