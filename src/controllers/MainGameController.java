package controllers;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;

/**
 * @author Slimane Siroukane
 * @author Fatima Chikh
 */
public class MainGameController {
    public Button genericButton;
    public Button nimButton;
    public Button gameButton;
    public Button aboutButton;
    public Button quitButton;

    public void quit(ActionEvent actionEvent) {
        System.exit(0);
    }

}
