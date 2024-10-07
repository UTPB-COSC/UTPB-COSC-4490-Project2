package src.Collision;

import java.awt.Point;

public class CollideLine extends CollisionBody {
    protected Point p1;
    protected Point p2;
    protected Point center;

    public CollideLine(Point p1, Point p2) {
        this.type = CollideType.LINE;
        this.p1 = p1;
        this.p2 = p2;
        center = new Point((p1.x + p2.x)/2, (p1.y + p2.y)/2);
    }

    public CollideLine(int x1, int y1, int x2, int y2) {
        this.type = CollideType.LINE;
        p1 = new Point(x1, y1);
        p2 = new Point(x2, y2);
        center = new Point((x1 + x2)/2, (y1 + y2)/2);
    }

    public Point direction() {
        return new Point(p2.x - p1.x, p2.y - p1.y);
    }

    @Override
    public boolean collideLine(CollideLine l) {
        return doLinesIntersect(this, l);
    }

    @Override
    public boolean collideCircle(CollideCircle l) {
        return doesLineIntersectCircle(l, this);
    }

    @Override
    public boolean collideRect(CollideRect l) {
        return doLinesIntersect(this, l.getEdges());
    }

    @Override
    public boolean collideConvShape(CollideConvShape l) {
        return doLinesIntersect(this, l.edges);
    }

    @Override
    public boolean collideConcShape(CollideConcShape l) {
        return doLinesIntersect(this, l.edges);
    }
}