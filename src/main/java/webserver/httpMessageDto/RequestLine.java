package webserver.httpMessageDto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.RequestHandler;

import java.util.Map;

import static util.HttpRequestUtils.parseQueryString;
import static webserver.httpMessageDto.HttpMethod.GET;
import static webserver.httpMessageDto.HttpMethod.toHttpMethod;

public class RequestLine {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private HttpMethod method;
    private String path;
    private Map<String, String> params;

    public RequestLine(String line) {
        log.info("request line = {}", line);
        String[] firstLine = line.split(" ");
        if (firstLine.length != 3) {
            log.error("invalid request line: {}", line);
        }

        this.method = toHttpMethod(firstLine[0]);
        String url = firstLine[1];

        if (method == GET && url.contains("?")) {
            String[] strings = url.split("\\?");
            this.path = strings[0];
            this.params = parseQueryString(strings[1]);
        } else {
            this.path = url;
        }
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public String setPath(String path) {
        this.path = path;
        return path;
    }

    public Map<String, String> getParams() {
        return params;
    }
}
