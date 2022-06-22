package us.insy.jdbc_human_resources;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.TilePane;
import javafx.stage.Popup;
import javafx.stage.Stage;
import org.kordamp.bootstrapfx.BootstrapFX;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

public class MainApplication extends Application {
    public static String user;
    public static String pass;
    private static Stage stage;
    @Override
    public void start(Stage stage) throws IOException {
        MainApplication.stage = stage;
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("main-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("HR Manager");
        stage.setScene(scene);
        stage.show();
    }

    public static void errorBox(String errorMsg) {
        Popup popup = new Popup();
        Label label = new Label(errorMsg);
        label.setStyle("-fx-background-color: rgba(255, 127, 127, 127); -fx-alignment: center; -fx-padding: 10");
        label.setMinHeight(50);
        label.setMinWidth(50);
        popup.getContent().add(label);
        popup.setAutoHide(true);

        popup.show(stage);
    }

    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("ERROR: Not enough line arguments provided");
            System.out.println("args: <username> <password>");
            return;
        }

        user = args[0];
        pass = args[1];
        launch();
    }
}