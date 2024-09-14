import org.junit.Test;
import webserver.httpMessageDto.RequestLine;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static webserver.httpMessageDto.HttpMethod.GET;
import static webserver.httpMessageDto.HttpMethod.POST;

public class RequestLineTest {
    @Test
    public void create_method(){
        RequestLine requestLine = new RequestLine("GET /index.html HTTP/1.1");
        assertEquals(GET, requestLine.getMethod());
        assertEquals("/index.html", requestLine.getPath());

        requestLine = new RequestLine("POST /index.html HTTP/1.1");
        assertEquals(POST, requestLine.getMethod());
        assertEquals("/index.html", requestLine.getPath());
    }

    @Test
    public void create_method_and_path(){
        RequestLine requestLine = new RequestLine("GET /user/create?userId=lyouxsun&password=password HTTP/1.1");
        assertEquals(GET, requestLine.getMethod());
        assertEquals("/user/create", requestLine.getPath());
        Map<String, String> params = requestLine.getParams();
        assertEquals("lyouxsun", params.get("userId"));
        assertEquals("password", params.get("password"));
    }
}
