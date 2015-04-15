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
    int projectileImgWidth;
    int projectileImgHeight;

    public Projectile(int xStart, int yStart, PlayerRocket.Direction direction, BufferedImage projectileImage)
    {
        xPos = xStart;
        yPos = yStart;
        this.direction = direction;
        this.projectileImage = projectileImage;
        projectileImgWidth = projectileImage.getWidth();
        projectileImgHeight = projectileImage.getHeight();
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
    }

    public boolean InFrame()
    {
        if(xPos + projectileImgWidth < 0 || xPos >= Framework.frameWidth ||
            yPos + projectileImgHeight < 0 || yPos >= Framework.frameHeight)
        {
            return false;
        }
        return true;
    }
}
