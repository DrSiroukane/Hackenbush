package models;

import javafx.scene.paint.Color;
import javafx.scene.shape.CubicCurveTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;

import java.util.List;

/**
 * @author Slimane Siroukane
 * @author Fatima Chikh
 */
public class Edge extends Path {
    public Node[] edgeNodes;
    public Node[] controls;
    public int nbrEdgeNodes = 2;
    public MoveTo moveTo;
    public CubicCurveTo cubicCurveTo;
    public int currentColor = 0;
    public Color[] colors = {Color.GREEN, Color.RED, Color.BLUE};

    public Edge(Node startNode, Node endNode) {
        super();
        edgeNodes = new Node[2];
        edgeNodes[0] = startNode;
        edgeNodes[1] = endNode;
        startNode.getEdges().add(this);
        if (!endNode.equals(startNode)) {
            endNode.getEdges().add(this);
        } else {
            nbrEdgeNodes = 1;
        }
        double startX = startNode.getCenterX();
        double startY = startNode.getCenterY();
        double endX = endNode.getCenterX();
        double endY = endNode.getCenterY();
        double controlX1, controlY1, controlX2, controlY2;
        if (startNode.equals(endNode)) {
            controlX1 = startX + 50;
            controlY1 = startY - 50;
            controlX2 = startX - 50;
            controlY2 = startY - 50;
        } else {
            double thirdOfX = (endX - startX) / 3;
            double thirdOfY = (endY - startY) / 3;
            controlX1 = startX + thirdOfX;
            controlY1 = startY + thirdOfY;
            controlX2 = controlX1 + thirdOfX;
            controlY2 = controlY1 + thirdOfY;
        }
        controls = new Node[2];
        controls[0] = new Node(controlX1, controlY1, 5, Node.CONTROL);
        controls[1] = new Node(controlX2, controlY2, 5, Node.CONTROL);
        setBind();
        getElements().addAll(moveTo, cubicCurveTo);
        setStroke(colors[currentColor]);
        setStrokeWidth(6);
    }

    public Edge(Node startNode, Node endNode, Node[] controls, double shift) {
        super();
        edgeNodes = new Node[2];
        edgeNodes[0] = startNode;
        edgeNodes[1] = endNode;
        startNode.getEdges().add(this);
        if (!endNode.equals(startNode)) {
            endNode.getEdges().add(this);
        } else {
            nbrEdgeNodes = 1;
        }
        this.controls = new Node[2];
        this.controls[0] = new Node(controls[0].getCenterX() - shift,
                controls[0].getCenterY() - shift, 5, Node.CONTROL);
        this.controls[1] = new Node(controls[1].getCenterX() - shift,
                controls[1].getCenterY() - shift, 5, Node.CONTROL);
        setBind();
        getElements().addAll(moveTo, cubicCurveTo);
        setStroke(colors[currentColor]);
        setStrokeWidth(6);
    }

    public Edge(Path path, List<Node> nodes) {
        moveTo = (MoveTo) path.getElements().get(0);
        cubicCurveTo = (CubicCurveTo) path.getElements().get(1);
        edgeNodes = new Node[2];
        for (Node node : nodes) {
            if (node.getCenterX() == moveTo.getX() && node.getCenterY() == moveTo.getY()) {
                edgeNodes[0] = node;
                node.getEdges().add(this);
            }
            if (node.getCenterX() == cubicCurveTo.getX() && node.getCenterY() == cubicCurveTo.getY()) {
                edgeNodes[1] = node;
                if (edgeNodes[0] != edgeNodes[1]) {
                    node.getEdges().add(this);
                } else {
                    nbrEdgeNodes = 1;
                }
            }
        }
        getElements().addAll(moveTo, cubicCurveTo);
        setStroke(path.getStroke());
        setStrokeWidth(6);
    }

    public void switchColor() {
        currentColor = (1 + currentColor) % 3;
        setStroke(colors[currentColor]);
    }

    public Edge clone(Node startNode, Node endNode, double shift) {
        return new Edge(startNode, endNode, controls, shift);
    }

    public void setBind() {
        moveTo = new MoveTo(edgeNodes[0].getCenterX(), edgeNodes[0].getCenterY());
        cubicCurveTo = new CubicCurveTo(controls[0].getCenterX(), controls[0].getCenterY(),
                this.controls[1].getCenterX(), this.controls[1].getCenterY(),
                edgeNodes[1].getCenterX(), edgeNodes[1].getCenterY());
        moveTo.xProperty().bind(edgeNodes[0].centerXProperty());
        moveTo.yProperty().bind(edgeNodes[0].centerYProperty());
        cubicCurveTo.xProperty().bind(edgeNodes[1].centerXProperty());
        cubicCurveTo.yProperty().bind(edgeNodes[1].centerYProperty());
        cubicCurveTo.controlX1Property().bind(this.controls[0].centerXProperty());
        cubicCurveTo.controlY1Property().bind(this.controls[0].centerYProperty());
        cubicCurveTo.controlX2Property().bind(this.controls[1].centerXProperty());
        cubicCurveTo.controlY2Property().bind(this.controls[1].centerYProperty());
    }

    @Override
    public String toString() {
        return super.toString() +
                "\n\tstock: " + getStroke() + "\tstock width: " + getStrokeWidth() +
                "\n\tstartX: " + edgeNodes[0].getCenterX() + "\t startY: " + edgeNodes[0].getCenterY() +
                "\n\tendX: " + edgeNodes[1].getCenterX() + "\t endY: " + edgeNodes[1].getCenterY();
    }
}
