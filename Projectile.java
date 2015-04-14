package DragonPoopGame;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

public class Projectile
{
    private static int maxProjectiles = 5;
    public static List <Projectile> s_Projectiles = 
        new ArrayList<Projectile>(maxProjectiles);

    int speed = 6; //in pix per frame
    int xPos;
    int yPos;
    PlayerRocket.Direction direction;
    BufferedImage projectileImage;

    public Projectile(int xStart, int yStart, PlayerRocket.Direction direction, BufferedImage projectileImage)
    {
        xPos = xStart;
        yPos = yStart;
        this.direction = direction;
        this.projectileImage = projectileImage;
    }

    public void Draw(Graphics2D g2d)
    {
        g2d.drawImage(projectileImage, xPos, yPos, null);
    }

    public void Update()
    {
        switch(direction)
        {
            case UP:
                yPos -= speed;
                break;
            case DOWN:
                yPos += speed;
                break;
            case LEFT:
                xPos -= speed;
                break;
            case RIGHT:
                xPos += speed;
                break;
            default: 
                //case NONE
        }

        //TODO: remove from list if out of frame
    }
}
