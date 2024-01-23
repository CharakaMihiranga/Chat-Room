package lk.ijse;

import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable{
    private Socket clientSocket;
    private BufferedReader reader;
    private DataOutputStream dataOutputStream;
    private DataInputStream dataInputStream;
    private PrintWriter writer;
    private String clientName;

    public ClientHandler(Socket clientSocket,String clientName) {
        this.clientSocket = clientSocket;
        this.clientName = clientName;
    }

    @Override
    public void run() {
        try {
            dataInputStream = new DataInputStream(clientSocket.getInputStream());
            dataOutputStream = new DataOutputStream(clientSocket.getOutputStream());

            while (!clientSocket.isClosed()) {
                String message = dataInputStream.readUTF();
                Server.broadcastMessage(message, this);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (dataOutputStream != null) {
                    dataOutputStream.close();
                }
                if (dataInputStream != null) {
                    dataInputStream.close();
                }
                if (clientSocket != null && !clientSocket.isClosed()) {
                    clientSocket.close();
                }
                System.out.println("Client " + clientName + " disconnected.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    // Send a message to this client
    public void sendMessage(String message) {
        try{
            dataOutputStream.writeUTF(message);
            dataOutputStream.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
    public String getClientName() {
        return clientName;
    }
}
