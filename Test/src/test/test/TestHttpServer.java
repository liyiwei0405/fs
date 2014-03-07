/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
/**
 *
 * @author Daniel
 */
public class TestHttpServer {
    
public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", 80), 0);
        server.createContext("/", new MyResponseHandler());
        server.setExecutor(null); // creates a default executor
        server.start();
        System.out.println("OK");
    }
    
public static class MyResponseHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            String response = "http://www.google.com";
            t.getResponseHeaders().add("location", response);
            t.sendResponseHeaders(301, response.length());
            t.close();
        }
    }
}