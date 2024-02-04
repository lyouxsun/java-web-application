package webserver;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import repository.MemoryMemberRepository;

import static util.HttpRequestUtils.parseQueryString;

public class RequestHandler extends Thread {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private Socket connection;
    private MemoryMemberRepository repository;

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
        repository =  MemoryMemberRepository.getInstance();
    }

    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {

            // TODO 1. index.html 반환하기
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            HashMap<String, String> map = new HashMap<>();
            DataOutputStream dos = new DataOutputStream(out);

            String firstLine = br.readLine();
            if(firstLine == null){
                return;
            }
            String[] firstTokens = firstLine.split(" ");
            String method = firstTokens[0];
            String url = firstTokens[1];

            if(url.contains("/user/create?")){
                System.out.println("method = " + method);
                System.out.println("1. url = " + url);
                String query = url.split("\\?")[1];
                Map<String, String> pairs = parseQueryString(query);
                User user = new User(pairs.get("userId"),
                        pairs.get("password"),
                        pairs.get("name"),
                        pairs.get("email"));
                repository.addUser(user);

                byte[] body = query.getBytes();
                log.debug(user.toString());
//                log.debug("1. url = " + url);
                response200Header(dos, body.length);
                responseBody(dos, body);
                return;
            }

            if (url.equals("/")){
                url = "/index.html";
            }
//            log.debug("2. url = " + url);
            response(dos, method, url);

//            String line;
//            while((line=br.readLine()) != null){
//                log.debug(line);
//                String[] tokens = line.split(": ");
//                map.put(tokens[0], tokens[1]);
//            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void response302Header(DataOutputStream dos) {
        try {
            dos.writeBytes("HTTP/1.1 302 FOUND \r\n");
            dos.writeBytes("Location: /index.html");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void response(DataOutputStream dos, String method, String url) throws IOException {
        log.debug(method + " " + url);
        Path path = Paths.get("./webapp"+url);
        byte[] body = Files.readAllBytes(path);
        response200Header(dos, body.length);
        responseBody(dos, body);
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

    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}