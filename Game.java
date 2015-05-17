package DragonPoopGame;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.LinkedList;
import javax.imageio.ImageIO;

/**
 * Actual game.
 * 
 * @author www.gametutorial.net
 */

public class Game {

    /**
     * The space rocket with which player will have to land.
     */
    private PlayerRocket playerRocket;
    
    /**
     * Landing area on which rocket will have to land.
     */
    private LandingArea landingArea;

    private List <PowerUp> powerUps;
    private Iterator <PowerUp> powerUpIterator;
    private Iterator <Enemy> enemyIterator;
    private int numPowerUps = 5;
    private int numEnemies = 3;
    
    private Iterator<Projectile> it;

    /**
     * Game background image.
     */
    private BufferedImage backgroundImg;
    
    /**
     * Red border of the frame. It is used when player crash the rocket.
     */
    private BufferedImage redBorderImg;

    /**
     * Sound object
     */
    private Audio audioInstance;
    
    private Font gameInstructionFont;
    private Font coordinateFont;

    public Game()
    {
        Framework.gameState = Framework.GameState.GAME_CONTENT_LOADING;
        
        Thread threadForInitGame = new Thread() {
            @Override
            public void run(){
                // Sets variables and objects for the game.
                Initialize();
                // Load game files (images, sounds, ...)
                LoadContent();
                
                Framework.gameState = Framework.GameState.PLAYING;
                audioInstance.PlayBGLoop();
            }
        };
        threadForInitGame.start();
    }
    
    
   /**
     * Set variables and objects for the game.
     */
    private void Initialize()
    {
        playerRocket = new PlayerRocket();
        landingArea  = new LandingArea();
        powerUps = new LinkedList<PowerUp>();
        PopulatePowerUps();
        InitEnemies();

        audioInstance = Audio.getInstance();

        gameInstructionFont = new Font("TimesRoman", Font.PLAIN, 12);
        coordinateFont = new Font(Font.SANS_SERIF, Font.PLAIN, 30);
    }
    
    private void PopulatePowerUps()
    {
        for(int i = 0; i < numPowerUps; i++)
        {
            powerUps.add(new PowerUp());
        }
    }

    private void InitEnemies()
    {
        for(int i = 0; i < numEnemies; i++)
        {
            new Enemy();
        }
    }

