package xmod.constants;

/**
 * Constants defining behaviour and label of the buttons in the main GUI.
 */
public final class Operations {
    private Operations() {
        //restrict instantiation
    }

    /** Load TMS file to control the experiment. */
    public static final String LOAD_TMS = "LOAD TMS FILE";
    /** Run the experiment. */
    public static final String RUN_EXP = "RUN EXPERIMENT";
    /** Turn monitors on via the controller box. */
    public static final String MONITOR_ON = "TURN MONITORS ON";
    /** Turn monitors on via the controller box. */
    public static final String MONITOR_OFF = "TURN MONITORS OFF";
    /** Get the controller box to flash LEDs or reconnect. */
    public static final String CHECK_CONNECTION = "CHECK CONNECTION";
    /** Retrieve information about the controller box. */
    public static final String CONTROLLER_INFO = "CONTROLLER INFO";
    /** Change the font for the experiment . */
    public static final String CHECK_FONT = "CHANGE FONT";
    /** Close the Xmod application. */
    public static final String CLOSE_XMOD = "CLOSE XMOD";
     /** Run the sample experiment. */
    public static final String TEST = "RUN TEST EXPERIMENT";
     /** Run the sample experiment. */
    public static final String TEST_AUDIO = "PLAY TEST AUDIO";

}
