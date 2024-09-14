package controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import repository.MemoryMemberRepository;
import webserver.HttpRequest;
import webserver.HttpResponse;
import webserver.RequestHandler;

public class HomeController extends AbstractController {

    @Override
    void doPost(HttpRequest request, HttpResponse response) {
        return;
    }

    @Override
    void doGet(HttpRequest request, HttpResponse response) {
        String url = request.getUrl();
        response.forward(url);
    }
}
