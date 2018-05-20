package controllers;

import helpers.FileGenerator;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
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
    public List<Pane> designs;

    double pressedX, pressedY;
    double pressedNode1X, pressedNode1Y,
            pressedNode2X, pressedNode2Y,
            pressedNode3X, pressedNode3Y,
            pressedNode4X, pressedNode4Y;

    Rectangle selectRectangle;

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
        // bind options
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
        // initialize select rectangle
        selectRectangle = new Rectangle();
        selectRectangle.setFill(Color.TRANSPARENT);
        selectRectangle.setStroke(Color.ORANGE);
        selectRectangle.setStrokeWidth(2);
        selectRectangle.getStrokeDashArray().addAll(20d, 5d);
        selectRectangle.setStrokeLineCap(StrokeLineCap.BUTT);

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
        edge.edgeNodes[0].getEdges().remove(edge);
        edge.edgeNodes[1].getEdges().remove(edge);
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
            } else if (currentAction.intValue() == DELETE) {
                List<Edge> edges = currentNode.getEdges();
                System.out.println("Number of connected edges : " + edges.size());
                while (edges.size() != 0) {
                    removeEdge(edges.get(0));
                }
                drawPanel.getChildren().remove(currentNode);
                nodes.remove(currentNode);
            }
        } else if (mouseEvent.getSource() instanceof Edge) { // edge get clicked
            System.out.println("onMouseClicked Edge");
            Edge currentEdge = (Edge) mouseEvent.getSource();
            if (currentAction.intValue() == CONTROL) {
                addControlToEdge(currentEdge);
            } else if (currentAction.intValue() == DELETE) {
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
            System.out.println("onMousePressed Node");
            Node currentNode = (Node) mouseEvent.getSource();
            if (((currentAction.intValue() == CONTROL) && currentNode.isControl()) || ((currentAction.intValue() == MOVE) && currentNode.isNode())) {
                pressedNode1X = currentNode.getCenterX();
                pressedNode1Y = currentNode.getCenterY();
            }
        } else if (mouseEvent.getSource() instanceof Edge) {
            System.out.println("onMousePressed Edge");
            Edge currentEdge = (Edge) mouseEvent.getSource();
            if (currentAction.intValue() == MOVE) {
                pressedNode1X = currentEdge.edgeNodes[0].getCenterX();
                pressedNode1Y = currentEdge.edgeNodes[0].getCenterY();
                pressedNode2X = currentEdge.edgeNodes[1].getCenterX();
                pressedNode2Y = currentEdge.edgeNodes[1].getCenterY();
                pressedNode3X = currentEdge.controls[0].getCenterX();
                pressedNode3Y = currentEdge.controls[0].getCenterY();
                pressedNode4X = currentEdge.controls[1].getCenterX();
                pressedNode4Y = currentEdge.controls[1].getCenterY();
            }
        } else if (mouseEvent.getSource() instanceof Pane) {
            System.out.println("onMousePressed Pane");
        }
    }

    public void onMouseDragged(MouseEvent mouseEvent) {
        if (mouseEvent.getSource() instanceof Node) {
//            System.out.println("onMouseDragged Node");
            Node currentNode = (Node) mouseEvent.getSource();
            if (((currentAction.intValue() == CONTROL) && currentNode.isControl()) || ((currentAction.intValue() == MOVE) && currentNode.isNode())) {
                double x = pressedNode1X - pressedX + mouseEvent.getX();
                double y = pressedNode1Y - pressedY + mouseEvent.getY();
                x = checkBorn(x, new double[] {7d, 750d});
                y = checkBorn(y, new double[] {7d, 460d});
                currentNode.setCenter(x, y);
                currentNode.setRoot(connectionLinePosition - 10 < y);
            }
        } else if (mouseEvent.getSource() instanceof Edge) {
            System.out.println("onMouseDragged Edge");
            Edge currentEdge = (Edge) mouseEvent.getSource();
            if (currentAction.intValue() == MOVE) {
                double[] xs = {
                        pressedNode1X - pressedX + mouseEvent.getX(),
                        pressedNode2X - pressedX + mouseEvent.getX(),
                        pressedNode3X - pressedX + mouseEvent.getX(),
                        pressedNode4X - pressedX + mouseEvent.getX()
                };
                double[] ys = {
                        pressedNode1Y - pressedY + mouseEvent.getY(),
                        pressedNode2Y - pressedY + mouseEvent.getY(),
                        pressedNode3Y - pressedY + mouseEvent.getY(),
                        pressedNode4Y - pressedY + mouseEvent.getY()
                };

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
                currentEdge.edgeNodes[0].setCenter(xs[0], ys[0]);
                currentEdge.edgeNodes[0].setRoot(connectionLinePosition - 10 < ys[0]);
                currentEdge.edgeNodes[1].setCenter(xs[1], ys[1]);
                currentEdge.edgeNodes[1].setRoot(connectionLinePosition - 10 < ys[1]);
                currentEdge.controls[0].setCenter(xs[2], ys[2]);
                currentEdge.controls[1].setCenter(xs[3], ys[3]);
            }
        } else if (mouseEvent.getSource() instanceof Pane) {
            System.out.println("onMouseDragged Pane");
            if (SELECT == currentAction.intValue()) {
                selectRectangle.setX(Math.min(pressedX, mouseEvent.getX()));
                selectRectangle.setY(Math.min(pressedY, mouseEvent.getY()));
                selectRectangle.setWidth(Math.abs(mouseEvent.getX() - pressedX));
                selectRectangle.setHeight(Math.abs(mouseEvent.getY() - pressedY));
                if (!drawPanel.getChildren().contains(selectRectangle)) {
                    drawPanel.getChildren().add(selectRectangle);
                }
            }
        }
    }

    public double checkBorn(double x, double[] xBorn) {
        if (x < xBorn[0]) {
            x = xBorn[0];
        } else if (xBorn[1] < x) {
            x = xBorn[1];
        }

        return x;
    }

    /***************************************************************************
     *
     *  onAction handlers for fxml buttons
     *
     ***************************************************************************/

    public void nodeAction(ActionEvent actionEvent) {
        currentAction.set(NODE);
        System.out.println("nodeAction");
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
        pasteButton.setDisable(false);
        disabledProperties[PASTE].set(false);
    }

    public void copyAction(ActionEvent actionEvent) {
        currentAction.set(COPY);
        pasteButton.setDisable(false);
        disabledProperties[PASTE].set(false);
        System.out.println("copyAction");
    }

    public void pasteAction(ActionEvent actionEvent) {
        currentAction.set(PASTE);
        System.out.println("pasteAction");
    }

    public void duplicateAction(ActionEvent actionEvent) {
        currentAction.set(DUPLICATE);
        System.out.println("duplicateAction");
    }

    public void deleteAction(ActionEvent actionEvent) {
        currentAction.set(DELETE);
        System.out.println("deleteAction");
    }

    public void changeColorAction(ActionEvent actionEvent) {
        currentAction.set(CHANGE_COLOR);
        System.out.println("deleteAction");
    }

    public void undoAction(ActionEvent actionEvent) {
        currentAction.set(UNDO);
        System.out.println("undoAction");
    }

    public void redoAction(ActionEvent actionEvent) {
        currentAction.set(REDO);
        System.out.println("redoAction");
    }

    public void moveAction(ActionEvent actionEvent) {
        currentAction.set(MOVE);
        System.out.println("moveAction");
    }

    public void controlAction(ActionEvent actionEvent) {
        currentAction.set(CONTROL);
        System.out.println("controlAction");
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
        FileGenerator.createFile(nodes, edges, "design_");
    }

    public void quitAction(ActionEvent actionEvent) {
        System.out.println("quitAction");
        System.exit(0);
    }
}