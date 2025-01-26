package ua.hillel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Objects;

public class ClientHandler implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(ClientHandler.class);

    private final Socket clientSocket;
    private final String clientName;

    public String getClientName() {
        return clientName;
    }

    public ClientHandler(Socket clientSocket, String clientName) {
        this.clientSocket = clientSocket;
        this.clientName = clientName;
    }


    @Override
    public void run() {
        logger.info("Processing client: {}", clientName);

        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

            out.println("[SERVER] Welcome " + clientName);
            out.println("For exit enter 'exit'");

            String message;
            while ((message = in.readLine()) != null) {
                if ("exit".equalsIgnoreCase(message)) {
                    out.println("bye " + clientName);
                    App.activeClients.remove(this);
                    break;
                }
                System.out.printf("[CLIENT] %s: %s%n", clientName, message);
                out.println("[SERVER] you: " + message);
            }
        }catch (IOException e) {
            App.activeClients.remove(this);
            logger.error("Error while reading message from {}", clientName + e.getMessage());
        }finally {
            try{
                clientSocket.close();
            }catch (IOException e) {
                logger.error("Error closing connection from {}", clientName + e.getMessage());
            }
            App.activeClients.remove(this);
            logger.info("{} has been disconnected.", clientName);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ClientHandler that = (ClientHandler) o;
        return Objects.equals(clientName, that.clientName);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(clientName);
    }

    @Override
    public String toString() {
        return String.format("%s%n", clientName);
    }
}
