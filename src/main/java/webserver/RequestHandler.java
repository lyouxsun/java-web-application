package webserver;

import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import repository.MemoryMemberRepository;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static util.HttpRequestUtils.parseCookies;
import static util.HttpRequestUtils.parseQueryString;

public class RequestHandler extends Thread {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private Socket connection;
    private MemoryMemberRepository repository;

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
        repository = MemoryMemberRepository.getInstance();
    }

    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(), connection.getPort());


        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {

            // TODO 1. index.html 반환하기
            HttpRequest request = new HttpRequest(in);
            DataOutputStream dos = new DataOutputStream(out);

            String url = request.getUrl();
            String method = request.getMethod();


            // TODO 7. CSS 적용하기
            if (url.contains(".css")) {
                Path path = new File("./webapp" + url).toPath();        // 요청 url ex) /css/bootstrap.min.css, /css/styles.css
                byte[] body = Files.readAllBytes(path);
                responseCssHeader(dos, body.length);
                responseBody(dos, body);
                return;
            }

            if (method.equals("POST")) {
                // TODO 3, 4 - post 방식으로 회원가입 후 redirect 방식으로 이동
                if (url.equals("/user/create")) {
                    signup(request, dos);
                }

                // TODO 5. 로그인하기
                else if (url.equals("/user/login")) {
                    login(request, dos);
                }
            }

            // TODO 6. 로그인 여부 확인 후 회원 목록 출력하기
            if (url.equals("/user/list")) {
                boolean logined = request.isLogin();

                if (logined) {        // 로그인 된 상태
                    log.info("cookies의 logined 값 = true");
                    List<User> users = repository.findAll();
                    StringBuilder sb = addUserList(users, dos);
                    byte[] responseBytes = sb.toString().getBytes();
                    response200Header(dos, responseBytes.length);
                    responseBody(dos, responseBytes);
                    return;
                }
                log.info("cookies의 logined 값 = false");
                response302HeaderWithCookie(dos, false, "/user/login.html");          // 로그인 안된 상태
            }
            responseResource(dos, method, url);      // url.equals("/") || url.equals("/index.html") 인 경우 등이 여기에 포함됨

        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private StringBuilder addUserList(List<User> users, DataOutputStream dos) {
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

        return sb;

    }


    private void signup(HttpRequest request, DataOutputStream dos) {
        User user = new User(request.getParam("userId"), request.getParam("password"),
                request.getParam("name"), request.getParam("email"));
        repository.addUser(user);
        response302HeaderWithCookie(dos, false, "/index.html");
    }

    private void login(HttpRequest request, DataOutputStream dos) {
        User user = repository.findUserById(request.getParam("userId"));
        log.info("[login] " + user.toString());
        if (user != null && user.getPassword().equals(request.getParam("password"))) {
            response302HeaderWithCookie(dos, true, "/index.html");
        }
        response302HeaderWithCookie(dos, false, "/user/login_failed.html");
    }

    // GET 요청 시 응답 데이터 처리 메서드
    private void responseResource(DataOutputStream dos, String method, String url) {
        log.debug(method + " " + url);
        Path path = new File("./webapp" + url).toPath();
        try {
            byte[] body = Files.readAllBytes(path);
            if (method.equals("GET")) {
                response200Header(dos, body.length);
                responseBody(dos, body);
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }


    // 응답 헤더 처리 메서드 (redirect, 200, 302)
    private void response200Header(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }


    private void response302HeaderWithCookie(DataOutputStream dos, boolean logined, String url) {
        log.debug("[response302HeaderWithCookie] login cookie=" + logined + " and redirect to " + url + "]");
        try {
            dos.writeBytes("HTTP/1.1 302 FOUND \r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Location: " + url + "\r\n");
            dos.writeBytes("Set-Cookie: logined=" + logined + " \r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    // 요청 url에 css가 포함된 경우 됨
    private void responseCssHeader(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/css;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

}