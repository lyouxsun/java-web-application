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
import java.util.Map;

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
//            String line;
//            while((line = br.readLine())!=null){
//                log.debug(line);       // 로그를 통해 inputstream 의 데이터 확인하기
//            }

            HashMap<String, String> map = new HashMap<>();
            DataOutputStream dos = new DataOutputStream(out);
            String firstLine = br.readLine();       // ex. GET /index.html HTTP/1.1 이런식으로 들어옴
            if (firstLine == null) {
                return;
            }
            String[] firstTokens = firstLine.split(" ");
            String method = firstTokens[0];
            String url = firstTokens[1];

            if (url.contains("/user/create")) {
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
                User user = new User(pairs.get("userId"), pairs.get("password"),
                        pairs.get("name"), pairs.get("email"));
                repository.addUser(user);
                redirect(dos, "/index.html");
                return;
            }

            if (url.equals("/")) {
                url = "/index.html";
            }
            response(dos, method, url);
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
        Path path = new File("./webapp" + url).toPath();
        byte[] body = Files.readAllBytes(path);
        if (method.equals("GET")) {
            response200Header(dos, body.length);
            responseBody(dos, body);
        }
    }

    private void redirect(DataOutputStream dos, String url) throws IOException {
        log.debug("[redirect to " + url + "]");
        Path path = new File("./webapp" + url).toPath();
        byte[] body = Files.readAllBytes(path);
        response302Header(dos);
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