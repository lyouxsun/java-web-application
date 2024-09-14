package webserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import static util.HttpRequestUtils.parseCookies;
import static util.HttpRequestUtils.parseQueryString;
import static util.IOUtils.readData;

public class HttpRequest {

    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private final Map<String, String> headers = new HashMap<>();
    private Map<String, String> params = new HashMap<>();
    private String body;           // GET은 body가 없을 수도 있으니 final (X)
    private String path;
    private final String url;
    private final String method;

    public HttpRequest(InputStream inputStream) throws IOException {

        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        String[] firstline = br.readLine().split(" ");
        this.method = firstline[0];
        this.url = firstline[1];

        log.info("[" + method + "] " + url + " 요청");
        if (url.contains("?")){
            String[] strings = url.split("\\?");
            this.path = strings[0];
            this.params = parseQueryString(strings[1]);
        } else{
            this.path = url;
            this.params = new HashMap<>();
        }

        // 헤더 저장
        while (true) {
            String line = br.readLine();
            if (line.isEmpty()) {           // line.trim().isEmpty() 사용 가능
                break;
            }
            String[] pair = line.split(": ");
            headers.put(pair[0], pair[1]);
        }

        // body 저장
        if (headers.get("Content-Length") != null) {
            int bodyLength = Integer.parseInt(headers.get("Content-Length"));
            this.body = readData(br, bodyLength);
            this.params = parseQueryString(body);
        }
    }

    // request header를 반환하는 메서드
    public String getHeader(String key) {
        log.info("[getHeader] " + key);
        return headers.get(key);
    }

    public String getBody() {
        log.info("[getBody] " + body);
        return body;
    }

    public String getMethod() {
        log.info("[getMethod] " + method);
        return method;
    }

    public String getUrl() {
        log.info("[getUrl] " + url);
        return url;
    }

    public String setPath(String path) {
        log.info("[setUrl] " + path);
        this.path = path;
        return path;
    }

    public String getPath() {
        log.info("[getPath] " + path);
        return path;
    }

    public String getParam(String key) {
        log.info("[getParam] " + key);
        return params.get(key);
    }

    public boolean isLogin() {
        String cookieValue = headers.get("Cookie");
        if (cookieValue == null) {
            return false;
        }
        Map<String, String> cookies = parseCookies(cookieValue);
        return Boolean.parseBoolean(cookies.get("logined"));        // 파라미터의 문자열이 null -> false 반환
    }

}
