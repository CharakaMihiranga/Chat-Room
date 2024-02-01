package lk.ijse.controller;

import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

public class LoginFormController {
    @FXML
    private AnchorPane anchorpane;
    @FXML
    private ImageView btnCloseOnAction;
    @FXML
    private TextField txtUsername;
    @FXML
    private Button btnLogin;

    @FXML
    private void btnCloseOnAction(MouseEvent mouseEvent) {

        System.exit(0);

    }

    @FXML
    private void btnLoginOnAction(ActionEvent actionEvent) throws IOException {

        String username = txtUsername.getText();

        if (!username.isEmpty()) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Client1Form.fxml"));
            Parent root = loader.load();
            Client1FormController controller = loader.getController();
            controller.setUsername(username);

            if (root != null) {
                Scene subScene = new Scene(root);
                Stage stage = (Stage) this.anchorpane.getScene().getWindow();
                stage.setScene(subScene);

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
    }
}
