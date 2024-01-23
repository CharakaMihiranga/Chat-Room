package lk.ijse;

import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable{
    private Socket clientSocket;
    private BufferedReader reader;
    private PrintWriter writer;
    private String clientName; // Added field to store client's username

    public ClientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;

        try {
            // Initialize input and output streams for this client
            InputStream inputStream = clientSocket.getInputStream();
            OutputStream outputStream = clientSocket.getOutputStream();
            reader = new BufferedReader(new InputStreamReader(inputStream));
            writer = new PrintWriter(outputStream, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            // Receive the username from the client during the login process
            clientName = reader.readLine();
            System.out.println("Client " + clientName + " connected.");

            // Listen for incoming messages from the client
            String message;
            while ((message = reader.readLine()) != null) {

                // Broadcast the message to all clients
                Server GroupChatServer = new Server();
                GroupChatServer.broadcastMessage(clientName + ": " + message, this);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                // Close resources when the client disconnects
                reader.close();
                writer.close();
                clientSocket.close();
                System.out.println("Client " + clientName + " disconnected.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Send a message to this client
    public void sendMessage(String message) {
        writer.println(message);
    }

    // Get the client's username
    public String getClientName() {
        return clientName;
    }
}
