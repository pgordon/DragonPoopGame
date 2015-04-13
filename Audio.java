package DragonPoopGame;

import java.net.URL;
import java.io.IOException;

import javax.sound.sampled.*;

public class Audio{

	//Resource re: playing sound in java: http://www3.ntu.edu.sg/home/ehchua/programming/java/J8c_PlayingSound.html
    //and Clip in Oracle docs: http://docs.oracle.com/javase/7/docs/api/javax/sound/sampled/Clip.html
    private static Audio instance = null;

    public static Audio getInstance(){
        if(instance == null)
            instance = new Audio();
        return instance;
    }

	private boolean firstTimeThisGame = true;

    public enum SituationForSound{
        HIT_WALL,
        HIT_SELF,
        MOVE_ERROR,
        POWER_UP,
        ENEMY_HIT,
        SHAT_ENEMY,
        NONE
    }

    //TODO later, have background music (looping sound) enum

	private String hitWallFileName = "/DragonPoopGame/resources/audio/meep.wav";
    private Clip hitWallSound;

    private String shatEnemyFileName = "/DragonPoopGame/resources/audio/bloop.wav";
    private Clip shatEnemySound;

    private String moveErrorFileName = "/DragonPoopGame/resources/audio/blip.wav";
    private Clip moveErrorSound;

    private String powerUpFileName = "/DragonPoopGame/resources/audio/aah.wav";
    private Clip powerUpSound;

    public Audio(){
        hitWallSound = LoadSound(hitWallFileName);
        shatEnemySound = LoadSound(shatEnemyFileName);
        moveErrorSound = LoadSound(moveErrorFileName);
        powerUpSound = LoadSound(powerUpFileName);
    }

	//Load sound
	public Clip LoadSound(String soundName){
        Clip clip = null;
		try {
             // Open an audio input stream.
             URL url = this.getClass().getResource(soundName);
             AudioInputStream audioIn = AudioSystem.getAudioInputStream(url);
             // Get a sound clip resource.
             clip = AudioSystem.getClip();
             // Open audio clip and load samples from the audio input stream.
             clip.open(audioIn);
        } catch (UnsupportedAudioFileException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }
        return clip;
	}

	public void GameRestarted()
	{
		firstTimeThisGame = true;
	}


    public void PlaySoundOnce(SituationForSound sit)
    {
        switch(sit)
        {
            case HIT_WALL:
                PlaySoundOnce(hitWallSound);
                break;
            default:
                //no play    
        }
    }

    public void PlaySound(SituationForSound sit)
    {
        switch(sit)
        {
            case HIT_WALL:
                PlaySound(hitWallSound);
                break;
            case SHAT_ENEMY:
                PlaySound(shatEnemySound);
                break;           
            case MOVE_ERROR:
                PlaySound(moveErrorSound);
                break;
            case POWER_UP:
                PlaySound(powerUpSound);
                break;      
            default:
                //no play    
        }
    }

    private void PlaySoundOnce(Clip clip)
    {
        if(!firstTimeThisGame)
            return;
        
        firstTimeThisGame = false;
        
        clip.setFramePosition(0);
        clip.start();        
    }

    //TODO: add a check to see if it is playing and if so, don't?
    private void PlaySound(Clip clip)
    {
        clip.setFramePosition(0);
        clip.start();
    }
}