package xmod.experimenter;

import xmod.utils.TestListener;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class ExperimentResulterTest {
    /** TMS file used for tests. */
        private String testFile = "./test/testFiles/charlie_short.tms";
    /** ExperimentResulter object. */
        ExperimentResulter resulter;
    /** Listener. */
        TestListener tester;


    /**Set up. */
    @BeforeEach
    void setup() {
        ExperimentLoader loader = new ExperimentLoader();
        loader.loadFile(this.testFile);
        loader.parseFile();
        Boolean experimentLoaded = true;
        int expLength = loader.getScreenItems().length;

        int[] tReactionTimeout = loader.getTReactionTimeout();
        int[] tMonitorOn = loader.getTMonitorOn();
        int[] tMonitorOff = loader.getTMonitorOff();
        String[] codingArray = loader.getCodingArray();
        String[] screenItems = loader.getScreenItems();
        int[] tReactionOffset = loader.getTReactionOffset();
        String codehead = loader.getCodehead();
        resulter = new ExperimentResulter(this.testFile,
                                            expLength,
                                            tReactionOffset,
                                            codehead,
                                            codingArray,
                                            screenItems
                                            );
        tester = new TestListener();
        resulter.addObserver(tester);
    }

    /** Check results directory created  */
    @DisplayName("Check results directory")
    @Test
    public void checkArrays() {
       // Will have created result directory in constructor
       Path parentDir = Paths.get(this.testFile).getParent();
        Path resultsDir = Paths.get(parentDir.toString(), "results");
        Assertions.assertEquals(false, tester.checkUpdate(),
            "An error update should not have been sent");
        Assertions.assertEquals(true, Files.exists(resultsDir),
        "result directory should have been created");
        try {
            Files.delete(resultsDir);
        } catch (IOException e ) {
            Assertions.assertEquals(false, true, "Could not delete resultsDir");
            e.printStackTrace();
        }
    }

    @DisplayName("Check key presses")
    @ParameterizedTest
    @ValueSource(ints = {0, 8, 15})
    public void checkKeys(final int boxNo) {
        resulter.getPressedKeys(1, boxNo, 1);
        Assertions.assertEquals("L", this.resulter.getCKey()[boxNo][1],
        "Key char should be 'L'");
        Assertions.assertEquals("1", this.resulter.getNKey()[boxNo][1],
        "Num char should be 1");
        Assertions.assertEquals(true, this.resulter.getRFlag()[boxNo][1],
        "RFlag should be true");
        resulter.getPressedKeys(2, boxNo, 1);
        Assertions.assertEquals("R", this.resulter.getCKey()[boxNo][1],
        "Key char should be 'R'");
        Assertions.assertEquals("3", this.resulter.getNKey()[boxNo][1],
        "Num char should be 3");
        Assertions.assertEquals(true, this.resulter.getRFlag()[boxNo][1],
         "RFlag should be true");
        resulter.getPressedKeys(3, boxNo, 1);
        Assertions.assertEquals("M", this.resulter.getCKey()[boxNo][1],
        "Key char should be 'M'");
        Assertions.assertEquals("2", this.resulter.getNKey()[boxNo][1],
        "Num char should be 2");
        Assertions.assertEquals(true, this.resulter.getRFlag()[boxNo][1],
        "RFlag should be true");
        resulter.getPressedKeys(4, boxNo, 1);
        Assertions.assertEquals(".", this.resulter.getCKey()[boxNo][1],
         "Key char should be '.'");
        Assertions.assertEquals(".", this.resulter.getNKey()[boxNo][1],
        "Num char should be .");
        Assertions.assertEquals(false, this.resulter.getRFlag()[boxNo][1],
        "RFlag should = false");
    }
    /** Utility func to reverse engineer the reaction array
     * @param num expected reaction as integer
     * @param boxNo which box
     * @return reaction as byte array
     * */
    byte[] getDummyReaction(final int num, final int boxNo) {
            int numToSplit = num + 500; // sample offset
            byte[] dummyReaction = new byte[36];
            int highByte = numToSplit / 256;
            int lowByte = numToSplit % 256;
            int x = ((boxNo + 1) * 2) + 3 - 1;
            dummyReaction[x + 1] = (byte) highByte;
            dummyReaction[x] = (byte) lowByte;

            return dummyReaction;
        }

    /** Check that reaction time in bytes is correctly converted to int.
     * @param expectedRT sample reaction times
     * These are converted by getDummyReaction to bytes to be converted back.
     */
    @DisplayName("Check Reaction Time")
    @ParameterizedTest
    @ValueSource(ints = {0, 250, 1000, 3123})
    public void checkRT(final int expectedRT) {



        int[] boxes = {0, 3, 9, 12, 15};
        for (int box : boxes) {
            byte[] dummyRT = getDummyReaction(expectedRT, box);
            this.resulter.getPressedKeys(1, box, 1);
            this.resulter.getReactionTime(dummyRT, box, 1);
            Assertions.assertEquals(expectedRT,
                                    this.resulter.getReacTime()[box][1],
                                    "Recorded RT should equal " + expectedRT);
        }
    }
}
