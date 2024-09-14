package controller;

import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import repository.MemoryMemberRepository;
import webserver.httpMessageDto.HttpRequest;
import webserver.httpMessageDto.HttpResponse;
import webserver.RequestHandler;

import java.util.List;

public class UserListController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);
    private final MemoryMemberRepository repository;

    public UserListController() {
        repository = MemoryMemberRepository.getInstance();
    }


    @Override
    void doGet(HttpRequest request, HttpResponse response) {
        log.info("[UserListController] service");
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

    @Override
    void doPost(HttpRequest request, HttpResponse response) {

    }
}
