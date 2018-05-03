package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.shape.*;
import models.*;

import java.net.URL;
import java.util.*;

public class GameController implements Initializable {

    public HBox toolbar;

    public ChoiceBox modeChoiceBox;

    public Button nodeButton;
    public Button edgeButton;
    public Button controlButton;
    public Button selectButton;
    public Button cutButton;
    public Button copyButton;
    public Button pasteButton;
    public Button duplicateButton;
    public Button saveButton;
    public Button deleteButton;
    public Button undoButton;
    public Button redoButton;
    public Button moveButton;

    public ListView listDesigns;

    public Pane drawPanel;

    /*
        current Mode: {
            -1: no mode selected,
            0: Generic edition mode selected,
            1: Nim edition mode,
            2: Game mode.
        }
     */
    public int currentMode;
    public int currentAction;

    public final int NODE = 0;
    public final int EDGE = 1;
    public final int CONTROL = 2;
    public final int SELECT = 3;
    public final int CUT = 4;
    public final int COPY = 5;
    public final int PASTE = 6;
    public final int DUPLICATE = 7;
    public final int SAVE = 8;
    public final int DELETE = 9;
    public final int UNDO = 10;
    public final int REDO = 11;
    public final int MOVE = 12;

    public boolean[] oldButtonsDisabledProperty = {
            false, // node Button disabled propriety
            false, // edge Button disabled propriety
            false, // control Button disabled propriety
            false, // select Button disabled propriety
            true, // cut Button disabled propriety
            true, // copy Button disabled propriety
            true, // paste Button disabled propriety
            true, // duplicate Button disabled propriety
            true, // save Button disabled propriety
            false, // delete Button disabled propriety
            true, // undo Button disabled propriety
            true,  // redo Button disabled propriety
            false // move Button disabled propriety
    };

    public List<Button> buttons;

    public volatile List<Node> nodes;
    public volatile List<Edge> edges;

    public volatile List<Node> edgeNodes;

    double pressedX, pressedY;
    double pressedNode1X, pressedNode1Y,
            pressedNode2X, pressedNode2Y,
            pressedNode3X, pressedNode3Y,
            pressedNode4X, pressedNode4Y;

    public volatile List<Shape> edgeControlTrash;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        modeChoiceBox.getSelectionModel().selectedIndexProperty().addListener((ov, oldVal, newVal) -> {
            System.out.println(modeChoiceBox.getItems().get(newVal.intValue()));
            setMode();
        });
        drawPanel.setOnMouseClicked(this::onMouseClicked);
        buttons = new ArrayList<>(
                Arrays.asList(
                        nodeButton,
                        edgeButton,
                        controlButton,
                        selectButton,
                        cutButton,
                        copyButton,
                        pasteButton,
                        duplicateButton,
                        saveButton,
                        deleteButton,
                        undoButton,
                        redoButton,
                        moveButton
                )
        );

        nodes = new ArrayList<>();
        edges = new ArrayList<>();
        edgeNodes = new ArrayList<>();
        edgeControlTrash = new ArrayList<>();

