package xmod.experimenter;

import xmod.utils.TestListener;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public final class ExperimentLoaderTest {


    /** Test ensuring valid file is loaded.
     * @param file from ValueSource
     */
    @DisplayName("Load valid files")
    @ParameterizedTest
    @ValueSource(strings = {"./test/testFiles/charlie_short.tms"})
    public void loadValidFiles(final String file) {
        try {
            ExperimentLoader e = new ExperimentLoader();
            TestListener tester = new TestListener();
            e.addObserver(tester);
            Boolean loaded = e.loadFile(file);
            Assertions.assertEquals(true, loaded, "Valid file was not loaded");
            Assertions.assertEquals(false, tester.checkUpdate(),
                 "ExperimentLoader sent update unexpectedly");
        } catch (Exception exc) {
            Assertions.assertEquals(1, 0, "File not loaded for test");
        }
     }

    /** Test ensuring invalid file is not loaded.
     * @param file from ValueSource
     */

    @DisplayName("Fail to load invalid files")
    @ParameterizedTest
    @ValueSource(strings = {"./test/testFiles/charlie_short.txt",
                            "./test/testFiles/charlie_shorts.tms"})
    public void loadValidFiles2(final String file) {
        try {
            ExperimentLoader e = new ExperimentLoader();
            TestListener tester = new TestListener();
            e.addObserver(tester);
            Boolean loaded = e.loadFile(file);
            Assertions.assertEquals(false, loaded, "Invalid file was loaded");
            Assertions.assertEquals(true, tester.checkUpdate(),
                 "ExperimentLoader should have sent an update");
        } catch (Exception exc) {
            Assertions.assertEquals(1, 1, "Invalid file not loaded for test");
        }
    }

}
