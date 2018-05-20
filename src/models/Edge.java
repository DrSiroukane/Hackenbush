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
    public MoveTo moveTo;
    public CubicCurveTo cubicCurveTo;
    public int currentColor = 0;
    public Color[] colors = {Color.GREEN, Color.RED, Color.BLUE};

    /*public Edge(Path path, List<Node> nodes) {
        System.out.println(path.getElements().size());
        moveTo = (MoveTo) path.getElements().get(0);
        cubicCurveTo = (CubicCurveTo) path.getElements().get(1);
        if (moveTo != null && cubicCurveTo != null) {
            for (Node node : nodes) {
                if (moveTo.getX() == node.getCenterX() && moveTo.getY() == node.getCenterY()) {
                    startNode = node;
                    node.getEdges().add(this);
                }
                if (cubicCurveTo.getX() == node.getCenterX() && cubicCurveTo.getY() == node.getCenterY()) {
                    endNode = node;
                    node.getEdges().add(this);
                }
            }

            *//*control1.setCenterX(cubicCurveTo.getControlX1());
            control1.setCenterY(cubicCurveTo.getControlY1());
            control2.setCenterX(cubicCurveTo.getControlX2());
            control2.setCenterY(cubicCurveTo.getControlY2());*//*

            moveTo.xProperty().bind(startNode.centerXProperty());
            moveTo.yProperty().bind(startNode.centerYProperty());
            cubicCurveTo.xProperty().bind(endNode.centerXProperty());
            cubicCurveTo.yProperty().bind(endNode.centerYProperty());
//            cubicCurveTo.controlX1Property().bind(control1.centerXProperty());
//            cubicCurveTo.controlY1Property().bind(control1.centerYProperty());
//            cubicCurveTo.controlX2Property().bind(control2.centerXProperty());
//            cubicCurveTo.controlY2Property().bind(control2.centerYProperty());
            getElements().addAll(moveTo, cubicCurveTo);
        }
        setStroke(path.getStroke());
        setStrokeWidth(path.getStrokeWidth());
    }
*/
    public Edge(Node startNode, Node endNode) {
        super();
        edgeNodes = new Node[2];
        edgeNodes[0] = startNode;
        edgeNodes[1] = endNode;
        startNode.getEdges().add(this);
        if (!endNode.equals(startNode)) {
            endNode.getEdges().add(this);
        }
        double startX = startNode.getCenterX();
        double startY = startNode.getCenterY();
        double endX = endNode.getCenterX();
        double endY = endNode.getCenterY();
        double controlX1;
        double controlY1;
        double controlX2;
        double controlY2;
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
        moveTo = new MoveTo(startX, startY);
        cubicCurveTo = new CubicCurveTo(controlX1, controlY1, controlX2, controlY2, endX, endY);
        moveTo.xProperty().bind(startNode.centerXProperty());
        moveTo.yProperty().bind(startNode.centerYProperty());
        cubicCurveTo.xProperty().bind(endNode.centerXProperty());
        cubicCurveTo.yProperty().bind(endNode.centerYProperty());
        cubicCurveTo.controlX1Property().bind(controls[0].centerXProperty());
        cubicCurveTo.controlY1Property().bind(controls[0].centerYProperty());
        cubicCurveTo.controlX2Property().bind(controls[1].centerXProperty());
        cubicCurveTo.controlY2Property().bind(controls[1].centerYProperty());
        getElements().addAll(moveTo, cubicCurveTo);
        setStroke(colors[currentColor]);
        setStrokeWidth(7);
    }

    public void switchColor() {
        currentColor = (1 + currentColor) % 3;
        setStroke(colors[currentColor]);
    }

    @Override
    public String toString() {
        return super.toString() +
                "\n\tstock: " + getStroke() + "\tstock width: " + getStrokeWidth() +
                "\n\tstartX: " + edgeNodes[0].getCenterX() + "\t startY: " + edgeNodes[0].getCenterY() +
                "\n\tendX: " + edgeNodes[1].getCenterX() + "\t endY: " + edgeNodes[1].getCenterY();
    }
}
