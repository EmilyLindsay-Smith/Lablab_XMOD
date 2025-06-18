package xmod.utils;

import xmod.constants.Typesetting;

import java.io.IOException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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
    private String tmsFileName;
    private int expLength;
    private String specificCodehead;
    private int[] tReactionOffset;
    private String[] codingArray;
    private String[] screenItems;

    private String[][] cKey;
    private String[][] nKey;
    private Boolean[][] rflag;
    private int[][] reacTime;

    private static final int NUM_BOXES = 16;

    /** Constructor.
     * @param expLength number of trials in experiment
     * @param tRTOffset offset for each trial
     * @param codehead
     * @param tmsFileName must include path
     * @param codingArray
     * @param screenItems

    */
    public ExperimentResulter(final int expLength, final int[] tRTOffset,
                                final String codehead,
                                final String tmsFileName,
                                final String[] codingArray,
                                final String[] screenItems) {
        this.expLength = expLength;
        this.tReactionOffset = tRTOffset;
        this.specificCodehead = codehead;
        this.tmsFileName = tmsFileName;
        this.codingArray = codingArray;
        this.screenItems = screenItems;
        createResultArrays();

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
        String dateString = getDate();
        String timeString = getTime();

        String resultsString = createResultsText(dateString, timeString);
        // check if 'results' folder exists else create it
        Path tmsFileHome = Paths.get(this.tmsFileName).getParent();
        Path tmsFileName = Paths.get(this.tmsFileName).getFileName();
        Path resultsDir = Paths.get(TMSFileHome.toString(), "results");
        if (!(Files.isDirectory(resultsDir))) {
            try {
                Files.createDirectories(resultsDir);
            } catch (IOException e) {
                System.out.println("Problem creating results directory");
                 e.printStackTrace();
                return;
            }
        }
        // Set results filename
        String filename = TMSFileName.toString() + "_"
                            + dateString + "_" + timeString + ".txt";
        Path resultsFile = Paths.get(resultsDir.toString(), filename);
        byte[] byteString = resultsString.getBytes();
        try {
            Files.write(resultsFile, byteString);
        } catch (IOException e) {
            System.out.println("Problem writing to results file: ");
            e.printStackTrace();
            // SEND UPDATE TO XMOD
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
        if (null == reaction) {
            return -1;
        }
        int[] pressedKeys = {reaction[0], reaction[1],
                                reaction[2], reaction[3]};
        int[] masks = {3, 12, 48, 192};
        int[] shiftRights = {0, 2, 4, 6};
        int pressedKey = pressedKeys[(int) ((box) / 4)];
        int x = (box) % 4;
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
            int x = ((boxNo + 1) * 2) + 3 - 1;
            // box 0 should look at reaction[4..5],
            // box 1 should look at reaction[6..7],
            // box 15 should look at reaction[34..35]
            int lowByte = (int) reaction[x]; // need ascii value
            int highByte = (int) reaction[x + 1] * 256; // need ascii value
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
        String results = "";
        String genericCodehead = "Item" + Typesetting.TAB
                                + "File" + Typesetting.TAB
                                + "Date Time SJ" + Typesetting.TAB
                                + "Target" + Typesetting.TAB
                                + "ReacTime" + Typesetting.TAB
                                + "Key" + Typesetting.TAB;
        // for the experiment specific coding headers
        String codehead = genericCodehead + this.specificCodehead;
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
        results = codehead + results;
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


}
