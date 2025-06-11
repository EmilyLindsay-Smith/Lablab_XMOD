package xmod.status;

public enum ReportCategory {
    /**Categories used in ObjectReport */
    STATUS("Status"),
    MESSAGE("Message"),
    ADVICE("Advice"),
    STACKTRACE("Stacktrace");

    private final String value;

    ReportCategory(final String stringValue) {
        this.value = stringValue;
    }

    public String getValue() {
        return this.value;
    }
};
