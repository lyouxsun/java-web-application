package controller;

import webserver.httpMessageDto.HttpRequest;
import webserver.httpMessageDto.HttpResponse;

public interface Controller {
    void service(HttpRequest request, HttpResponse response);
}