    /**
     * Load game files - images, sounds, ...
     */
    private void LoadContent()
    {
        try
        {
            URL backgroundImgUrl = this.getClass().getResource("/DragonPoopGame/resources/images/background.jpg");
            backgroundImg = ImageIO.read(backgroundImgUrl);
            
            URL redBorderImgUrl = this.getClass().getResource("/DragonPoopGame/resources/images/red_border.png");
            redBorderImg = ImageIO.read(redBorderImgUrl);
        }
        catch (IOException ex) {
            Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    /**
     * Restart game - reset some variables.
     */
    public void RestartGame()
    {
        playerRocket.ResetPlayer();
        powerUps.clear();
        PopulatePowerUps();
        Enemy.Exterminate();
        InitEnemies();
        audioInstance.GameRestarted();
    }
    
    Projectile temp;
    Enemy tempEnemy;
    /**
     * Update game logic.
     * 
     * @param gameTime gameTime of the game.
     * @param mousePosition current mouse position.
     */
    public void UpdateGame(long gameTime, Point mousePosition)
    {
        // Move the projectiles
        it = Projectile.s_Projectiles.iterator();
        while(it.hasNext())
        {
            temp = it.next();
            if(temp.InFrame())
                temp.Update();
            else
                it.remove();
        }

        // Move the rocket
        playerRocket.Update();

        // Enemy Update
        enemyIterator = Enemy.s_Enemies.iterator();
        while(enemyIterator.hasNext())
        {
            tempEnemy = enemyIterator.next();

            //TODO: check body segments too, for endgame trigger
            if(tempEnemy.isTouching(playerRocket.x, playerRocket.y, 
                playerRocket.rocketImgWidth, playerRocket.rocketImgHeight))
            {
                //TODO: little animation? 
                audioInstance.PlaySound(Audio.SituationForSound.POWER_UP);
                enemyIterator.remove();

                playerRocket.incrementAmmo();
            }
            else
            {
                tempEnemy.Update(playerRocket.x, playerRocket.y);
            }
        }        
        
        // Checks where the player rocket is. Is it still in the space or is it landed or crashed?
        //hitting the walls kills you; leaving in landing area behaviour to save or exit game
        if(playerRocket.x <= 0 || playerRocket.y <= 0 || 
            playerRocket.x + playerRocket.rocketImgWidth >= Framework.frameWidth || 
            playerRocket.y + playerRocket.rocketImgHeight >= Framework.frameHeight)
        {    
            playerRocket.crashed = true;
            Framework.gameState = Framework.GameState.GAMEOVER;
        }
        else if(playerRocket.y + playerRocket.rocketImgHeight - landingArea.landingAreaImgHeight > landingArea.y)
        {
            // Here we check if the rocket is over landing area.
            if((playerRocket.x > landingArea.x) && 
                (playerRocket.x < landingArea.x + landingArea.landingAreaImgWidth - playerRocket.rocketImgWidth))
            {
                playerRocket.landed = true;
                Framework.gameState = Framework.GameState.GAMEOVER;
            }
        }

        // Check to see if powerups were collected
        powerUpIterator = powerUps.iterator();
        while(powerUpIterator.hasNext())
        {
            if(powerUpIterator.next().isTouching(playerRocket.x, playerRocket.y, 
                playerRocket.rocketImgWidth, playerRocket.rocketImgHeight))
            {
                //TODO: little animation? 
                audioInstance.PlaySound(Audio.SituationForSound.POWER_UP);
                powerUpIterator.remove();

                playerRocket.incrementAmmo();
                playerRocket.AddSegment();
            }
        }

    }
    
    /**
     * Draw the game to the screen.
     * 
     * @param g2d Graphics2D
     * @param mousePosition current mouse position.
     */
    public void Draw(Graphics2D g2d, Point mousePosition)
    {
        g2d.drawImage(backgroundImg, 0, 0, Framework.frameWidth, Framework.frameHeight, null);
        
        //DEBUG
        g2d.setColor(Color.white);
        g2d.setFont(coordinateFont); 
        g2d.drawString("# enemies: " + Enemy.s_Enemies.size(), 25, 25);
        for(int i = 0; i < Enemy.s_Enemies.size(); i++)
        {
            g2d.drawString(i + " at " + Enemy.s_Enemies.get(i).x + ", " + 
                Enemy.s_Enemies.get(i).y, 25, 50+25*i);
        }

        landingArea.Draw(g2d);
        
        playerRocket.Draw(g2d);
        powerUpIterator = powerUps.iterator();
        while(powerUpIterator.hasNext())
        {
            powerUpIterator.next().Draw(g2d);
        }

        it = Projectile.s_Projectiles.iterator();
        while(it.hasNext())
        {
            it.next().Draw(g2d);
        }

        enemyIterator = Enemy.s_Enemies.iterator();
        while(enemyIterator.hasNext())
        {
            enemyIterator.next().Draw(g2d);
        }
    }
    
    /**
    * Clean up the previous game
    */
    public void GameOver()
    {
        Projectile.s_Projectiles.clear();

    }
    
    /**
     * Draw the game over screen.
     * 
     * @param g2d Graphics2D
     * @param mousePosition Current mouse position.
     * @param gameTime Game time in nanoseconds.
     */
    public void DrawGameOver(Graphics2D g2d, Point mousePosition, long gameTime)
    {
        Draw(g2d, mousePosition);
        g2d.setFont(gameInstructionFont);
        g2d.drawString("Press spacebar or Enter to restart.", Framework.frameWidth / 2 - 100, Framework.frameHeight / 3 + 70);
        
        if(playerRocket.landed)
        {
            g2d.drawString("You have successfully landed!", Framework.frameWidth / 2 - 100, Framework.frameHeight / 3);
            g2d.drawString("It took you " + gameTime / Framework.secInNanosec + " seconds.", Framework.frameWidth / 2 - 100, Framework.frameHeight / 3 + 20);
        }
        else
        {
            audioInstance.PlaySoundOnce(Audio.SituationForSound.HIT_WALL);

            g2d.setColor(Color.red);
            g2d.drawString("You hit a wall!", Framework.frameWidth / 2 - 95, Framework.frameHeight / 3);
            g2d.drawImage(redBorderImg, 0, 0, Framework.frameWidth, Framework.frameHeight, null);
        }
    }
}
