package controller;

import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import repository.MemoryMemberRepository;
import webserver.httpMessageDto.HttpRequest;
import webserver.httpMessageDto.HttpResponse;
import webserver.RequestHandler;

public class SignupController extends AbstractController{

    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);
    private final MemoryMemberRepository repository;

    public SignupController(){
        repository = MemoryMemberRepository.getInstance();
    }

    @Override
    public void doPost(HttpRequest request, HttpResponse response) {
        log.info("[SignupController] service");
        User user = new User(request.getParam("userId"), request.getParam("password"),
                request.getParam("name"), request.getParam("email"));
        repository.addUser(user);
        response.addHeader("Cookie", "logined=true");
        response.sendRedirect("/index.html");
    }

}
