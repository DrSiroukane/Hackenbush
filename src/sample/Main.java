package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.stage.Stage;

public class Main extends Application {


    public boolean mouse_clicked = false;
    public double x1 = 0, x2 = 0, y1 = 0, y2 =0;


    @Override
    public void start(Stage primaryStage) throws Exception{
        Pane root = (Pane) FXMLLoader.load(getClass().getResource("sample.fxml"));
        double startX = 50;
        double startY = 40;
        double endX = 150;
        double endY = 250;

        Circle circle = new Circle(10);
        circle.setCenterX(startX);
        circle.setCenterY(startY);
        circle.setFill(Color.WHITE);

        Circle circle1 = new Circle(10);
        circle1.setCenterX(endX);
        circle1.setCenterY(endY);
        circle1.setFill(Color.WHITE);

        Line line = new Line();
        line.setStartX(startX);
        line.setStartY(startY);
        line.setEndX(endX);
        line.setEndY(endY);
        line.setFill(Color.BLUE);
        line.setStrokeWidth(5);
        line.setOnMouseClicked(event -> {
            System.out.println(event.getX() + " , " + event.getY());
        });

        root.setOnMouseClicked(event -> {
            if(!mouse_clicked){
                x1 = event.getX();
                y1 = event.getY();
                System.out.println("first click");
            }else{
                x2 = event.getX();
                y2 = event.getY();
                System.out.println("second click");

                Line line1 = new Line();


                root.getChildren().add(createLine(x1, x2, y1, y2));
            }

            mouse_clicked = !mouse_clicked;

            System.out.println("setOnMouseClicked");
            System.out.println(event.getX() + " , " + event.getY());
        });

        /*root.setOnMouseDragged(event -> {
            System.out.println("setOnMouseDragged");
            System.out.println(event.getX() + " , " + event.getY());
        });

        root.setOnMouseDragEntered(event -> {
            System.out.println("setOnMouseDragEntered");
            System.out.println(event.getX() + " , " + event.getY());
        });

        root.setOnMouseDragExited(event -> {
            System.out.println("setOnMouseDragExited");
            System.out.println(event.getX() + " , " + event.getY());
        });*/


        root.getChildren().addAll(circle, line, circle1);
        Scene scene = new Scene(root, 300, 300);


        primaryStage.setTitle("Hello World");
        primaryStage.setScene(scene);
        primaryStage.show();
    }


    public Line createLine(double startX, double endX, double startY, double endY){
        Line line = new Line();
        line.setStartX(startX);
        line.setStartY(startY);
        line.setEndX(endX);
        line.setEndY(endY);
        line.setFill(Color.BLUE);
        line.setStrokeWidth(5);
        line.setOnMouseClicked(event -> {
            System.out.println(event.getX() + " , " + event.getY());
        });
        return line;
    }


    public static void main(String[] args) {
        launch(args);
    }
}
