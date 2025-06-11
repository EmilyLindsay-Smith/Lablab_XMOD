package xmod.status;
/**
 * Enum to handle different labels for reporting status to user.
 */
public enum ReportLabel {
    STATUS("Status"),
    TMS("TMS"),
    AUDIO("AUDIO"),
    CONNECTION("CONNECTION"),
    FONT("FONT"),
    MONITORS("MONITORS");

    /** String value of enum */
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
