package src.Collision;

import java.awt.Point;
import java.util.ArrayList;

public abstract class CollisionBody {
    protected CollideType type;

    public abstract boolean collideLine(CollideLine l);
    public abstract boolean collideCircle(CollideCircle c);
    public abstract boolean collideRect(CollideRect r);
    public abstract boolean collideConvShape(CollideConvShape s);
    public abstract boolean collideConcShape(CollideConcShape s);

    public boolean collide(CollisionBody cb) {
        switch (cb.type) {
            case CollideType.LINE:
                return this.collideLine((CollideLine) cb);
            case CollideType.RECT:
                return this.collideRect((CollideRect) cb);
            case CollideType.CIRCLE:
                return this.collideCircle((CollideCircle) cb);
            case CollideType.CONVSHAPE:
                return this.collideConvShape((CollideConvShape) cb);
            case CollideType.CONCSHAPE:
                return this.collideConcShape((CollideConcShape)cb);
            default:
                return false;
        }
    }

    public static boolean doLinesIntersect(CollideLine l1, CollideLine l2) {
        Point p1 = l1.p1, q1 = l1.p2;
        Point p2 = l2.p1, q2 = l2.p2;

        // Get the four orientations needed for general and special cases
        int o1 = orientation(p1, q1, p2);
        int o2 = orientation(p1, q1, q2);
        int o3 = orientation(p2, q2, p1);
        int o4 = orientation(p2, q2, q1);

        // General case: lines intersect if orientations differ
        if (o1 != o2 && o3 != o4) {
            return true;
        }

        // Special case: lines are collinear and overlap
        if (o1 == 0 && onSegment(p1, p2, q1)) return true;
        if (o2 == 0 && onSegment(p1, q2, q1)) return true;
        if (o3 == 0 && onSegment(p2, p1, q2)) return true;
        if (o4 == 0 && onSegment(p2, q1, q2)) return true;

        // Otherwise, lines do not intersect
        return false;
    }

    public static boolean doLinesIntersect(CollideLine line, ArrayList<CollideLine> edges) {
        for (CollideLine edge : edges) {
            if (doLinesIntersect(line, edge)) {
                return true;
            }
        }
        return false;
    }

    public static boolean doLinesIntersect(ArrayList<CollideLine> edges1, ArrayList<CollideLine> edges2) {
        // Iterate over all line segments of shape1
        for (CollideLine line1 : edges1) {
            // Check each line segment of shape1 against each line segment of shape2
            for (CollideLine line2 : edges2) {
                // If any pair of lines intersect, the shapes overlap
                if (doLinesIntersect(line1, line2)) {
                    return true;
                }
            }
        }
        // If no line segment pairs intersect, the shapes do not overlap
        return false;
    }

    // Helper method to find orientation of the triplet (p, q, r)
    // 0 = collinear, 1 = clockwise, 2 = counterclockwise
    private static int orientation(Point p, Point q, Point r) {
        double val = (q.y - p.y) * (r.x - q.x) - (q.x - p.x) * (r.y - q.y);
        if (val == 0) return 0;  // Collinear
        return (val > 0) ? 1 : 2; // Clockwise or counterclockwise
    }

    // Helper method to check if point r lies on segment pq
    private static boolean onSegment(Point p, Point q, Point r) {
        return (q.x <= Math.max(p.x, r.x) && q.x >= Math.min(p.x, r.x) &&
                q.y <= Math.max(p.y, r.y) && q.y >= Math.min(p.y, r.y));
    }

    // Method to check if two circles overlap
    public static boolean doCirclesOverlap(CollideCircle c1, CollideCircle c2) {
        // Calculate the distance between the centers of the two circles
        double distance = distanceBetweenPoints(c1.center, c2.center);

        // Check if the distance is less than or equal to the sum of the radii
        return distance <= (c1.radius + c2.radius) && distance >= Math.abs(c1.radius - c2.radius);
    }

