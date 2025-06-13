package xmod.constants;

public final class Actions {
    private Actions() {
        //restrict instantiation
    }
    /** Update Reporter with new information (including errors). */
    public static final String UPDATE = "UPDATE";
    /** Button Operation (options defined in constnats.Operations.java). */
    public static final String OPERATION = "OPERATION";
    /** Abort the experiment mid-run. */
    public static final String ABORT_EXPERIMENT = "ABORT_EXPERIMENT";
    /** Change the font. */
    public static final String UPDATE_FONT = "UPDATE_FONT";
    /** End the experiment. */
    public static final String FINISH_EXPERIMENT = "FINISH_EXPERIMENT";
    /** Close the Xmod application. */
    public static final String CLOSE_XMOD = "CLOSE XMOD";
}
