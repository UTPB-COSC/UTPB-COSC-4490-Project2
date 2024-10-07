package src.Drawing;

import java.awt.Point;

public interface Parallax {
    // Get the depth factor (how much the object moves relative to others)
    double getFactor();
    Point getOrigin();
    void setOrigin(Point o);

    default void parallax(int dx, int dy) {
        double f = getFactor();
        Point o = getOrigin();
        o.x += (int) (dx * f);
        o.y += (int) (dy * f);
        setOrigin(o);
    }
}
