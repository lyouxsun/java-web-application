## 요구사항 1: index.html 반환하기

### WebServer 클래스
- 웹 서버를 시작한다.
- 사용자의 요청이 있을 때까지 기다리다가 요청이 들어오면 RequeestHandler에게 요청 처리를 위임한다.
- `listenSocket`이라는 **환영 소켓** 객체를 통해서 통신을 받아들이고, 연결이 되었다면 실질적인 **연결 소켓**인 `connection` 객체를 만들어서 통신한다.

### inputStream을 읽는 방법
#### 1. InputStream 의 `read()` 메서드 사용하기

```java
    import java.io.InputStream;

    InputStream in = connection.getInputStream();
    in.read();
```
- 1byte 씩 읽으며 아스키코드값 (int) 을 반환한다.
- inputStream은 바이트 스트림으로 작동하기 때문에 데이터를 바이트 단위로만 읽을 수 있다. 텍스트 데이터를 읽으려면 바이트를 문자로 변환하는 과정이 필요하다. 

  이 때 InputStreamReader를 사용하면 바이트 데이터를 문자 데이터로 변환할 수 있다. 
  여기에 BufferedReader를 사용해 inputStreamReader를 감싸면 문자 데이터를 라인 단위로 효율적으로 읽을 수 있다.
```java
    import java.io.InputStreamReader;
    import java.io.BufferedReader;
    
    BufferedReader br = new BufferedReader(new InputStreamReader(in));
    String line = br.readLine();
```

<details>
  <summary>로그를 통해 inputstream 의 데이터 확인하기</summary>
        
    GET /index.html HTTP/1.1
    Host: localhost:8080
    Connection: keep-alive
    Cache-Control: max-age=0
    sec-ch-ua: "Not)A;Brand";v="99", "Google Chrome";v="127", "Chromium";v="127"
    sec-ch-ua-mobile: ?0
    sec-ch-ua-platform: "macOS"
    Upgrade-Insecure-Requests: 1
    User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/127.0.0.0 Safari/537.36
    Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7
    Sec-Fetch-Site: none
    Sec-Fetch-Mode: navigate
    Sec-Fetch-User: ?1
    Sec-Fetch-Dest: document
    Accept-Encoding: gzip, deflate, br, zstd
    Accept-Language: ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7
    Cookie: JSESSIONID=27061636078BB70965CFFA4B05465CE6

</details>

### 파일을 responseBody에 담는 방법
- 요구사항 1에서는 사용자가 `/index.html` 이나 `/` url을 GET 요청하면 index.html을 응답하였다.
  이 때 index.html을 `responseBody()` 메서드에 byte 배열 형태로 담아줘야 했는데, 파일을 어떻게 배열로 변환할까?

```java
    import java.io.File;
    import java.nio.file.Path;

    Path path = new File("./webapp/index.html").toPath();
    byte[] body = Files.readAllBytes(path);
```
- File 클래스와 Files 클래스의 차이
    - File 클래스
        - 파일 또는 디렉터리를 생성, 삭제, 이름 변경 및 검색하는 메서드를 제공
        - I/O 작업에 대한 간단한 인터페이스를 제공하기 때문에 다양한 작업을 하기 위해선 메서드를 직접 구현해야 한다.
    - Files 클래스
        - 파일 복사, 이동, 삭제, 디렉터리 생성, 파일 읽기/쓰기 등의 다양한 기능 제공
        - Files 클래스는 정적 메서드로 구성되어 있어 객체를 만들 수가 없다.
        - Files 클래스의 메서드들은 Path 객체를 파라미터로 받는다.

### 주의
- 데이터를 읽어드릴때 조심해야할 점은 br.readline을 무심코 하면 뒤에 /r/n으로 끝나지 않는다면 서버에서 계속 데이터가 들어오는 것을 기다려 broken pipe 오류가 나는 것을 확인할 수 있다.
- 즉, /r/n으로 분리되어 있지 않은 body의 내용은 꼭! IOUtils에 있는 것 처럼 contentLength와 함께 넘겨주어 읽어야만 정확히 데이터를 읽어올 수 있다.
```java
    public class IOUtils {
        public static String readData(BufferedReader br, int contentLength) throws IOException {
            char[] body = new char[contentLength];
            br.read(body, 0, contentLength);
            return String.copyValueOf(body);
        }
    }
```