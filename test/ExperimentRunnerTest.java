package xmod.experimenter;

import xmod.serial.Serial;
import xmod.status.ObjectReport;
import xmod.status.ReportCategory;
import xmod.status.Responses;
import xmod.view.ExperimentWindow;

import xmod.utils.TestListener;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public final class ExperimentRunnerTest {
    /** Test ensuring valid file is loaded.
     * @param file file to load
     */
    @DisplayName("Load valid files")
    @ParameterizedTest
    @ValueSource(strings = {"./test/testFiles/charlie_short.tms"})
    public void loadValidFiles(final String file) {
        try {
            Serial serial = new Serial();
            ExperimentWindow expWindow = new ExperimentWindow();
            ExperimentRunner runner = new ExperimentRunner(serial, expWindow);
            TestListener tester = new TestListener();
            runner.addObserver(tester);
            runner.setUpExperiment(file);
            Assertions.assertEquals(true, tester.checkUpdate(),
                 "ExperimentRunner should have sent update");
            ObjectReport report = tester.getUpdateReport();
            String status = report.get(ReportCategory.STATUS).get(0);
            Assertions.assertEquals(Responses.FILE_LOAD_SUCCESS + file, status,
            "status should denote file load success");
        } catch (Exception exc) {
            Assertions.assertEquals(1, 0, "File not loaded for test");
        }
     }

    /** Test ensuring invalid file is not loaded.
     * @param file file to load
     */
    @DisplayName("Fail to load invalid files")
    @ParameterizedTest
    @ValueSource(strings = {"./test/testFiles/charlie_short.txt",
                            "./test/testFiles/charlie_shorts.tms"})
    public void loadValidFiles2(final String file) {
        try {
            Serial serial = new Serial();
            ExperimentWindow expWindow = new ExperimentWindow();
            ExperimentRunner runner = new ExperimentRunner(serial, expWindow);
            TestListener tester = new TestListener();
            runner.addObserver(tester);
            runner.setUpExperiment(file);
            Assertions.assertEquals(true, tester.checkUpdate(),
                 "ExperimentRunner should have sent update");

            ObjectReport report = tester.getUpdateReport();
            String status = report.get(ReportCategory.STATUS).get(0);
            Assertions.assertEquals(Responses.FILE_LOAD_FAILURE + file, status,
            "status should denote file load failure");
        } catch (Exception exc) {
            Assertions.assertEquals(1, 1, "Invalid file not loaded for test");
        }
    }
}
