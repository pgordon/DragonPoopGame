package DragonPoopGame;

import java.net.URL;
import java.io.IOException;

import javax.sound.sampled.*;

public class Audio{

	//Resource re: playing sound in java: http://www3.ntu.edu.sg/home/ehchua/programming/java/J8c_PlayingSound.html
    //and Clip in Oracle docs: http://docs.oracle.com/javase/7/docs/api/javax/sound/sampled/Clip.html

	private boolean firstTimeThisGame = true;
	private Clip clip;

	//Load sound
	public Audio(String soundName){
		try {
             // Open an audio input stream.
             //File soundFile = new File("/DragonPoopGame/resources/audio/meep.wav");
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
	}

	public void GameRestarted()
	{
		firstTimeThisGame = true;
	}

    public void PlaySoundOnce()
    {
        if(!firstTimeThisGame)
            return;
        
        firstTimeThisGame = false;
        
        clip.setFramePosition(0);
        clip.start();        
    }
    

}