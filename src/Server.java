import java.io.*;
import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

public class Server {
    private static Logger log = Logger.getLogger(Server.class.getName());
    private static final int DEFAULT_PORT = 4444;

    public static void main(String[] args) throws IOException {
        ExecutorService pool = Executors.newCachedThreadPool();
        log.info("Welcome to Server side");

        int port = DEFAULT_PORT;
        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                log.info("Cannot parse integer argument");
            }
        }
        // Create new socket
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(port);
            log.info("Server started on port: "
                    + serverSocket.getLocalPort() + "\n");
        } catch (IOException e) {
            log.info("Port " + port + " is blocked.");
            System.exit(-1);
        }
        // Wait for clients.
        while (true) {
            try {
                Socket clientSocket = serverSocket.accept();
                /* Submit task to thread pool */
                ClientSession session = new ClientSession(clientSocket);
                pool.submit(session);
            } catch (IOException e) {
                log.info("Failed to establish connection.");
                log.info(e.getMessage());
                System.exit(-1);
            }
        }
    }
}