package xmod.status;

import java.util.ArrayList;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.Set;


/** ObjectReport manages reports on status of a Report Category.
 * Used by Reporter to handle overall state
 * Used by other classes to format updates to send to Reporter
 *
 * @author ELS
 * @version 2.0
 * @since 2025-06-09
 * KNOWN BUGS:
 */
public class ObjectReport {
    /**Map to hold info for each ReportCategory. */
    private Map<ReportCategory, ArrayList<String>> report;
    /** Name of category e.g. audio, tms, connection etc. */
    private ReportLabel name;
    /**
     * Constructor.
     * @param className name of category e.g. audio, tms, connection etc.
     */
    public ObjectReport(final ReportLabel className) {
        this.name = className;
        final int initialCapacity = 6; //Number of categories + 1/3
        final float loadFactor = (float) 0.75;
        // To prints keys in insertion order not recent access order
        Boolean accessOrder = false;

        this.report = new LinkedHashMap<ReportCategory, ArrayList<String>>(
            initialCapacity, loadFactor, accessOrder);
        initialiseValues();
    }


   /**
     * Returns ArrayList<String> for given label in report.
     * @param label ReportCategory for the category
     * @return ArrayList<String> stored at that label
     */
    public ArrayList<String> get(final ReportCategory label) {
        return this.report.get(label);
    }
    /**
     * Returns this.name.
     * Mostly useful for testing
     * @return name
     */
    public String getName() {
        return this.name.getValue();
    }


    /** Returns size of status.
     * @return int number of entries in report
     */
    public int size() {
        return this.report.size();
    }
    /**
     * Returns this.report.entrySet.
     * Mostly useful for testing
     * @return name
     */
    public Set<Map.Entry<ReportCategory, ArrayList<String>>> entrySet() {
        return this.report.entrySet();
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
     * @param category ReportCategory enum
     * @param newValues ArrayList of multiple new values
     * For each value in newValues, it calls the overloaded function below
     */
    public void updateValues(final ReportCategory category,
                                final ArrayList<String> newValues) {

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
    public void updateValues(final ReportCategory category,
                                final String newValue) {
        if (null == category || null == newValue) {
            return;
        }
        if (!this.report.containsKey(category)) { // if cateogry label not found
            return;
        }
        //Check for duplication
        if (this.report.get(category).contains(newValue)) {
            return;
        }
        // append new value
        this.report.get(category).add(newValue);
        return;
    }

    /**
     * Clear report category.
     * @param category ReportCategory enum
     */
        public void clearValues(final ReportCategory category) {
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
     * Returns string representation of status to display to user.
     * html used to format here
     * @return htmlstring representation of report
     */
    public String toString() {
        String output = "";

        //No need for Category Name - this is handled in Reporter
        for (Map.Entry<ReportCategory, ArrayList<String>> e
            : this.report.entrySet()) {
            ReportCategory key = e.getKey(); //get name of key
            ArrayList<String> values = e.getValue(); // get values
            // check if no values are set
            if (null != values && !values.isEmpty()) {
                /*
                output += "<span style="
                        + "\"display:inline-block;"
                        + "margin-left:40px;\">"
                        + "<u>"
                        + key.getValue()
                        + ":</u></span><br/>";
                */
                output += "<p style="
                        + "\"display:inline-block;"
                        + "margin-left:40px;\">";
                for (String value: values) { //add each on a separate line
                    if (value != "") {
                        output = output + value + "<br/><br/>";
                    }
                }
                output = output + "</p>";
            }
        }
        return output;
    }

    /**
     * Returns true if none of the ReportCategories contain content.
     * @return Boolean
     */
    public Boolean isEmpty() {
        Boolean empty = true;
        for (Map.Entry<ReportCategory, ArrayList<String>> e
            :this.report.entrySet()) {
            ReportCategory key = e.getKey(); //get name of key
            ArrayList<String> values = e.getValue(); // get values
            if (null != values && !values.isEmpty()) {
                empty = false;
            }
        }
        return empty;
    }
}
