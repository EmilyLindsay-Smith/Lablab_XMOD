package xmod.status;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import static org.junit.Assert.assertNotEquals;
import org.junit.jupiter.params.ParameterizedTest;
//import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.api.Test;

import java.util.stream.Stream;
import java.util.*;

public class ObjectReportTest {
    ObjectReport objectReport;
    ReportLabel objectReportName = ReportLabel.STATUS;

    @BeforeEach
    void makeObjectReport() {
        objectReport = new ObjectReport(objectReportName);
    }

    @DisplayName("Check ObjectReport name")
    @Test
    public void test_objectReportName() {
        Assertions.assertEquals(true,
            this.objectReport.getName() == this.objectReportName.getValue(),
            "ObjectReport name should equal "
            + this.objectReportName.getValue());
    };

    @DisplayName("Initialise ObjectReport")
    @Test
    public void test_objectReportInitialised() {
        Assertions.assertEquals(false, this.objectReport.report == null,
        "ObjectReport should not be null");
    };

    @DisplayName("Check report not empty")
    @Test
    public void test_objectReportInitialised2() {
        Assertions.assertEquals(false, this.objectReport.report.isEmpty(),
        "ObjectReport should not be empty");
    };

    @DisplayName("Check correct number of keys")
    @Test
    public void test_correctNumberOfKeys() {
        int numKeys = ReportCategory.values().length;
        Assertions.assertEquals(numKeys, this.objectReport.report.size(),
        "ObjectReport should have " + numKeys + " keys");
    }


    private static Stream<Arguments> objectReportInitialValues() {
        return Stream.of(
            Arguments.of(ReportCategory.STATUS),
            Arguments.of(ReportCategory.MESSAGE),
            Arguments.of(ReportCategory.ADVICE),
            Arguments.of(ReportCategory.STACKTRACE)
        );
    }

    @DisplayName("Check correct initial entries")
    @ParameterizedTest
    @MethodSource("objectReportInitialValues")
    public void test_initialEntries(ReportCategory key) {
        ArrayList<String> realValues = this.objectReport.get(key);
        Assertions.assertEquals(0, realValues.size(),
         "there should no items in the values");
    }

    private static Stream<Arguments> updatingObjectReportValues1() {
        return Stream.of(
            Arguments.of(ReportCategory.STATUS, "DISCONNECTED"),
            Arguments.of(ReportCategory.MESSAGE,
                "Disconnected from control box"),
            Arguments.of(ReportCategory.ADVICE,
                "Please reconnect then click the CHECK CONNECTION button"),
            Arguments.of(ReportCategory.STACKTRACE,
                "Lengthy stack trace here")
        );
    }

    @DisplayName("Updating previously empty values correctly")
    @ParameterizedTest
    @MethodSource("updatingObjectReportValues1")
    public void test_updatingValues2(ReportCategory category,
    String newValue) {
        this.objectReport.updateValues(category, newValue);
        ArrayList<String> realValues = this.objectReport.get(category);
        Assertions.assertEquals(newValue, realValues.get(0),
        "The new value was not corrected added");
    }

    private static Stream<Arguments> updatingObjectReportValues2() {
        return Stream.of(
            Arguments.of(ReportCategory.STATUS, "DISCONNECTED",
                "CONNECTED"),
            Arguments.of(ReportCategory.MESSAGE,
                "Disconnected from control box",
                "Connected control box"),
            Arguments.of(ReportCategory.ADVICE,
                "Connect to Control Box", ""),
            Arguments.of(ReportCategory.STACKTRACE,
                "Lengthy stack trace", "")
        );
    }

    @DisplayName("Updating previously full values correctly")
    @ParameterizedTest
    @MethodSource("updatingObjectReportValues2")
    public void test_updatingValues3(
        ReportCategory category,
        String newValue,
        String newValue2
        ) {
        //First Update
        this.objectReport.updateValues(category, newValue);
        //Second Update
        this.objectReport.updateValues(category, newValue2);
        //Get current Values
        ArrayList<String> realValues = this.objectReport.get(category);
        Assertions.assertEquals(false, 0 == realValues.size(),
        "realValues should not be empty");
        Assertions.assertEquals(newValue, realValues.get(0),
        "The first new value should not be overwritten");
        Assertions.assertEquals(newValue2, realValues.get(1),
        "The second new value should be at index 2");
    }

    @DisplayName("Updating previously full values correctly with clearing")
    @ParameterizedTest
    @MethodSource("updatingObjectReportValues2")
    public void test_updatingValues4(
        ReportCategory category,
        String newValue,
        String newValue2
        ) {
        //First Update
        this.objectReport.updateValues(category, newValue);
        //Second Update
        this.objectReport.clearValues(category);
        this.objectReport.updateValues(category, newValue2);
        //Get current Values
        ArrayList<String> realValues = this.objectReport.get(category);
        Assertions.assertEquals(false, 0 == realValues.size(),
        "realValues should not be empty");
        Assertions.assertEquals(false, newValue == realValues.get(0),
        "The first new value should be overwritten");
        Assertions.assertEquals(newValue2, realValues.get(0),
        "The second new value should be at index 1");
    }


    @DisplayName("Clearing previously present values correctly")
    @ParameterizedTest
    @MethodSource("updatingObjectReportValues1")
    public void test_clearingValues(ReportCategory category,
    String newValue) {
        this.objectReport.updateValues(category, newValue);
        ArrayList<String> realValues = this.objectReport.get(category);
        this.objectReport.clearValues(category);
        ArrayList<String> realValues2 = this.objectReport.get(category);
        Assertions.assertEquals(0, realValues.size(),
         "there should no items in the values");
    }

    @DisplayName("Checking string output")
    @Test
    public void test_conversionToString() {
        String update = "Experiment Aborted";
        this.objectReport.updateValues(ReportCategory.STATUS,
                                        update);
        String output = this.objectReport.toString();

        String expectedOutput =  "<span style="
                                + "\"display:inline-block;"
                                + "margin-left:40px;\">"
                                + "<u>"
                                + "Status"
                                + ":</u></span><br/>";
        expectedOutput += "<p style="
                        + "\"display:inline-block;"
                        + "margin-left:40px;\">"
                        + update
                        + "<br/><br/>";
        expectedOutput += "</p>";

        Assertions.assertEquals(expectedOutput, output,
        "The conversion to string is not correct");
    }
}
