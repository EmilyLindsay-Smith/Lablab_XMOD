package xmod.experimenter;

import xmod.constants.Actions;
import xmod.constants.Typesetting;
import xmod.status.ObjectReport;
import xmod.status.ReportCategory;
import xmod.status.ReportLabel;
import xmod.status.Responses;

import xmod.utils.Utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**s
 * Loads the .tms file and parses the information needed to run the experiment.
 * @author ELS
 * @version 1.0
 * @since 2025-06-09
 * BUGS:
 * PCS MUST BE EXPERIMENT RUNNER NOT XMOD.JAVA
 */

public class ExperimentLoader {
     /** PCS to handle sending updates. */
    private PropertyChangeSupport pcs;
    /* Define class variables */
    /** tmsFilePath for where the tms file is. */
    private String tmsFilePath;
    /** Whether the tms file has been loaded. */
    private Boolean tmsLoaded = false;
    /** Base file name for tmsFile. */
    private String tmsFileName;
    /** Codehead from the tms file. */
    private String codehead;

    /** timeout from RT start. */
    private int[] tReactionTimeout;
    /** offset from start to start recording RT. */
    private int[] tReactionOffset;
    /** time to turn monitor on from bleep. */
    private int[] tMonitorOn;
    /** time to turn monitor off from bleep. */
    private int[] tMonitorOff;
    /**  contains code text fields. */
    private String[] codingArray;
    /**  array of visual trial items. */
    private String[] screenItems;

    /** list of lines in the tms file. */
    private ArrayList<String> tmsFileLines;

    /** time in ms reserved for communication with controller.
     * Note why this is 400ms is unknown - but was set in Xmod 1.0
     */
    private final int tcommreserve = 400;


    /** Constructor
    **/
    public ExperimentLoader() {
        pcs = new PropertyChangeSupport(this);
    }

    /**
     * Reads the tms file into a buffer to be parsed.
     * Checks that file has a valid filename and exists
     * Note the file to be loaded is the tmsFilePath
     * this is set when the ExperimentLoader is initalised
     * @param filepath location of TMS file
     * @return returns whether file loaded or not
     */
    public Boolean loadFile(final String filepath) {

        if (null == filepath) {
            updateStatus(Responses.NO_FILE_SELECTED,
                "Could not load file " + filepath + " as file path was null",
                "Please try again", "");
            return false;
        }
        if (!Utils.fileHasExtension(filepath, "tms")) {
            updateStatus(Responses.NO_FILE_SELECTED,
                "Could not load file " + filepath
                + " as file did not have .tms extension",
                "Please try again", "");
            return false;
        }
        if (!Utils.fileExists(filepath)) {
            updateStatus(Responses.NO_FILE_SELECTED,
                "Could not load file " + filepath + " as file does not exist",
                "Please try again", "");
            return false;
        }

        this.tmsFilePath = filepath;

        //Now filepath must exist and have .tms extension so read the file:

        this.tmsFileName = this.tmsFilePath;
        this.tmsFileLines = new ArrayList<String>();
        try (// This will automatically close the BufferedReader at the end
            BufferedReader file = new BufferedReader(
                                    new FileReader(new File(this.tmsFilePath))
                                    );
        ) {
            String line;
            while ((line = file.readLine()) != null) {
                this.tmsFileLines.add(line);
            }
            if (this.tmsFileLines.size() == 0) {
                updateStatus(Responses.NO_FILE_SELECTED,
                "Could not load file " + filepath + " as file is empty",
                "Please try again", "");
                return false;
            }
        } catch (IOException e) {
            String stackTrace = Utils.getStackTrace(e);
            updateStatus(Responses.NO_FILE_SELECTED,
                "Could not load file " + filepath + " due to error",
                "Please try again", stackTrace);
            return false;
        }
        return true; //successfully read the file
    };

    /**
     * Initialises the experiment arrays and parses tms information.
     * Creates arrays the length of the tms file
     * Generates the code head
     * Corrects the timeouts to account for tcommreserve
     */
    public void parseFile() {
        try {
            initialiseArrays();
            getTmsInfo();
            extractInfo();
            correctTimeouts();
            this.tmsLoaded = true;
        } catch (ExperimentLoaderException e) {
            updateStatus(Responses.NO_FILE_SELECTED,
            "Failed to parse the tms file " + this.tmsFilePath,
            "",
            e.getMessage()
            );
        }
    };

    /**
     * Gets size of arrays needed to hold all the experiment info from tms file.
     * @return integer length
     */
    private int getArraySize() {
        if (null == this.tmsFileLines) {
            return -1;
        }
        // ignore first two lines of tms file
        int length = (int) Math.floor((this.tmsFileLines.size() - 2) / 2);
        // if even, need to ignore that last one
        // last char in tms file is a carriage return to empty element created
        if (length / 2 == 0) {
            length = length - 1;
        }
        return length;
    }

