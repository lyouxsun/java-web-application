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

#### 2. InputStreamReader 의 `read()` 메서드 사용하기

```java
    import java.io.InputStreamReader;

    InputStreamReader reader = new InputStreamReader(in);
    char[] arr = new char[200];
    reader.read(arr);
```
- InputStreamReader를 생성할 때 파라미터로 InputStream을 넣어야 한다.
- `read()` 메서드의 파라미터에 char 배열을 넣어주면 읽은 내용을 거기에 써준다.
- 문제점 : char 배열의 크기만큼만 읽기 때문에 크기를 신경써서 배열을 생성해야 한다.

#### 3. BufferedReader 의 `readLine()` 메서드 사용하기

```java
    import java.io.BufferedReader;

    BufferedReader br = new BufferedReader(new InputStreamReader(in));
    String line = br.readLine();
```
- BufferedReader를 생성할 때에는 파라미터로 InputStreamReader를 넣어야 한다.
- `br.readLine()` 메서드를 통해서 String을 길이 제한 없이 **한 줄씩** 받을 수 있다.

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