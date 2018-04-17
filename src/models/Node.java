package models;

import javafx.scene.shape.Circle;

public class Node extends Circle{

    public int radius = 10;

    public Node(double centerX, double centerY) {
        super();
        setRadius(radius);
        setCenterX(centerX);
        setCenterY(centerY);
    }

    public Node(double centerX, double centerY, int radius) {
        super();
        this.radius = radius;
        setRadius(radius);
        setCenterX(centerX);
        setCenterY(centerY);
    }

}
