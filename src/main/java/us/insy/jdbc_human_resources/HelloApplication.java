package us.insy.jdbc_human_resources;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

public class HelloApplication extends Application {
    public static String user;
    public static String pass;
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("add.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        assert (args.length == 2);
        user = args[0];
        pass = args[1];

        launch();
    }

    public static void dbcTester(DatabaseConnector dbc) {
        try {
            ResultSet rs = dbc.executeStatement("SELECT * FROM t_city");
            while (rs.next()) {
                System.out.println(rs.getString("name"));
            }
            System.out.println("geht nu");
        } catch (Exception ignored) {}
    }
}