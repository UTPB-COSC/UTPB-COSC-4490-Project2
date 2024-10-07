package src.Drawing;

import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.util.ArrayList;

public interface Transformable {

    ArrayList<Point> verts = new ArrayList<>();

    Point getCenter();

    default AffineTransformOp Rotate(double radians) {
        Point center = getCenter();
        AffineTransform at = new AffineTransform();
        at.rotate(radians, center.x, center.y);
        return new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
    }

    default AffineTransformOp Move(int dx, int dy) {
        Point center = getCenter();
        Point target = new Point(center.x + dx, center.y + dy);
        AffineTransform at = new AffineTransform();
        at.transform(center, target);
        return new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
    }

    default AffineTransformOp Scale(double factor) {
        Point center = getCenter();
        AffineTransform at = new AffineTransform();
        at.scale(factor, factor);
        return new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
    }
}