package server;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import toyosu_model.Main;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

public class SimpleHttpServer {
    public static void main(String[] args) throws IOException {
        int port = 8080;
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

        // "/add" エンドポイントを登録
        server.createContext("/add", new AddHandler());

        server.setExecutor(null);
        System.out.println("Server is running on port " + port);
        server.start();
    }

    // GETリクエストを処理するハンドラー
    static class AddHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                sendResponse(exchange, 405, "Method Not Allowed");
                return;
            }

            // クエリパラメータを取得
            Map<String, String> queryParams = parseQueryParams(exchange.getRequestURI().getQuery());
            if (!queryParams.containsKey("a") || !queryParams.containsKey("b")) {
                sendResponse(exchange, 400, "Missing parameters 'a' and 'b'");
                return;
            }

            try {
                int a = Integer.parseInt(queryParams.get("a"));
                int b = Integer.parseInt(queryParams.get("b"));
                int result = Main.plus(a, b);

                sendResponse(exchange, 200, "Result: " + result);
            } catch (NumberFormatException e) {
                sendResponse(exchange, 400, "Invalid number format");
            }
        }

        // クエリパラメータを解析するメソッド
        private Map<String, String> parseQueryParams(String query) {
            Map<String, String> queryParams = new HashMap<>();
            if (query == null || query.isEmpty()) return queryParams;

            String[] pairs = query.split("&");
            for (String pair : pairs) {
                String[] keyValue = pair.split("=", 2);
                if (keyValue.length == 2) {
                    queryParams.put(keyValue[0], keyValue[1]);
                }
            }
            return queryParams;
        }

        // レスポンスを送信するメソッド
        private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
            exchange.sendResponseHeaders(statusCode, response.length());
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        }
    }
}
