package webserver;

import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpResponse {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);
    private final DataOutputStream dos;
    private final Map<String, String> header = new HashMap<>();

    public HttpResponse(OutputStream outputStream) {
        this.dos = new DataOutputStream(outputStream);
    }

    public void addHeader(String key, String value) {
        if (header.containsKey(key)) {
            header.replace(key, header.get(key) + ";" + value);
        }
        header.put(key, value);
    }


    // GET 요청 시 응답 데이터 처리 메서드
    public void forward(String path) {
        try {
            log.info("[forward] " + path);
            Path filePath = new File("./webapp" + path).toPath();
            byte[] body = Files.readAllBytes(filePath);
            if (path.contains(".css")) {
                header.put("Content-Type", "text/css;charset=utf-8");
            } else {
                header.put("Content-Type", "text/html;charset=utf-8");
            }
            header.put("Content-Length", String.valueOf(body.length));

            response200Header();
            responseBody(body);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void responseBody(byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }


    // 응답 헤더 처리 메서드 (redirect, 200, 302)
    private void response200Header() {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            for (String key : header.keySet()) {
                dos.writeBytes(key + ": " + header.get(key) + "\r\n");
            }
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }


    public void sendRedirect(String path) {
        log.info("[sendRedirect] redirect to " + path);
        header.put("Content-Type", "text/html;charset=utf-8");
        header.put("Location", path);
        try {
            dos.writeBytes("HTTP/1.1 302 FOUND \r\n");
            for (String key : header.keySet()) {
                dos.writeBytes(key + ": " + header.get(key) + "\r\n");
            }
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    public void userList(List<User> users) {
        StringBuilder sb = new StringBuilder();
        int index = 1;
        sb.append("<div class=\"container\" id=\"main\">\n");
        sb.append("   <div class=\"col-md-10 col-md-offset-1\">\n");
        sb.append("      <div class=\"panel panel-default\">\n");
        sb.append("          <table class=\"table table-hover\">\n");
        sb.append("              <thead>\n");
        sb.append("                <tr>\n");
        sb.append("                    <th>#</th> <th>사용자 아이디</th> <th>이름</th> <th>이메일</th><th></th>\n");
        sb.append("                </tr>\n");
        sb.append("              </thead>\n");
        sb.append("              <tbody>\n");
        for (User user : users) {
            sb.append("<tr>");
            sb.append("<th scope=\"row\">").append(index++).append("</th>");
            sb.append("<td>").append(user.getUserId()).append("</td>");
            sb.append("<td>").append(user.getName()).append("</td>");
            sb.append("<td>").append(user.getEmail()).append("</td>");
            sb.append("<td><a href=\"#\" class=\"btn btn-success\" role=\"button\">수정</a></td>");
            sb.append("</tr>");
        }

        sb.append("              </tbody>\n");
        sb.append("          </table>\n");
        sb.append("        </div>\n");
        sb.append("    </div>\n");
        sb.append("</div>\n");
        sb.append("<script src=\"../js/jquery-2.2.0.min.js\"></script>\n");
        sb.append("<script src=\"../js/bootstrap.min.js\"></script>\n");
        sb.append("<script src=\"../js/scripts.js\"></script>\n");
        sb.append("\t</body>\n");
        sb.append("</html>");
        byte[] bytes = sb.toString().getBytes();

        header.put("Content-Length", String.valueOf(bytes.length));
        response200Header();
        responseBody(bytes);
    }
}
