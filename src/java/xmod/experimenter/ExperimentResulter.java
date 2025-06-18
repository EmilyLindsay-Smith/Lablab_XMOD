package xmod.experimenter;

import xmod.constants.Actions;
import xmod.constants.Typesetting;
import xmod.status.ObjectReport;
import xmod.status.ReportCategory;
import xmod.status.ReportLabel;
import xmod.status.Responses;

import xmod.utils.Utils;
import java.io.IOException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * ExperimentResulter class holds all the experiment results and prints to file.
 * @author ELS
 * @version 2.0
 * @since 2024-12-10
 * BUGS:

 * NOTES:
 * Old Xmod records both char and num of the pressed key but only reports num
 * Old Xmod doesn't specify output file extension - I've put .tsv here
 */

public class ExperimentResulter {
     /** PCS to handle sending updates. */
    private PropertyChangeSupport pcs;

    /** Path and name of tms file (extracted from expLoader). */
    private String tmsFileName;
    /** Number of trials in experiment (extracted from expLoader). */
    private int expLength;
    /** Codehead for researcher-specific codes (extracted from expLoader). */
    private String specificCodehead;
    /** Reaction time offset (extracted from expLoader). */
    private int[] tReactionOffset;
    /** Coding array for each trial (extracted from expLoader). */
    private String[] codingArray;
    /** Item to show on screen for each trial (extracted from expLoader). */
    private String[] screenItems;

    /** Array holding character key pressed for each reaction. */
    private String[][] cKey;
    /** Array holding numeric key pressed for each reaction. */
    private String[][] nKey;
    /** Array holding flag for reaction pressed for each reaction. */
    private Boolean[][] rflag;
    /** Array holding reaction time for each reaction. */
    private int[][] reacTime;

    /** Directory to save results to. */
    private Path resultsDir;
    /** Number of boxes in the system - fixed at 16. */
    private static final int NUM_BOXES = 16;

    /** Constructor.
     * @param experimentLength number of trials in experiment
     * @param tReactionTOffset offset for each trial
     * @param experimentCodehead labels for the columns
     * @param fileName must include path
     * @param experimentCodingArray all the coding info from the tms file
     * @param experimentScreenItems items for each trial
    */
    public ExperimentResulter(final String fileName,
                                final int experimentLength,
                                final int[] tReactionTOffset,
                                final String experimentCodehead,
                                final String[] experimentCodingArray,
                                final String[] experimentScreenItems) {

        this.pcs = new PropertyChangeSupport(this);

        this.expLength = experimentLength;
        this.tReactionOffset = tReactionTOffset;
        this.specificCodehead = experimentCodehead;
        this.tmsFileName = fileName;
        this.codingArray = experimentCodingArray;
        this.screenItems = experimentScreenItems;
        createResultsDirectory();
        createResultArrays();
    }

    /**
     * Check directory exists to house results and create if it doesn't exist.
     */
    private void createResultsDirectory() {
        // check if 'results' folder exists else create it
        Path tmsFileHome = Paths.get(this.tmsFileName).getParent();
        this.resultsDir = Paths.get(tmsFileHome.toString(), "results");
        if (!(Files.isDirectory(resultsDir))) {
            try {
                Files.createDirectories(resultsDir);
            } catch (IOException e) {
                updateStatus(Responses.ERROR_RESULTS_DIRECTORY,
                "Could not create results directory",
                "Please check stack trace and ensure permissions are correct",
                Utils.getStackTrace(e));
                return;
            }
        }
    }

    /**
     * For a given trial, parse the byte array from the control box.
     * This records the pressed key and reaction time
     * Need to run this for each trial
     * @param reaction byte array from control box
     * @param currentTrialIndex integer to decide where the results are saved
     */
    public void collectTrialResults(final byte[] reaction,
                                    final int currentTrialIndex) {
        if (null == reaction) {
            return;
        }
        for (int box = 0; box < this.NUM_BOXES; box++) {
            int reactionOutput = getOutput(reaction, box);
            getPressedKeys(reactionOutput, box, currentTrialIndex);
            getReactionTime(reaction, box, currentTrialIndex);
        }
    }