    // Method to check if a line segment intersects a circle or terminates inside it
    public static boolean doesLineIntersectCircle(CollideCircle circle, CollideLine line) {
        Point p1 = line.p1;
        Point p2 = line.p2;
        Point center = circle.center;
        double radius = circle.radius;

        // Calculate the projection of the circle's center onto the line segment
        double dx = p2.x - p1.x;
        double dy = p2.y - p1.y;
        double lengthSquared = dx * dx + dy * dy;

        if (lengthSquared == 0) {
            // The line is actually a point, so just check the distance from the circle's center
            return distanceBetweenPoints(p1, center) <= radius;
        }

        // Calculate the projection of the center onto the line
        double t = ((center.x - p1.x) * dx + (center.y - p1.y) * dy) / lengthSquared;

        // Clamp t to the range [0, 1] to keep the projection within the line segment
        t = Math.max(0, Math.min(1, t));

        // Find the point on the line segment closest to the circle's center
        int closestX = (int)(p1.x + t * dx);
        int closestY = (int)(p1.y + t * dy);

        // Calculate the distance from the circle's center to this closest point
        double distanceToClosestPoint = distanceBetweenPoints(new Point(closestX, closestY), center);

        // Check if this distance is less than or equal to the circle's radius
        if (distanceToClosestPoint <= radius) {
            return true; // Line intersects the circle
        }

        // Check if either of the line's endpoints is inside the circle (terminates inside)
        return distanceBetweenPoints(p1, center) <= radius || distanceBetweenPoints(p2, center) <= radius;
    }

    public static boolean doesLineIntersectCircle(CollideCircle circle, ArrayList<CollideLine> edges) {
        for (CollideLine edge : edges) {
            if (doesLineIntersectCircle(circle, edge)) {
                return true;
            }
        }
        return false;
    }

    // Helper method to calculate the distance between two points
    private static double distanceBetweenPoints(Point p1, Point p2) {
        return Math.sqrt((p2.x - p1.x) * (p2.x - p1.x) + (p2.y - p1.y) * (p2.y - p1.y));
    }

    // Method to check if two rectangles overlap
    public static boolean doRectanglesOverlap(CollideRect r1, CollideRect r2) {
        // Check if one rectangle is to the left of the other
        if (r1.origin.x + r1.width < r2.origin.x || r2.origin.x + r2.width < r1.origin.x) {
            return false;
        }

        // Check if one rectangle is above the other
        if (r1.origin.y + r1.height < r2.origin.y || r2.origin.y + r2.height < r1.origin.y) {
            return false;
        }

        // If neither of the above conditions is true, the rectangles overlap
        return true;
    }

    // Method to check if two shapes overlap by checking line segment intersections
    public static boolean doShapesOverlap(CollideShape shape1, CollideShape shape2) {
        return doLinesIntersect(shape1.edges, shape2.edges);
    }

    // Method to check if two shapes overlap using the Separating Axis Theorem
    public static boolean doShapesOverlap(CollideConvShape shape1, CollideConvShape shape2) {
        // Check overlap along all axes formed by edges of both shapes
        return checkOverlapOnAxes(shape1, shape2) && checkOverlapOnAxes(shape2, shape1);
    }

    // Helper method to check overlap on all axes formed by one shape's edges
    private static boolean checkOverlapOnAxes(CollideConvShape shape1, CollideConvShape shape2) {
        for (CollideLine edge : shape1.edges) {
            // Get the normal (perpendicular vector) of the current edge
            Point axis = getPerpendicular(edge.direction());

            // Project both shapes onto the axis
            double[] shape1Projection = projectShapeOnAxis(shape1, axis);
            double[] shape2Projection = projectShapeOnAxis(shape2, axis);

            // Check if projections overlap, if not, the shapes do not overlap
            if (!projectionsOverlap(shape1Projection, shape2Projection)) {
                return false; // Separating axis found, shapes do not overlap
            }
        }
        return true; // No separating axis found, shapes overlap
    }

    // Helper method to get the perpendicular vector of a line (normal)
    private static Point getPerpendicular(Point vector) {
        return new Point(-vector.y, vector.x); // Rotate vector by 90 degrees
    }

    // Helper method to project a shape onto an axis
    private static double[] projectShapeOnAxis(CollideConvShape shape, Point axis) {
        ArrayList<Point> corners = shape.getCorners();
        double min = dotProduct(corners.getFirst(), axis);
        double max = min;

        // Project each corner of the shape onto the axis
        for (int i = 1; i < corners.size(); i++) {
            double projection = dotProduct(corners.get(i), axis);
            if (projection < min) {
                min = projection;
            }
            if (projection > max) {
                max = projection;
            }
        }

        // Return the min and max projection values
        return new double[]{min, max};
    }

    // Helper method to check if two projections overlap
    private static boolean projectionsOverlap(double[] proj1, double[] proj2) {
        return !(proj1[1] < proj2[0] || proj2[1] < proj1[0]); // Projections must overlap
    }

    // Helper method to calculate the dot product of two points (vectors)
    private static double dotProduct(Point p1, Point p2) {
        return p1.x * p2.x + p1.y * p2.y;
    }
}