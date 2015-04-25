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

    /*TODO later, have more background music for diff levels or moods; also the option to not have any
    public enum BackgroundSound{
        BRIGHT_MIDI_LOOP
    }*/

	private String hitWallFileName = "/DragonPoopGame/resources/audio/meep.wav";
    private Clip hitWallSound;

    private String shatEnemyFileName = "/DragonPoopGame/resources/audio/bloop.wav";
    private Clip shatEnemySound;

    private String moveErrorFileName = "/DragonPoopGame/resources/audio/blip.wav";
    private Clip moveErrorSound;

    private String powerUpFileName = "/DragonPoopGame/resources/audio/aah.wav";
    private Clip powerUpSound;

    private String brightMidiLoopFileName = "/DragonPoopGame/resources/audio/heyMoonrise.wav";
    private Clip brightMidiBG;

    private int loopPosition = 0;

    public Audio(){
        hitWallSound = LoadSound(hitWallFileName);
        shatEnemySound = LoadSound(shatEnemyFileName);
        moveErrorSound = LoadSound(moveErrorFileName);
        powerUpSound = LoadSound(powerUpFileName);

        brightMidiBG = LoadSound(brightMidiLoopFileName);
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
        PlaySoundOnce(GetClip(sit));
    }

    public void PlaySound(SituationForSound sit)
    {
        PlaySound(GetClip(sit));
    }

    private Clip GetClip(SituationForSound sit)
    {
        switch(sit)
        {
            case HIT_WALL:
                return hitWallSound;
            case SHAT_ENEMY:
                return shatEnemySound;
            case MOVE_ERROR:
                return moveErrorSound;
            case POWER_UP:
                return powerUpSound;
            default:
                //no clip  
                System.err.println("No audio clip found for " + sit.toString());
                return null;
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

    private void PlaySound(Clip clip)
    {
        if(!clip.isRunning())
        {
            clip.setFramePosition(0);
            clip.start();
        }
    }

    //There's just the one for now
    public void PlayBGLoop()
    {
        PlayLoopingSound(brightMidiBG);
    }

    private void PlayLoopingSound(Clip clip)
    {
        clip.setFramePosition(loopPosition);
        clip.loop(Clip.LOOP_CONTINUOUSLY);
    }

    private void StopSound(Clip clip)
    {
        if(clip.isRunning())
            clip.stop();
    }

    public void StopBGLoop()
    {
        if(brightMidiBG.isRunning())
        {
            loopPosition = brightMidiBG.getFramePosition();
            brightMidiBG.stop();
        }
    }
}