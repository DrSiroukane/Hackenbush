package models;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.util.*;

public class Node extends Circle{
    public static final int NODE = 0;
    public static final int CONTROL = 1;

    private int type = NODE;
    private boolean root = false;

    private List<Edge> edges = null;

    public Node(Circle circle){
        super();
        setCenterX(circle.getCenterX());
        setCenterY(circle.getCenterY());
        setRadius(circle.getRadius());
        edges = new ArrayList<>();
        getStyleClass().add(circle.getStyle());
    }

    public Node(double centerX, double centerY) {
        super();
        setRadius(7);
        setCenterX(centerX);
        setCenterY(centerY);
        edges = new ArrayList<>();
        getStyleClass().add("node-style");
    }

    public Node(double centerX, double centerY, int radius) {
        super();
        setRadius(radius);
        setCenterX(centerX);
        setCenterY(centerY);
        getStyleClass().add("node-style");
    }

    public Node(double centerX, double centerY, int radius, int type) {
        super();
        setRadius(radius);
        setCenterX(centerX);
        setCenterY(centerY);
        setType(type);
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
        if(isNode()){
            getStyleClass().add("node-style");
        }else{
            getStyleClass().add("node-tangent");
        }
    }

    public List<Edge> getEdges() {
        return edges;
    }

    public void setEdges(List<Edge> edges) {
        this.edges = edges;
    }

    public void setCenter(double centerX, double centerY){
        setCenterX(centerX);
        setCenterY(centerY);
    }

    public void setRoot(boolean root){
        if(isNode()){
            getStyleClass().clear();
            if(root){
                getStyleClass().add("root-node-style");
            }else{
                getStyleClass().add("node-style");
            }
        }
        this.root = root;
    }

    public boolean isNode(){
        return type == NODE;
    }

    public boolean isControl(){
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
