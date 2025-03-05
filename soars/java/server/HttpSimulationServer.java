package server;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpSimulationServer {
    public static void main(String[] args) throws IOException {
        int port = 8080;

        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/calculate", new CalculationHandler());
        server.setExecutor(null);
        server.start();
        System.out.println("Server started on port " + port);
    }
}
class CalculationHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("GET".equals(exchange.getRequestMethod())) {
            Map<String, String> queryParams = queryToMap(exchange.getRequestURI().getQuery());

            if (queryParams.containsKey("n1") && queryParams.containsKey("n2")) {
                try {
                    int n1 = Integer.parseInt(queryParams.get("n1"));
                    int n2 = Integer.parseInt(queryParams.get("n2"));
                    int sum = toyosu_model.Main(n1,n2);
                    String response = "{\"sum\": " + sum + "}";

                    exchange.sendResponseHeaders(200, response.length());
                    OutputStream os = exchange.getResponseBody();
                    os.write(response.getBytes());
                    os.close();
                } catch (NumberFormatException e) {
                    String response = "Invalid number format";
                    exchange.sendResponseHeaders(400, response.length());
                    OutputStream os = exchange.getResponseBody();
                    os.write(response.getBytes());
                    os.close();
                }
            } else {
                String response = "Missing parameters n1 and n2";
                exchange.sendResponseHeaders(400, response.length());
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }
        } else {
            exchange.sendResponseHeaders(405, -1); // Method Not Allowed
        }
    }

    private Map<String, String> queryToMap(String query) {
        return query == null ? Map.of() :
                java.util.Arrays.stream(query.split("&"))
                        .map(param -> param.split("=", 2))
                        .collect(Collectors.toMap(
                                keyValue -> decode(keyValue[0]),
                                keyValue -> keyValue.length > 1 ? decode(keyValue[1]) : ""));
    }

    private String decode(String value) {
        try {
            return java.net.URLDecoder.decode(value, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return value;
        }
    }
}


