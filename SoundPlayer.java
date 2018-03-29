import javax.sound.sampled.Clip;

import com.pi4j.io.gpio.GpioPinDigitalOutput;

/**
 * The class that handles the playing of sound files. It polls some given 
 * <code>PulseSensor</code> objects, and if any of them detect that the user's
 * skin has been pressed, it plays the pitch associated with the sensor.
 * 
 * @author Matthew Flynn
 * @version January 25th, 2018
 */
public class SoundPlayer {
    
    // the pulse sensors that can alert this SoundPlayer to play sound
    private final PulseSensor[] sensors;
    
    /** 
     * Creates a new <code>SoundPlayer</code> Object that will poll and play the
     * sounds of the given <code>PulseSensor</code> Objects.
     * 
     * @param sensors the sensors for this <code>SoundPlayer</code> Object to
     * poll
     */
    public SoundPlayer(PulseSensor... sensors) {
        this.sensors = sensors;
    }
    
    /**
     * Begin polling all of the pulse sensors to see if they've been pressed.
     * This method loops forever if it's not stopped externally (which is fine)
     */
    public void start() {
        int count = 0;
        while (true) {
            for (PulseSensor sensor : sensors) {
                if (sensor.readAdc()) {
                    playNote(sensor.pitch, Instrument.PIANO);
                }
            }// for loop
        }// while (true) loop
    }

    //===========================STATIC METHODS=================================
    
    /**
     * plays a pitch on the specified instrument
     * 
     * @param p the pitch to be played
     * @param instrument the instrument for the pitch to be played on
     * 
     * @see Pitch
     * @see Instrument
     */
    public static void playNote(Pitch p, Instrument instrument) {
        Clip note = instrument.getSound(p);
        // if the note hasn't been played yet, this doesn't do anything
        // if it has, then it's necessary
        note.stop();
        note.setFramePosition(0);
        // play the note
        note.start();
    }
    
    /**
     * Creates a sequence of <code>PulseSensor</code> objects all attached
     * to the given MCP3208, in channels 0 ~ <code>num</code>, with pithces
     * ascending from C4
     * 
     * @param num the number of sensors (values above 8 will still only return
     * 8 sensors, since there's only 8 channels on the MCP3208)
     * @param adc the physical MCP3208 that the physical sensors are attached to
     * @return an array of <code>PulseSensor</code> objects attached to the
     * given MCP3208, with channel numbers each equal to thier index in the
     * array, and pitches rising diatonically from C4
     * 
     * @see MCP3208
     */
    public static PulseSensor[] sequentialSensors(int num, MCP3208 adc) {
        if (num > 8) {
            num = 8;
        }
        Pitch[] pitches = Pitch.CMajor();
        PulseSensor[] ret = new PulseSensor[num];
        for (int i = 0;i < num;i++) {
            ret[i] = new PulseSensor(adc, i, pitches[i]);
        }
        return ret;
    }
    
    /**
     * Main method. Excecution of the program begins here
     * 
     * @param args any command line arguments passed to the program (this
     * program requires no arguments)
     */
    public static void main(String[] args) {
        MCP3208 adc = new MCP3208(21, 22, 23, 24);
        SoundPlayer player = new SoundPlayer(sequentialSensors(4, adc));
        player.start();
    }
    
}
