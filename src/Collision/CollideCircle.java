package src.Collision;

import java.awt.Point;

public class CollideCircle extends CollisionBody {
    Point center;
    double radius;

    public CollideCircle(Point center, double radius) {
        this.type = CollideType.CIRCLE;
        this.center = center;
        this.radius = radius;
    }

    @Override
    public boolean collideLine(CollideLine l) {
        return doesLineIntersectCircle(this, l);
    }

    @Override
    public boolean collideCircle(CollideCircle l) {
        return doCirclesOverlap(this, l);
    }

    @Override
    public boolean collideRect(CollideRect l) {
        return doesLineIntersectCircle(this, l.getEdges());
    }

    @Override
    public boolean collideConvShape(CollideConvShape l) {
        return doesLineIntersectCircle(this, l.edges);
    }

    @Override
    public boolean collideConcShape(CollideConcShape l) {
        return doesLineIntersectCircle(this, l.edges);
    }
}