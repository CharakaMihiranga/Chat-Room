package lk.ijse.controller;

import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class Client1FormController {


    @FXML
    private ImageView btnAttach;

    @FXML
    private Button btnBack;

    @FXML
    private Button btnClose;

    @FXML
    private ImageView btnEmoji;

    @FXML
    private Button btnMinimize;

    @FXML
    private Label lblUsername;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private TextField txtMessage;

    @FXML
    private VBox vBox;


    private String username = "charaka";
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
        if (!message.matches(".*\\.(png|jpe?g|gif|mp3|wav|ogg|flac|mp4|mov|avi|wmv)$")){
            setReceivedText(message);
        }
    }


    void sendMessage(String message) {
       try{
           if (!message.isEmpty()){
               if(!message.matches(".*\\.(png|jpe?g|gif|mp3|wav|ogg|flac|mp4|mov|avi|wmv)$")){

                   vBox.setSpacing(10);

                   HBox hBox = new HBox();
                   hBox.setAlignment(Pos.CENTER_RIGHT);
                   hBox.setPadding(new Insets(5, 10, 5, 10));

                   Text text = new Text(message);
                   text.setStyle("-fx-font-size: 16; -fx-font-family: 'Sans Serif';");

                   LocalDateTime currentTime = LocalDateTime.now();
                   String formattedTime = currentTime.format(DateTimeFormatter.ofPattern("hh:mm a")); // 'a' for AM/PM indicator
                   Text timeText = new Text("  "+formattedTime);
                   timeText.setStyle("-fx-font-size: 11");
                   timeText.setFill(Color.GRAY);

                   TextFlow messageTextFlow = new TextFlow(text);

                   Region spacer = new Region();
                   HBox.setHgrow(spacer, Priority.ALWAYS);

                   messageTextFlow.setBackground(new Background(new BackgroundFill(Color.web("#D9D9D9"), new CornerRadii(10), null)));
                   messageTextFlow.setPadding(new Insets(10, 10, 10, 10));

                   messageTextFlow.getChildren().addAll(spacer, timeText);
                   text.setFill(Color.BLACK);

                   HBox innerHBox = new HBox(messageTextFlow);
                   innerHBox.setAlignment(Pos.BOTTOM_RIGHT);

                   hBox.getChildren().addAll(innerHBox);
                   vBox.getChildren().add(hBox);

               }
           }
           txtMessage.clear();
           dataOutputStream.writeUTF(message);
           dataOutputStream.flush();
       } catch (Exception e) {
           throw new RuntimeException(e);
       }
    }


    void setReceivedText(String message){
        if (!message.matches(".*\\.(png|jpe?g|gif|mp3|wav|ogg|flac|mp4|mov|avi|wmv)$")){
            vBox.setSpacing(10);

            HBox hBox = new HBox();
            hBox.setAlignment(Pos.CENTER_LEFT);
            hBox.setPadding(new Insets(5, 10, 5, 10));

            Text text = new Text(message);
            text.setStyle("-fx-font-size: 16; -fx-font-family: 'Sans Serif';");

            LocalDateTime currentTime = LocalDateTime.now();
            String formattedTime = currentTime.format(DateTimeFormatter.ofPattern("hh:mm a")); // 'a' for AM/PM indicator
            Text timeText = new Text("  "+formattedTime);
            timeText.setStyle("-fx-font-size: 11");
            timeText.setFill(Color.GRAY);

            TextFlow messageTextFlow = new TextFlow(text);

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            messageTextFlow.setBackground(new Background(new BackgroundFill(Color.web("#CBD2FF"), new CornerRadii(10), null)));
            messageTextFlow.setPadding(new Insets(10, 10, 10, 10));

            messageTextFlow.getChildren().addAll(spacer, timeText);
            text.setFill(Color.BLACK);

            HBox innerHBox = new HBox(messageTextFlow);
            innerHBox.setAlignment(Pos.BOTTOM_RIGHT);

            hBox.getChildren().addAll(innerHBox);
            vBox.getChildren().add(hBox);
        }
    }
    public void txtMessageOnAction(ActionEvent actionEvent) {

        String msg = txtMessage.getText();
        if(!msg.isEmpty()) {
            sendMessage(txtMessage.getText());
        }
    }

    @FXML
    private void btnCloseOnAction(ActionEvent actionEvent) {
        Stage stage = (Stage) btnClose.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void btnMinimizeOnAction(ActionEvent actionEvent) {
        Stage stage = (Stage) btnMinimize.getScene().getWindow();
        stage.setIconified(true);
    }


    @FXML
    private void btnBackOnAction(ActionEvent actionEvent) {
        System.out.println("Back");
    }

}
