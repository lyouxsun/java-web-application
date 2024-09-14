import org.junit.Test;
import webserver.httpMessageDto.HttpRequest;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import static org.junit.Assert.assertEquals;
import static webserver.httpMessageDto.HttpMethod.GET;
import static webserver.httpMessageDto.HttpMethod.POST;

public class HttpRequestTest {
    private static final String TEST_DIRECTORY = "./src/test/resources/";

    @Test
    public void request_GET() throws Exception {
        InputStream in = new FileInputStream(new File(TEST_DIRECTORY + "Http_GET.txt"));
        HttpRequest request = new HttpRequest(in);

        assertEquals(GET, request.getMethod());
        assertEquals("/user/create", request.getPath());
        assertEquals("keep-alive", request.getHeader("Connection"));
        assertEquals("lyouxsun", request.getParam("userId"));
        assertEquals("localhost:8080", request.getHeader("Host"));
    }

    @Test
    public void request_POST() throws Exception {
        InputStream in = new FileInputStream(new File(TEST_DIRECTORY + "Http_POST.txt"));
        HttpRequest request = new HttpRequest(in);

        assertEquals(POST, request.getMethod());
        assertEquals("/user/create", request.getPath());
        assertEquals("keep-alive", request.getHeader("Connection"));
        assertEquals("lyouxsun", request.getParam("userId"));
        assertEquals("localhost:8080", request.getHeader("Host"));
    }
}
