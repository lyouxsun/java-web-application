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
- [x] 요구사항 1에서 "/" url만 처리하는 메서드가 다른 메서드를 처리하도록 수정
- [x] 쿼리 파라미터에 있는 정보를 파싱하는 방법 알아보기 (? 앞부분은 버리고 -> &을 기준으로 파싱 -> =을 기준으로 다시 파싱)
- [x] 파싱하여 받은 데이터를 User 객체에 저장하기
- [x] 생성한 User객체 repository에 저장하기
- [x] User 객체에 데이터 저장한 후에 / 화면으로 돌아오도록 url 바꿔주기
  -> 계속 오류가 나서 200이 아니라 302 status code로 처리해줌

[요구사항 2 학습 내용](/Requirement_2)

### 요구사항 3, 4 - post 방식으로 회원가입 후 redirect 방식으로 이동
- [x] "/user/create" 이 포함된 url은 헤더를 모두 읽으며 Content-Length 정보를 얻고, 이를 통해 body를 읽는 작업을 수행한다.

  (request body를 읽을 때에는 기존에 구현되어 있는 IOUtils 클래스의 `readData()` 메서드를 사용한다.)
- [x] 회원가입 처리를 모두 마친 뒤 홈으로 redirect 처리를 한다. `redirect()`

  `response302Header()`를 통해 response의 상태코드를 302로 설정하고, body에 인자로 받은 url의 html 파일을 넣어 클라이언트에게 전송한다.

### 요구사항 5 - 로그인하기 with cookie
- [x] request body에서 정보를 읽는 과정이 회원가입 처리와 똑같아서 이 부분은 같이 사용하고, 이후 부분만 메서드를 통해 분리한다.

  http method가 POST인 것은 content length 정보와 body 내용을 읽고, 이후 url 분기문을 통해 `signup()`, `login()`을 분리했다.
- 아이디와 비밀번호를 사용해서 repository에 저장된 user를 조회 결과에 따라 `addCookieAndRedirect()` 파라미터에 true/false를 넘겨준다.
- true/false를 response header의 Set-Cookie 필드에 추가한다. 그러면 다음 request 부터는 헤더의 Cookie 필드에 쿠키가 세팅된 것을 확인할 수 있다.

  이 쿠키의 boolean 값으로 로그인 유무를 판단한다.

### 요구사항 6 - 사용자 목록 출력하기
- 쿠키의 login값이 true인 경우에만 사용자 목록을 출력하도록 하고, 회원이 아닌 경우 로그인 페이지로 이동시킨다.
- java의 string builder를 사용하여 html을 모두 저장한다.
- sb를 byte array로 변환한 후 data output stream에 `write()` 한 후, `flush()` 하면 화면에 잘 나온다.

  (list.html 파일을 string builder에 모두 저장해야겠지만,, 너무 양이 많아서 user 목록만 보여주도록 변경했다.

  이러한 단점을 보완하기 위해 서블릿, JSP가 나왔다는 것만 상기하자!)

### 요구사항 7 - css 적용하기
- css의 url을 dos에 적어 response body에 담아줘야 한다!
- 요청 url의 확장자가 css이거나 요청 헤더가 `Accept: text/css,*/*` 인 경우 -> 응답 헤더를 `Content-Type: text/css`로 해야한다.
