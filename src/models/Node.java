package models;

import javafx.scene.shape.Circle;

import java.util.*;

public class Node extends Circle{

    public static final int NODE = 0;
    public static final int CONTROL = 1;

    private int type = NODE;

    private List<Edge> edges = null;

    public Node(double centerX, double centerY) {
        super();
        setRadius(10);
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

    public boolean isNode(){
        return type == NODE;
    }

    public boolean isControl(){
        return type == CONTROL;
    }

    public void setCenter(double centerX, double centerY){
        setCenterX(centerX);
        setCenterY(centerY);
    }
}
