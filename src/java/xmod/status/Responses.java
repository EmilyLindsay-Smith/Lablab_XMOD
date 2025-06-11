package xmod.status;

/**
 * Defining Labels for the Reporter.
 */
public final class Responses {
    private Responses() {
        //restrict instantiation
    }

    public static final String WELCOME = "Welcome to XMOD";
    public static final String NO_FILE_SELECTED = "No file selected";
    public static final String FILE_LOAD_SUCCESS = "Success loading file: ";
    public static final String FILE_LOAD_FAILURE = "Failure loading file: ";

    public static final String WAV_UNAVAILABLE = "No valid wav file available"
                    + "<br/><br/> XMOD expects the experiment audio file to:"
                    + "<ul> <li> have the same name as the .tms file</li>"
                    + "<li> be in the same directory as the .tms file</li>"
                    + "<li> have a .wav filename </li></ul>";
    public static final String EXPERIMENT_READY = "Experiment Ready";
    public static final String EXPERIMENT_NOT_READY = "Experiment not ready";
    public static final String EXPERIMENT_ABORTED = "Experiment aborted";
    public static final String EXPERIMENT_COMPLETE = "Experiment run success";

    public static final String SERIAL_UNCONNECTED = "Not connected";
    public static final String SERIAL_CONNECTED = "Connected to";
    public static final String SERIAL_INFO_UNAVAILABLE = "Cannot provide "
                                                    + "controller info";
    public static final String MONITORS_OFF = "Turning monitors off...";
    public static final String MONITORS_ON = "Turning monitors on...";
    public static final String MONITORS_UNAVAILABLE = "Cannot control monitors";

}
