package controllers;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import models.Edge;
import models.Node;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

public class GameController implements Initializable {

    public HBox toolbar;

    public ChoiceBox modeChoiceBox;

    public Button nodeButton;
    public Button edgeButton;
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
    public final int SELECT = 2;
    public final int CUT = 3;
    public final int COPY = 4;
    public final int PASTE = 5;
    public final int DUPLICATE = 6;
    public final int SAVE = 7;
    public final int DELETE = 8;
    public final int UNDO = 9;
    public final int REDO = 10;
    public final int MOVE = 11;

    public boolean[] oldButtonsDisabledProperty = {
            false, // node Button disabled proprity
            false, // edge Button disabled proprity
            false, // select Button disabled proprity
            true, // cut Button disabled proprity
            true, // copy Button disabled proprity
            true, // paste Button disabled proprity
            true, // duplicate Button disabled proprity
            true, // save Button disabled proprity
            false, // delete Button disabled proprity
            true, // undo Button disabled proprity
            true,  // redo Button disabled proprity
            false // move Button disabled propority
    };

    public List<Button> buttons;

    public volatile List<Node> nodes;
    public volatile List<Edge> edges;

    public volatile List<Node> edgeNodes;

    double pressedX, pressedY;
    double draggedX, draggedY;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        modeChoiceBox.getSelectionModel().selectedIndexProperty().addListener((ov, oldVal, newVal) -> {
            System.out.println(modeChoiceBox.getItems().get(newVal.intValue()));
            setMode();
        });

        drawPanel.setOnMouseClicked(mouseEvent -> {
            double x = mouseEvent.getX();
            double y = mouseEvent.getY();

            setPaneCurrentAction(x,y);
        });

