package util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class NavigationUtil {

    public static void navigateTo(Stage stage, String fxmlPath, String title) {
        try {
            URL fxmlUrl = NavigationUtil.class.getResource(fxmlPath);

            if (fxmlUrl == null) {
                throw new IOException("FXML file not found at path: " + fxmlPath);
            } else {

                // Load the FXML file
                Parent root = FXMLLoader.load(fxmlUrl);

                // Create a new scene with the loaded FXML
                Scene scene = new Scene(root);

                // Set the new scene on the stage
                stage.setScene(scene);
                stage.setTitle(title);
                stage.show();
                System.out.println("Successfully navigated to " + fxmlPath);
            }

        } catch (IOException e) {
            System.err.println("Error navigating to " + fxmlPath + ": " + e.getMessage());
            e.printStackTrace();
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
            alert.setTitle("Navigation Error");
            alert.setHeaderText("Could not load page");
            alert.setContentText("Failed to load " + fxmlPath + ". Please check the file path and ensure it exists.");
            alert.showAndWait();
        }
    }
}
