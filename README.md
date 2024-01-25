# 실습을 위한 개발 환경 세팅
* 출처 : https://github.com/slipp/web-application-server

# 웹 서버 시작 및 테스트
* webserver.WebServer 는 사용자의 요청을 받아 RequestHandler에 작업을 위임하는 클래스이다.
* 사용자 요청에 대한 모든 처리는 RequestHandler 클래스의 run() 메서드가 담당한다.
* WebServer를 실행한 후 브라우저에서 http://localhost:8080으로 접속해 "Hello World" 메시지가 출력되는지 확인한다.

# 각 요구사항별 학습 내용 정리
* 구현 단계에서는 각 요구사항을 구현하는데 집중한다.
* 구현을 완료한 후 구현 과정에서 새롭게 알게된 내용, 궁금한 내용을 기록한다.
* 각 요구사항을 구현하는 것이 중요한 것이 아니라 구현 과정을 통해 학습한 내용을 인식하는 것이 배움에 중요하다.

### 요구사항 1 - http://localhost:8080/index.html로 접속시 응답
- [x] Request Header가 어떻게 생겼는지 파악하기 위해 모두 출력해본다.
- [x] Request Header의 첫번째 줄에서 HTTP method와 요청 url을 구분하여 저장해야 한다.
- [x] `GET /index.html`  요청을 처리할 메서드를 만든다.
- [x] css가 적용되지 않는 문제 해결하기

[요구사항 1 학습 내용](/Requirement_1)

### 요구사항 2 - get 방식으로 회원가입
- [ ] 요구사항 1에서 "/" url만 처리하는 메서드가 다른 메서드를 처리하도록 수정
- [ ] 쿼리 파라미터에 있는 정보를 파싱하는 방법 알아보기 (? 앞부분은 버리고 -> &을 기준으로 파싱 -> =을 기준으로 다시 파싱)
- [ ] 파싱하여 받은 데이터를 User 객체에 저장하기
- [ ] 생성한 User객체 repository에 저장하기

### 요구사항 3 - post 방식으로 회원가입
*

### 요구사항 4 - redirect 방식으로 이동
*

### 요구사항 5 - cookie
*

### 요구사항 6 - stylesheet 적용
*

### heroku 서버에 배포 후
*
