package xmod.status;

/*
public final class ReportLabel{
    private ReportLabel(){
        //restrict instantiation
    }
*/
    public enum ReportLabel {
        STATUS ("Status"),
        TMS ("TMS"),
        AUDIO ("AUDIO"),
        CONNECTION ("CONNECTION"),
        FONT ("FONT"),
        MONITORS ("MONITORS");
        
        private final String value;

        ReportLabel(String value) {
            this.value = value;
        }

        public String getValue() {
            return this.value;
        }
        };



//}
