package test;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;

public class test extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("GenerateFxml.fxml"));
        Pane design = (Pane) FXMLLoader.load(getClass().getResource("../designs/test1.fxml"));

        Pane pane = (Pane) root.lookup("#pane");
        if (pane != null) {
            pane.getChildren().addAll(design.getChildren());
        }

        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Hackenbush");
        primaryStage.show();
    }
}
