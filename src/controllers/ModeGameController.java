package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Path;
import javafx.scene.shape.StrokeLineCap;
import javafx.stage.FileChooser;
import models.Edge;
import models.Node;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * @author Slimane Siroukane
 * @author Fatima Chikh
 */
public class ModeGameController implements Initializable {
    public ListView listViewDesigns;
    public MenuItem switchModeMenuItem;
    public Pane drawPanel;
    public MenuItem undoMenuItem;
    public MenuItem redoMenuItem;
    public MenuItem startMenuItem;
    public MenuItem restartMenuItem;
    public RadioMenuItem hxHRadioMenuItem;
    public RadioMenuItem hxBRadioMenuItem;
    public RadioMenuItem redRadioMenuItem;
    public RadioMenuItem blueRadioMenuItem;
    public Label notificationLabel;

    public Line connectionLine;
    public int connectionLinePosition = 460;

    public final int HUMAN = 0;
    public final int BOT = 1;

    public final int RED = 0;
    public final int BLUE = 1;
    public final int GREEN = 2;
    public final Color[] COLORS = {Color.RED, Color.BLUE, Color.GREEN};

    public final int NO_PLAY = 0;
    public final int PLAY = 1;

    public int currentOpponent = 0;
    public int currentColor = 0;
    public int currentMode = 0;
    public int currentDesign = -1;
    public int currentDo = -1;

