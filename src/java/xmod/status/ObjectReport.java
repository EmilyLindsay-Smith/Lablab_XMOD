package xmod.status;

import java.util.*;

public class ObjectReport {
    public enum ReportCategory { STATUS , MESSAGE, ADVICE, STACKTRACE };
    public Map<ReportCategory, ArrayList<String>> report;
    private String name;
    /**
     * Constructor.
     * @param className name of category e.g. audio, tms, connection etc.
     */
    public ObjectReport(String className){
        this.name = className;
        int initialCapacity = 6; //Number of categories + 1/3 
        float loadFactor = (float) 0.75;
        // To prints keys in insertion order not recent access order 
        Boolean accessOrder = false; 

        this.report = new LinkedHashMap<ReportCategory, ArrayList<String>>(
            initialCapacity, loadFactor, accessOrder);      
        initialiseValues();
    }

    /**
     * Returns this.name.
     * Mostly useful for testing
     */
    public String getName() {
        return this.name;
    }
    /** 
     * Initialise report map values.
     */
    private void initialiseValues() {
        // Initialise arrays to hold values
        ArrayList<String> statusInitialValue = new ArrayList<String>();
        ArrayList<String> messageInitialValue = new ArrayList<String>();
        ArrayList<String> adviceInitialValue = new ArrayList<String>();
        ArrayList<String> stackTraceInitialValue = new ArrayList<String>();

        // Add to report map
        this.report.put(ReportCategory.STATUS, statusInitialValue); 
        this.report.put(ReportCategory.MESSAGE, messageInitialValue);
        this.report.put(ReportCategory.ADVICE, adviceInitialValue); 
        this.report.put(ReportCategory.STACKTRACE, stackTraceInitialValue);

        return;
    }

    /**
     * Update array list report map values.
     * @param category ReportCategory enum (Status, Message, Advice or StackTrace)
     * @param newValues ArrayList of multiple new values
     * For each value in newValues, it calls the overloaded function below
     */
    public void updateValues(ReportCategory category, 
                                ArrayList<String> newValues) {
        if (null == category || null == newValues) {
            return;
        }
        if (!this.report.containsKey(category)) { // if category label not found
            return;
        }
        // append new value
        for (String newValue: newValues) {
            this.updateValues(category, newValue);
        }
        return;
    }

    /**
     * Update single string report map values.
     * @param category ReportCategory enum (Status, Message, Advice, StackTrace)
     * @param newValue single new value string
     * Adds newValue 
     */
    public void updateValues(ReportCategory category, 
                                String newValue) {
        if (null == category || null == newValue) {
            return;
        }
        if (!this.report.containsKey(category)) { // if cateogry label not found
            return;
        }
        // append new value
        this.report.get(category).add(newValue);
        return;
    }

    /**
     * Clear report category
     * @param category ReportCategory enum (Status, Message, Advice or StackTrace)
     */
        public void clearValues(ReportCategory category) {
        if (null == category) {
            return;
        }
        if (!this.report.containsKey(category)) { // if category label not found
            return;
        }
        // append new value
        this.report.get(category).clear();
        return;
    }

    /**
     * Returns string representation of status to display to user
     * html used to format here
     * @return htmlstring representation of report
     */
    public String convertToString(){
        String output = "";

        //Add Category Name 
        output += "<span style=\"font-weight:bold\">"
                + this.name 
                + "</span><br/>";

        for(Map.Entry<ReportCategory, ArrayList<String>> e: this.report.entrySet()){
            ReportCategory key = e.getKey(); //get name of key
            List<String> values = e.getValue(); // get values
            if (null != values && !values.isEmpty()){ // check if no values are set
                output += "<span style="
                        + "\"display:inline-block;"
                        + "margin-left:40px;\">"
                        + "<u>"
                        + getCategoryName(key) 
                        + ":</u></span><br/>";
                output += "<p style="
                        + "\"display:inline-block;"
                        + "margin-left:40px;\">";
                for (String value: values){ //add each on a separate line
                    if (value != ""){
                        output = output + value + "<br/><br/>";
                    }
                }
                output = output + "</p>";
            }
        }
        return output;

    }

    /**
     * Return string representation of ReportCategory category.
     * @param category ReportCategory enum
     */
    private String getCategoryName(ReportCategory category){  
        switch(category) {
            case STATUS : return "Status";
            case MESSAGE : return "Message";
            case ADVICE : return "Advice";
            case STACKTRACE : return "Stack Trace";
            default: return "";
        }
    }
}