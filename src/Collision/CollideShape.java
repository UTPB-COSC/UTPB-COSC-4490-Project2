package src.Collision;

import java.awt.Point;
import java.util.ArrayList;

public abstract class CollideShape extends CollisionBody{
    ArrayList<CollideLine> edges;

    public CollideShape() {
        this.edges = new ArrayList<>();
    }

    public void addLine(CollideLine line) {
        edges.add(line);
    }

    // Helper method to get all the corner points of the shape
    public ArrayList<Point> getCorners() {
        ArrayList<Point> corners = new ArrayList<>();
        for (CollideLine edge : edges) {
            corners.add(edge.p1);
        }
        return corners;
    }
}
