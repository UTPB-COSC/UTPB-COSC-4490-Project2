package src;

import java.awt.*;

public class Bomber
{
    Game game;

    private Toolkit tk;

    public int yPos;
    public int xPos;

    public double defaultVel = 4.0;
    public double xVel = 3.0;

    public int width;
    public int height;

    private boolean scoreable = true;
    public boolean spawnable = true;

    public Bomber(Game g, Toolkit tk, int y, int w, int h)
    {
        game = g;
        this.tk = tk;

        xPos = tk.getScreenSize().width;
        int maxOffset = tk.getScreenSize().height / 5;
        width = w;
        height = h;

        if (g.randomGaps) {
            yPos = (int) (Math.random() * (tk.getScreenSize().height - height));
        } else {
            // Constrain the new bomber's yPos to be within ±maxOffset of prevBomberY
            int minY = Math.max(0, y - maxOffset); // Ensure bomber stays within top boundary
            int maxY = Math.min(tk.getScreenSize().height - height, y + maxOffset); // Ensure bomber stays within bottom boundary
            yPos = minY + (int) (Math.random() * (maxY - minY + 1));
        }
    }

    public void drawBomber(Graphics g)
    {
        Graphics2D g2d = (Graphics2D) g;
        g2d.drawImage(game.bomberImage, xPos, yPos, null);

        if (game.debug)
        {
            int hitboxX = xPos + width / 2;
            int hitboxWidth = width / 2;
            g2d.setColor(Color.RED);
            g2d.drawRect(hitboxX, yPos, hitboxWidth, height);
        }
    }

    public boolean update()
    {
        xVel = defaultVel + game.difficulty;
        xPos -= xVel;

        if (scoreable && xPos < tk.getScreenSize().width / 2)
        {
            scoreable = false;
            return true;
        }
        return false;
    }
}
