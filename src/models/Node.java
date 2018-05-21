package models;

import javafx.scene.shape.Circle;

import java.util.ArrayList;
import java.util.List;

public class Node extends Circle {
    public static final int NODE = 0;
    public static final int CONTROL = 1;

    private int type = NODE;
    private boolean root = false;

    private List<Edge> edges = null;

    public Node(Circle circle) {
        super();
        setCenterX(circle.getCenterX());
        setCenterY(circle.getCenterY());
        setRadius(circle.getRadius());
        edges = new ArrayList<>();
        getStyleClass().add(circle.getStyleClass().toString());
        setRoot(getStyleClass().contains("root-node-style"));
    }

    public Node(double centerX, double centerY) {
        super();
        setRadius(5);
        setCenterX(centerX);
        setCenterY(centerY);
        edges = new ArrayList<>();
        getStyleClass().add("node-style");
    }

    public Node(double centerX, double centerY, double radius) {
        super();
        setRadius(radius);
        setCenterX(centerX);
        setCenterY(centerY);
        getStyleClass().add("node-style");
    }

    public Node(double centerX, double centerY, double radius, int type) {
        super();
        setRadius(radius);
        setCenterX(centerX);
        setCenterY(centerY);
        setType(type);
        if (isNode()) {
            edges = new ArrayList<>();
            getStyleClass().add("node-style");
        }
    }

    public Node clone(double shift) {
        return new Node(getCenterX() - shift, getCenterY() - shift, getRadius(), getType());
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
        if (isNode()) {
            getStyleClass().add("node-style");
        } else {
            getStyleClass().add("node-tangent");
        }
    }

    public List<Edge> getEdges() {
        return edges;
    }

    public void setEdges(List<Edge> edges) {
        this.edges = edges;
    }

    public void setCenter(double centerX, double centerY) {
        setCenterX(centerX);
        setCenterY(centerY);
    }

    public void setRoot(boolean root) {
        this.root = root;
        if (isNode()) {
            boolean selectNode = getStyleClass().contains("node-selected");
            getStyleClass().clear();
            getStyleClass().add((root) ? "root-node-style" : "node-style");
            if (selectNode) getStyleClass().add("node-selected");
        }
    }

    public boolean isNode() {
        return type == NODE;
    }

    public boolean isControl() {
        return type == CONTROL;
    }

    public boolean isRootNode() {
        return root;
    }

    @Override
    public String toString() {
        return super.toString() +
                "\n\tcenterX: " + getCenterX() + "\tcenterY: " + getCenterY();
    }
}
