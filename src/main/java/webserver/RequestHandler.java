package webserver;

import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import repository.MemoryMemberRepository;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.List;

public class RequestHandler extends Thread {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private final Socket connection;
    private final MemoryMemberRepository repository;
    private HttpRequest request;
    private HttpResponse response;

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
        repository = MemoryMemberRepository.getInstance();
    }

    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(), connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            request = new HttpRequest(in);
            response = new HttpResponse(out);

            String url = request.getUrl();
            String method = request.getMethod();


            if (url.contains(".css")) {
                response.css(request.getPath());        // TODO 7. CSS 적용하기
                return;
            }

            if (method.equals("POST")) {
                if (url.equals("/user/create")) {
                    signup();       // TODO 3, 4 - post 방식으로 회원가입 후 redirect 방식으로 이동
                } else if (url.equals("/user/login")) {
                    login();        // TODO 5. 로그인하기
                }
            }

            if (url.equals("/user/list")) {
                boolean logined = request.isLogin();

                if (logined) {        // 로그인 된 상태
                    log.info("cookies의 logined 값 = true");
                    List<User> users = repository.findAll();
                    response.userList(users);       // TODO 6. 로그인 여부 확인 후 회원 목록 출력하기
                    return;
                }
                log.info("cookies의 logined 값 = false");
                response.addHeader("Cookie", "logined=false");
                response.sendRedirect("/user/login.html");          // 로그인 안된 상태
            }
            response.forward(url);      // url.equals("/") || url.equals("/index.html") 인 경우 등이 여기에 포함됨

        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }


    private void signup() {
        User user = new User(request.getParam("userId"), request.getParam("password"),
                request.getParam("name"), request.getParam("email"));
        repository.addUser(user);
        response.addHeader("Cookie", "logined=true");
        response.sendRedirect("/index.html");
    }

    private void login() {
        User user = repository.findUserById(request.getParam("userId"));
        if (user != null && user.getPassword().equals(request.getParam("password"))) {
            log.info("[login] " + user.toString());
            response.addHeader("Cookie", "logined=true");
            response.sendRedirect("/index.html");
        }
        response.addHeader("Cookie", "logined=false");
        response.sendRedirect("/user/login_failed.html");
    }


}