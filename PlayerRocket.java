package DragonPoopGame;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 * The space rocket with which player will have to land.
 * 
 * @author www.gametutorial.net
 */

public class PlayerRocket {
    
    /**
     * We use this to generate a random number for starting x coordinate of the rocket.
     */
    private Random random;
 
    /**
     * X coordinate of the rocket.
     */
    public int x;
    /**
     * Y coordinate of the rocket.
     */
    public int y;
    
    /**
     * Is rocket landed?
     */
    public boolean landed;
    
    /**
     * Has rocket crashed?
     */
    public boolean crashed;
        
    /**
     * Accelerating speed of the rocket.
     */
    private int speedAccelerating;
    /**
     * Stopping/Falling speed of the rocket. Falling speed because, the gravity pulls the rocket down to the moon.
     */
    private int speedStopping;
    
    /**
     * Maximum speed that rocket can have without having a crash when landing.
     */
    public int topLandingSpeed;
    
    /**
     * How fast and to which direction rocket is moving on x coordinate?
     */
    private int speedX;
    /**
     * How fast and to which direction rocket is moving on y coordinate?
     */
    public int speedY;
            
    /**
     * Image of the rocket in air.
     */
    private BufferedImage rocketImg;
    private BufferedImage rocketImgDown;
    private BufferedImage rocketImgLeft;
    private BufferedImage rocketImgRight;
    /**
     * Image of the rocket when landed.
     */
    private BufferedImage rocketLandedImg;
    /**
     * Image of the rocket when crashed.
     */
    private BufferedImage rocketCrashedImg;
    /**
     * Image of the rocket fire.
     */
    private BufferedImage rocketFireImg;
    private BufferedImage rocketFireImgDown;
    private BufferedImage rocketFireImgRight;
    private BufferedImage rocketFireImgLeft;

    private Font coordinateFont;
    
    /**
     * Width of rocket.
     */
    public int rocketImgWidth;
    /**
     * Height of rocket.
     */
    public int rocketImgHeight;
    
    
    public PlayerRocket()
    {
        Initialize();
        LoadContent();
        
        // Now that we have rocketImgWidth we set starting x coordinate.
        x = random.nextInt(Framework.frameWidth - rocketImgWidth);
    }
    
    
    private void Initialize()
    {
        random = new Random();
        
        ResetPlayer();
        
        speedAccelerating = 5;
        
        topLandingSpeed = 7;

        coordinateFont = new Font(Font.SANS_SERIF, Font.PLAIN, 30);
    }
    
