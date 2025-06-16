package xmod.experimenter;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ExperimentLoaderFurtherTest {
    // Tests for Parsing Files Correctly
        private ExperimentLoader e;

        @BeforeEach
        void parseFile() {
            String file = "./test/testFiles/charlie_short.tms";
            e = new ExperimentLoader();
            try {
                Boolean loaded = e.loadFile(file);
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
            //Note difference from getarraySize() due to getTMSInfo() code
            int arraySize = (int) Math.floor((e.tmsFileLines.size() + 1 ) / 2);
            if (arraySize / 2 == 0) {
                arraySize = arraySize - 1;
            }
            if (e.tmsLoaded) {
                Assertions.assertEquals(arraySize, e.tReactionTimeout.length,
                    "e.tReactionTimeout is wrong size");
                Assertions.assertEquals(arraySize, e.tReactionOffset.length,
                    "e.tReactionOffset is wrong size");
                Assertions.assertEquals(arraySize, e.tMonitorOn.length,
                    "e.tMonitorOn is wrong size");
                Assertions.assertEquals(arraySize, e.tMonitorOff.length,
                    "e.tMonitorOff is wrong size");
                Assertions.assertEquals(arraySize, e.codingArray.length,
                    "e.codingArray is wrong size");
                Assertions.assertEquals(arraySize, e.screenItems.length,
                    "e.screenItems is wrong size");
            } else {
                Assertions.assertEquals(true, e.tmsLoaded,
                    "parseFile() not working correctly");
            }
        }

        @DisplayName("Check Extracted Info")
        @Test
        public void checkExtractedInfo() {
            if (e.tmsLoaded) {
            String[] expectedItems = {"5", "4", "3", "2", "1", "darsh",
                         "unskilled", "scarble", "perfume", "unfixed"};
            Assertions.assertEquals(expectedItems.length, e.screenItems.length,
                 "Wrong number of expected items");
            for (int i = 0; i < expectedItems.length; i++) {
                Assertions.assertEquals(expectedItems[i], e.screenItems[i],
                    "Wrong expected item in screenItems");
            }
            } else {
                Assertions.assertEquals(true, e.tmsLoaded,
                    "parseFile() not working correctly");
            }
        }


}