    public List<Node> nodes;
    public List<Edge> edges;
    public List<Node> rootNodes;
    public List<Node> connectedNodes;
    public List<Edge> connectedEdges;
    public List<List<Node>> removedNodes;
    public List<List<Edge>> removedEdges;
    public List<Pane> designs;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // initialize options
        undoMenuItem.setDisable(true);
        redoMenuItem.setDisable(true);
        restartMenuItem.setDisable(true);
        startMenuItem.setDisable(true);
        hxHRadioMenuItem.setDisable(true);
        hxBRadioMenuItem.setDisable(true);
        redRadioMenuItem.setDisable(true);
        blueRadioMenuItem.setDisable(true);
        // initialize lists
        nodes = new ArrayList<>();
        rootNodes = new ArrayList<>();
        edges = new ArrayList<>();
        connectedNodes = new ArrayList<>();
        connectedEdges = new ArrayList<>();
        removedNodes = new ArrayList<>();
        removedEdges = new ArrayList<>();
        designs = new ArrayList<>();
        // create connection line
        connectionLine = new Line();
        connectionLine.setStroke(Color.BLACK);
        connectionLine.setStrokeWidth(10);
        connectionLine.getStrokeDashArray().addAll(20d, 10d);
        connectionLine.setStrokeLineCap(StrokeLineCap.BUTT);
        connectionLine.setStartX(0);
        connectionLine.setEndX(760);
        connectionLine.setStartY(connectionLinePosition);
        connectionLine.setEndY(connectionLinePosition);
        connectionLine.getStyleClass().add("connected-line");
        connectionLine.toBack();
        drawPanel.getChildren().add(connectionLine);
        // set handler
        listViewDesigns.setOnMouseClicked(this::onMouseClicked);
        drawPanel.getStylesheets().add(getClass()
                .getResource("../views/css/modeGeneric.css").toExternalForm());
    }

    private void clearDrawSpace() {
        drawPanel.getChildren().removeAll(nodes);
        nodes.clear();
        drawPanel.getChildren().removeAll(edges);
        edges.clear();
        clearConnectedElement();
        rootNodes.clear();
        removedNodes.clear();
        removedEdges.clear();
        currentDo = -1;
        currentMode = NO_PLAY;
        restartMenuItem.setDisable(true);
        undoMenuItem.setDisable(true);
        redoMenuItem.setDisable(true);
    }

    private void clearConnectedElement() {
        connectedNodes.clear();
        connectedEdges.clear();
    }

    private void fillDrawSpace(int index) {
        clearDrawSpace();
        Pane pane = designs.get(index);
        List<javafx.scene.Node> designNode = pane.getChildren();
        for (javafx.scene.Node node : designNode) {
            try {
                Circle circle = (Circle) node;
                Node node1 = new Node(circle);
                nodes.add(node1);
                if (node1.isRootNode()) {
                    rootNodes.add(node1);
                }
            } catch (ClassCastException e) {
//                System.out.println("Node is not a Circle");
            }
        }
        for (javafx.scene.Node node_ : designNode) {
            try {
                Path path = (Path) node_;
                Edge edge = new Edge(path, nodes);
                edge.setOnMouseClicked(this::onMouseClicked);
            } catch (ClassCastException e) {
//                System.out.println("Node is not a Path");
            }
        }
        checkConnectivity(null);
        edges.addAll(connectedEdges);
        nodes.clear();
        nodes.addAll(connectedNodes);
        drawPanel.getChildren().addAll(edges);
        drawPanel.getChildren().addAll(nodes);
        System.out.println("Nbr Roots " + rootNodes.size());
        if (!edges.isEmpty()) {
            startMenuItem.setDisable(false);
            hxHRadioMenuItem.setDisable(false);
            hxBRadioMenuItem.setDisable(false);
            redRadioMenuItem.setDisable(false);
            blueRadioMenuItem.setDisable(false);
        }
    }

    public void checkConnectivity(Node node) {
        if (node == null) {
            for (Node node1 : rootNodes) {
                checkConnectivity(node1);
            }
        } else if (!connectedNodes.contains(node)) {
            if (0 < node.getEdges().size()) {
                connectedNodes.add(node);
//                node.getStyleClass().add("node-selected");
                for (Edge edge : node.getEdges()) {
                    if (!connectedEdges.contains(edge)) {
                        connectedEdges.add(edge);
//                    edge.getStyleClass().add("edge-selected");
                        checkConnectivity((edge.edgeNodes[0].equals(node)) ? edge.edgeNodes[1] : edge.edgeNodes[0]);
                    }
                }
            }
        }
    }

    public void switchPlayer() {
        currentColor = (currentColor + 1) % 2;
        System.out.println("Red Edge : " + countEdges(Color.RED) +
                " Blue Edge : " + countEdges(Color.BLUE));
        if ((0 == countEdges(Color.RED)) && (countEdges(Color.BLUE) == 0)) {
            currentMode = NO_PLAY;
            notificationLabel.setText("Game Over, No One Win");
        } else if (countEdges(Color.BLUE) == 0) {
            currentMode = NO_PLAY;
            notificationLabel.setText("Game Over, Red Win");
        } else if (countEdges(Color.RED) == 0) {
            currentMode = NO_PLAY;
            notificationLabel.setText("Game Over, Blue Win");
        } else {
            notificationLabel.setText(((currentColor == RED) ? "Red" : "Blue") + " turn");
        }
    }

    private void onMouseClicked(MouseEvent mouseEvent) {
        if (mouseEvent.getSource() instanceof ListView) {
            currentDesign = listViewDesigns.getSelectionModel().getSelectedIndex();
            if (currentDesign != -1) {
                fillDrawSpace(currentDesign);
            }
        } else if (mouseEvent.getSource() instanceof Edge) {
            Edge edge = (Edge) mouseEvent.getSource();
            if ((currentMode == PLAY)) {
                if (edge.getStroke().equals(COLORS[GREEN]) || edge.getStroke().equals(COLORS[currentColor])) {
                    drawPanel.getChildren().remove(edge);
                    edges.remove(edge);
                    for (int i = 0; i < edge.nbrEdgeNodes; i++) {
                        edge.edgeNodes[i].getEdges().remove(edge);
                    }
                    clearConnectedElement();
                    checkConnectivity(null);

                    List<Node> removedNodes = new ArrayList<>();
                    removedNodes.addAll(nodes);
                    removedNodes.removeAll(connectedNodes);
                    drawPanel.getChildren().removeAll(removedNodes);
                    nodes.removeAll(removedNodes);

                    List<Edge> removedEdges = new ArrayList<>();
                    removedEdges.addAll(edges);
                    removedEdges.removeAll(connectedEdges);
                    drawPanel.getChildren().removeAll(removedEdges);
                    edges.removeAll(removedEdges);
                    System.out.println("removed size : " + this.removedNodes.size());
                    System.out.println("current do : " + this.currentDo);
                    while ((this.removedNodes.size() != (currentDo + 1)) &&
                            (this.removedEdges.size() != (currentDo + 1))) {
                        this.removedNodes.remove(currentDo + 1);
                        this.removedEdges.remove(currentDo + 1);
                    }
                    removedEdges.add(edge);
                    this.removedNodes.add(removedNodes);
                    this.removedEdges.add(removedEdges);
                    currentDo++;
                    redoMenuItem.setDisable(true);
                    undoMenuItem.setDisable(false);
                    switchPlayer();
                }
            }
        }
    }

    public int countEdges(Color color) {
        int count = 0;
        for (Edge edge : edges) {
            if (edge.getStroke().equals(color)) {
                count++;
            }
        }
        return count;
    }

    /***************************************************************************
     *
     *  onAction handlers for different option (OpenFile, Start, ...)
     *
     ***************************************************************************/
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
                    File file = files.get(i);
                    Pane design = FXMLLoader.load(file.toURI().toURL());
                    designs.add(design);
                    listViewDesigns.getItems().add(file.getName());
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

    public void undoAction(ActionEvent actionEvent) {
        if (currentDo != -1) {
            nodes.addAll(removedNodes.get(currentDo));
            edges.addAll(removedEdges.get(currentDo));
            drawPanel.getChildren().addAll(removedNodes.get(currentDo));
            drawPanel.getChildren().addAll(removedEdges.get(currentDo));
            for (Edge edge : removedEdges.get(currentDo)) {
                edge.toBack();
                edge.edgeNodes[0].getEdges().add(edge);
                edge.edgeNodes[1].getEdges().add(edge);
            }
            currentDo--;
            if (currentDo == -1) {
                undoMenuItem.setDisable(true);
            }
            redoMenuItem.setDisable(false);
            switchPlayer();
            if (currentMode == NO_PLAY) {
                currentMode = PLAY;
            }
        }
    }

    public void redoAction(ActionEvent actionEvent) {
        if (currentDo != (removedNodes.size() - 1)) {
            currentDo++;
            nodes.removeAll(removedNodes.get(currentDo));
            edges.removeAll(removedEdges.get(currentDo));
            drawPanel.getChildren().removeAll(removedNodes.get(currentDo));
            drawPanel.getChildren().removeAll(removedEdges.get(currentDo));
            if (currentDo == (removedNodes.size() - 1)) {
                redoMenuItem.setDisable(true);
            }
            undoMenuItem.setDisable(false);
            switchPlayer();
        }
    }

    public void startAction(ActionEvent actionEvent) {
        restartMenuItem.setDisable(false);
        startMenuItem.setDisable(true);
        hxHRadioMenuItem.setDisable(true);
        hxBRadioMenuItem.setDisable(true);
        redRadioMenuItem.setDisable(true);
        blueRadioMenuItem.setDisable(true);
        currentMode = PLAY;
        String notification = "Start Game (Human .vs " +
                ((currentOpponent == HUMAN) ? "Human" : "Bot") + ") - " +
                ((currentColor == RED) ? "Red " : "Blue ") + " start playing";
        notificationLabel.setText(notification);
    }

    public void restartAction(ActionEvent actionEvent) {
        restartMenuItem.setDisable(true);
        startMenuItem.setDisable(false);
        hxHRadioMenuItem.setDisable(false);
        hxBRadioMenuItem.setDisable(false);
        redRadioMenuItem.setDisable(false);
        blueRadioMenuItem.setDisable(false);
        currentMode = NO_PLAY;
        fillDrawSpace(currentDesign);
        notificationLabel.setText("Hit Start to play again ...");
    }

    public void setOpponentAction(ActionEvent actionEvent) {
        if (actionEvent.getSource().equals(hxHRadioMenuItem)) {
            currentOpponent = HUMAN;
        } else if (actionEvent.getSource().equals(hxBRadioMenuItem)) {
            currentOpponent = BOT;
        }
        System.out.println("currentOpponent " + currentOpponent);
    }

    public void setStartColorAction(ActionEvent actionEvent) {
        if (actionEvent.getSource().equals(redRadioMenuItem)) {
            currentColor = RED;
        } else if (actionEvent.getSource().equals(blueRadioMenuItem)) {
            currentColor = BLUE;
        }
        System.out.println("currentColor " + currentColor);
    }
}
