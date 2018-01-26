import java.io.File;
import java.io.IOException;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * An enum representing all the different instruments that can be played in
 * this project. Each instrument handles the loading and storage of its own
 * sound files. To add a new instrument, all you must do is add a new instance
 * of the enum, and specify the folder in which the sound files are located
 * 
 * @author Matthew Flynn
 * @version January 25th, 2018
 */
public enum Instrument {
    
    PIANO("piano"), BASS("bass");
    
    /**
     * The name of the instrument that this instance plays. Also the name of the
     * foler which contains the audio samples for this instrument
     */
    public final String instrumentName;
    // privately stores the sound files in a playable format
    private final Clip[] SOUNDS = new Clip[Pitch.values().length];
    /**
     * The absolute path to the folder which contains the audio files
     */
    public static final String ALL_SAMPLES ="/home/pi/Documents/flynn/SoundsWithPulse/samples";
    
    /**
     * Gets the audio file for the pitch given as a <code>Clip</code> object.
     * This method's speed may vary. If the audio file requested is already
     * loaded, then it will be fast.  If the audio file needs to be loaded, it
     * may take a bit more time, but then that audio file will be saved by the
     * program, so that any subsequent calls for that note will be fast.
     * 
     * @param p The pitch of the audio file to be returned
     * @return A <code>Clip</code> of a note of the given pitch
     */
    public Clip getSound(Pitch p) {
        // check if the sound file has already been loaded.  If so, return it
        if (SOUNDS[p.ordinal()] != null) {
            return SOUNDS[p.ordinal()];
        }
        Clip ret = loadSoundFile(new File(filenameOf(p)));
        SOUNDS[p.ordinal()] = ret;
        return ret;
    }                       
    
    // Overriding this method allows the returned String to be a little more
    // user friendly
    @Override
    public String toString() {
        return instrumentName;
    }
    
    // creates the filename of a sound file given its pitch
    private String filenameOf(Pitch p) {
        return ALL_SAMPLES +"/"+ instrumentName +"/"+ p.toString() + ".wav";
    }
    
    // private constructor
    private Instrument(String folderName) {
        instrumentName = folderName;
    }
    
    // loads the specified sound file and returns it as a Clip object
    private static Clip loadSoundFile(File soundFile) {
        AudioInputStream inStream = null;
        Clip clip;
        // get the input stream from the given file
        try {
            inStream = AudioSystem.getAudioInputStream(soundFile);
        } catch (IOException | UnsupportedAudioFileException e) {
            return null;
        }
        // get a line for the output of the audio
        try {
            clip = AudioSystem.getClip();
        } catch (LineUnavailableException e) {
            return null;
        }
        // open the clip, and give it the audio to play
        try {
            clip.open(inStream);
        } catch (IOException | LineUnavailableException e) {
            return null;
        }
        // close the InputStream
        try {
            inStream.close();
        } catch (IOException e) {
            return null;
        }
        return clip;
    }
    
}
