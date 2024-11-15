package src;

import java.awt.*;

public class Tile {
    public enum Type {
        EMPTY, WALL, BLOCK
    }

    public Type type;

    public Tile(Type type) {
        this.type = type;
    }

    public void draw(Graphics g, int x, int y, int size) {
        switch (type) {
            case EMPTY:
                g.setColor(Color.LIGHT_GRAY);
                break;
            case WALL:
                g.setColor(Color.DARK_GRAY);
                break;
            case BLOCK:
                g.setColor(Color.ORANGE);
                break;
        }
        g.fillRect(x, y, size, size);
        g.setColor(Color.BLACK);
        g.drawRect(x, y, size, size); 
    }
}