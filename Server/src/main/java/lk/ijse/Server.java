package lk.ijse;

import javafx.application.Platform;
import javafx.scene.control.Alert;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;


public class Server {
    private static final int PORT = 5000;
    private static final List<ClientHandler> clients = new ArrayList<>();

    public static void main(String[] args) {
        try {

            ServerSocket serverSocket = new ServerSocket(PORT);
            System.out.println("Server is Started!");

            while (true) {

                Socket clientSocket = serverSocket.accept();

                DataInputStream dataInputStream = new DataInputStream(clientSocket.getInputStream());

                String clientName = dataInputStream.readUTF();
                System.out.println("Client "+clientName+" connected!");

                ClientHandler clientHandler = new ClientHandler(clientSocket,clientName);
                clients.add(clientHandler);

                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            new Alert(Alert.AlertType.ERROR, "Unable to connect with the client! (" + e.getMessage() + ")").showAndWait();
        }
    }

    public static void broadcastMessage(String message, ClientHandler sender) {
        for (ClientHandler client : clients) {
            if (client != sender) {
                client.sendMessage(sender.getClientName()+"-"+message);
            }
        }
    }

}
