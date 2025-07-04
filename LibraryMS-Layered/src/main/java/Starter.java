import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Starter extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        try {
            stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/view/dashboard_view.fxml"))));
            stage.setTitle("LMS");
            stage.show();
        } catch (IOException e) {
            System.err.println("Error loading dashboard_view.fxml: " + e.getMessage());
            e.printStackTrace();
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
            alert.setTitle("Application Startup Error");
            alert.setHeaderText("Could not load main dashboard");
            alert.setContentText("The application failed to start because the main dashboard file could not be loaded. Please check the file path and ensure it exists.");
            alert.showAndWait();
        }
    }
}