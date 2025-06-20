package xmod.status;


import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

/** Representation of status.
 * @author ELS
 * @version 2.0
 * @since 2024-2-25
 * BUGS:
 *
 *  */

public class Reporter {
    /**Contains ObjectReport for each label. */
    private Map<ReportLabel, ObjectReport> status;

    /**
     * Constructor.
     * creates the the status hashmap  and initialises its values
     */
    public Reporter() {
        final int initialCapacity = 8; //Number of categories + 1/3
        final float loadFactor = (float) 0.75;
        // so it prints keys in insertion order not recent access order
        Boolean accessOrder = false;

        this.status = new LinkedHashMap<ReportLabel, ObjectReport>(
            initialCapacity, loadFactor, accessOrder);

        initialiseValues();
    }

    /**

     * Returns ObjectReport for given label in status.
     * @param label ReportLabel for the category
     * @return ObjectReport stored at that label
     */
    public ObjectReport get(final ReportLabel label) {
        return this.status.get(label);
    }

    /**
     * Returns size of status.
     * @return int number of entries in status
     */
    public int size() {
        return this.status.size();
    }

    /**
     * Returns whether status is empty.
     * @return Boolean emptiness
     */
    public Boolean isEmpty() {
        return this.status.isEmpty();
    }

    /**
     * Initialises the hashmap values.
     */
    private void initialiseValues() {
        // Initialise initial value arraylists
        ObjectReport statusInitialValue = new ObjectReport(
                                                ReportLabel.STATUS);
        statusInitialValue.updateValues(ReportCategory.MESSAGE,
                                        Responses.WELCOME);

        ObjectReport tmsInitialValue = new ObjectReport(
                                            ReportLabel.TMS);
        tmsInitialValue.updateValues(ReportCategory.MESSAGE,
                                    Responses.NO_FILE_SELECTED);

        ObjectReport audioInitialValue = new ObjectReport(
                                            ReportLabel.AUDIO);
        audioInitialValue.updateValues(ReportCategory.MESSAGE,
                                        Responses.NO_FILE_SELECTED);

        ObjectReport connectionInitialValue = new ObjectReport(
                                            ReportLabel.CONNECTION);
        ObjectReport fontInitialValue = new ObjectReport(
                                            ReportLabel.FONT);
        ObjectReport monitorInitialValue = new ObjectReport(
                                            ReportLabel.MONITORS);

        // Add to status hashmap: categoryName, initialValue
        this.status.put(ReportLabel.STATUS, statusInitialValue);
        this.status.put(ReportLabel.TMS, tmsInitialValue);
        this.status.put(ReportLabel.AUDIO, audioInitialValue);
        this.status.put(ReportLabel.CONNECTION, connectionInitialValue);
        this.status.put(ReportLabel.FONT, fontInitialValue);
        this.status.put(ReportLabel.MONITORS, monitorInitialValue);
    }

    /**
     * Updates the status for a given category.
     * @param category the ReportLabel of the category
     * @param newValues the ObjectReport to merge in
     * If the ReportCategory STATUS is different, the existing content will be
     * removed; if it is the same, it will just append
     */
    public void updateValues(final ReportLabel category,
                            final ObjectReport newValues) {
        Boolean clearOldValues = false;
        // If ReportCategory.STATUS is not null and has changed,
        // If the ReportCategory.STATUS has not changed

        ArrayList<String> oldStatus = this.status.get(category)
                                        .get(ReportCategory.STATUS);
        ArrayList<String> newStatus = newValues
                                        .get(ReportCategory.STATUS);

        // If newStatus is different, clear the values
        if (!newStatus.equals(oldStatus)) {
            clearOldValues = true;
        }
        // Keep the old values if newStatus is empty
        if (newStatus.equals("")) {
            clearOldValues = false;
        }

        //For each ReportCategory in the ObjectReport
        for (Map.Entry<ReportCategory, ArrayList<String>> e
            :newValues.entrySet()) {
                ReportCategory key = e.getKey(); // get key
                ArrayList<String> value = e.getValue(); // get values
                // Clear array if replace == true
                if (clearOldValues) {
                    this.status.get(category).clearValues(key);
                }
                //If old value equals new value, do nothing
                this.status.get(category).updateValues(key, value);
        }

    }

    /** Clears all the status for the given ReportLabel category.
     * @param category ReportLabel e.g. STATUS, TMS, AUDIO etc.
     */
    public void clearValues(final ReportLabel category) {
        ObjectReport currentValues = this.status.get(category);
        for (Map.Entry<ReportCategory, ArrayList<String>> e
            :currentValues.entrySet()) {
                ReportCategory key = e.getKey(); // get key
                ArrayList<String> value = e.getValue(); // get values
                this.status.get(category).clearValues(key);
            }
    }

    /**
     * Creates a string representation of the status to displaying to user.
     * To control the visuals, html is used here
     * @return htmlstring representation of the status
     */
    public String toString() {
        String output = "";

        for (Map.Entry<ReportLabel, ObjectReport> e: this.status.entrySet()) {
            ReportLabel key = e.getKey(); //get name of key
            ObjectReport values = e.getValue(); // get values
            // check if no values are set
            if (null != values && !values.isEmpty()) {
                output += "<span style=\"font-weight:bold\">"
                        + key.getValue()
                        + "</span>"; //<br/>";
                output += "<span style=\"display:inline-block;"
                        + "margin-left:40px;\">";
                output += values.toString();
                output = output + "</span>";
            }
        }
        return output;
    }
}
