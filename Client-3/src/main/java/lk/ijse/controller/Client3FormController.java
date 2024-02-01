package lk.ijse.controller;

import com.github.sarxos.webcam.Webcam;
import com.vdurmont.emoji.EmojiManager;
import com.vdurmont.emoji.EmojiParser;
import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import lk.ijse.emoji.EmojiPicker;
import lombok.Setter;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.concurrent.atomic.AtomicReference;


public class Client3FormController {



    @FXML
    private AnchorPane capturedImagePane;

    @FXML
    private ImageView capturedImage;

    @FXML
    private Button btnNoOnAction;

    @FXML
    private Button btnYesOnAction;
    @FXML
    private AnchorPane emojiBar;

    @FXML
    private AnchorPane anchorpane;


    @FXML
    private Button btnBack;

    @FXML
    private Button btnClose;


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
    @FXML
    private ImageView imgView;

    @FXML
    private Button btnEmoji;
    @Setter
    private String username;
    private Socket socket;
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;
    private static String message;
    private File fileToSend;
    private String imgSender;
    private File capturedImageFile;

    public void initialize() {

        FadeTransition fadeIn = new FadeTransition(Duration.millis(2000), anchorpane);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);
        fadeIn.play();

        lblUsername.setText(username);
        capturedImagePane.setVisible(false);

