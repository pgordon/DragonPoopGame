package DragonPoopGame;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import java.util.Random;

/**
 * Collectible
 *  copied from LandingArea 
 * @author www.gametutorial.net
 */

public class PowerUp {
    
    /**
     * coordinates
     */
    public int x;
    public int y;

    private BufferedImage powerUpImg;

    public int powerUpImgWidth;
    public int powerUpImgHeight;

    private Animation animation;
    private long frameTime = 40; //24 frames a second
    private long delay = 500; //one second in ms
    private int numAnimFrames = 12;
    private boolean loopAnim = true;
    
    /**
     * We use this to generate a random number for starting coordinates
     */
    private Random random;
    
    public PowerUp()
    {   
        LoadContent();
        random = new Random();
        x = random.nextInt(Framework.frameWidth - powerUpImgWidth);
        y = random.nextInt(Framework.frameHeight - powerUpImgHeight);

        animation = new Animation(powerUpImg, powerUpImgWidth, powerUpImgHeight, 
            numAnimFrames, frameTime, loopAnim, x, y, delay);
    }
    
    private void LoadContent()
    {
        try
        {
            URL powerUpImgUrl = this.getClass().getResource("/DragonPoopGame/resources/images/power_up_anim.png");
            powerUpImg = ImageIO.read(powerUpImgUrl);
            powerUpImgWidth = powerUpImg.getWidth()/numAnimFrames;
            powerUpImgHeight = powerUpImg.getHeight();
        }
        catch (IOException ex) {
            Logger.getLogger(PowerUp.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    //don't have to change co-ordinate because power-ups don't move
    public void Draw(Graphics2D g2d)
    {
        animation.Draw(g2d);
    }

    public boolean isTouching(int x_in, int y_in, int width, int height)
    {
        if((this.x + powerUpImgWidth >= x_in && this.x <= x_in + width)
            &&
        (this.y + powerUpImgHeight >= y_in && this.y <= y_in + height))
            return true;

        return false;
    }
    
}
