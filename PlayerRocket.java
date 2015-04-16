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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * The space rocket with which player will have to land.
 * 
 * @author www.gametutorial.net
 */

public class PlayerRocket implements ActionListener {
    
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

    private int rocketStartingY;
    
    /**
     * Width of rocket.
     */
    public int rocketImgWidth;
    /**
     * Height of rocket.
     */
    public int rocketImgHeight;

    //Dimensions of fired projectile
    private int rocketFireImgWidth;
    private int rocketFireImgHeight;
    private int overlapFractionInverted;
    
    private Audio audioInstance;
    
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
        
        rocketStartingY = rocketImgHeight + 10;
        
        ResetPlayer();
        
        speedAccelerating = 5;
        
        topLandingSpeed = 7;

        // 1/overlapFractionInverted is multiplied by the projectile image height
        overlapFractionInverted = 4; 

        coordinateFont = new Font(Font.SANS_SERIF, Font.PLAIN, 30);

        audioInstance = Audio.getInstance();
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
            rocketFireImgWidth = rocketFireImg.getWidth();
            rocketFireImgHeight = rocketFireImg.getHeight();

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
        y = rocketStartingY;
        
        speedX = 0;
        speedY = 0;
    }
    
    public enum Direction{
        UP, DOWN, LEFT, RIGHT, NONE
    }
    private Direction rocketFacing = Direction.NONE;
    /**
     * Here we move the rocket.
     */
    public void Update()
    {
        // Calculating speed for moving up or down.
        if(Canvas.keyboardKeyState(KeyEvent.VK_W) || Canvas.keyboardKeyState(KeyEvent.VK_UP))
        {
            if(rocketFacing == Direction.DOWN)
            {
                audioInstance.PlaySound(Audio.SituationForSound.MOVE_ERROR);
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
                audioInstance.PlaySound(Audio.SituationForSound.MOVE_ERROR);
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
                audioInstance.PlaySound(Audio.SituationForSound.MOVE_ERROR);
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
                audioInstance.PlaySound(Audio.SituationForSound.MOVE_ERROR);
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

    private boolean FireWasTriggered = false;

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
                    x_fire = x + (rocketImgWidth - rocketFireImgWidth)/2;
                    y_fire = y + rocketImgHeight - rocketFireImgHeight/overlapFractionInverted;
                    break;
                case DOWN:
                    rocketPlaceholder = rocketImgDown;
                    rocketFirePlaceholder = rocketFireImgDown;
                    x_fire = x + (rocketImgWidth - rocketFireImgWidth)/2;
                    y_fire = y - rocketImgHeight + rocketFireImgHeight/overlapFractionInverted;
                    break;
                case LEFT:
                    rocketPlaceholder = rocketImgLeft;
                    rocketFirePlaceholder = rocketFireImgLeft;
                    x_fire = x + rocketImgHeight - rocketFireImgHeight/overlapFractionInverted;
                    y_fire = y + (rocketImgWidth - rocketFireImgWidth)/2;
                    break;
                case RIGHT:
                    rocketPlaceholder = rocketImgRight;
                    rocketFirePlaceholder = rocketFireImgRight;
                    x_fire = x - rocketImgHeight + rocketFireImgHeight/overlapFractionInverted;
                    y_fire = y + (rocketImgWidth - rocketFireImgWidth)/2;
                    break;
                default: //case NONE
                    //player hasn't started moving yet
                    rocketPlaceholder = rocketImg;
                    rocketFirePlaceholder = rocketFireImg;
                    x_fire = x + (rocketImgWidth - rocketFireImgWidth)/2;
                    y_fire = y + rocketImgHeight- rocketFireImgHeight/overlapFractionInverted;
            }
             // draw rocket fire 
            //TODO: restrict sensitivity of keypresses: only one projectile per strike
            //TODO: read about events some more here: https://docs.oracle.com/javase/tutorial/uiswing/events/intro.html
            //and here: http://www.javaworld.com/article/2077333/core-java/mr-happy-object-teaches-custom-events.html
            if(FireWasTriggered)
            {
                FireWasTriggered = false;
                g2d.drawImage(rocketFirePlaceholder, x_fire, y_fire, null);
                Projectile.s_Projectiles.add( new Projectile(x_fire, y_fire, 
                    OppositeDirection(rocketFacing), rocketFirePlaceholder) );
                audioInstance.PlaySound(Audio.SituationForSound.SHAT_ENEMY);
            }

            g2d.drawImage(rocketPlaceholder, x, y, null);
        }
    }

    private Direction OppositeDirection(Direction direction)
    {
        switch(rocketFacing)
        {
            case UP:
                return Direction.DOWN;
            case DOWN:
                return Direction.UP;
            case LEFT:
                return Direction.RIGHT;
            case RIGHT:
                return Direction.LEFT;
            default: //case NONE
                return Direction.NONE;
        }
    }

    public void actionPerformed(ActionEvent e)
    {

    }
    
}