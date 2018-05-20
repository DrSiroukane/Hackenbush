package controllers;

import helpers.FileGenerator;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.shape.StrokeLineCap;
import javafx.stage.FileChooser;
import models.Edge;
import models.Node;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

public class ModeGenericController implements Initializable {
    public VBox toolbar;
    public Button nodeButton;
    public Button edgeButton;
    public Button controlButton;
    public Button selectButton;
    public Button cutButton;
    public Button copyButton;
    public Button pasteButton;
    public Button duplicateButton;
    public Button deleteButton;
    public Button changeColorButton;
    public Button moveButton;
    public Pane drawPanel;
    public MenuItem switchModeMenuItem;
    public MenuItem undoMenuItem;
    public MenuItem redoMenuItem;
    public MenuItem cutMenuItem;
    public MenuItem copyMenuItem;
    public MenuItem pasteMenuItem;
    public MenuItem duplicateMenuItem;
    public MenuItem deleteMenuItem;
    public MenuItem selectMenuItem;
    public MenuItem nodeMenuItem;
    public MenuItem edgeMenuItem;
    public MenuItem controlMenuItem;
    public MenuItem changeColorMenuItem;

    public Line connectionLine;
    public int connectionLinePosition = 460;

    public SimpleIntegerProperty currentAction;
    // actions
    public final int NODE = 0;
    public final int EDGE = 1;
    public final int CONTROL = 2;
    public final int SELECT = 3;
    public final int CUT = 4;
    public final int COPY = 5;
    public final int PASTE = 6;
    public final int DUPLICATE = 7;
    public final int DELETE = 8;
    public final int MOVE = 9;
    public final int CHANGE_COLOR = 10;
    public final int UNDO = 11;
    public final int REDO = 12;

    public SimpleBooleanProperty[] disabledProperties = {
            new SimpleBooleanProperty(false), // node disabled propriety
            new SimpleBooleanProperty(false), // edge disabled propriety
            new SimpleBooleanProperty(false), // control disabled propriety
            new SimpleBooleanProperty(false), // select disabled propriety
            new SimpleBooleanProperty(true), // cut disabled propriety
            new SimpleBooleanProperty(true), // copy disabled propriety
            new SimpleBooleanProperty(true), // paste disabled propriety
            new SimpleBooleanProperty(true), // duplicate disabled propriety
            new SimpleBooleanProperty(false), // delete disabled propriety
            new SimpleBooleanProperty(false), // move disabled propriety
            new SimpleBooleanProperty(false), // change color disabled propriety
            new SimpleBooleanProperty(true), // undo disabled propriety
            new SimpleBooleanProperty(true)  // redo disabled propriety
    };

    public List<Node> nodes;
    public List<Edge> edges;
    public List<Node> edgeNodes;
    public List<Shape> edgeControlTrash;
    public List<Node> selectNodes;
    public List<Edge> selectEdges;
    public List<Node> copyNodes;
    public List<Edge> copyEdges;
    public List<Pane> designs;

    public int copyAction = -1;

