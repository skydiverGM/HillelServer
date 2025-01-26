package ua.hillel;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class App {
    private static final int PORT = 4999;
    private static final AtomicInteger clientCounter = new AtomicInteger(1);
    static final Set<ClientHandler> activeClients = Collections.synchronizedSet(new HashSet<ClientHandler>());
    private static final Logger logger = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) {
        logger.info("Starting server...");

        try(ServerSocket serverSocket = new ServerSocket(PORT)) {
            logger.info("Server started on port {}", PORT);

            while(true) {
                logger.info("Waiting for connection...");
                Socket clientSocket = serverSocket.accept();
                String clientName = "client-" + clientCounter.getAndIncrement();
                ClientHandler clientHandler = new ClientHandler(clientSocket, clientName);
                new Thread(clientHandler).start();
                logger.info("New connection with: {}", clientName);
                activeClients.add(clientHandler);
                System.out.printf("Active connections:%n %s", activeClients);
            }
        }catch (IOException e) {
            logger.error("Server error: {}", e.getMessage());
        }

    }
}