    private void LoadContent()
    {
        try
        {
            URL rocketImgUrl = this.getClass().getResource("/DragonPoopGame/resources/images/rocket.png");
            rocketImg = ImageIO.read(rocketImgUrl);
            rocketImgWidth = rocketImg.getWidth();
            rocketImgHeight = rocketImg.getHeight();

            URL rocketImgUrlDir = this.getClass().getResource("/DragonPoopGame/resources/images/rocket_down.png");
            rocketImgDown = ImageIO.read(rocketImgUrlDir);
            rocketImgUrlDir = this.getClass().getResource("/DragonPoopGame/resources/images/rocket_left.png");
            rocketImgLeft = ImageIO.read(rocketImgUrlDir);
            rocketImgUrlDir = this.getClass().getResource("/DragonPoopGame/resources/images/rocket_right.png");
            rocketImgRight = ImageIO.read(rocketImgUrlDir);
            
            URL rocketLandedImgUrl = this.getClass().getResource("/DragonPoopGame/resources/images/rocket_landed.png");
            rocketLandedImg = ImageIO.read(rocketLandedImgUrl);
            
            URL rocketCrashedImgUrl = this.getClass().getResource("/DragonPoopGame/resources/images/rocket_crashed.png");
            rocketCrashedImg = ImageIO.read(rocketCrashedImgUrl);
            
            URL rocketFireImgUrl = this.getClass().getResource("/DragonPoopGame/resources/images/rocket_fire.png");
            rocketFireImg = ImageIO.read(rocketFireImgUrl);

            URL rocketFireImgUrlDir = this.getClass().getResource("/DragonPoopGame/resources/images/rocket_fire_down.png");
            rocketFireImgDown = ImageIO.read(rocketFireImgUrlDir);
            rocketFireImgUrlDir = this.getClass().getResource("/DragonPoopGame/resources/images/rocket_fire_left.png");
            rocketFireImgLeft = ImageIO.read(rocketFireImgUrlDir);
            rocketFireImgUrlDir = this.getClass().getResource("/DragonPoopGame/resources/images/rocket_fire_right.png");
            rocketFireImgRight = ImageIO.read(rocketFireImgUrlDir);
        }
        catch (IOException ex) {
            Logger.getLogger(PlayerRocket.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Here we set up the rocket when we starting a new game.
     */
    public void ResetPlayer()
    {
        landed = false;
        crashed = false;
        
        x = random.nextInt(Framework.frameWidth - rocketImgWidth);
        y = 10;
        
        speedX = 0;
        speedY = 0;
    }
    
    private enum Direction{
        UP, DOWN, LEFT, RIGHT, NONE
    }
    private Direction rocketFacing = Direction.NONE;
    /**
     * Here we move the rocket.
     */
    public void Update()
    {
        
        //hitting the walls kills you
        if(x <= 0 || y <= 0 || x + rocketImgWidth >= Framework.frameWidth || y + rocketImgHeight >= Framework.frameHeight)
            crashed = true;

        // Calculating speed for moving up or down.
        if(Canvas.keyboardKeyState(KeyEvent.VK_W) || Canvas.keyboardKeyState(KeyEvent.VK_UP))
        {
            if(rocketFacing == Direction.DOWN)
            {
                //TODO: a sound
            }
            else
            {
                speedY = -speedAccelerating;
                speedX = 0;
                rocketFacing = Direction.UP;
            }
        }
        // Calculating speed for moving up or down.
        else if(Canvas.keyboardKeyState(KeyEvent.VK_S) ||Canvas.keyboardKeyState(KeyEvent.VK_DOWN))
        {
            if(rocketFacing == Direction.UP)
            {
                //TODO: a sound
            }
            else
            {
                 speedY = speedAccelerating;
                 speedX = 0;
                 rocketFacing = Direction.DOWN;
            }
         }           
        // Calculating speed for moving or stopping to the left.
        else if(Canvas.keyboardKeyState(KeyEvent.VK_A)||Canvas.keyboardKeyState(KeyEvent.VK_LEFT))
        {
            if(rocketFacing == Direction.RIGHT)
            {
                //TODO: a sound
            }
            else
            {
                speedX = -speedAccelerating;
                speedY = 0;
                rocketFacing = Direction.LEFT;
            }
        }
        // Calculating speed for moving or stopping to the right.
        else if(Canvas.keyboardKeyState(KeyEvent.VK_D)||Canvas.keyboardKeyState(KeyEvent.VK_RIGHT))
        {
            if(rocketFacing == Direction.LEFT)
            {
                //TODO: a sound
            }
            else
            {
                speedX = speedAccelerating;
                speedY = 0;
                rocketFacing = Direction.RIGHT;
            }
        }
        // Moves the rocket.
        x += speedX;
        y += speedY;
    }
    
    private BufferedImage rocketPlaceholder;
    private BufferedImage rocketFirePlaceholder;
    private int x_fire = 0;
    private int y_fire = 0;

    public void Draw(Graphics2D g2d)
    {
        g2d.setColor(Color.white);
        g2d.setFont(coordinateFont); 
        
        // If the rocket is landed.
        if(landed)
        {
            g2d.drawImage(rocketLandedImg, x, y, null);
        }
        // If the rocket is crashed.
        else if(crashed)
        {
            g2d.drawImage(rocketCrashedImg, x, y + rocketImgHeight - rocketCrashedImg.getHeight(), null);
        }
        // If the rocket is still in the space.
        else
        {
            switch(rocketFacing)
            {
                case UP:
                    rocketPlaceholder = rocketImg;
                    rocketFirePlaceholder = rocketFireImg;
                    x_fire = x + 12;
                    y_fire = y + 66;
                    break;
                case DOWN:
                    rocketPlaceholder = rocketImgDown;
                    rocketFirePlaceholder = rocketFireImgDown;
                    x_fire = x + 12;
                    y_fire = y - 66;
                    break;
                case LEFT:
                    rocketPlaceholder = rocketImgLeft;
                    rocketFirePlaceholder = rocketFireImgLeft;
                    x_fire = x + 66;
                    y_fire = y + 12;
                    break;
                case RIGHT:
                    rocketPlaceholder = rocketImgRight;
                    rocketFirePlaceholder = rocketFireImgRight;
                    x_fire = x - 66;
                    y_fire = y + 12;
                    break;
                default:
                    throw new RuntimeException("unknown case for rocket facing direction");
            }
                        // draw rocket fire 
            if(Canvas.keyboardKeyState(KeyEvent.VK_SPACE))
                g2d.drawImage(rocketFirePlaceholder, x_fire, y_fire, null);

            g2d.drawImage(rocketPlaceholder, x, y, null);
        }
    }
    
}
