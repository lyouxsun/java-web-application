package controller;

import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import repository.MemoryMemberRepository;
import webserver.HttpRequest;
import webserver.HttpResponse;
import webserver.RequestHandler;

public class LoginController extends AbstractController{
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);
    private final MemoryMemberRepository repository;

    public LoginController(){
        repository = MemoryMemberRepository.getInstance();
    }

    @Override
    void doPost(HttpRequest request, HttpResponse response) {
        log.info("[LoginController] service");
        User user = repository.findUserById(request.getParam("userId"));
        if (user != null && user.getPassword().equals(request.getParam("password"))) {
            log.info("[login] " + user.toString());
            response.addHeader("Cookie", "logined=true");
            response.sendRedirect("/index.html");
        }
        response.addHeader("Cookie", "logined=false");
        response.sendRedirect("/user/login_failed.html");
    }

    @Override
    void doGet(HttpRequest request, HttpResponse response) {

    }
}
