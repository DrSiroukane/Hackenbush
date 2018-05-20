package helpers;

import models.Edge;
import models.Node;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class FileGenerator {
    public static void createFile(List<Node> nodes, List<Edge> edges, File file) {
        try {
            FileWriter fileW;
            fileW = new FileWriter(file);
            System.out.format("start writing in a file %s%n", file);
            PrintWriter printLine = new PrintWriter(fileW);
            printLine.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                    "\n" +
                    "<?import javafx.scene.layout.Pane?>\n" +
                    "<?import javafx.scene.shape.Circle?>\n" +
                    "<?import javafx.scene.shape.CubicCurveTo?>\n" +
                    "<?import javafx.scene.shape.MoveTo?>\n" +
                    "<?import javafx.scene.shape.Path?>\n" +
                    "<Pane stylesheets=\"" + FileGenerator.class.getResource("../views/css/modeGeneric.css").toExternalForm() + "\">");
            for (int i = 0; i < edges.size(); i++) {
                printLine.println(getFxml(edges.get(i)));
            }
            for (int i = 0; i < nodes.size(); i++) {
                printLine.println(getFxml(nodes.get(i)));
            }
            printLine.println("</Pane>");
            printLine.close();
            fileW.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getFxml(Edge edge) {
        return "\t<Path strokeWidth=\"" + edge.getStrokeWidth() + "\" stroke=\"" + edge.getStroke() + "\">\n\t\t<elements>\n" +
                "\t\t\t<MoveTo x=\"" + edge.edgeNodes[0].getCenterX() + "\" y=\"" + edge.edgeNodes[0].getCenterY() + "\" />\n" +
                "\t\t\t<CubicCurveTo x=\"" + edge.edgeNodes[1].getCenterX() + "\" y=\"" + edge.edgeNodes[1].getCenterY() +
                " \" controlX1=\"" + edge.controls[0].getCenterX() + "\" controlY1=\"" + edge.controls[0].getCenterY() +
                " \" controlX2=\"" + edge.controls[1].getCenterX() + "\" controlY2=\"" + edge.controls[1].getCenterY() + "\"/>\n" +
                "\t\t</elements>\n\t</Path>";
    }

    public static String getFxml(Node node) {
        return "\t<Circle styleClass=\"" + node.getStyleClass() + "\" radius=\"" + node.getRadius() +
                "\" centerX=\"" + node.getCenterX() + "\" centerY=\"" + node.getCenterY() + "\"/>";
    }

}
