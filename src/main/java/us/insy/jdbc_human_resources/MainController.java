package us.insy.jdbc_human_resources;

import javafx.event.ActionEvent;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.TilePane;
import javafx.stage.Popup;
import org.kordamp.bootstrapfx.BootstrapFX;

import java.io.IOException;
import java.net.URL;
import java.util.Collection;

public class MainController {
    public HBox hBoxMainArea;
    public Menu view;

    public void initialize() throws IOException {
        setSceneOnPane("view-view.fxml");
    }

    @FXML
    private void onMenuViewOpen(ActionEvent e) throws IOException {
        setSceneOnPane("view-view.fxml");
    }

    @FXML
    private void onMenuEditClicked() throws IOException {
        setSceneOnPane("edit-view.fxml");
    }

    @FXML
    private void onMenuAddClicked() throws IOException {
        setSceneOnPane("add.fxml");
    }



    public void onMenuEditOpen(ActionEvent actionEvent) {
    }

    public void onMenuAddOpen(ActionEvent actionEvent) {
    }


    public void onMenuClose(ActionEvent actionEvent) {
        hBoxMainArea.getChildren().clear();
    }

    private void setSceneOnPane(String fileName) throws IOException {
        hBoxMainArea.getChildren().clear();
        URL u = MainApplication.class.getResource(fileName);
        assert u != null;
        Pane pane = FXMLLoader.load(u);
        hBoxMainArea.getChildren().add(pane);
    }


}