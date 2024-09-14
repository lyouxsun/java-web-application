package controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.HttpRequest;
import webserver.HttpResponse;
import webserver.RequestHandler;

public abstract class AbstractController implements Controller {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    @Override
    public void service(HttpRequest request, HttpResponse response) {
        log.info("[" + this.getClass().getSimpleName() + "] service");
        if (request.getMethod().equals("GET")) {
            doGet(request, response);
        } else if (request.getMethod().equals("POST")) {
            doPost(request, response);
        }
    }

    abstract void doPost(HttpRequest request, HttpResponse response);

    abstract void doGet(HttpRequest request, HttpResponse response);
}
