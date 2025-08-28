import com.sun.net.httpserver.*;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*; 
import java.util.concurrent.*; 
 
public class WhiteboardServer { 
    private static final Set<SseClient> clients = ConcurrentHashMap.newKeySet(); 
    public static void main(String[] args) throws Exception { 
        int port = 8080; 
        HttpServer http = HttpServer.create(new InetSocketAddress(port), 0); 
        http.createContext("/", WhiteboardServer::staticFiles); 
        http.createContext("/draw", WhiteboardServer::handleDraw); 
        http.createContext("/events", WhiteboardServer::handleEvents); 
        http.setExecutor(Executors.newCachedThreadPool()); 
        http.start(); 
        System.out.println("Open http://localhost:" + port); 
    } 
    private static void staticFiles(HttpExchange ex) throws IOException { 
        String path = ex.getRequestURI().getPath(); 
        if (path.equals("/")) path = "/index.html"; 
        File f = new File("." + path); 
        if (!f.exists() || f.isDirectory()) { 
            byte[] not = "404".getBytes(StandardCharsets.UTF_8); 
            ex.sendResponseHeaders(404, not.length); 
            ex.getResponseBody().write(not); 
            ex.close(); 
            return; 
        } 
        String type = path.endsWith(".html") ? "text/html; charset=utf-8" 
                   : path.endsWith(".css")  ? "text/css; charset=utf-8" 
                   : path.endsWith(".js")   ? "application/javascript; charset=utf-8" 
                   : "text/plain; charset=utf-8"; 
        ex.getResponseHeaders().add("Content-Type", type); 
        byte[] bytes = readAll(new FileInputStream(f)); 
        ex.sendResponseHeaders(200, bytes.length); 
        ex.getResponseBody().write(bytes); 
        ex.close(); 
    } 
    private static void handleDraw(HttpExchange ex) throws IOException { 
        if (!"POST".equals(ex.getRequestMethod())) { ex.sendResponseHeaders(405, -1); ex.close(); 
return; } 
        String json = new String(readAll(ex.getRequestBody()), StandardCharsets.UTF_8); 
        broadcast(json); // send to everyone 
        byte[] ok = "OK".getBytes(StandardCharsets.UTF_8); 
        ex.sendResponseHeaders(200, ok.length); 
        ex.getResponseBody().write(ok); 
        ex.close(); 
    } 
    private static void handleEvents(HttpExchange ex) throws IOException { 
        Headers h = ex.getResponseHeaders(); 
        h.add("Content-Type", "text/event-stream"); 
        h.add("Cache-Control", "no-cache"); 
        h.add("Connection", "keep-alive"); 
        ex.sendResponseHeaders(200, 0);  
        SseClient client = new SseClient(ex); 
        clients.add(client); 
        client.send("event: hello\ndata: {\"type\":\"hello\"}\n\n"); 
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> { 
            if (!client.isOpen()) return; 
            try { client.send(": ping\n\n"); } catch (Exception ignored) {} 
        }, 10, 10, TimeUnit.SECONDS); 
    } 
    private static void broadcast(String json) { 
        for (SseClient c : clients) { 
            try { c.send("data: " + json + "\n\n"); } catch (IOException ignored) {} 
        } 
    } 
    private static byte[] readAll(InputStream in) throws IOException { 
        ByteArrayOutputStream bos = new ByteArrayOutputStream(); 
        in.transferTo(bos); 
        return bos.toByteArray(); 
    } 
    private static class SseClient { 
        private final HttpExchange ex; 
        private final OutputStream os; 
        SseClient(HttpExchange ex) { this.ex = ex; this.os = ex.getResponseBody(); } 
        void send(String chunk) throws IOException {        
             os.write(chunk.getBytes(StandardCharsets.UTF_8)); os.flush(); } 
        boolean isOpen() { return ex != null; }  
    } 
} 