    Rectangle selectRectangle;
    double pressedX, pressedY;
    double[] drawSpaceIntervalX = {7d, 750d};
    double[] drawSpaceIntervalY = {7d, 460d};

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        drawPanel.setOnMouseClicked(this::onMouseClicked);
        drawPanel.setOnMousePressed(this::onMousePressed);
        drawPanel.setOnMouseDragged(this::onMouseDragged);
        // initialize lists
        nodes = new ArrayList<>();
        edges = new ArrayList<>();
        edgeNodes = new ArrayList<>();
        edgeControlTrash = new ArrayList<>();
        designs = new ArrayList<>();
        selectNodes = new ArrayList<>();
        selectEdges = new ArrayList<>();
        copyNodes = new ArrayList<>();
        copyEdges = new ArrayList<>();
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
        // bind button options
        nodeButton.disableProperty().bind(disabledProperties[NODE]);
        edgeButton.disableProperty().bind(disabledProperties[EDGE]);
        controlButton.disableProperty().bind(disabledProperties[CONTROL]);
        selectButton.disableProperty().bind(disabledProperties[SELECT]);
        cutButton.disableProperty().bind(disabledProperties[CUT]);
        copyButton.disableProperty().bind(disabledProperties[COPY]);
        pasteButton.disableProperty().bind(disabledProperties[PASTE]);
        duplicateButton.disableProperty().bind(disabledProperties[DUPLICATE]);
        deleteButton.disableProperty().bind(disabledProperties[DELETE]);
        moveButton.disableProperty().bind(disabledProperties[MOVE]);
        changeColorButton.disableProperty().bind(disabledProperties[CHANGE_COLOR]);
        // bind menuItem options
        undoMenuItem.disableProperty().bind(disabledProperties[UNDO]);
        redoMenuItem.disableProperty().bind(disabledProperties[REDO]);
        cutMenuItem.disableProperty().bind(disabledProperties[CUT]);
        copyMenuItem.disableProperty().bind(disabledProperties[COPY]);
        pasteMenuItem.disableProperty().bind(disabledProperties[PASTE]);
        duplicateMenuItem.disableProperty().bind(disabledProperties[DUPLICATE]);
        deleteMenuItem.disableProperty().bind(disabledProperties[DELETE]);
        selectMenuItem.disableProperty().bind(disabledProperties[SELECT]);
        nodeMenuItem.disableProperty().bind(disabledProperties[NODE]);
        edgeMenuItem.disableProperty().bind(disabledProperties[EDGE]);
        controlMenuItem.disableProperty().bind(disabledProperties[CONTROL]);
        changeColorMenuItem.disableProperty().bind(disabledProperties[CHANGE_COLOR]);
        // initialize select rectangle
        selectRectangle = new Rectangle();
        selectRectangle.setFill(Color.TRANSPARENT);
        selectRectangle.setStroke(Color.ORANGE);
        selectRectangle.setStrokeWidth(2);
        selectRectangle.getStrokeDashArray().addAll(20d, 5d);
        selectRectangle.setStrokeLineCap(StrokeLineCap.BUTT);
        // initialize current action
        currentAction = new SimpleIntegerProperty(-1);
        currentAction.addListener(
                (ov, oldVal, newVal) -> {
                    if (CONTROL == oldVal.intValue()) {
                        clearEdgeControlTrash();
                    }
                    if (SELECT == oldVal.intValue()) {
                        selectRectangle.setWidth(0);
                        selectRectangle.setHeight(0);
                        selectRectangle.setX(0);
                        selectRectangle.setY(0);
                    }
                    if (SELECT == oldVal.intValue() || MOVE == oldVal.intValue()) {
                        if (NODE == newVal.intValue() || EDGE == newVal.intValue() ||
                                CONTROL == newVal.intValue() || CHANGE_COLOR == newVal.intValue()) {
                            clearSelection();
                        }
                    }

                }
        );
    }

    private void clearEdgeControlTrash() {
        System.out.println("clear edge control trash");
        for (Shape shape : edgeControlTrash) {
            drawPanel.getChildren().remove(shape);
        }
        edgeControlTrash.clear();
    }

    private void removeEdge(Edge edge) {
        drawPanel.getChildren().remove(edge);
        edges.remove(edge);
        for (Node node : edge.edgeNodes) {
            node.getEdges().remove(edge);
        }
    }

    public void removeNode(Node node) {
        if (node.getEdges().isEmpty()) {
            drawPanel.getChildren().remove(node);
            nodes.remove(node);
        }
    }

    private void clearDrawSpace() {
        drawPanel.getChildren().removeAll(nodes);
        drawPanel.getChildren().removeAll(edges);
        drawPanel.getChildren().removeAll(edgeNodes);
        drawPanel.getChildren().removeAll(edgeControlTrash);
        nodes.clear();
        edges.clear();
        edgeNodes.clear();
        edgeControlTrash.clear();
    }

    public void addControlToEdge(Edge edge) {
        System.out.println("addControlToEdge");
        if (edgeControlTrash.size() != 0) {
            clearEdgeControlTrash();
        }
        Line[] lines = new Line[2];
        // prepare edge controls
        for (int i = 0; i < 2; i++) {
            edge.controls[i].setOnMousePressed(this::onMousePressed);
            edge.controls[i].setOnMouseDragged(this::onMouseDragged);
            lines[i] = new Line(
                    edge.edgeNodes[i].getCenterX(), edge.edgeNodes[i].getCenterY(),
                    edge.controls[i].getCenterX(), edge.controls[i].getCenterY()
            );
            lines[i].startXProperty().bind(edge.edgeNodes[i].centerXProperty());
            lines[i].startYProperty().bind(edge.edgeNodes[i].centerYProperty());
            lines[i].endXProperty().bind(edge.controls[i].centerXProperty());
            lines[i].endYProperty().bind(edge.controls[i].centerYProperty());
            edgeControlTrash.add(lines[i]);
            edgeControlTrash.add(edge.controls[i]);
            edge.controls[i].toFront();
        }
        drawPanel.getChildren().addAll(edgeControlTrash);
    }

    public double checkBorn(double x, double[] xBorn) {
        if (x < xBorn[0]) {
            x = xBorn[0];
        } else if (xBorn[1] < x) {
            x = xBorn[1];
        }
        return x;
    }

    public boolean checkInterval(double x, double[] interval) {
        return (interval[0] < x) && (x < interval[1]);
    }

    public void clearSelection() {
        for (Node node : selectNodes) {
            node.getStyleClass().remove("node-selected");
        }
        for (Edge edge : selectEdges) {
            edge.getStyleClass().remove("edge-selected");
        }
        selectNodes.clear();
        selectEdges.clear();
        disabledProperties[CUT].set(true);
        disabledProperties[COPY].set(true);
        disabledProperties[DUPLICATE].set(true);
    }

    /***************************************************************************
     *
     *  onMouse event handlers (click, press, ...)
     *
     ***************************************************************************/
    public void onMouseClicked(MouseEvent mouseEvent) {
        if (mouseEvent.getSource() instanceof Pane) { // Pane get clicked
            System.out.println("onMouseClicked Pane");
            double x = mouseEvent.getX();
            double y = mouseEvent.getY();
            if (currentAction.intValue() == NODE) {
                if (y < connectionLinePosition) {
                    Node node = new Node(x, y);
                    if (connectionLinePosition - 10 < y) {
                        System.out.println("root");
                        node.setRoot(true);
                        node.setCenterY(connectionLinePosition);
                    }
                    node.setOnMouseClicked(this::onMouseClicked);
                    node.setOnMousePressed(this::onMousePressed);
                    node.setOnMouseDragged(this::onMouseDragged);
                    nodes.add(node);
                    drawPanel.getChildren().add(node);
                    node.toFront();
                }
            } else if (currentAction.intValue() == SELECT) {
                drawPanel.getChildren().remove(selectRectangle);
                if (mouseEvent.getButton().equals(MouseButton.SECONDARY)) {
                    clearSelection();
                }
            } else if (currentAction.intValue() == MOVE) {
                drawPanel.setCursor(Cursor.DEFAULT);
                if (mouseEvent.getButton().equals(MouseButton.SECONDARY)) {
                    clearSelection();
                }
            } else if (currentAction.intValue() == CONTROL) {
                if (mouseEvent.getButton().equals(MouseButton.SECONDARY)) {
                    clearEdgeControlTrash();
                }
            }
        } else if (mouseEvent.getSource() instanceof Node) { // Node get clicked
            System.out.println("onMouseClicked Node");
            Node currentNode = (Node) mouseEvent.getSource();
            if (currentAction.intValue() == EDGE) {
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
            } else if (DELETE == currentAction.intValue()) {
                removeNode(currentNode);
            }
        } else if (mouseEvent.getSource() instanceof Edge) { // edge get clicked
            System.out.println("onMouseClicked Edge");
            Edge currentEdge = (Edge) mouseEvent.getSource();
            if (currentAction.intValue() == CONTROL) {
                addControlToEdge(currentEdge);
            } else if (DELETE == currentAction.intValue()) {
                removeEdge(currentEdge);
            } else if (currentAction.intValue() == CHANGE_COLOR) {
                currentEdge.switchColor();
            }
        }
    }

    public void onMousePressed(MouseEvent mouseEvent) {
        pressedX = mouseEvent.getX();
        pressedY = mouseEvent.getY();
        if (mouseEvent.getSource() instanceof Node) {
//            System.out.println("onMousePressed Node");
            Node node = (Node) mouseEvent.getSource();
            if (currentAction.intValue() == MOVE || currentAction.intValue() == SELECT) {
                clearSelection();
                selectNodes.add(node);
                node.getStyleClass().add("node-selected");
                disabledProperties[CUT].set(false);
                disabledProperties[COPY].set(false);
                disabledProperties[DUPLICATE].set(false);
            }
        } else if (mouseEvent.getSource() instanceof Edge) {
//            System.out.println("onMousePressed Edge");
            Edge edge = (Edge) mouseEvent.getSource();
            if (currentAction.intValue() == MOVE || currentAction.intValue() == SELECT) {
                if (!selectEdges.contains(edge)) {
                    clearSelection();
                    selectEdges.add(edge);
                    selectNodes.add(edge.edgeNodes[0]);
                    edge.getStyleClass().add("edge-selected");
                    edge.edgeNodes[0].getStyleClass().add("node-selected");
                    if(edge.nbrEdgeNodes == 2){
                        edge.edgeNodes[1].getStyleClass().add("node-selected");
                        selectNodes.add(edge.edgeNodes[1]);
                    }
                    disabledProperties[CUT].set(false);
                    disabledProperties[COPY].set(false);
                    disabledProperties[DUPLICATE].set(false);
                }
            }
        } else if (mouseEvent.getSource() instanceof Pane) {
//            System.out.println("onMousePressed Pane");
            if (currentAction.intValue() == MOVE) {
                drawPanel.setCursor(Cursor.MOVE);
            }
        }
    }

    public void onMouseDragged(MouseEvent mouseEvent) {
        if (mouseEvent.getSource() instanceof Node) {
//            System.out.println("onMouseDragged Node");
            Node currentNode = (Node) mouseEvent.getSource();
            if (((currentAction.intValue() == CONTROL) && currentNode.isControl()) /*||
                    ((currentAction.intValue() == MOVE) && currentNode.isNode() && (selectNodes.isEmpty()))*/) {
                double x = currentNode.getCenterX() - pressedX + mouseEvent.getX();
                double y = currentNode.getCenterY() - pressedY + mouseEvent.getY();
                x = checkBorn(x, drawSpaceIntervalX);
                y = checkBorn(y, drawSpaceIntervalY);
                currentNode.setCenter(x, y);
                currentNode.setRoot(connectionLinePosition - 10 < y);
                pressedX = mouseEvent.getX();
                pressedY = mouseEvent.getY();
            }
        } else if (mouseEvent.getSource() instanceof Edge) {
//            System.out.println("onMouseDragged Edge");
            /*Edge currentEdge = (Edge) mouseEvent.getSource();
            if (currentAction.intValue() == MOVE) {
                if (selectNodes.isEmpty()) {
                    double[] xs = new double[4];
                    double[] ys = new double[4];
                    for (int i = 0; i < 2; i++) {
                        xs[i] = currentEdge.edgeNodes[i].getCenterX() - pressedX + mouseEvent.getX();
                        xs[i + 2] = currentEdge.controls[i].getCenterX() - pressedX + mouseEvent.getX();
                        ys[i] = currentEdge.edgeNodes[i].getCenterY() - pressedY + mouseEvent.getY();
                        ys[i + 2] = currentEdge.controls[i].getCenterY() - pressedY + mouseEvent.getY();
                    }
                    for (int i = 0; i < 4; i++) {
                        xs[i] = checkBorn(xs[i], new double[] {7d, 750d});
                        if (ys[i] < 7) {
                            ys[i] = 7;
                        } else if ((460 < ys[i]) && (i < 2)) {
                            ys[i] = 460;
                        } else if ((440 < ys[i]) && (1 < i)) {
                            ys[i] = 440;
                        }
                    }
                    for (int i = 0; i < 2; i++) {
                        currentEdge.edgeNodes[i].setCenter(xs[i], ys[i]);
                        currentEdge.edgeNodes[i].setRoot(connectionLinePosition - 10 < ys[i]);
                        currentEdge.controls[i].setCenter(xs[i + 2], ys[i + 2]);
                    }
                }
            }
            pressedX = mouseEvent.getX();
            pressedY = mouseEvent.getY();*/
        } else if (mouseEvent.getSource() instanceof Pane) {
//            System.out.println("onMouseDragged Pane");
            if (currentAction.intValue() == SELECT) {
                double[] intervalX = {Math.min(pressedX, mouseEvent.getX()), Math.max(pressedX, mouseEvent.getX())};
                double[] intervalY = {Math.min(pressedY, mouseEvent.getY()), Math.max(pressedY, mouseEvent.getY())};
                selectRectangle.setX(intervalX[0]);
                selectRectangle.setY(intervalY[0]);
                selectRectangle.setWidth(Math.abs(mouseEvent.getX() - pressedX));
                selectRectangle.setHeight(Math.abs(mouseEvent.getY() - pressedY));
                if (!drawPanel.getChildren().contains(selectRectangle)) {
                    drawPanel.getChildren().add(selectRectangle);
                }
                for (Node node : nodes) {
                    if (checkInterval(node.getCenterX(), intervalX) && checkInterval(node.getCenterY(), intervalY)) {
                        if (!selectNodes.contains(node)) {
                            node.getStyleClass().add("node-selected");
                            selectNodes.add(node);
                        }
                    } else {
                        if (selectNodes.contains(node)) {
                            node.getStyleClass().remove("node-selected");
                            selectNodes.remove(node);
                        }
                    }
                }
                for (Edge edge : edges) {
                    boolean edgeSelected = true;
                    int i = 0;
                    while (edgeSelected && (i < 2)) {
                        edgeSelected &= (checkInterval(edge.edgeNodes[i].getCenterX(), intervalX) &&
                                checkInterval(edge.edgeNodes[i].getCenterY(), intervalY));
                        i++;
                    }
                    if (edgeSelected) {
                        if (!selectEdges.contains(edge)) {
                            edge.getStyleClass().add("edge-selected");
                            selectEdges.add(edge);
                        }
                    } else {
                        if (selectEdges.contains(edge)) {
                            edge.getStyleClass().remove("edge-selected");
                            selectEdges.remove(edge);
                        }
                    }
                }
                if ((0 < selectNodes.size())) {
                    disabledProperties[CUT].set(false);
                    disabledProperties[COPY].set(false);
                    disabledProperties[DUPLICATE].set(false);
                } else {
                    disabledProperties[CUT].set(true);
                    disabledProperties[COPY].set(true);
                    disabledProperties[DUPLICATE].set(true);
                }
            } else if (currentAction.intValue() == MOVE) {
                if (0 != selectNodes.size()) {
                    boolean moveSelection = true;
                    for (Node node : selectNodes) {
                        moveSelection &= ((checkInterval(node.getCenterX() + (mouseEvent.getX() - pressedX), drawSpaceIntervalX) &&
                                checkInterval(node.getCenterY() + (mouseEvent.getY() - pressedY), drawSpaceIntervalY)));
                    }
                    for (Edge edge : selectEdges) {
                        for (Node node : edge.controls) {
                            moveSelection &= ((checkInterval(node.getCenterX() + (mouseEvent.getY() - pressedY), drawSpaceIntervalX) &&
                                    checkInterval(node.getCenterY() + (mouseEvent.getY() - pressedY), drawSpaceIntervalY)));
                        }
                    }
                    if (moveSelection) {
                        for (Node node : selectNodes) {
                            node.setCenterX(node.getCenterX() + (mouseEvent.getX() - pressedX));
                            node.setCenterY(node.getCenterY() + (mouseEvent.getY() - pressedY));
                        }
                        for (Edge edge : selectEdges) {
                            for (Node node : edge.controls) {
                                node.setCenterX(node.getCenterX() + (mouseEvent.getX() - pressedX));
                                node.setCenterY(node.getCenterY() + (mouseEvent.getY() - pressedY));
                            }
                        }
                    }
                    pressedX = mouseEvent.getX();
                    pressedY = mouseEvent.getY();
                }
            }
        }
    }

    public void fillCopy() {
        copyNodes.clear();
        copyEdges.clear();
        copyNodes.addAll(selectNodes);
        copyEdges.addAll(selectEdges);
    }

    private void clearCopy() {
        copyNodes.clear();
        copyEdges.clear();
    }

    public void createCopy(List<Node> nodes_, List<Edge> edges_, double shift) {
        List<Node> duplicateNodes = new ArrayList<>();
        for (Node node : nodes_) {
            Node dNode = node.clone(shift);
            dNode.setOnMouseClicked(this::onMouseClicked);
            dNode.setOnMousePressed(this::onMousePressed);
            dNode.setOnMouseDragged(this::onMouseDragged);
            dNode.getStyleClass().add("node-selected");
            duplicateNodes.add(dNode);
        }
        List<Edge> duplicateEdges = new ArrayList<>();
        for (Edge edge : edges_) {
            Edge dEdge = edge.clone(duplicateNodes.get(nodes_.indexOf(edge.edgeNodes[0])),
                    duplicateNodes.get(nodes_.indexOf(edge.edgeNodes[1])), shift);
            dEdge.setOnMouseClicked(this::onMouseClicked);
            dEdge.setOnMousePressed(this::onMousePressed);
            dEdge.setOnMouseDragged(this::onMouseDragged);
            dEdge.getStyleClass().add("edge-selected");
            duplicateEdges.add(dEdge);
        }
        clearSelection();
        selectEdges.addAll(duplicateEdges);
        edges.addAll(duplicateEdges);
        drawPanel.getChildren().addAll(duplicateEdges);
        selectNodes.addAll(duplicateNodes);
        nodes.addAll(duplicateNodes);
        drawPanel.getChildren().addAll(duplicateNodes);
    }

    /***************************************************************************
     *
     *  onAction handlers for different option (NewFile, OpenFile, Copy, ...)
     *
     ***************************************************************************/

    public void nodeAction(ActionEvent actionEvent) {
        System.out.println("nodeAction");
        currentAction.set(NODE);
    }

    public void edgeAction(ActionEvent actionEvent) {
        System.out.println("edgeAction");
        currentAction.set(EDGE);
    }

    public void selectAction(ActionEvent actionEvent) {
        System.out.println("selectAction");
        currentAction.set(SELECT);
    }

    public void cutAction(ActionEvent actionEvent) {
        System.out.println("cutAction");
        currentAction.set(CUT);
        if (!selectNodes.isEmpty()) {
            fillCopy();
            clearSelection();
            nodes.removeAll(copyNodes);
            edges.removeAll(copyEdges);
            drawPanel.getChildren().removeAll(copyNodes);
            drawPanel.getChildren().removeAll(copyEdges);
            copyAction = 0;
            disabledProperties[PASTE].set(false);
        }
    }

    public void copyAction(ActionEvent actionEvent) {
        System.out.println("copyAction");
        currentAction.set(COPY);
        fillCopy();
        copyAction = 1;
        disabledProperties[PASTE].set(false);
    }

    public void pasteAction(ActionEvent actionEvent) {
        System.out.println("pasteAction");
        currentAction.set(PASTE);
        double shift = 15;
        if (0 == copyAction) {
            createCopy(copyNodes, copyEdges, shift);
            clearCopy();
            copyAction = -1;
            disabledProperties[PASTE].set(true);
        } else if (0 < copyAction) {
            createCopy(copyNodes, copyEdges, shift * copyAction);
            copyAction++;
        }
    }

    public void duplicateAction(ActionEvent actionEvent) {
        System.out.println("duplicateAction");
        currentAction.set(DUPLICATE);
        if (!selectNodes.isEmpty()) {
            double shift = 15;
            createCopy(selectNodes, selectEdges, shift);
        }
    }

    public void deleteAction(ActionEvent actionEvent) {
        System.out.println("deleteAction");
        currentAction.set(DELETE);
        if (!selectNodes.isEmpty()) {
            for (Edge edge : selectEdges) {
                removeEdge(edge);
            }
            for (Node node : selectNodes) {
                removeNode(node);
            }
            selectEdges.clear();
            for (Node node : selectNodes) {
                node.getStyleClass().remove("node-selected");
            }
            selectNodes.clear();
        }
    }

    public void changeColorAction(ActionEvent actionEvent) {
        System.out.println("changeColorAction");
        currentAction.set(CHANGE_COLOR);
    }

    public void undoAction(ActionEvent actionEvent) {
        System.out.println("undoAction");
        currentAction.set(UNDO);
    }

    public void redoAction(ActionEvent actionEvent) {
        System.out.println("redoAction");
        currentAction.set(REDO);
    }

    public void moveAction(ActionEvent actionEvent) {
        System.out.println("moveAction");
        currentAction.set(MOVE);
    }

    public void controlAction(ActionEvent actionEvent) {
        System.out.println("controlAction");
        currentAction.set(CONTROL);
    }

    public void newFileAction(ActionEvent actionEvent) {
        System.out.println("newFileAction");
        clearDrawSpace();
    }

    public void openFileAction(ActionEvent actionEvent) {
        System.out.println("openFileAction");
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open FXML Design ...");
        fileChooser.setInitialDirectory(new File("./src/designs/"));
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("FXML File", "*.fxml"));
        File file = fileChooser.showOpenDialog(
                ((MenuItem) actionEvent.getSource()).getParentPopup().getScene().getWindow());
        if (file != null) {
            try {
                Pane design = FXMLLoader.load(Paths.get(file.getPath()).toUri().toURL());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void saveFileAction(ActionEvent actionEvent) {
        System.out.println("saveFileAction");
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save FXML Design ...");
        fileChooser.setInitialDirectory(new File("./src/designs/"));
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("FXML File", "*.fxml"));
        File file = fileChooser.showSaveDialog(null);
        if (file != null) {
            FileGenerator.createFile(nodes, edges, file);
        }
    }

    public void quitAction(ActionEvent actionEvent) {
        System.out.println("quitAction");
        System.exit(0);
    }
}