package src.Collision;

import java.awt.Point;
import java.util.ArrayList;

public class CollideRect extends CollisionBody {
    Point origin;
    Point center;
    int width, height;

    public CollideRect(Point origin, int width, int height) {
        this.type = CollideType.RECT;
        this.origin = origin;
        this.width = width;
        this.height = height;
        this.center = new Point(origin.x + width/2, origin.y+height/2);
    }

    public ArrayList<CollideLine> getEdges() {
        ArrayList<CollideLine> edges = new ArrayList<>();
        Point a = new Point(origin.x, origin.y);
        Point b = new Point(origin.x + width, origin.y);
        Point c = new Point(origin.x + width, origin.y + height);
        Point d = new Point(origin.x, origin.y + height);
        edges.add(new CollideLine(a, b));
        edges.add(new CollideLine(b, c));
        edges.add(new CollideLine(c, d));
        edges.add(new CollideLine(d, a));
        return edges;
    }

    // Helper method to check complete overlap
    public static boolean doesRect1CompletelyOverlapRect2(CollideRect rect1, CollideRect rect2) {
        return (rect1.origin.x <= rect2.origin.x &&
                rect1.origin.y <= rect2.origin.y &&
                rect1.origin.x + rect1.width >= rect2.origin.x + rect2.width &&
                rect1.origin.y + rect1.height >= rect2.origin.y + rect2.height);
    }

    @Override
    public boolean collideLine(CollideLine l) {
        return doLinesIntersect(l, getEdges());
    }

    @Override
    public boolean collideCircle(CollideCircle l) {
        return doesLineIntersectCircle(l, getEdges());
    }

    @Override
    public boolean collideRect(CollideRect l) {
        return doRectanglesOverlap(this, l);
    }

    @Override
    public boolean collideConvShape(CollideConvShape l) {
        return doLinesIntersect(getEdges(), l.edges);
    }

    @Override
    public boolean collideConcShape(CollideConcShape l) {
        return doLinesIntersect(getEdges(), l.edges);
    }
}