package controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.httpMessageDto.HttpRequest;
import webserver.httpMessageDto.HttpResponse;
import webserver.RequestHandler;

import static webserver.httpMessageDto.HttpMethod.GET;
import static webserver.httpMessageDto.HttpMethod.POST;

public abstract class AbstractController implements Controller {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    @Override
    public void service(HttpRequest request, HttpResponse response) {
        log.info("[" + this.getClass().getSimpleName() + "] service");
        if (request.getMethod() == GET) {
            doGet(request, response);
        } else if (request.getMethod() == POST) {
            doPost(request, response);
        }
    }

    protected void doPost(HttpRequest request, HttpResponse response){}

    protected void doGet(HttpRequest request, HttpResponse response){}
}
