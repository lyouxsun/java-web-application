package webserver.httpMessageDto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.RequestHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import static util.HttpRequestUtils.parseCookies;
import static util.HttpRequestUtils.parseQueryString;
import static util.IOUtils.readData;
import static webserver.httpMessageDto.HttpMethod.POST;

public class HttpRequest {

    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private final Map<String, String> headers = new HashMap<>();
    private Map<String, String> params;
    private RequestLine requestLine;

    public HttpRequest(InputStream inputStream) {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            String line = br.readLine();
            if (line == null) {
                return;
            }
            this.requestLine = new RequestLine(line);
            processRequestHeader(br);
            if (getMethod() == POST) {
                this.params = processRequestBody(br);
            } else {
                this.params = requestLine.getParams();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }



    private void processRequestHeader(BufferedReader br) {
        try {
            while (true) {
                String line = br.readLine();
                if (line.isEmpty()) {
                    return;
                }
                String[] strings = line.split(": ");
                headers.put(strings[0], strings[1]);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Map<String, String> processRequestBody(BufferedReader br) {
        try {
            String body = readData(br, Integer.parseInt(headers.get("Content-Length")));
            return parseQueryString(body);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // request header를 반환하는 메서드
    public String getHeader(String key) {
        return headers.get(key);
    }

    public HttpMethod getMethod() {
        return requestLine.getMethod();
    }

    public String setPath(String path) {
        return requestLine.setPath(path);
    }

    public String getPath() {
        if (requestLine.getPath().equals("/")) {
            return requestLine.setPath("/index.html");
        }
        return requestLine.getPath();
    }

    public String getParam(String key) {
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
