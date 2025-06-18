package xmod.status;

/**
 * Enum to handle different labels for reporting status to user.
 */
public enum ReportLabel {
    /** Overall experiment status. */
    STATUS("STATUS"),
    /** TMS file to control experiment run. */
    TMS("TMS"),
    /** Audio file to play during experiment. */
    AUDIO("AUDIO"),
    /** Connection to control box via serial port. */
    CONNECTION("CONNECTION"),
    /** Font (changed). */
    FONT("FONT"),
    /** Monitors (On/Off). */
    MONITORS("MONITORS");

    /** String value of enum.
     */
    private final String value;

    ReportLabel(final String stringValue) {
        this.value = stringValue;
    }

    /** Returns string representation of the enum to show to the user.
     * @return value of enum
    */
    public String getValue() {
        return this.value;
    }
    };
