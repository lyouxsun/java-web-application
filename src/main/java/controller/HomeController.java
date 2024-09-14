package controller;

import webserver.httpMessageDto.HttpRequest;
import webserver.httpMessageDto.HttpResponse;

public class HomeController extends AbstractController {

    @Override
    public void doGet(HttpRequest request, HttpResponse response) {
        String url = request.getPath();
        response.forward(url);
    }
}
