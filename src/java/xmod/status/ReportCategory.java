package xmod.status;
public enum ReportCategory {
    STATUS ("Status"),
    MESSAGE ("Message"),
    ADVICE ("Advice"),
    STACKTRACE ("Stacktrace");

    private final String value;

    ReportCategory(String value){
        this.value = value;
    }
    
    public String getValue(){
        return this.value;
    }
};