    /**
     * Save the results of all the trials to file.
     */
    public void printResults() {
        // Get time and date strings
        String dateString = getDate();
        String timeString = getTime();
        // Get results as string
        String resultsString = createResultsText(dateString, timeString);
        byte[] byteString = resultsString.getBytes();

        // Set results filename
        Path tmsBareFileName = Paths.get(this.tmsFileName).getFileName();
        String resultsFilename = tmsBareFileName.toString() + "_"
                            + dateString + "_" + timeString + ".txt";
        Path resultsFile = Paths.get(this.resultsDir.toString(),
                                    resultsFilename);
        // Write to file
        try {
            Files.write(resultsFile, byteString);
            updateStatus("", "Results successfully printed to file "
                        + resultsFile, "", "");
        } catch (IOException e) {
            updateStatus("",
                "Could not create results file",
                "Please check stack trace and ensure permissions are correct",
                Utils.getStackTrace(e));
        }
    }

    /**
     * instantiate the arrays to hold the results.
     */
    public void createResultArrays() {
        this.cKey = new String[this.NUM_BOXES][this.expLength];
        this.nKey = new String[this.NUM_BOXES][this.expLength];
        this.rflag = new Boolean[this.NUM_BOXES][this.expLength];
        this.reacTime = new int[this.NUM_BOXES][this.expLength];
    }

    /**
     * Parse the pressed key byte.
     * 1 byte covers the reactions of 4 boxes, with 2 bits per box
     * The mask only looks at hte two bits for each box
     * The right shift makes them in the 2 and 4 position
     * @param reaction byte array from control box
     * @param box integer box identifier
     * @return the number corresponding to the pressed key for that box
     */
    private int getOutput(final byte[] reaction, final int box) {
        int maskOne = 3;
        int maskTwo = 12;
        int maskThree = 48;
        int maskFour = 192;
        int shiftOne = 0;
        int shiftTwo = 2;
        int shiftThree = 4;
        int shiftFour = 6;


        if (null == reaction) {
            return -1;
        }
        int[] pressedKeys = {reaction[0], reaction[1],
                                reaction[2], reaction[3]};
        int[] masks = {maskOne, maskTwo, maskThree, maskFour};
        int[] shiftRights = {shiftOne, shiftTwo, shiftThree, shiftFour};
        int boxDivisor = 4;
        int pressedKey = pressedKeys[(int) ((box) / boxDivisor)];
        int x = (box) % boxDivisor;
        int mask = masks[x];
        int rightShift = shiftRights[x];
        int output = (pressedKey & mask) >> rightShift;
        return output;
    }

    /**
     * Record the pressed keys in the result arrays.
     * @param output reaction byte
     * @param boxNo which box(1-16)
     * @param currentIndex index of current trial
     */
    private void getPressedKeys(final int output, final int boxNo,
                                final int currentIndex) {
        switch (output) {
            case 1:
                this.cKey[boxNo][currentIndex] = "L";
                this.nKey[boxNo][currentIndex] = "1";
                this.rflag[boxNo][currentIndex] = true;
                break;
            case 2:
                this.cKey[boxNo][currentIndex] = "R";
                this.nKey[boxNo][currentIndex] = "3";
                this.rflag[boxNo][currentIndex] = true;
                break;
            case 3:
                this.cKey[boxNo][currentIndex] = "M";
                this.nKey[boxNo][currentIndex] = "2";
                this.rflag[boxNo][currentIndex] = true;
                break;
            default:
                this.cKey[boxNo][currentIndex] = ".";
                this.nKey[boxNo][currentIndex] = ".";
                this.rflag[boxNo][currentIndex] = false;
                this.reacTime[boxNo][currentIndex] = 0;
                break;
            }
    }

