package webserver;

import controller.*;

import java.util.HashMap;
import java.util.Map;

public class RequestMapping {
    private static Map<String, Controller> controllers = new HashMap<>();
    static{
        controllers.put("/index.html", new HomeController());
        controllers.put("/user/login", new LoginController());
        controllers.put("/user/create", new SignupController());
        controllers.put("/user/list", new UserListController());
    }
    public static Controller getController(String path){
        return controllers.get(path);
    }
}
