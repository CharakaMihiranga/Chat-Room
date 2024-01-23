package lk.ijse.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;



public class Client2FormController {


    @FXML
    private TextArea txtArea;

    @FXML
    private TextField txtMsg;

    private String username = "mihiranga";
    private Socket socket;
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;
    private static String message;

    public void initialize() {
        new Thread(() -> {
            try {
                socket = new Socket("localhost", 5000);
                dataOutputStream = new DataOutputStream(socket.getOutputStream());
                dataInputStream = new DataInputStream(socket.getInputStream());

                dataOutputStream.writeUTF(username);
                dataOutputStream.flush();

                while (socket.isConnected()) {
                    try {
                        String msg = dataInputStream.readUTF();
                        Platform.runLater(() -> receiveMessage(msg));
                    } catch (EOFException e) {
                        // Handle the end of the stream (socket closure) gracefully
                        System.out.println("Server has closed the connection.");
                        break;
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }


    private void receiveMessage(String message) {
        txtArea.appendText("From another :"+ message);
    }

    @FXML
    void sendMsgOnAction(ActionEvent event) {
        try{
            String message = txtMsg.getText();
            txtArea.appendText("From me : "+message);
            dataOutputStream.writeUTF(message);
            dataOutputStream.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
