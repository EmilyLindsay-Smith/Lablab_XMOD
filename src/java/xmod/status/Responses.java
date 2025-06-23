package xmod.status;

/**
 * Defining Labels for the Reporter.
 */
public final class Responses {
    private Responses() {
        //restrict instantiation
    }
    /** Welcome message. */
    public static final String WELCOME = "Welcome to XMOD";
    /** No TMS file selected by user . */
    public static final String NO_FILE_SELECTED = "No file selected";
    /** Successfully loaded file (wav/tms). */
    public static final String FILE_LOAD_SUCCESS = "Success loading file: ";
    /** Could not load the file (wav/tms). */
    public static final String FILE_LOAD_FAILURE = "Failure loading file: ";
    /**WAV file not automatically found, here are instructions. */
    public static final String WAV_UNAVAILABLE = "No valid wav file available"
                    + "<br/><br/> XMOD expects the experiment audio file to:"
                    + "<ul> <li> have the same name as the .tms file</li>"
                    + "<li> be in the same directory as the .tms file</li>"
                    + "<li> have a .wav filename </li></ul>";

    /**Results directory could not be created. */
    public static final String ERROR_RESULTS_DIRECTORY = "Results directory "
                    + "could not be created";
    /** Experiment fully set up and ready to run. */
    public static final String EXPERIMENT_READY = "Experiment Ready";
    /** Experiment not fully set up. */
    public static final String EXPERIMENT_NOT_READY = "Experiment not ready";
    /** Experiment aborted (by user pressing esc). */
    public static final String EXPERIMENT_ABORTED = "Experiment aborted";
    /** Experiment completed successfully. */
    public static final String EXPERIMENT_COMPLETE = "Experiment run success";
    /** No connection established to control box via serial port. */
    public static final String SERIAL_UNCONNECTED = "Not connected";
    /** Disconnection to control box via serial port. */
    public static final String SERIAL_DISCONNECTED = "Not connected";
    /** Successful connection to control box via serial port. */
    public static final String SERIAL_CONNECTED = "Connected to controller box";
    /** Error retrieving information from the control box. */
    public static final String SERIAL_INFO_UNAVAILABLE = "Cannot provide "
                                                    + "controller info";
    /** Trying to turn the monitors on via the control box. */
    public static final String MONITORS_OFF = "Turning monitors off...";
    /** Trying to turn the monitors on via the control box. */
    public static final String MONITORS_ON = "Turning monitors on...";
    /** Cannot contact/control the monitors via the control box. */
    public static final String MONITORS_UNAVAILABLE = "Cannot control monitors";
    /** Problem with AudioPLayer. */
    public static final String AUDIO_ERROR = "Audio Error";

}
