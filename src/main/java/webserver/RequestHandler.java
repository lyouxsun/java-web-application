package webserver;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestHandler extends Thread {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private Socket connection;

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
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
            String[] firstTokens = firstLine.split(" ");
            String method = firstTokens[0];
            String url = firstTokens[1];

            if (method.equals("GET") && (url.equals("/index.html") || url.equals("/"))) {
                log.debug("GET /index.html");
                responseDefaultUrl(dos);
            }
            String line;
            while((line=br.readLine()) != null){
                String[] tokens = line.split(": ");
                map.put(tokens[0], tokens[1]);
                System.out.println(tokens[0]+"   "+tokens[1]);
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void responseDefaultUrl(DataOutputStream dos) throws IOException {
        log.debug("GET /index.html");
//        Path path = new File("./webapp/index.html").toPath();
        Path path = Paths.get("./webapp/index.html");
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