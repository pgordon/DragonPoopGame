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
//import java.util.Iterator;
//import java.util.LinkedList;
import java.util.ArrayList;
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
    private int speedY;

    //Body speed broken into directions
    private int bodySpeedX;
    private int bodySpeedY;

    //How much ammo is left to shoot. Incremented by enemies eaten
    private int ammoStored = 0;

    //How long a dragon (how many units of tail, including head)
    private int segments = 1; //TODO: ref to tail segment
    
    //Keeps track of positions for drawing dragon
    private ArrayList <MoveNode> dragonBody = new ArrayList<MoveNode>();
            
    //properties of the body segment
    private int bodyX;
    private int bodyY;
    private Direction bodyDirection;
    
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

    //Dimensions of dragon tail segment
    private int dragonTailImgWidth;
    private int dragonTailImgHeight;
    
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
            dragonTailImgWidth = dragonTailImg.getWidth();
            dragonTailImgHeight = dragonTailImg.getHeight();
            
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
        y = rocketStartingY + dragonTailImgHeight + 10;;
        
        speedX = 0;
        speedY = 0;

        ammoStored = 0;

        dragonBody.clear();
        dragonBody.add(new MoveNode(x, y, Direction.DOWN));

        dragonBody.get(0).direction = Direction.DOWN;

        dragonBody.add(new MoveNode(x, y-dragonTailImgHeight, Direction.NONE));
        segments = 2;
    }

    public void AddSegment()
    {
        int tail = dragonBody.size()-1;
        MoveNode temp = dragonBody.get(tail);

        switch(temp.direction)
        {
            case UP:
                temp.y = temp.y + dragonTailImgHeight;
                break;
            case DOWN:
                temp.y = temp.y - dragonTailImgHeight;
                break;
            case LEFT:
                temp.x = temp.x + dragonTailImgWidth;
                break;
            case RIGHT:
                temp.x = temp.x - dragonTailImgWidth;
                break;
            default: //case NONE
                //player hasn't started moving yet, default to down
                temp.y = temp.y - dragonTailImgHeight;
        }

        dragonBody.add(new MoveNode(temp.x, temp.y, temp.direction));
        segments++;
    }

    public void RemoveSegment()
    {
        int tail = dragonBody.size()-1;
        if(tail != 0)
        {
            dragonBody.remove(tail);
            segments--;
        }
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
            if(dragonBody.get(0).direction == Direction.DOWN)
            {
                audioInstance.PlaySound(Audio.SituationForSound.MOVE_ERROR);
            }
            else
            {
                speedY = -speed;
                speedX = 0;
                rocketFacing = Direction.UP;                
            }
        }
        // Calculating speed for moving up or down.
        else if(Canvas.keyboardKeyState(KeyEvent.VK_S) ||Canvas.keyboardKeyState(KeyEvent.VK_DOWN))
        {
            if(dragonBody.get(0).direction == Direction.UP)
            {
                audioInstance.PlaySound(Audio.SituationForSound.MOVE_ERROR);
            }
            else
            {
                 speedY = speed;
                 speedX = 0;
                 rocketFacing = Direction.DOWN;
            }
         }           
        // Calculating speed for moving or stopping to the left.
        else if(Canvas.keyboardKeyState(KeyEvent.VK_A)||Canvas.keyboardKeyState(KeyEvent.VK_LEFT))
        {
            if(dragonBody.get(0).direction == Direction.RIGHT)
            {
                audioInstance.PlaySound(Audio.SituationForSound.MOVE_ERROR);
            }
            else
            {
                speedX = -speed;
                speedY = 0;
                rocketFacing = Direction.LEFT;
            }
        }
        // Calculating speed for moving or stopping to the right.
        else if(Canvas.keyboardKeyState(KeyEvent.VK_D)||Canvas.keyboardKeyState(KeyEvent.VK_RIGHT))
        {
            if(dragonBody.get(0).direction == Direction.LEFT)
            {
                audioInstance.PlaySound(Audio.SituationForSound.MOVE_ERROR);
            }
            else
            {
                speedX = speed;
                speedY = 0;
                rocketFacing = Direction.RIGHT;
            }
        }

        x += speedX;
        y += speedY;

        //Move the tail:
        MoveNode temp;
        for(int i = dragonBody.size() - 1; i > 0; i--)
        {
            temp = dragonBody.get(i-1);
            dragonBody.get(i).x = temp.x;
            dragonBody.get(i).y = temp.y;
            dragonBody.get(i).direction = temp.direction;
        }

        // Moves the head
        dragonBody.get(0).x = x;
        dragonBody.get(0).y = y;
        dragonBody.get(0).direction = rocketFacing;
         
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
        g2d.drawString("# segments: " + segments, 25, 25);
        for(int i = 0; i < dragonBody.size(); i++)
        {
            g2d.drawString(i + " at " + dragonBody.get(i).x + ", " + 
                dragonBody.get(i).y, 25, 50+25*i);
        }
        
        // If the rocket is landed.
        if(landed)
        {
            g2d.drawImage(rocketLandedImg, dragonBody.get(0).x, dragonBody.get(0).y, null);
        }
        // If the rocket is crashed.
        else if(crashed)
        {
            g2d.drawImage(rocketCrashedImg, dragonBody.get(0).x, dragonBody.get(0).y + rocketImgHeight - rocketCrashedImg.getHeight(), null);
        }
        // If the rocket is still in the space.
        else
        {
            switch(dragonBody.get(0).direction)
            {
                case UP:
                    rocketPlaceholder = rocketImg;
                    break;
                case DOWN:
                    rocketPlaceholder = rocketImgDown;
                    break;
                case LEFT:
                    rocketPlaceholder = rocketImgLeft;
                    break;
                case RIGHT:
                    rocketPlaceholder = rocketImgRight;
                    break;
                default: //case NONE
                    //player hasn't started moving yet
                    rocketPlaceholder = rocketImg;
            }

            MoveNode temp = dragonBody.get(dragonBody.size()-1);
            switch(temp.direction)
            {
                case UP:
                    rocketFirePlaceholder = rocketFireImg;
                    x_fire = temp.x + (dragonTailImgWidth - rocketFireImgWidth)/2;
                    y_fire = temp.y + dragonTailImgHeight - rocketFireImgHeight/overlapFractionInverted;
                    break;
                case DOWN:
                    rocketFirePlaceholder = rocketFireImgDown;
                    x_fire = temp.x + (dragonTailImgWidth - rocketFireImgWidth)/2;
                    y_fire = temp.y - dragonTailImgHeight + rocketFireImgHeight/overlapFractionInverted;
                    break;
                case LEFT:
                    rocketFirePlaceholder = rocketFireImgLeft;
                    x_fire = temp.x + dragonTailImgHeight - rocketFireImgHeight/overlapFractionInverted;
                    y_fire = temp.y + (dragonTailImgWidth - rocketFireImgWidth)/2;
                    break;
                case RIGHT:
                    rocketFirePlaceholder = rocketFireImgRight;
                    x_fire = temp.x - dragonTailImgHeight + rocketFireImgHeight/overlapFractionInverted;
                    y_fire = temp.y + (dragonTailImgWidth - rocketFireImgWidth)/2;
                    break;
                default: //case NONE
                    //player hasn't started moving yet
                    //TODO: prevent this from even happening
                    rocketFirePlaceholder = rocketFireImgDown;
                    x_fire = temp.x + (dragonTailImgWidth - rocketFireImgWidth)/2;
                    y_fire = temp.y - dragonTailImgHeight + rocketFireImgHeight/overlapFractionInverted;
            }

            //TODO: in for loop
            for(int i = 1; i < dragonBody.size(); i++)
                g2d.drawImage(dragonTailImg, dragonBody.get(i).x, dragonBody.get(i).y, null);

             // draw rocket fire 
            if(FireWasTriggered)
            {
                FireWasTriggered = false;
                if(ammoStored > 0)
                {
                    g2d.drawImage(rocketFirePlaceholder, x_fire, y_fire, null);
                    Projectile.s_Projectiles.add( new Projectile(x_fire, y_fire, 
                        OppositeDirection(dragonBody.get(dragonBody.size()-1).direction), rocketFirePlaceholder) );
                    audioInstance.PlaySound(Audio.SituationForSound.SHAT_ENEMY);
                    ammoStored--;
                }
                else
                { 
                    audioInstance.PlaySound(Audio.SituationForSound.OUT_OF_AMMO);
                }
            }


            g2d.drawImage(rocketPlaceholder, dragonBody.get(0).x, dragonBody.get(0).y, null);
        }
    }

    private Direction OppositeDirection(Direction direction)
    {
        switch(direction)
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

