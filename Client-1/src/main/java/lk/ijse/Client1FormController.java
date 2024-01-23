package lk.ijse;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client1FormController {
    public static void main(String[] args) {
        String serverHost = "localhost";
        int serverPort = 5000;

        try {
            Socket socket = new Socket(serverHost, serverPort);
            System.out.println("Connected to the server.");

            // Get input and output streams from the socket
            InputStream inputStream = socket.getInputStream();
            OutputStream outputStream = socket.getOutputStream();

            // Scanner to read from console
            Scanner scanner = new Scanner(System.in);

            // Prompt the user for a username
            System.out.print("Enter your username: ");
            String username = scanner.nextLine();

            // Send the username to the server
            PrintWriter writer = new PrintWriter(outputStream, true);
            writer.println(username);

            // Start a new thread to handle receiving messages from the server
            Thread receiveThread = new Thread(() -> {
                try {
                    Scanner serverScanner = new Scanner(inputStream);
                    while (serverScanner.hasNextLine()) {
                        String receivedMessage = serverScanner.nextLine();
                        System.out.println(receivedMessage);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            receiveThread.start();

            // Send messages to the server
            while (true) {
                String message = scanner.nextLine();
                writer.println(message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
