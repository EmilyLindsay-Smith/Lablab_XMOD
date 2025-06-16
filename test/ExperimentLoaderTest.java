package xmod.experimenter;

import xmod.status.ObjectReport;
import xmod.constants.Actions;
import xmod.utils.TestListener;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.api.Test;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

public class ExperimentLoaderTest {

    @DisplayName("Load valid files")
    @ParameterizedTest
    @ValueSource(strings = {"./test/testFiles/charlie_short.tms"})
    public void loadValidFiles(String file) {
        try {
            ExperimentLoader e = new ExperimentLoader();
            TestListener tester = new TestListener();
            e.addObserver(tester);
            Boolean loaded = e.loadFile(file);
            Assertions.assertEquals(true, loaded, "Valid file was not loaded");
            Assertions.assertEquals(false, tester.checkUpdate(),
                 "ExperimentLoader sent update unexpectedly");
        } catch (Exception exc){
            Assertions.assertEquals(1, 0, "File not loaded for test");
        }
     }

    @DisplayName("Fail to load invalid files")
    @ParameterizedTest
    @ValueSource(strings = {"./test/testFiles/charlie_short.txt",
                            "./test/testFiles/charlie_shorts.tms"})
    public void loadValidFiles2(String file) {
        try {
            ExperimentLoader e = new ExperimentLoader();
            TestListener tester = new TestListener();
            e.addObserver(tester);
            Boolean loaded = e.loadFile(file);
            Assertions.assertEquals(false, loaded, "Invalid file was loaded");
            Assertions.assertEquals(true, tester.checkUpdate(),
                 "ExperimentLoader should have sent an update");
        } catch (Exception exc){
            Assertions.assertEquals(1, 1, "Invalid file not loaded for test");
        }
    }

}
