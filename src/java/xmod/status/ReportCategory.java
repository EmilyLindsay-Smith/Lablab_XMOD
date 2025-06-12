package xmod.status;
/**Categories used in ObjectReport. */
public enum ReportCategory {
    /** Status of the category. */
    STATUS("Status"),
    /** Message to give to user. */
    MESSAGE("Message"),
    /** Advice to fix the problem. */
    ADVICE("Advice"),
    /** Stacktrace of any exceptions raised. */
    STACKTRACE("Stacktrace");

    /** String representation of the enum. */
    private final String value;

    ReportCategory(final String stringValue) {
        this.value = stringValue;
    }

    /**
     * Returns the string valuation of the enum.
     * @return this.value - e.g. "Status" for STATUS
     */
    public String getValue() {
        return this.value;
    }
};
