package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import service.Service;

import java.io.IOException;
public class LoginController {

    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Label messageLabel;
    @FXML
    private Button btnLogin;

    private Service service;

    public void setService(Service service) {
        this.service = service;
    }

    @FXML
    private void handleLogin() throws IOException
    {
        String username = usernameField.getText();
        String password = passwordField.getText();
        if (service.authenticateUser(username, password))
        {
            messageLabel.setText("Login successful!");
            openMainWindow();
        }
        else
        {
            messageLabel.setText("Login failed!");
        }
    }

    private void openMainWindow() throws IOException
    {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/app-view.fxml"));
        Stage stage = new Stage();
        stage.setScene(new Scene(fxmlLoader.load()));
        ApplicationController controller = fxmlLoader.getController();
        controller.initialize(service);
        stage.show();
        closeWindow();
    }

    private void closeWindow()
    {
        Stage stage = (Stage) messageLabel.getScene().getWindow();
        stage.close();
    }
}
