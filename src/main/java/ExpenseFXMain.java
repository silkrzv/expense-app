import controller.LoginController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import repository.ExpenseDBRepository;
import repository.ExpenseRepository;
import repository.UserDBRepository;
import repository.UserRepository;
import service.Service;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class ExpenseFXMain extends Application {

    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/login.fxml"));
        Parent root = fxmlLoader.load();

        Properties props = new Properties();
        try
        {
            props.load(new FileReader("bd.config"));
        }
        catch (IOException e)
        {
            System.out.println("Cannot find bd.config " + e);
        }

        UserRepository userRepository = new UserDBRepository(props);
        ExpenseRepository expenseRepository = new ExpenseDBRepository(props);

        Service service = new Service(expenseRepository, userRepository);

        LoginController loginController = fxmlLoader.getController();
        loginController.setService(service);

        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();

    }

    public static void main(String[] args) {
        launch(args);
    }
}
