package controllers;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import models.Edge;
import models.Node;

import java.util.ArrayList;
import java.util.List;

public class Hackenbush extends Application {

    public final int DRAW_NOTHING = 0;
    public final int DRAW_NODE = 1;
    public final int DRAW_LINE = 2;
    public final int MOVE_NODE = 3;
    public final int MOVE_LINE = 4;

    public List<Node> nodes = null;
    public List<Edge> edges = null;

    public int draw = 0;
    public int nbrClickedNodes = 0;
    public int nbrClickPane = 0;
    public Node startNode = null;
    public Node endNode = null;
    public Node moveNode = null;

    @Override
    public void start(Stage primaryStage) throws Exception {
        nodes = new ArrayList<>();
        edges = new ArrayList<>();

        Pane root = new Pane();
        root.getStyleClass().add("container");

        root.setOnMouseClicked(event -> {
            if(draw == DRAW_NODE){
                Node node = new Node(event.getX(), event.getY());
                node.setOnMouseClicked(mouseEvent -> {
//                    System.out.println(mouseEvent.getSource());
                    if(draw == DRAW_LINE){
                        if(0 == nbrClickedNodes){
                            startNode = (Node) mouseEvent.getSource();
//                            System.out.println("startNode " + startNode.toString());
                            nbrClickedNodes++;
                        }else if(1 == nbrClickedNodes){
                            endNode = (Node) mouseEvent.getSource();
//                            System.out.println("endNode " + endNode.toString());

//                            System.out.println(startNode.hashCode());
//                            System.out.println(endNode.hashCode());

                            Edge edge = new Edge(startNode, endNode);

                            edge.setOnMouseClicked(mouseEvent1 -> {

                            });

//                            System.out.println(edge.startNode.hashCode());
//                            System.out.println(edge.endNode.hashCode());

                            edges.add(edge);
                            root.getChildren().add(edge);

                            reset();
                        }
                    }else if(draw == MOVE_NODE){
                        System.out.println(mouseEvent.getSource());
                        if(0 == nbrClickedNodes){
                            moveNode = (Node) mouseEvent.getSource();
                            System.out.println("Before moving " + moveNode.toString());
                            nbrClickedNodes++;
                        }
                    }
                });

                nodes.add(node);
                root.getChildren().add(node);
            }else if(draw == MOVE_NODE){
                System.out.println("Move");
                if(1 == nbrClickedNodes){
                    if(1 == nbrClickPane){
                        moveNode.setCenterX(event.getX());
                        moveNode.setCenterY(event.getY());
                        System.out.println("After moving " + moveNode.toString());

                        reset();
                    }
                    nbrClickPane++;
                }

            }
        });

        Scene scene = new Scene(root, 600, 600);
        scene.getStylesheets().add(getClass().getResource("../views/css/style.css").toExternalForm());
        scene.setOnKeyPressed(keyEvent -> {
            if(keyEvent.getCode().equals(KeyCode.N)){
                draw = DRAW_NOTHING;
            }else if(keyEvent.getCode().equals(KeyCode.L)){
                draw = DRAW_LINE;
            }else if(keyEvent.getCode().equals(KeyCode.C)){
                draw = DRAW_NODE;
            }else if(keyEvent.getCode().equals(KeyCode.M)){
                draw = MOVE_NODE;
            }
            reset();
        });

        primaryStage.setTitle("Hackenbush Game");
        primaryStage.setMaximized(false);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void reset(){
        nbrClickedNodes = 0;
        nbrClickPane = 0;
        startNode = null;
        endNode = null;
        moveNode = null;
    }

    public static void main(String[] args){
        launch(args);
    }
}
