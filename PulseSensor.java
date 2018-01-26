/**
 * A class to represent actual pulse sensors in the physical world. Objects
 * determine if thier physical sensor counterpart has been pressed. However,
 * as Raspberry Pi has only digital input, an analog/ditigal converter must be
 * used to get the data, and this class does not implement that. It uses an
 * <code>MCP3208</code> object for A/D conversion.
 * 
 * @see MCP3208
 * @author Matthew Flynn
 * @version January 25th, 2018
 */
public class PulseSensor{
    
    /**
     * The pitch that should be played when this sensor is pressed
     */
    public final Pitch pitch;
    
    // The physical MCP3208 that the physical pulse sensor is connected to
    private final MCP3208 adc;
    // The channel on the MCP3208 that the physical sensor is connected to
    private final int channel;

    // For a signal to be recogznied as a press, it must go below THRESHOLD. 
    // Another press will not be recognized until the signal then goes back 
    // into the range DOWN_NORMAL to UP_NORMAL
    private static final int THRESHOLD = 900;
    private static final int UP_NORMAL = 2100;
    private static final int DOWN_NORMAL = 1900;
    private boolean isPressed;
    
    /**
     * Constructs a new <code>PulseSensor</code> Object
     * 
     * @param adc the <code>MCP3208</code> that the sensor is connected to
     * @param channel the channel on the physical MCP3208 that this sensor is 
     * connected to
     * @param pitch the pitch that should be played when the physical sensor is
     * pressed
     * 
     * @see MCP3208
     */
    public PulseSensor(MCP3208 adc, int channel, Pitch pitch) {
        this.adc = adc;
        this.channel = channel;
        this.pitch = pitch;
        isPressed = false;
    }
    
    /**
     * Takes a sample of the physical pulse sensor, and determines if it's been
     * pressed
     * 
     * @return whether or not the pulse sensor has been pressed
     */
    public boolean readAdc() {
        short result = adc.read(channel);
        if (isPressed) {
            if (DOWN_NORMAL < result && result < UP_NORMAL) {
                isPressed = false;
            }
            return false;
        }
        if (result < THRESHOLD) {
            isPressed = true;
            return true;
        } else {
            return false;
        }
    }
    
    // for testing only! Handling the data itself outside of this class would 
    // be bad programming practice!
    /*
    public short data() {
        return adc.read(channel);
    }*/
    
    // for testing: displays heart rate on a graph
    /*
    public static void testHeartRate() {
        // fill the array with zeroes
        short[] readings = new short[512];
        for (int i = 0;i < readings.length;i++) {
            readings[i] = 0;
        }
        ReadingGraph graph = new ReadingGraph(readings);
        graph.pack();
        graph.setVisible(true);
        int count = 0; // so that every 1000 samples the graph can be updated
        while (true) {
            for (int i = readings.length - 1 ; i > 0 ; i--) {
                readings[i] = readings[i - 1];
            }
            readings[0] = new MCP3208(21, 22, 23, 24).read(0);
            if (count % 100 == 0) {
                graph.dispose();
                graph = new ReadingGraph(readings);
                graph.pack();
                graph.setVisible(true);
            }
            count++;
        }
    }*/
    
    // may be used in later iterations of this program
    /*/**
     * Performs a Fourier Transformation on the data given
     * 
     * @param data the data to do the transformation on (must have a length
     * which is an integer ^ 2)
     * @param freq the sampling frequency of the data
     * @return an array of x-y pairs (which are 2 element arrays) mapping
     * frequency to amplitude
     */
    /*private static double[][] fourierTrans(double[] input, int freq) {
        // perform the FFT
        FFT instance = new FFT(input.length);
        double[] real = Arrays.copyOf(input, input.length);
        double[] imag = new double[input.length];
        instance.fft(real, imag);
        // translate the fft values into amplitudes
        double[] magnitudes = new double[input.length];
        for (int i = 0;i < magnitudes.length;i++) {
            magnitudes[i] = Math.sqrt((real[i]*real[i]) + (imag[i]*imag[i]));
        }
        // assign each amplitude returned an x-value based on the sampling rate
        double resolution = (double)freq / input.length;
        double[][] ret = new double[magnitudes.length][2];
        for (int i = 0;i < ret.length;i++) {
            ret[i][0] = i * resolution;
            ret[i][1] = magnitudes[i];
        }
        // return the result
        return ret;
    }*/
    
 }