    /**
     * Record the reaction times in the result arrays.
     * @param reaction reaction byte array
     * @param boxNo which box(1-16)
     * @param index index of current trial
     */
    private void getReactionTime(final byte[] reaction,
                                        final int boxNo, final int index) {
        if (null != reaction && cKey[boxNo][index] != ".") {
            //int x = (currentIndex * 2) + 3 - 1;
            // which byte look at in reaction array
            int x = ((boxNo + 1) * 2) + 2;
            // box 0 should look at reaction[4..5],
            // box 1 should look at reaction[6..7],
            // box 15 should look at reaction[34..35]
            int lowByte = (int) reaction[x]; // need ascii value
            // need ascii value
            int highByteMultiplier = 256;
            int highByte = (int) reaction[x + 1] * highByteMultiplier;
            this.reacTime[boxNo][index] = highByte
                                                + lowByte
                                                - this.tReactionOffset[index];
        }
    }

    /**
     * Create the results text.
     * @param dateString date representation
     * @param timeString time representation
     * @return results rtext
     */
    private String createResultsText(final String dateString,
                                    final String timeString) {
        String genericCodehead = "Item" + Typesetting.TAB
                                + "File" + Typesetting.TAB
                                + "Date Time SJ" + Typesetting.TAB
                                + "Target" + Typesetting.TAB
                                + "ReacTime" + Typesetting.TAB
                                + "Key" + Typesetting.TAB;
        // for the experiment specific coding headers
        String codehead = genericCodehead + this.specificCodehead;
        String results = codehead;
        for (int trialIndex = 0; trialIndex < this.expLength; trialIndex++) {
            for (int boxNo = 0; boxNo < this.NUM_BOXES; boxNo++) {
                if (null != this.rflag[boxNo][trialIndex]) {
                    results = results + Integer.toString(trialIndex + 1)
                            + Typesetting.TAB
                            + this.tmsFileName
                            + Typesetting.TAB
                            + dateString + " " + timeString + " "
                            //Add zero if box number is less than 0
                            + ((boxNo >= 10)
                                ? Integer.toString(boxNo)
                                : "0" + Integer.toString(boxNo))
                            + Typesetting.TAB
                            + this.screenItems[trialIndex]
                            + Typesetting.TAB
                            + Integer.toString(this.reacTime[boxNo][trialIndex])
                            + Typesetting.TAB
                            + this.nKey[boxNo][trialIndex]
                            + Typesetting.TAB
                            + this.codingArray[trialIndex]
                            + Typesetting.CR;
                }
            }
        }
        return results;
        }

    /**
     * Get datestamp.
     * @return string of formatted date
     */
    private String getDate() {
            LocalDate date = LocalDate.now();
            DateTimeFormatter format = DateTimeFormatter.ofPattern("dd.MM.yy");
            String formattedDate = date.format(format);
            return formattedDate;
    }

    /**
     * Get timestamp.
     * @return string of formatted time
     */
    private String getTime() {
        LocalTime time = LocalTime.now();
        DateTimeFormatter format = DateTimeFormatter.ofPattern("HH.mm.ss");
        String formattedTime = time.format(format);
        return formattedTime;
    }

    /** Send updates to main Xmod.java.
     * @param newStatus status
     * @param newMessage message
     * @param newAdvice advice
     * @param newStackTrace any stack trace
     */
    private void updateStatus(final String newStatus,
                                final String newMessage,
                                final String newAdvice,
                                final String newStackTrace) {
        ObjectReport report = new ObjectReport(ReportLabel.STATUS);
        if  (newStatus != "") {
            report.updateValues(ReportCategory.STATUS, newStatus);
        }
        if  (newMessage != "") {
            report.updateValues(ReportCategory.MESSAGE, newMessage);
        }
        if  (newAdvice != "") {
            report.updateValues(ReportCategory.ADVICE, newAdvice);
        }
        if  (newStackTrace != "") {
            report.updateValues(ReportCategory.STACKTRACE, newStackTrace);
        }
        pcs.firePropertyChange(Actions.UPDATE, null, report);
        return;
    }

    /**
     * Used in ExperimentRunnner to listen for pcs.
     * @param l listener i.e.ExperimentRunner
     */

    public void addObserver(final PropertyChangeListener l) {
        pcs.addPropertyChangeListener(Actions.UPDATE, l);
    }

}
