package xmod.experimenter;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;
import java.nio.file.Files;
import java.util.List;

import java.io.IOException;

class ExperimentLoaderFurtherTest {
    // Tests for Parsing Files Correctly
        /** Experiment loader object. */
        private ExperimentLoader e;
        /** TMS file used for tests. */
        private String testFile = "./test/testFiles/charlie_short.tms";

        @BeforeEach
        void parseFile() {
            e = new ExperimentLoader();
            try {
                Boolean loaded = e.loadFile(testFile);
                e.parseFile();
                Assertions.assertEquals(true, loaded,
                     "Valid file was not loaded");
            } catch (Exception exc) {
            Assertions.assertEquals(1, 0, "File not loaded for test");
            }
         }


        @DisplayName("Check Arrays")
        @Test
        public void checkArrays() {
            int arrSize = 0;
            try {
                List<String> fStream = Files.readAllLines(
                                                Paths.get(this.testFile));
                arrSize = (int) (fStream.size() - 2) / 2;
                if (arrSize / 2 == 0) {
                    arrSize = arrSize - 1;
                }
            } catch (IOException exc) {
                Assertions.assertEquals(false, true,
                "Could not read testfile needed to calculate number of lines");
            }

            if (e.getTMSLoaded()) {
                Assertions.assertEquals(arrSize, e.getTReactionTimeout().length,
                    "e.tReactionTimeout is wrong size");
                Assertions.assertEquals(arrSize, e.getTReactionOffset().length,
                    "e.tReactionOffset is wrong size");
                Assertions.assertEquals(arrSize, e.getTMonitorOn().length,
                    "e.tMonitorOn is wrong size");
                Assertions.assertEquals(arrSize, e.getTMonitorOff().length,
                    "e.tMonitorOff is wrong size");
                Assertions.assertEquals(arrSize, e.getCodingArray().length,
                    "e.codingArray is wrong size");
                Assertions.assertEquals(arrSize, e.getScreenItems().length,
                    "e.screenItems is wrong size");
            } else {
                Assertions.assertEquals(true, e.getTMSLoaded(),
                    "parseFile() not working correctly");
            }
        }

        @DisplayName("Check Extracted Info")
        @Test
        public void checkExtractedInfo() {
            if (e.getTMSLoaded()) {
            String[] expectedItems = {"5", "4", "3", "2", "1", "darsh",
                         "unskilled", "scarble", "perfume", "unfixed"};
            String[] screenItems = e.getScreenItems();
            Assertions.assertEquals(expectedItems.length, screenItems.length,
                 "Wrong number of expected items");
            for (int i = 0; i < expectedItems.length; i++) {
                Assertions.assertEquals(expectedItems[i], screenItems[i],
                    "Wrong expected item in screenItems");
            }
            } else {
                Assertions.assertEquals(true, e.getTMSLoaded(),
                    "parseFile() not working correctly");
            }
        }


}
