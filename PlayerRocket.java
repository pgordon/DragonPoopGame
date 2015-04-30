package DragonPoopGame;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.LinkedList;
import java.util.Queue;
import javax.imageio.ImageIO;

/**
 * The space rocket with which player will have to land.
 * 
 * @author www.gametutorial.net
 */

public class PlayerRocket implements KeyReleaseListener {
    
    /**
     * We use this to generate a random number for starting x coordinate of the rocket.
     */
    private Random random;
 
    //Dragon head coordinates
    public int x;
    public int y;
    
    //Game won?
    public boolean landed;
    
    //Game lost?
    public boolean crashed;
    
    //Dragon speed
    private int speed;

    //Speed broken into directions
    private int speedX;
    public int speedY;

    //How much ammo is left to shoot. Incremented by enemies eaten
    private int ammoStored = 0;

    //How long a dragon (how many units of tail, including head)
    private int segments = 2; //TODO: ref to tail segment
    
    //Keeps track of turns for drawing tail
    private Queue <MoveNode> moveHistory = new LinkedList<MoveNode>();
            
    
    //Image of the dragon head, different directions
    private BufferedImage rocketImg;
    private BufferedImage rocketImgDown;
    private BufferedImage rocketImgLeft;
    private BufferedImage rocketImgRight;

    //Dragon tail segment
    private BufferedImage dragonTailImg;

    //Image of the game won dragon head
    private BufferedImage rocketLandedImg;
    
    //Image of the game over dragon head
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
        LoadContent();
        Initialize();        
    }
    
    //should be called after LoadContent, as it requires rocket width, height
    private void Initialize()
    {
        random = new Random();
        
        x = random.nextInt(Framework.frameWidth - rocketImgWidth);

        rocketStartingY = rocketImgHeight + 10;
        
        ResetPlayer();
        
        speed = rocketImgWidth/2;

        // 1/overlapFractionInverted is multiplied by the projectile image height
        overlapFractionInverted = 4; 

        coordinateFont = new Font(Font.SANS_SERIF, Font.PLAIN, 30);

        audioInstance = Audio.getInstance();

        Framework.subscribeToKeyboardInGame(this);
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

            URL dragonTailImgUrl = this.getClass().getResource("/DragonPoopGame/resources/images/dragonBody.png");
            dragonTailImg = ImageIO.read(dragonTailImgUrl);
            
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

        ammoStored = 0;

        moveHistory.clear();

        rocketFacing = Direction.NONE;
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
                if(rocketFacing != Direction.UP)
                    moveHistory.add(new MoveNode(x, y, Direction.UP));
                speedY = -speed;
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
                if(rocketFacing != Direction.DOWN)
                    moveHistory.add(new MoveNode(x, y, Direction.DOWN));
                 speedY = speed;
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
                if(rocketFacing != Direction.LEFT)
                    moveHistory.add(new MoveNode(x, y, Direction.LEFT));
                speedX = -speed;
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
                if(rocketFacing != Direction.RIGHT)
                    moveHistory.add(new MoveNode(x, y, Direction.RIGHT));
                speedX = speed;
                speedY = 0;
                rocketFacing = Direction.RIGHT;
            }
        }
        // Moves the head
        x += speedX;
        y += speedY;

        //Move the tail:
        //TODO
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
        g2d.drawString("# moves: " + moveHistory.size(), 25, 25);
        
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
            if(FireWasTriggered)
            {
                FireWasTriggered = false;
                if(ammoStored > 0)
                {
                    g2d.drawImage(rocketFirePlaceholder, x_fire, y_fire, null);
                    Projectile.s_Projectiles.add( new Projectile(x_fire, y_fire, 
                        OppositeDirection(rocketFacing), rocketFirePlaceholder) );
                    audioInstance.PlaySound(Audio.SituationForSound.SHAT_ENEMY);
                    ammoStored--;
                }
                else
                { 
                    audioInstance.PlaySound(Audio.SituationForSound.OUT_OF_AMMO);
                }
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

    public void keyReleasedEvent(KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_SPACE)
            FireWasTriggered = true;
    }

    public void incrementAmmo()
    {
        ammoStored++;
    }
    
}

