package src.Collision;

public class CollideConvShape extends CollideShape {

    public CollideConvShape() {
        super();
        this.type = CollideType.CONVSHAPE;
    }

    @Override
    public boolean collideLine(CollideLine l) {
        return doLinesIntersect(l, edges);
    }

    @Override
    public boolean collideCircle(CollideCircle l) {
        return doesLineIntersectCircle(l, edges);
    }

    @Override
    public boolean collideRect(CollideRect l) {
        return doLinesIntersect(edges, l.getEdges());
    }

    @Override
    public boolean collideConvShape(CollideConvShape l) {
        return doShapesOverlap(this, l);
    }

    @Override
    public boolean collideConcShape(CollideConcShape l) {
        return doShapesOverlap(this, l);
    }
}