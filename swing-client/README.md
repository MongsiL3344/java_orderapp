# Swing Client

배달 주문 프로그램의 Java Swing GUI 클라이언트입니다.

## 실행 순서

1. Spring Boot 백엔드를 먼저 실행합니다.
2. 루트 디렉터리에서 Swing 클라이언트를 실행합니다.

```bash
./backend/gradlew -p . :swing-client:run
```

또는 `swing-client` 디렉터리에서 독립 프로젝트처럼 실행할 수 있습니다.

```bash
cd swing-client
../backend/gradlew run
```

## 백엔드 연동

주문하기 버튼을 누르면 기본적으로 아래 엔드포인트에 JSON 주문서를 전송합니다.

```text
POST http://localhost:8080/api/orders
```

GUI의 서버 URL 입력칸에서 주소를 바꿀 수 있습니다.
