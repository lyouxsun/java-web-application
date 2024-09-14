package webserver;

import controller.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class RequestHandler extends Thread {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private final Socket connection;
    private final Map<String, Controller> controllers = new HashMap<>();
    private HttpRequest request;
    private HttpResponse response;

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;

        controllers.put("/index.html", new HomeController());
        controllers.put("/user/login", new LoginController());
        controllers.put("/user/create", new SignupController());
        controllers.put("/user/list", new UserListController());

    }

    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(), connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            request = new HttpRequest(in);
            response = new HttpResponse(out);

            String path = request.getPath();
            Controller controller = controllers.get(path);

            if(controller == null) {
                path = getDefaultPath(path);
                response.forward(path);
            } else{
                controller.service(request, response);
            }

        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private String getDefaultPath(String path) {
        if (path.equals("/")) {
            return request.setPath("/index.html");
        }
        return path;
    }

}