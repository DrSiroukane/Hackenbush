package models;

import javafx.scene.paint.Color;
import javafx.scene.shape.CubicCurveTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;

public class Edge extends Path {

    public final int START = 0;
    public final int END = 1;

    public Node startNode = null;
    public Node endNode = null;

    public Node control1 = null;
    public Node control2 = null;

    public MoveTo moveTo;
    public CubicCurveTo cubicCurveTo;

    public Edge(Node startNode, Node endNode) {
        super();

        this.startNode = startNode;
        this.endNode = endNode;

        double startX = startNode.getCenterX();
        double startY = startNode.getCenterY();

        double endX = endNode.getCenterX();
        double endY = endNode.getCenterY();

        double controlX1;
        double controlY1;

        double controlX2;
        double controlY2;

        if(startNode.equals(endNode)){
            controlX1 = startX + 50;
            controlY1 = startY - 50;

            controlX2 = startX - 50;
            controlY2 = startY - 50;
        }else{
            double thirdOfX = (endX - startX) / 3;
            double thirdOfY = (endY - startY) / 3;

            controlX1 = startX + thirdOfX;
            controlY1 = startY + thirdOfY;

            controlX2 = controlX1 + thirdOfX;
            controlY2 = controlY1 + thirdOfY;
        }

        control1 = new Node(controlX1, controlY1, 5);
        control1.getStyleClass().add("node-tangent");
        control2 = new Node(controlX2, controlY2, 5);
        control2.getStyleClass().add("node-tangent");

        moveTo = new MoveTo(startX, startY);
        cubicCurveTo = new CubicCurveTo(controlX1, controlY1, controlX2, controlY2, endX, endY);

        moveTo.xProperty().bind(startNode.centerXProperty());
        moveTo.yProperty().bind(startNode.centerYProperty());

        cubicCurveTo.xProperty().bind(endNode.centerXProperty());
        cubicCurveTo.yProperty().bind(endNode.centerYProperty());

        cubicCurveTo.controlX1Property().bind(control1.centerXProperty());
        cubicCurveTo.controlY1Property().bind(control1.centerYProperty());
        cubicCurveTo.controlX2Property().bind(control2.centerXProperty());
        cubicCurveTo.controlY2Property().bind(control2.centerYProperty());

        setStroke(Color.RED);
        setStrokeWidth(10);
        getElements().addAll(moveTo, cubicCurveTo);
    }

    /*public void setLineCoordinate(int position) {
        if ((line != null)) {
            if ((position == START) && (startNode != null)) {
                line.setStartX(startNode.getCenterX());
                line.setStartY(startNode.getCenterY());
            } else if ((position == END) && (endNode != null)) {
                line.setEndX(endNode.getCenterX());
                line.setEndY(endNode.getCenterY());
            }
        }
    }

    public void bindNodesWithEdges(){
        line.startXProperty().bindBidirectional(startNode.centerXProperty());
        line.startYProperty().bindBidirectional(startNode.centerYProperty());
    }*/
}
