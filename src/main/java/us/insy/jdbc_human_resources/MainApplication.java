package us.insy.jdbc_human_resources;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.kordamp.bootstrapfx.BootstrapFX;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

public class MainApplication extends Application {
    public static String user;
    public static String pass;
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("main-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("SHEEESH!");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        System.out.println(Arrays.toString(args));
        user = args[0];
        pass = args[1];

        DatabaseConnector dbc = DatabaseConnector.getInstance();
        ResultSet rs = dbc.executeStatement("SELECT * FROM t_city");
        try {
            while (rs.next()) {
                System.out.println(rs.getString("name"));
            }
        } catch (Exception ignored) {}

        launch();
    }
}