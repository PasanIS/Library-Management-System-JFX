package util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class NavigationUtil {

    public static void loadScene(String issueBook_view, Button btnIssue) throws IOException {
        Stage stage = (Stage) btnIssue.getScene().getWindow();
        Scene scene = new Scene(FXMLLoader.load(NavigationUtil.class.getResource("/view/" +issueBook_view)));
        stage.setScene(scene);
        stage.show();
    }
}
