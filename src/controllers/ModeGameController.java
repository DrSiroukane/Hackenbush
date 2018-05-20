package controllers;

import helpers.FileGenerator;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import models.Edge;
import models.Node;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

/**
 * @author Slimane Siroukane
 * @author Fatima Chikh
 */
public class ModeGameController {
    public ListView listViewDesigns;
    public MenuItem switchModeMenuItem;

    public List<Node> nodes;
    public List<Edge> edges;
    public List<Pane> designs;

    void clearDrawSpace(){

    }
    public void newFileAction(ActionEvent actionEvent) {
        System.out.println("newFileAction");
        clearDrawSpace();
    }
    public void openFileAction(ActionEvent actionEvent) {
        System.out.println("openFileAction");
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File("./src/designs/"));
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("FXML File", "*.fxml")
        );
        List<File> files = fileChooser.showOpenMultipleDialog(
                ((MenuItem) actionEvent.getSource()).getParentPopup().getScene().getWindow());
        if (files != null) {
            for (int i = 0; i < files.size(); i++) {
                try {
                    Pane design = (Pane) FXMLLoader.load(Paths.get(files.get(i).getPath()).toUri().toURL());
                    designs.add(design);
                    listViewDesigns.getItems().add("Design " + i);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void quitAction(ActionEvent actionEvent) {
        System.out.println("quitAction");
        System.exit(0);
    }
}
