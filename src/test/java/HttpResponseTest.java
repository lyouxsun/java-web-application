import org.junit.Test;
import webserver.httpMessageDto.HttpResponse;

import java.io.*;
import java.nio.file.Files;

public class HttpResponseTest {
    private static final String TEST_DIRECTORY = "./src/test/resources/";

    @Test
    public void responseForward() throws Exception {
        // forward를 하기 위해선 response body에 index.html이 포함되어 있어야 한다.
        HttpResponse response = new HttpResponse(createOutputStream("Http_Forward.txt"));
        response.forward("/index.html");
    }

    @Test
    public void responseRedirect() throws Exception {
        // redirect를 보내기 위해선 response header에 Location: redirect_url 이 포함되어 있어야 한다.
        HttpResponse response = new HttpResponse(createOutputStream("Http_Redirect.txt"));
        response.addHeader("Cookie", "logined=false");
        response.sendRedirect("/index.html");

    }

    @Test
    public void responseCookies() throws Exception{
        HttpResponse response = new HttpResponse(createOutputStream("Http_Cookies.txt"));
        response.addHeader("SetCookie", "logined=true");
        response.sendRedirect("/index.html");
    }

    private OutputStream createOutputStream(String fileName) throws IOException {
        return Files.newOutputStream(new File(TEST_DIRECTORY + fileName).toPath());
    }
}
