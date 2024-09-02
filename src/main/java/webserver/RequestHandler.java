package webserver;

import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import repository.MemoryMemberRepository;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static util.HttpRequestUtils.parseCookies;
import static util.HttpRequestUtils.parseQueryString;
import static util.IOUtils.readData;

public class RequestHandler extends Thread {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private Socket connection;
    private MemoryMemberRepository repository;

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
        repository = MemoryMemberRepository.getInstance();
    }

    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {

            // TODO 1. index.html 반환하기
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            HashMap<String, String> map = new HashMap<>();
            DataOutputStream dos = new DataOutputStream(out);
            String firstLine = br.readLine();       // ex. GET /index.html HTTP/1.1 이런식으로 들어옴
            if (firstLine == null) {
                return;
            }
            String[] firstTokens = firstLine.split(" ");
            String method = firstTokens[0];
            String url = firstTokens[1];

            if (url.equals("/")) {
                url = "/index.html";
            }

            if(url.contains("css")){
//                System.out.println("url = " + url);
                Path path = new File("./webapp" + url).toPath();        // 요청 url ex) /css/bootstrap.min.css, /css/styles.css
                byte[] body = Files.readAllBytes(path);
                responseCssHeader(dos);
                responseBody(dos, body);
                return;
            }

            if (method.equals("POST")) {
                System.out.println("method = " + method + ", url = " + url);
                String line;
                int bodyLength = 0;
                while (true) {
                    line = br.readLine();
                    if (line.isEmpty()) {        // 요청 헤더를 다 읽고 while문 빠져나가기
                        break;
                    }
                    System.out.println(line);
                    if (line.startsWith("Content-Length")) {
                        String[] strings = line.split(": ");
                        bodyLength = Integer.parseInt(strings[1]);
                    }
                }
                String query = readData(br, bodyLength);
                System.out.println("query = " + query);
                Map<String, String> pairs = parseQueryString(query);

                // TODO 3, 4 - post 방식으로 회원가입 후 redirect 방식으로 이동
                if (url.equals("/user/create")) {
                    signup(pairs, dos);
                }

                // TODO 5. 로그인하기
                else if (url.equals("/user/login")) {
                    login(pairs, dos);
                }
            }

            if (url.equals("/user/list")) {
                System.out.println("method = " + method + ", url = " + url);
                String line;
                Map<String, String> cookies = new HashMap<>();
                while (true) {
                    line = br.readLine();
                    if (line.isEmpty()) {        // 요청 헤더를 다 읽고 while문 빠져나가기
                        break;
                    }
                    System.out.println(line);
                    if (line.startsWith("Cookie")) {
                        cookies = parseCookies(line.split(": ")[1]);
                    }
                }
                if (Boolean.parseBoolean(cookies.get("logined"))) {        // 로그인 된 상태

                    System.out.println("cookies.logined = " + Boolean.parseBoolean(cookies.get("logined")));
                    List<User> users = repository.findAll();
                    StringBuilder sb = addUserList(users, dos);
                    byte[] responseBytes = sb.toString().getBytes();
                    response200Header(dos, responseBytes.length);
                    dos.write(responseBytes, 0, responseBytes.length);
                    dos.flush();
                    return;
                }
                System.out.println("로그인 안된 상태!!");
                redirect(dos, "/user/login.html");          // 로그인 안된 상태
                return;
            }
            response(dos, method, url);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private StringBuilder addUserList(List<User> users, DataOutputStream dos) {
        StringBuilder sb = new StringBuilder();
        int index = 1;
        sb.append("<div class=\"container\" id=\"main\">\n");
        sb.append("   <div class=\"col-md-10 col-md-offset-1\">\n");
        sb.append("      <div class=\"panel panel-default\">\n");
        sb.append("          <table class=\"table table-hover\">\n");
        sb.append("              <thead>\n");
        sb.append("                <tr>\n");
        sb.append("                    <th>#</th> <th>사용자 아이디</th> <th>이름</th> <th>이메일</th><th></th>\n");
        sb.append("                </tr>\n");
        sb.append("              </thead>\n");
        sb.append("              <tbody>\n");
        for (User user : users) {
            sb.append("<tr>");
            sb.append("<th scope=\"row\">").append(index++).append("</th>");
            sb.append("<td>").append(user.getUserId()).append("</td>");
            sb.append("<td>").append(user.getName()).append("</td>");
            sb.append("<td>").append(user.getEmail()).append("</td>");
            sb.append("<td><a href=\"#\" class=\"btn btn-success\" role=\"button\">수정</a></td>");
            sb.append("</tr>");
        }

        sb.append("              </tbody>\n");
        sb.append("          </table>\n");
        sb.append("        </div>\n");
        sb.append("    </div>\n");
        sb.append("</div>\n");
        sb.append("<script src=\"../js/jquery-2.2.0.min.js\"></script>\n");
        sb.append("<script src=\"../js/bootstrap.min.js\"></script>\n");
        sb.append("<script src=\"../js/scripts.js\"></script>\n");
        sb.append("\t</body>\n");
        sb.append("</html>");

        return sb;

    }



    private void response302Header(DataOutputStream dos) {
        try {
            dos.writeBytes("HTTP/1.1 302 FOUND \r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Location: /index.html\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void response(DataOutputStream dos, String method, String url) {
        log.debug(method + " " + url);
        Path path = new File("./webapp" + url).toPath();
        try {
            byte[] body = Files.readAllBytes(path);
            if (method.equals("GET")) {
                response200Header(dos, body.length);
                responseBody(dos, body);
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void redirect(DataOutputStream dos, String redirectUrl) {
        log.debug("[redirect to " + redirectUrl + "]");
        Path path = new File("./webapp" + redirectUrl).toPath();
        try {
            byte[] body = Files.readAllBytes(path);
            response302Header(dos);
            responseBody(dos, body);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void response200Header(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    // 요청 url에 css가 포함된 경우 됨
    private void responseCssHeader(DataOutputStream dos){
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/css;charset=utf-8\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void signup(Map<String, String> pairs, DataOutputStream dos) {
        User user = new User(pairs.get("userId"), pairs.get("password"),
                pairs.get("name"), pairs.get("email"));
        repository.addUser(user);
        addCookieAndRedirect(dos, false, "/index.html");
    }

    private void login(Map<String, String> pairs, DataOutputStream dos) {
        User user = repository.findUserById(pairs.get("userId"));
        System.out.println("[login] " + user.toString());
        if (user != null && user.getPassword().equals(pairs.get("password"))) {
            addCookieAndRedirect(dos, true, "/index.html");
        }
        addCookieAndRedirect(dos, false, "/user/login_failed.html");
    }

    private void addCookieAndRedirect(DataOutputStream dos, boolean logined, String url) {
        log.debug("[add login cookie=" + logined + " and redirect to " + url + "]");
        Path path = new File("./webapp" + url).toPath();
        try {
            byte[] body = Files.readAllBytes(path);
            dos.writeBytes("HTTP/1.1 302 FOUND \r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Location: " + url + " \r\n");
            dos.writeBytes("Set-Cookie: logined=" + logined + " \r\n");
            dos.writeBytes("\r\n");

            responseBody(dos, body);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

}