        setMode();
        currentAction = -1;
    }

    private void checkReset() {
        System.out.println("checkReset");
        for (Shape shape : edgeControlTrash) {
            drawPanel.getChildren().remove(shape);
        }
        edgeControlTrash.clear();
    }

    private void removeEdge(Edge edge) {
        drawPanel.getChildren().remove(edge);
        edges.remove(edge);
        edge.startNode.getEdges().remove(edge);
        edge.endNode.getEdges().remove(edge);
    }

    /**
     * Method to update current mode depend on selected one
     */
    public void setMode() {
        currentMode = modeChoiceBox.getSelectionModel().getSelectedIndex();
        System.out.println(currentMode);

        if (currentMode == 0) {
            for (int i = 0; i < buttons.size(); i++) {
                buttons.get(i).setDisable(oldButtonsDisabledProperty[i]);
            }
        } else {
            for (int i = 0; i < buttons.size(); i++) {
                buttons.get(i).setDisable(true);
            }
        }
    }

    /**
     * Method to set current action on pane depend current action
     *
     * @param x
     * @param y
     */
    public void setPaneCurrentAction(double x, double y) {
        switch (currentAction) {
            case NODE:
                // TODO  node action

                addNode(x, y);

                break;
            case EDGE:
                // TODO  edge action

//                Path path = createPath();
//                drawPanel.getChildren().add(path);
                break;
            case CONTROL:
                // TODO  control action

                break;
            case SELECT:
                // TODO  select action

                break;
            case CUT:
                // TODO cut action

                pasteButton.setDisable(false);
                oldButtonsDisabledProperty[PASTE] = false;
                break;
            case COPY:
                // TODO copy action

                pasteButton.setDisable(false);
                oldButtonsDisabledProperty[PASTE] = false;
                break;
            case PASTE:
                // TODO paste action

                break;
            case DUPLICATE:
                // TODO duplicate action

                break;
            case SAVE:
                // TODO save action

                break;
            case DELETE:
                // TODO  delete action

                break;
            case UNDO:
                // TODO undo action

                break;
            case REDO:
                // TODO redo action

                break;
            default:

                break;
        }
    }

    public void addNode(double x, double y) {
        Node node = new Node(x, y);
        node.setOnMouseClicked(this::onMouseClicked);
        node.setOnMousePressed(this::onMousePressed);
        node.setOnMouseDragged(this::onMouseDragged);

        nodes.add(node);
        drawPanel.getChildren().add(node);
        node.toFront();
    }

    public void addControlToEdge(Edge edge) {
        System.out.println("addControlToEdge");
        if (edgeControlTrash.size() != 0) {
            checkReset();
        }

        /**
         * Prepare control 1
         */
        edge.control1.setOnMousePressed(this::onMousePressed);
        edge.control1.setOnMouseDragged(this::onMouseDragged);
        Line startLine = new Line(
                edge.startNode.getCenterX(),
                edge.startNode.getCenterY(),
                edge.control1.getCenterX(),
                edge.control1.getCenterY()
        );
        startLine.startXProperty().bind(edge.startNode.centerXProperty());
        startLine.startYProperty().bind(edge.startNode.centerYProperty());
        startLine.endXProperty().bind(edge.control1.centerXProperty());
        startLine.endYProperty().bind(edge.control1.centerYProperty());

        /**
         * Prepare control 2
         */
        edge.control2.setOnMousePressed(this::onMousePressed);
        edge.control2.setOnMouseDragged(this::onMouseDragged);
        Line endLine = new Line(
                edge.endNode.getCenterX(),
                edge.endNode.getCenterY(),
                edge.control2.getCenterX(),
                edge.control2.getCenterY()
        );
        endLine.startXProperty().bind(edge.endNode.centerXProperty());
        endLine.startYProperty().bind(edge.endNode.centerYProperty());
        endLine.endXProperty().bind(edge.control2.centerXProperty());
        endLine.endYProperty().bind(edge.control2.centerYProperty());

        edgeControlTrash.add(edge.control1);
        edgeControlTrash.add(edge.control2);
        edgeControlTrash.add(startLine);
        edgeControlTrash.add(endLine);

        drawPanel.getChildren().addAll(edgeControlTrash);

        edge.control1.toFront();
        edge.control2.toFront();
    }

    public void onMouseClicked(MouseEvent mouseEvent) {
        if (mouseEvent.getSource() instanceof Pane) { // Pane get clicked
            System.out.println("onMouseClicked Pane");
            double x = mouseEvent.getX();
            double y = mouseEvent.getY();
            setPaneCurrentAction(x, y);
        } else if (mouseEvent.getSource() instanceof Node) { // Node get clicked
            System.out.println("onMouseClicked Node");
            Node currentNode = (Node) mouseEvent.getSource();
            if (currentAction == EDGE) {
                if (edgeNodes.size() > 1) {
                    edgeNodes.clear();
                }
                edgeNodes.add(currentNode);
                if (edgeNodes.size() == 2) {
                    Edge edge = new Edge(edgeNodes.get(0), edgeNodes.get(1));
                    edge.setOnMouseClicked(this::onMouseClicked);
                    edge.setOnMousePressed(this::onMousePressed);
                    edge.setOnMouseDragged(this::onMouseDragged);
                    edges.add(edge);
                    drawPanel.getChildren().add(edge);
                    edge.toBack();
                }
            }else if (currentAction == DELETE){
                List<Edge> edges = currentNode.getEdges();
                System.out.println("Number of connected edges : " + edges.size());
                while(edges.size() != 0){
                    removeEdge(edges.get(0));
                }
                drawPanel.getChildren().remove(currentNode);
                nodes.remove(currentNode);
            }
        } else if (mouseEvent.getSource() instanceof Edge) { // edge get clicked
            System.out.println("onMouseClicked Edge");
            Edge currentEdge = (Edge) mouseEvent.getSource();
            if (currentAction == CONTROL) {
                addControlToEdge(currentEdge);
            }else if(currentAction == DELETE){
                removeEdge(currentEdge);
            }
        }
    }

    public void onMousePressed(MouseEvent mouseEvent) {
        pressedX = mouseEvent.getX();
        pressedY = mouseEvent.getY();
        if (mouseEvent.getSource() instanceof Node) {
            System.out.println("onMousePressed Node");
            Node currentNode = (Node) mouseEvent.getSource();
            if (((currentAction == CONTROL) && currentNode.isControl()) || ((currentAction == MOVE) && currentNode.isNode())) {
                pressedNode1X = currentNode.getCenterX();
                pressedNode1Y = currentNode.getCenterY();
            }
        } else if (mouseEvent.getSource() instanceof Edge) {
            System.out.println("onMousePressed Edge");
            Edge currentEdge = (Edge) mouseEvent.getSource();
            if (currentAction == MOVE) {
                pressedNode1X = currentEdge.startNode.getCenterX();
                pressedNode1Y = currentEdge.startNode.getCenterY();
                pressedNode2X = currentEdge.endNode.getCenterX();
                pressedNode2Y = currentEdge.endNode.getCenterY();
                pressedNode3X = currentEdge.control1.getCenterX();
                pressedNode3Y = currentEdge.control1.getCenterY();
                pressedNode4X = currentEdge.control2.getCenterX();
                pressedNode4Y = currentEdge.control2.getCenterY();
            }
        }
    }

    public void onMouseDragged(MouseEvent mouseEvent) {
        if (mouseEvent.getSource() instanceof Node) {
//            System.out.println("onMouseDragged Node");
            Node currentNode = (Node) mouseEvent.getSource();
            if (((currentAction == CONTROL) && currentNode.isControl()) || ((currentAction == MOVE) && currentNode.isNode())) {
                currentNode.setCenter(
                        pressedNode1X - pressedX + mouseEvent.getX(),
                        pressedNode1Y - pressedY + mouseEvent.getY()
                );
            }
        } else if (mouseEvent.getSource() instanceof Edge) {
            System.out.println("onMouseDragged Edge");
            Edge currentEdge = (Edge) mouseEvent.getSource();
            if (currentAction == MOVE) {
                currentEdge.startNode.setCenter(
                        pressedNode1X - pressedX + mouseEvent.getX(),
                        pressedNode1Y - pressedY + mouseEvent.getY()
                );
                currentEdge.endNode.setCenter(
                        pressedNode2X - pressedX + mouseEvent.getX(),
                        pressedNode2Y - pressedY + mouseEvent.getY()
                );
                currentEdge.control1.setCenter(
                        pressedNode3X - pressedX + mouseEvent.getX(),
                        pressedNode3Y - pressedY + mouseEvent.getY()
                );
                currentEdge.control2.setCenter(
                        pressedNode4X - pressedX + mouseEvent.getX(),
                        pressedNode4Y - pressedY + mouseEvent.getY()
                );
            }
        }
    }

    /***************************************************************************
     *
     *  onAction handlers for fxml buttons
     *
     ***************************************************************************/

    public void nodeAction(ActionEvent actionEvent) {
        currentAction = buttons.indexOf((Button) actionEvent.getSource());

        System.out.println("nodeAction");
    }

    public void edgeAction(ActionEvent actionEvent) {
        currentAction = buttons.indexOf((Button) actionEvent.getSource());

        System.out.println("edgeAction");
    }

    public void selectAction(ActionEvent actionEvent) {
        currentAction = buttons.indexOf((Button) actionEvent.getSource());

        System.out.println("selectAction");
    }

    public void cutAction(ActionEvent actionEvent) {
        currentAction = buttons.indexOf((Button) actionEvent.getSource());

        pasteButton.setDisable(false);
        oldButtonsDisabledProperty[PASTE] = false;

        System.out.println("cutAction");
    }

    public void copyAction(ActionEvent actionEvent) {
        currentAction = buttons.indexOf((Button) actionEvent.getSource());

        pasteButton.setDisable(false);
        oldButtonsDisabledProperty[PASTE] = false;

        System.out.println("copyAction");
    }

    public void pasteAction(ActionEvent actionEvent) {
        currentAction = buttons.indexOf((Button) actionEvent.getSource());

        System.out.println("pasteAction");
    }

    public void duplicateAction(ActionEvent actionEvent) {
        currentAction = buttons.indexOf((Button) actionEvent.getSource());

        System.out.println("duplicateAction");
    }

    public void saveAction(ActionEvent actionEvent) {
        currentAction = buttons.indexOf((Button) actionEvent.getSource());

        System.out.println("saveAction");
    }

    public void deleteAction(ActionEvent actionEvent) {
        currentAction = buttons.indexOf((Button) actionEvent.getSource());

        System.out.println("deleteAction");
    }

    public void undoAction(ActionEvent actionEvent) {
        currentAction = buttons.indexOf((Button) actionEvent.getSource());

        System.out.println("undoAction");
    }

    public void redoAction(ActionEvent actionEvent) {
        currentAction = buttons.indexOf((Button) actionEvent.getSource());

        System.out.println("redoAction");
    }

    public void moveAction(ActionEvent actionEvent) {
        currentAction = buttons.indexOf((Button) actionEvent.getSource());

        checkReset();
        System.out.println("moveAction");
    }

    public void controlAction(ActionEvent actionEvent) {
        currentAction = buttons.indexOf((Button) actionEvent.getSource());

        System.out.println("controlAction");
    }
}