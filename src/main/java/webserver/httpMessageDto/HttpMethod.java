package webserver.httpMessageDto;

public enum HttpMethod {
    GET, POST;

    public static HttpMethod toHttpMethod(String method){
        if (method.equals("GET")){
            return GET;
        } else {
            return POST;
        }
    }
}
