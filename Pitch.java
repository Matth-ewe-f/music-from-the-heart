/**
 * Enum type to represent different pitches (in scientific pitch notation).
 * Because '#' is not permitted in Enum names, a lowercase 's', for sharp,
 * is used in it's place.  Currently, only the pitches from C4 to C5 
 * inclusive are represented. That's all this project needs right now.
 * 
 * @author Matthew Flynn
 * @version January 25th, 2018
 */
public enum Pitch {
    C4(0x3C), Cs4(0x3D), D4(0x3E), Ds4(0x3F), E4(0x40), F4(0x41), Fs4(0x42),
    G4(0x43), Gs4(0x44), A4(0x45), As4(0x46), B4(0x47), C5(0x48);
    
    /** 
     * how the pitch name will appear in the path to any audio files.
     */
    public final String filename;
    
    /**
     * the MIDI encoding of the pitch
     */
    public final byte MIDICode;
    
    @Override
    public String toString(){
        return filename;
    }
    
    // private constructor
    private Pitch(int midi){
        MIDICode = (byte)midi;
        String temp = this.name();
        temp = temp.replace("s", "#");
        filename = temp;
    }
    
    /**
     * Returns all the pitches in the C major scale (all the white
     * keys on a piano) from C4 to C5.
     * 
     * @returns     An array containing the C major scale
     */
    public static Pitch[] CMajor() {
        return new Pitch[]{C4, D4, E4, F4, G4, A4, B4, C5};
    }
    
}

