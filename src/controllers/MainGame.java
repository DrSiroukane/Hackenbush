package controllers;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainGame extends Application {
    public Scene primaryScene, secondaryScene;

    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader mainGameFXMLLoader = new FXMLLoader(getClass().getResource("../views/MainGame.fxml"));
        Parent mainGamelayout = mainGameFXMLLoader.load();
        MainGameController mainGameController = mainGameFXMLLoader.getController();

        mainGameController.genericButton.setOnAction(e -> {
            try {
                FXMLLoader modeGenericFXMLLoader = new FXMLLoader(getClass().getResource("../views/ModeGeneric.fxml"));
                Parent modeGenericLayout = modeGenericFXMLLoader.load();
                ModeGenericController modeGenericController = modeGenericFXMLLoader.getController();

                modeGenericController.switchModeMenuItem.setOnAction(e1 -> {
                    secondaryScene = null;
                    primaryStage.setScene(primaryScene);
                });

                secondaryScene = new Scene(modeGenericLayout);
                primaryStage.setScene(secondaryScene);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        });

        mainGameController.gameButton.setOnAction(e -> {
            try {
                FXMLLoader modeGameFXMLLoader = new FXMLLoader(getClass().getResource("../views/ModeGame.fxml"));
                Parent modeGameLayout = modeGameFXMLLoader.load();
                ModeGameController modeGameController = modeGameFXMLLoader.getController();

                modeGameController.switchModeMenuItem.setOnAction(e1 -> {
                    secondaryScene = null;
                    primaryStage.setScene(primaryScene);
                });

                secondaryScene = new Scene(modeGameLayout);
                primaryStage.setScene(secondaryScene);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        });

        primaryScene = new Scene(mainGamelayout);
        primaryStage.setScene(primaryScene);
//        primaryStage.setResizable(false);
        primaryStage.setTitle("Hackenbush");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
