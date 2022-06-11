package us.insy.jdbc_human_resources;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import org.kordamp.bootstrapfx.BootstrapFX;

import java.io.IOException;
import java.net.URL;
import java.util.Collection;

public class MainController {
    public HBox hBoxMainArea;

    public void initialize() throws IOException {

        setSceneOnPane("view-view.fxml");
    }

    @FXML
    private void onMenuViewClicked(ActionEvent e) throws IOException {
        System.out.println("Asd");
        setSceneOnPane("view-view.fxml");
    }

    @FXML
    private void onMenuEditClicked() {

        System.out.println("ASAAA");
    }

    @FXML
    private void onMenuAddClicked() {

        System.out.println("ASAAA");
        System.out.println("ASAAA");
        System.out.println("ASAAA");
    }

    private void setSceneOnPane(String fileName) throws IOException {
//        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource(fileName));
//        Scene scene = new Scene(fxmlLoader.load());
//        scene.getStylesheets().add(BootstrapFX.bootstrapFXStylesheet());
//        paneFXView.getChildren()
        hBoxMainArea.getChildren().clear();
        URL u = MainApplication.class.getResource(fileName);
        System.out.println(u);
        assert u != null;
        Pane pane = FXMLLoader.load(u);
        hBoxMainArea.getChildren().add(pane);

    }
}