        buttons = new ArrayList<>(
                Arrays.asList(
                        nodeButton,
                        edgeButton,
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

        setMode();
        currentAction = -1;

//        createPath();
    }

    /**
     * Method to update current mode depend on selected one
     */
    public void setMode(){
        currentMode = modeChoiceBox.getSelectionModel().getSelectedIndex();
        System.out.println(currentMode);

        if(currentMode == 0){
            for(int i=0; i<buttons.size(); i++){
                buttons.get(i).setDisable(oldButtonsDisabledProperty[i]);
            }
        }else{
            for(int i=0; i<buttons.size(); i++){
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
    public void setPaneCurrentAction(double x, double y){
        switch(currentAction) {
            case NODE:
                // TODO  node action

                addNode(x,y);

                break;
            case EDGE:
                // TODO  edge action

//                Path path = createPath();
//                drawPanel.getChildren().add(path);
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

    public void addNode(double x, double y){
        Node node = new Node(x, y);

        node.getStyleClass().add("node-style");

        node.setOnMouseClicked(mouseEvent -> {
            if(currentAction == EDGE){
                System.out.println(edgeNodes.size());

                if(edgeNodes.size() > 1){
                    edgeNodes.clear();
                }

                System.out.println(edgeNodes.size());

                Node clickedNode = (Node) mouseEvent.getSource();

                edgeNodes.add(clickedNode);

                if(edgeNodes.size() == 2){
                    Edge edge = new Edge(edgeNodes.get(0), edgeNodes.get(1));
                    edge.setOnMouseClicked(mouseEdgeEvent -> {
                        if(currentAction == EDGE){
                            Edge currentEdge = (Edge) mouseEdgeEvent.getSource();
                            addNode(currentEdge);
                        }
                    });

                    edges.add(edge);
                    drawPanel.getChildren().add(edge);
                    edge.toBack();
                }
            }
        });

        node.setOnMousePressed(mouseEvent -> {
            if(currentAction == MOVE){
                Node currentNode = (Node) mouseEvent.getSource();
                pressedX = mouseEvent.getX();
                pressedY = mouseEvent.getY();
                draggedX = currentNode.getCenterX();
                draggedY = currentNode.getCenterY();
            }
        });

        node.setOnMouseDragged(mouseEvent -> {
            if(currentAction == MOVE) {
                Node currentNode = (Node) mouseEvent.getSource();
                currentNode.setCenterX(draggedX - pressedX + mouseEvent.getX());
                currentNode.setCenterY(draggedY - pressedY + mouseEvent.getY());

            }
        });

        nodes.add(node);

        drawPanel.getChildren().add(node);

        node.toFront();
    }

    public void addNode(Edge edge){
        edge.control1.getStyleClass().add("node-tangent");
        edge.control1.setOnMousePressed(mouseEvent -> {
            if(currentAction == EDGE){
                Node currentNode = (Node) mouseEvent.getSource();
                pressedX = mouseEvent.getX();
                pressedY = mouseEvent.getY();
                draggedX = currentNode.getCenterX();
                draggedY = currentNode.getCenterY();
            }
        });
        edge.control1.setOnMouseDragged(mouseEvent -> {
            if(currentAction == EDGE) {
                Node currentNode = (Node) mouseEvent.getSource();
                currentNode.setCenterX(draggedX - pressedX + mouseEvent.getX());
                currentNode.setCenterY(draggedY - pressedY + mouseEvent.getY());

            }
        });

        edge.control2.getStyleClass().add("node-tangent");
        edge.control2.setOnMousePressed(mouseEvent -> {
            if(currentAction == EDGE){
                Node currentNode = (Node) mouseEvent.getSource();
                pressedX = mouseEvent.getX();
                pressedY = mouseEvent.getY();
                draggedX = currentNode.getCenterX();
                draggedY = currentNode.getCenterY();
            }
        });
        edge.control2.setOnMouseDragged(mouseEvent -> {
            if(currentAction == EDGE) {
                Node currentNode = (Node) mouseEvent.getSource();
                currentNode.setCenterX(draggedX - pressedX + mouseEvent.getX());
                currentNode.setCenterY(draggedY - pressedY + mouseEvent.getY());

            }
        });

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

        drawPanel.getChildren().addAll(edge.control1, edge.control2, startLine, endLine);

        edge.control1.toFront();
        edge.control2.toFront();
    }

    private Path createPath() {

        double startX = 30;
        double startY = 20;

        double controlX1 = 30;
        double controlY1 = 260;

        double controlX2 = 300;
        double controlY2 = 20;

        double endX = 200;
        double endY = 200;


       /* double controlX12 = 26;
        double controlY12 = 31;
        double controlX22 = 92;
        double controlY22 = 92;
        double endX2 = 250;
        double endY2 = 150;*/


        Node node1 = new Node(startX,startY);
        Node node2 = new Node(controlX1,controlY1);
        Node node3 = new Node(controlX2,controlY2);
        Node node4 = new Node(endX, endY);

//        Edge edge1 = new Edge(node1, node2);
        Edge edge2 = new Edge(node1, node4);


        /*Node node12 = new Node(endX,endY);
        Node node22 = new Node(controlX12,controlY12);
        Node node32 = new Node(controlX12,controlY12);
        Node node42 = new Node(endX2, endY2);*/

        /*Edge edge12 = new Edge(node12, node22);
        Edge edge22 = new Edge(node42, node32);*/

        drawPanel.getChildren().addAll(
                node1,
//                node2,
//                node3,
                node4,
                edge2.control1,
                edge2.control2,
//                edge1,
                edge2
        );

        Path path = new Path();
        /*path.setStroke(Color.RED);
        path.setStrokeWidth(10);
        path.getElements().addAll(
                new MoveTo(startX, startY),
                new CubicCurveTo(controlX1, controlY1, controlX2, controlY2, endX, endY)
        );
        drawPanel.getChildren().add(path);*/

        return path;
    }

    /***************************************************************************/
    /**
     *
     *  onAction handlers for fxml buttons
     *
     */
    /***************************************************************************/

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

        System.out.println("moveAction");
    }
}