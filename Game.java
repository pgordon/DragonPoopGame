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
import javax.imageio.ImageIO;

//TODO: move all audio into own class and file
import javax.sound.sampled.*;

/**
 * Actual game.
 * 
 * @author www.gametutorial.net
 */

public class Game {

    //Resource re: playing sound in java: http://www3.ntu.edu.sg/home/ehchua/programming/java/J8c_PlayingSound.html
    //and Clip in Oracle docs: http://docs.oracle.com/javase/7/docs/api/javax/sound/sampled/Clip.html
    private void playSound(boolean play)
    {
        if(!play)
            return;
        try {
             // Open an audio input stream.
             //File soundFile = new File("/DragonPoopGame/resources/audio/meep.wav");
             URL url = this.getClass().getResource("/DragonPoopGame/resources/audio/meep.wav");
             AudioInputStream audioIn = AudioSystem.getAudioInputStream(url);
             // Get a sound clip resource.
             Clip clip = AudioSystem.getClip();
             // Open audio clip and load samples from the audio input stream.
             clip.open(audioIn);
             clip.start();
        } catch (UnsupportedAudioFileException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }
    }
    private void playSound(){ playSound(true); }
    private boolean firstTimeThisGame = true;


    /**
     * The space rocket with which player will have to land.
     */
    private PlayerRocket playerRocket;
    
    /**
     * Landing area on which rocket will have to land.
     */
    private LandingArea landingArea;
    
    /**
     * Game background image.
     */
    private BufferedImage backgroundImg;
    
    /**
     * Red border of the frame. It is used when player crash the rocket.
     */
    private BufferedImage redBorderImg;
    
    private Font gameInstructionFont;

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

        gameInstructionFont = new Font("TimesRoman", Font.PLAIN, 12);
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
        firstTimeThisGame = true;
    }
    
    
    /**
     * Update game logic.
     * 
     * @param gameTime gameTime of the game.
     * @param mousePosition current mouse position.
     */
    public void UpdateGame(long gameTime, Point mousePosition)
    {
        // Move the rocket
        playerRocket.Update();
        
        // Checks where the player rocket is. Is it still in the space or is it landed or crashed?
        //hitting the walls kills you; leaving in landing area behaviour to save or exit game
        if(playerRocket.x <= 0 || playerRocket.y <= 0 || 
            playerRocket.x + playerRocket.rocketImgWidth >= Framework.frameWidth || 
            playerRocket.y + playerRocket.rocketImgHeight >= Framework.frameHeight)
        {    
            playerRocket.crashed = true;
            Framework.gameState = Framework.GameState.GAMEOVER;
        }
        else if(playerRocket.y + playerRocket.rocketImgHeight - 10 > landingArea.y)
        {
            // Here we check if the rocket is over landing area.
            if((playerRocket.x > landingArea.x) && 
                (playerRocket.x < landingArea.x + landingArea.landingAreaImgWidth - playerRocket.rocketImgWidth))
            {
                playerRocket.landed = true;
                Framework.gameState = Framework.GameState.GAMEOVER;
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
        
        landingArea.Draw(g2d);
        
        playerRocket.Draw(g2d);
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
            playSound(firstTimeThisGame);
            firstTimeThisGame=false;

            g2d.setColor(Color.red);
            g2d.drawString("You hit a wall!", Framework.frameWidth / 2 - 95, Framework.frameHeight / 3);
            g2d.drawImage(redBorderImg, 0, 0, Framework.frameWidth, Framework.frameHeight, null);
        }
    }
}