    /**
     * Sets up all the arrays needed to hold experiment info from tms file.
     */
    private void initialiseArrays() throws ExperimentLoaderException {
        int length = getArraySize();
        if (length == -1) {
            throw new ExperimentLoaderException("TMS file has no trials");
        }
        this.tReactionTimeout = new int[length]; // timeout from RT start
        this.tReactionOffset = new int[length]; //offset to start recording RT
        this.tMonitorOn = new int[length]; // time to monitor on from bleep
        this.tMonitorOff = new int[length]; // time to monitor off from bleep
        this.codingArray = new String[length]; // contains codee text fields
        this.screenItems = new String[length]; // array of visual trial items
        return;
    };

    /**
     * Collects tmsVersion and the generates the codehead for the results file.
     * Removes lines from tms that are not needed
     */
    private void getTmsInfo() throws ExperimentLoaderException {
        try {
            // remove first element (%% CROSSMODAL STRING)
            this.tmsFileLines.remove(0);
            // remove tms version line
            this.tmsFileLines.remove(0);
            // extract codehead info
            codehead = this.tmsFileLines.get(0).trim() + Typesetting.CR;
            this.tmsFileLines.remove(0);
        } catch (IndexOutOfBoundsException e) {
            throw new ExperimentLoaderException("TMS file lacks first 3 lines");
        }
        return;
    };

    /**
     * Extracts the timings and items from the tms file to populate the arrays.
     * Note first three lines of file have been removed by gettmsInfo
     * therefore this.tmsFileLines only has the experiment trials now
     */
    private void extractInfo() {
        int length = this.tmsFileLines.size();
        final int numLineComponents = 3;
        for (int i = 0; i < length / 2; i++) { // length/2 as 2 lines per trial
            // Get Timing Info on every other line
            //starts with even lines as 0 indexed
            // tmsLine will have length 3 when split by tabs
            String[] tmsTimingLine = this.tmsFileLines.get(2 * i)
                                        .split("\t", numLineComponents);
            this.tReactionTimeout[i] = (int) Float.parseFloat(
                                            tmsTimingLine[0].trim()
                                            ); // 2000.0 in .tms -> 2000 here
            this.tReactionOffset[i] = (int) Float.parseFloat(
                                            tmsTimingLine[1].trim()
                                            );
            // remove trailing whitespace from coding array
            this.codingArray[i] = tmsTimingLine[2].trim();
            // Get Visual Info: screen item and monitor timing
            String[] tmsVisualLine = this.tmsFileLines.get(2 * i + 1)
                                                        .split("\t");
            this.screenItems[i] = tmsVisualLine[0].trim();
            this.tMonitorOn[i] = (int) Float.parseFloat(
                                                tmsVisualLine[1].trim());
            this.tMonitorOff[i] = (int) Float.parseFloat(
                                                tmsVisualLine[2].trim());
        }
        return;
    }


    /**
     * Corrects the timeouts to account for the tcommreserve.
     * tcommreserve is the time in ms reserved for communication with controller
     */
    private void correctTimeouts() {
        for (int i = 0; i < this.tReactionTimeout.length; i++) {
            this.tReactionTimeout[i] = this.tReactionTimeout[i]
                                        + this.tReactionOffset[i]
                                        - this.tcommreserve;
        }
        return;
    }

    /** Send update to Xmod.java to send to reporter */
    private void updateStatus(final String newStatus,
                                final String newMessage,
                                final String newAdvice,
                                final String newStackTrace) {
        ObjectReport report = new ObjectReport(ReportLabel.TMS);
        if (newStatus != "") {
            report.updateValues(ReportCategory.STATUS, newStatus);
        }
        if (newMessage != "") {
            report.updateValues(ReportCategory.MESSAGE, newMessage);
        }
        if (newAdvice != "") {
            report.updateValues(ReportCategory.ADVICE, newAdvice);
        }
        if (newStackTrace != "") {
            report.updateValues(ReportCategory.STACKTRACE, newStackTrace);
        }
        pcs.firePropertyChange(Actions.UPDATE, null, report);
        return;
    }
    /**
     * Used in Xmod.java to allow the controller to listen for pcs.
     * @param l listener i.e. Xmod.java
     */
    public void addObserver(final PropertyChangeListener l) {
        pcs.addPropertyChangeListener(Actions.UPDATE, l);
    }
}

/** Custom Exception to stop parsing if errors occur */
class ExperimentLoaderException extends Exception {
    /** Constructor */
    public ExperimentLoaderException(String msg) {
        super(msg);
    }

}
