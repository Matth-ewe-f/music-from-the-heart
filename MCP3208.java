import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPin;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import java.util.Collection;

/**
 * A class to represent a physical MCP3208 connected to a Raspberry Pi. The
 * MCP3208 is an A/D converter with 8 channels that utilizes SPI to communicate
 * with microcontrollers. The SPI is implemented in this class privately, any
 * clients of this class do not have to deal with it directly.
 * 
 * @author Matthew Flynn
 * @version January 24th, 2018
 */
public class MCP3208 {
    
    // the pins for the SPI
    private final GpioPinDigitalOutput chipSelectPin;
    private final GpioPinDigitalOutput clockPin;
    private final GpioPinDigitalOutput MOSIPin;
    private final GpioPinDigitalInput MISOPin;
    
    // maps integers to pins
    private static final Pin[] pinMap = new Pin[] {RaspiPin.GPIO_00,
        RaspiPin.GPIO_01, RaspiPin.GPIO_02, RaspiPin.GPIO_03, RaspiPin.GPIO_04,
        RaspiPin.GPIO_05, RaspiPin.GPIO_06, RaspiPin.GPIO_07, RaspiPin.GPIO_08,
        RaspiPin.GPIO_09, RaspiPin.GPIO_10, RaspiPin.GPIO_11, RaspiPin.GPIO_12,
        RaspiPin.GPIO_13, RaspiPin.GPIO_14, RaspiPin.GPIO_15, RaspiPin.GPIO_16,
        RaspiPin.GPIO_17, RaspiPin.GPIO_18, RaspiPin.GPIO_19, RaspiPin.GPIO_20,
        RaspiPin.GPIO_21, RaspiPin.GPIO_22, RaspiPin.GPIO_23, RaspiPin.GPIO_24,
        RaspiPin.GPIO_25, RaspiPin.GPIO_26, RaspiPin.GPIO_27, RaspiPin.GPIO_28,
        RaspiPin.GPIO_29, RaspiPin.GPIO_30, RaspiPin.GPIO_31};
    
    /**
     * Constructs a MCP3208 object attached to the specified pins on the
     * Raspberry Pi
     * 
     * @param cs The pin that the Chip Select is connected to
     * @param clk The pin that the Clock is connected to
     * @param dIn The pin that the Digital In is connected to
     * @param dOut The pin that the Digital Out is connected to
     */
    public MCP3208(int cs, int clk, int dOut, int dIn) {
        chipSelectPin = getAndProvisionOutputPin(cs);
        // beign the CS pin HIGH. LOW means that it should start converting
        chipSelectPin.high();
        clockPin = getAndProvisionOutputPin(clk);
        MOSIPin = getAndProvisionOutputPin(dIn);
        MISOPin = getAndProvisionInputPin(dOut);
    }
    
    /**
     * Tells the physical MCP3208 to collect a reading of analog data and
     * convert it into digital data
     * 
     * @param channel the channel to get the analog input from (0 ~ 7).
     * @return the result of the analog to digital conversion (12-bit data)
     */
    public short read(int channel) {
        if (channel > 7 || channel < 0) {
            throw new IllegalArgumentException("The requested channel (channel " + channel + ") does not exist.");
        }
        // WRITE START BIT
        // the first HIGH after CS LOW is interpreted as the start bit
        chipSelectPin.low();
        MOSIPin.high();
        clockCycle();
        // WRITE INPUT CONFIG (4 bits)
        byte command = 0b00001000;
        command |= channel;
        for (int i = 0;i < 4;i++) {
            if ((command & 0b00001000) != 0) {
                MOSIPin.high();
            } else {
                MOSIPin.low();
            }
            clockCycle();
            command <<= 1;
        }
        // RECIEVE CONVERSTION DATA (12 bits + 2 null bits)
        clockCycle();
        short result = 0;
        for (int i = 0;i < 12;i++) {
            clockCycle();
            if (MISOPin.isHigh()) {
                result |= 1;
            }
            result <<= 1;
        }
        result >>= 1;
        chipSelectPin.high();
        return result;
    }
    
    // to send a bit, set a pin to the correct state, then call clockCycle()
    // To recieve a bit, call clockCycle() and then check the state of a pin
    private void clockCycle() {
        // the MCP3208 needs at least 250ns between CLK changes. 100 iterations
        // is plenty for this
        for (int i = 0;i < 100;i++) {}
        clockPin.high();
        for (int i = 0;i < 100;i++) {}
        clockPin.low();
    }
    
    /**
     * Configures a GPIO pin for input.
     * 
     * @param num the number of the pin to configure (wiringPi numbering)
     * @return a GPIO pin that can be used for input
     */
    public static GpioPinDigitalInput getAndProvisionInputPin(int num) {
        GpioController controller = GpioFactory.getInstance();
        // check to see if the desired pin is already provisioned
        Collection<GpioPin> existingPins = controller.getProvisionedPins();
        Pin desiredPin = pinMap[num];
        GpioPinDigitalInput ret = null;
        for (GpioPin i : existingPins) {
            // if the pin has been found to already be provisioned, use it
            if (i.getPin() == desiredPin) {
                ret = (GpioPinDigitalInput)i;
            }
        }
        // if the pin hasn't yet been provisioned, then provision it
        if (ret == null) {
            ret = controller.provisionDigitalInputPin(pinMap[num], 
                PinPullResistance.PULL_DOWN);
        }
        return ret;
    }
    
    /**
     * Configures a GPIO pin for output. the default state is LOW
     * 
     * @param num the number of the pin to configure (wiringPi numbering)
     * @return a GPIO pin that can be used for output
     */
    public static GpioPinDigitalOutput getAndProvisionOutputPin(int num) {
        GpioController controller = GpioFactory.getInstance();
        return controller.provisionDigitalOutputPin(pinMap[num], PinState.LOW);
    }
    
}