        new Thread(() -> {
            try {
                socket = new Socket("localhost", 5000);
                dataOutputStream = new DataOutputStream(socket.getOutputStream());
                dataInputStream = new DataInputStream(socket.getInputStream());

                lblUsername.setText(username);
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
                Platform.runLater(() -> new Alert(Alert.AlertType.ERROR, "Unable to connect with the server! (" + e.getMessage() + ")").showAndWait());
            }
        }).start();
    }


    private void receiveMessage(String message) {
        String received = message;

        if (received.length() > 200){
            setReceived(received);
        }else {
            setReceivedText(message);
        }
    }

    private void setReceived(String received) {
        try{
            System.out.println("Received code :"+received);

            Image image = convertStringToImage(received);
            String sender = imgSender;

            vBox.setSpacing(10);

            HBox hBox = new HBox();
            hBox.setAlignment(Pos.CENTER_LEFT);
            hBox.setPadding(new Insets(5, 10, 5, 10));

            VBox messageBox = new VBox();
            messageBox.setAlignment(Pos.TOP_LEFT);
            messageBox.setSpacing(5); // Adjust spacing as needed

            Text senderText = new Text(sender);
            senderText.setStyle("-fx-font-size: 14; -fx-font-weight: bold; -fx-font-family: 'Sans Serif';");

            ImageView imageView = new ImageView(image);
            imageView.setFitWidth(300);
            imageView.setPreserveRatio(true);


            LocalDateTime currentTime = LocalDateTime.now();
            String formattedTime = currentTime.format(DateTimeFormatter.ofPattern("hh:mm a"));
            Text timeText = new Text(formattedTime);
            timeText.setStyle("-fx-font-size: 11");
            timeText.setFill(Color.GRAY);

            messageBox.getChildren().addAll(senderText, imageView, timeText);

            VBox.setMargin(timeText, new Insets(0, 0, 0, 10));

            messageBox.setBackground(new Background(new BackgroundFill(Color.web("#CBD2FF"), new CornerRadii(10), null)));
            messageBox.setPadding(new Insets(10, 10, 10, 10));

            hBox.getChildren().addAll(messageBox);
            vBox.getChildren().add(hBox);


        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Image convertStringToImage(String received) {

        String [] imgMessage = splitImage(received);

        imgSender = imgMessage[0];
        String img = imgMessage[1];
        byte[] imageBytes = Base64.getDecoder().decode(img);
        ByteArrayInputStream bis = new ByteArrayInputStream(imageBytes);

        return new Image(bis);
    }


    private String[] splitImage(String received) {

        // Split the text into two parts based on the hyphen "-"
        String[] parts = received.split("-");

        return parts;
    }


    void sendMessage(String message) {
        try{
            if (!message.isEmpty()){
                if(message != null){

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
        if (message != null){

            String [] msgDetail = splitReceivedMsg(message);

            String sender = msgDetail[0];
            String receivedMessage = msgDetail[1];

            String textWithUnicodeEmoji = EmojiParser.parseToUnicode(receivedMessage);

            vBox.setSpacing(10);

            HBox hBox = new HBox();
            hBox.setAlignment(Pos.CENTER_LEFT);
            hBox.setPadding(new Insets(5, 10, 5, 10));

            VBox messageBox = new VBox();
            messageBox.setAlignment(Pos.TOP_LEFT);

            Text senderText = new Text(sender);
            senderText.setStyle("-fx-font-size: 14; -fx-font-weight: bold; -fx-font-family: 'Sans Serif';");

            Text messageText = new Text(textWithUnicodeEmoji);
            messageText.setStyle("-fx-font-size: 16; -fx-font-family: 'Sans Serif';");

            LocalDateTime currentTime = LocalDateTime.now();
            String formattedTime = currentTime.format(DateTimeFormatter.ofPattern("hh:mm a"));
            Text timeText = new Text("  " + formattedTime);
            timeText.setStyle("-fx-font-size: 11");
            timeText.setFill(Color.GRAY);

            TextFlow senderFlow = new TextFlow(senderText);
            TextFlow messageFlow = new TextFlow(messageText, timeText);

            messageBox.getChildren().addAll(senderFlow, messageFlow);

            messageBox.setBackground(new Background(new BackgroundFill(Color.web("#CBD2FF"), new CornerRadii(10), null)));
            messageBox.setPadding(new Insets(10, 10, 10, 10));

            hBox.getChildren().addAll(messageBox);
            vBox.getChildren().add(hBox);

        }
    }

    private String[] splitReceivedMsg(String message) {

        System.out.println(EmojiManager.getForAlias("smile").getUnicode());

        String[] parts = message.split("-");

        return parts;
    }

    public void txtMessageOnAction(ActionEvent actionEvent) {

        String msg = txtMessage.getText();
        if(!msg.isEmpty()) {
            sendMessage(txtMessage.getText());
        }
    }


    @FXML
    void btnAttachOnAction(ActionEvent actionEvent) {

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select File to Send");

        try {
            fileToSend = fileChooser.showOpenDialog(null);

            if (fileToSend != null) {
                String fileName = fileToSend.getName();

                if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg") ||
                        fileName.endsWith(".png") || fileName.endsWith(".gif") ||
                        fileName.endsWith(".bmp")) {

                    sendImage(fileToSend);
                }
            }else{
                System.out.println("select file first!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void sendImage(File fileToSend) {

        try {

            Image image = new Image(fileToSend.getPath());



            String imageAsString = convertImageToString(image);
            System.out.println("Image :"+imageAsString);

            dataOutputStream.writeUTF(imageAsString);
            dataOutputStream.flush();
            setSentImage(image);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void setSentImage(Image image) {

        vBox.setSpacing(10);

        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER_RIGHT);
        hBox.setPadding(new Insets(5, 10, 5, 10));

        VBox messageBox = new VBox();
        messageBox.setAlignment(Pos.TOP_RIGHT);
        messageBox.setSpacing(5); // Adjust spacing as needed


// Create an ImageView with the loaded image
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(300); // Adjust the width as needed
        imageView.setPreserveRatio(true); // Maintain aspect ratio

// Text for displaying time
        LocalDateTime currentTime = LocalDateTime.now();
        String formattedTime = currentTime.format(DateTimeFormatter.ofPattern("hh:mm a"));
        Text timeText = new Text(formattedTime);
        timeText.setStyle("-fx-font-size: 11");
        timeText.setFill(Color.GRAY);

// Add sender text and image to the messageBox
        messageBox.getChildren().addAll(imageView, timeText);

// Set the alignment of timeText to center left
        VBox.setMargin(timeText, new Insets(0, 0, 0, 10));

        messageBox.setBackground(new Background(new BackgroundFill(Color.web("#CBD2FF"), new CornerRadii(10), null)));
        messageBox.setPadding(new Insets(10, 10, 10, 10));

        hBox.getChildren().addAll(messageBox);
        vBox.getChildren().add(hBox);
    }

    private String convertImageToString(Image image) {

        try {

            double maxWidth = 600;
            double maxHeight = 400;
            double width = image.getWidth();
            double height = image.getHeight();

            double scaleFactor = (width > maxWidth || height > maxHeight) ? Math.min(maxWidth / width, maxHeight / height) : 1.0;
            width *= scaleFactor;
            height *= scaleFactor;

            BufferedImage resizedImage = new BufferedImage((int) width, (int) height, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = resizedImage.createGraphics();
            g.drawImage(SwingFXUtils.fromFXImage(image, null), 0, 0, (int) width, (int) height, null);
            g.dispose();

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(resizedImage, "jpg", outputStream);


            byte[] imageBytes = outputStream.toByteArray();
            return Base64.getEncoder().encodeToString(imageBytes);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }


    @FXML
    private void btnCloseOnAction(ActionEvent actionEvent) throws IOException {

        System.exit(0);

    }

    @FXML
    private void btnMinimizeOnAction(ActionEvent actionEvent) {
        Stage stage = (Stage) btnMinimize.getScene().getWindow();
        stage.setIconified(true);
    }


    @FXML
    private void btnBackOnAction(ActionEvent actionEvent) throws IOException {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/LoginForm.fxml"));
        Parent root = loader.load();
        if (root != null) {
            Scene subScene = new Scene(root);
            Stage stage = (Stage) this.anchorpane.getScene().getWindow();
            stage.setScene(subScene);
            stage.centerOnScreen();

            TranslateTransition tt = new TranslateTransition(Duration.millis(350), subScene.getRoot());
            tt.setFromX(-subScene.getWidth());
            tt.setToX(0);
            tt.play();

            AtomicReference<Double> xOffset = new AtomicReference<>((double) 0);
            AtomicReference<Double> yOffset = new AtomicReference<>((double) 0);

            root.setOnMousePressed(event -> {
                xOffset.set(event.getSceneX());
                yOffset.set(event.getSceneY());
            });

            root.setOnMouseDragged(event -> {
                // Check if the mouse movement starts from the top part of the pane
                if (event.getY() < root.getLayoutY() + 20) { // You can adjust the value 20 according to your preference
                    double newX = event.getScreenX() - xOffset.get();
                    double newY = event.getScreenY() - yOffset.get();
                    stage.setX(newX);
                    stage.setY(newY);
                }
            });


            stage.setScene(subScene);
            stage.setResizable(false);
            stage.show();

        }
    }


    @FXML
    private void btnTakePhotoOnAction(ActionEvent actionEvent) {
        // Open the camera and capture the photo
        Webcam webcam = Webcam.getDefault();
        if (webcam != null) {
            // Open the webcam
            webcam.open();

            // Capture the photo
            java.awt.Image awtImage = webcam.getImage();

            // Convert the AWT image to JavaFX Image
            Image fxImage = SwingFXUtils.toFXImage((BufferedImage) awtImage, null);

            // Close the webcam
            webcam.close();

            // Specify the file path where you want to save the image
            String filePath = "C:\\Users\\chara\\Desktop\\photos\\";

            // Save the captured photo to the specified file path
            saveImageToFile(fxImage, filePath);

            capturedImage.setImage(fxImage);
            capturedImagePane.setVisible(true);

        }
    }

    private void saveImageToFile(Image image, String filePath) {
        // Convert the JavaFX Image to a BufferedImage
        BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);

        // Write the BufferedImage to a file
        try {

            File file = new File(filePath);
            ImageIO.write(bufferedImage, "png", file);

            if (file != null){

                setCapturedImageFile(file);

            }
            System.out.println("Image saved to: " + filePath);
        } catch (IOException e) {
            System.out.println("Error saving image: " + e.getMessage());
        }
    }

    private void setCapturedImageFile(File file){
        capturedImageFile = file;
    }
    private File returnCapturedImage() {
        return capturedImageFile;
    }

    @FXML
    private void btnEmojiOnAction(ActionEvent actionEvent) {

        EmojiPicker emojiPicker = new EmojiPicker();

        VBox vBox = new VBox(emojiPicker);
        vBox.setPrefSize(465, 236);
        vBox.setLayoutX(400);
        vBox.setLayoutY(175);

        emojiBar.getChildren().add(vBox);

        btnEmoji.setOnAction(event -> {
            emojiPicker.setVisible(!emojiPicker.isVisible());
        });

        emojiPicker.getEmojiListView().setOnMouseClicked(event -> {
            String selectedEmoji = emojiPicker.getEmojiListView().getSelectionModel().getSelectedItem();
            if (selectedEmoji != null) {
                txtMessage.appendText(selectedEmoji);
            }
            emojiPicker.setVisible(false);
        });
    }

    @FXML
    private void btnYesOnAction(ActionEvent actionEvent) {
        File file = returnCapturedImage();
        sendImage(file);
        capturedImagePane.setVisible(false);
    }

    @FXML
    private void btnNoOnAction(ActionEvent actionEvent) {
        capturedImagePane.setVisible(false);
    }
}
