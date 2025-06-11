package xmod.status;

import java.util.*;
import xmod.status.ObjectReport;
import xmod.status.ReportLabel;
import xmod.status.ReportCategory;
/**
 * Representation of status
 * @author ELS
 * @version 1.0
 * @since 2024-2-25
 * BUGS:
 *  
 *  */

public class Reporter{
    Map<ReportLabel, ObjectReport> status;
    /**
     * Constructor
     * creates the the status hashmap to hold status information and initialises its values
     */
    public Reporter(){
        int initialCapacity = 8; //Number of categories + 1/3 
        float loadFactor = (float) 0.75;
        // so it prints keys in insertion order not recent access order 
        Boolean accessOrder = false; 

        this.status = new LinkedHashMap<ReportLabel, ObjectReport>(
            initialCapacity, loadFactor, accessOrder);      
        initialiseValues();
    }

    /**
     * Initialises the hashmap values 
     */
    private void initialiseValues(){
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
    public void updateValues(ReportLabel category, ObjectReport newValues){
        Boolean clearOldValues = false;
        // If ReportCategory.STATUS is not null and has changed,
        // If the ReportCategory.STATUS has not changed

        ArrayList<String> oldStatus = this.status.get(category)
                                        .report.get(ReportCategory.STATUS);
        ArrayList<String> newStatus =newValues.report.get(ReportCategory.STATUS);

        if (! newStatus.equals(oldStatus)){
            clearOldValues = true;
        }else{
            clearOldValues = false;
        }
         
        System.out.println("clearOldValues: " + clearOldValues);
        System.out.println("Initial Status: " + oldStatus);
        System.out.println("New Status: " + newStatus);
        //For each ReportCategory in the ObjectReport
        for(Map.Entry<ReportCategory, ArrayList<String>> e:
            newValues.report.entrySet()) {
                ReportCategory key = e.getKey(); // get key
                ArrayList<String> value = e.getValue(); // get values
                // Clear array if replace == true
                if (clearOldValues){
                    this.status.get(category).clearValues(key);
                }
                //If old value equals new value, do nothing
                this.status.get(category).updateValues(key, value);
        }
 
    }

    /**
     * Creates a string representation of the status to be used in the displaying to user
     * To control the visuals, html is used here
     * @return htmlstring representation of the status
     */
    public String toString(){
        String output = "";

        for(Map.Entry<ReportLabel, ObjectReport> e: this.status.entrySet()){
            ReportLabel key = e.getKey(); //get name of key
            ObjectReport values = e.getValue(); // get values
            if (null != values && !values.isEmpty()){ // check if no values are set
                output += "<span style=\"font-weight:bold\">"
                        + key.getValue() 
                        + "</span><br/>";
                output += "<p style=\"display:inline-block;"
                        + "margin-left:40px;\">";
                output += values.toString();
                output = output + "</p>";
            }
        }
        return output;
    }
}
