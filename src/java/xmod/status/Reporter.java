package xmod.status;

import java.util.*;
/**
 * Representation of status
 * @author ELS
 * @version 1.0
 * @since 2024-2-25
 * BUGS:
 *  
 *  */

public class Reporter{
    Map<String, ArrayList<String>> status;
    
    /**
     * Constructor
     * creates the the status hashmap to hold status information and initialises its values
     */
    public Reporter(){
        int initialCapacity = 8; //Number of categories + 1/3 
        float loadFactor = (float) 0.75;
        Boolean accessOrder = false; // so it prints keys in insertion order not recent access order 

        this.status = new LinkedHashMap<String, ArrayList<String>>(initialCapacity, loadFactor, accessOrder); // initial capacity, load factor, accessOrder        
        initialiseValues();
    }

    /**
     * Initialises the hashmap values 
     */
    private void initialiseValues(){
        // Initialise initial value arraylists        
        ArrayList<String> statusInitialValue = new ArrayList<String>();
        statusInitialValue.add(Responses.WELCOME);

        ArrayList<String> tmsInitialValue = new ArrayList<String>();
        tmsInitialValue.add(Responses.NO_FILE_SELECTED);

        ArrayList<String> audioInitialValue = new ArrayList<String>();
        audioInitialValue.add(Responses.NO_FILE_SELECTED);

        ArrayList<String> connectionInitialValue = new ArrayList<String>();
        ArrayList<String> fontInitialValue = new ArrayList<String>();
        ArrayList<String> monitorInitialValue = new ArrayList<String>();
         
        // Add to status hashmap: categoryName, initialValue
        this.status.put(ReportLabel.STATUS, statusInitialValue);
        this.status.put(ReportLabel.TMS, tmsInitialValue);
        this.status.put(ReportLabel.AUDIO, audioInitialValue);
        this.status.put(ReportLabel.CONNECTION, connectionInitialValue);
        this.status.put(ReportLabel.FONT, fontInitialValue);
        this.status.put(ReportLabel.MONITORS, monitorInitialValue);
    }

    /**
     * Updates the status for a given category
     * @param category the name of the category
     * @param newValue the string to add to the category status
     * @param replace if true, delete previous status and replace wtih newValue; if false add to existing status
     */
    public void updateValues(String category, String newValue, Boolean replace){
        if (null == category || null == newValue){
            return;
        }
        if (!this.status.containsKey(category)){ // if category not found
            return;
        }

        if (replace){ //remove existing value
            this.status.get(category).clear();
        }
        // append new value 
        this.status.get(category).add(newValue);
    };

    /**
     * Creates a string representation of the status to be used in the displaying to user
     * To control the visuals, html is used here
     * @return htmlstring representation of the status
     */
    public String convertToString(){
        String output = "";

        for(Map.Entry<String, ArrayList<String>> e: this.status.entrySet()){
            String key = e.getKey(); //get name of key
            List<String> values = e.getValue(); // get values
            if (null != values && !values.isEmpty()){ // check if no values are set
                output = output + "<span style=\"font-weight:bold\">"+ key + "</span><br/>";
                output = output + "<p style=\"display:inline-block;margin-left:40px;\">";
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
}
