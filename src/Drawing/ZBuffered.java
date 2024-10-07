package src.Drawing;

import src.Collision.CollideRect;

public interface ZBuffered {
    // Getters for transparency, zLayer, and bounding rectangle
    boolean isTransparent();
    int getZLayer();
    CollideRect getBoundingRect(); // Assuming Rectangle represents the bounding box

    // Method to determine if rect1 (this object) occludes rect2
    default boolean doesOcclude(ZBuffered rect2) {
        // Check if rect1 is non-transparent and has a lower zLayer than rect2
        if (!this.isTransparent() && this.getZLayer() < rect2.getZLayer()) {
            // Check if this object's bounding rectangle completely overlaps rect2's bounding rectangle
            return CollideRect.doesRect1CompletelyOverlapRect2(this.getBoundingRect(), rect2.getBoundingRect());
        }
        return false;
    }
}
