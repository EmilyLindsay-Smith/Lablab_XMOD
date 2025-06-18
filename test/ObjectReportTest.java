package xmod.status;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
//import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.api.Test;

import java.util.stream.Stream;
import java.util.ArrayList;

public final class ObjectReportTest {
    /** ObjectReport object. */
    private ObjectReport objectReport;
    /** Name for objectReport. */
    private ReportLabel objectReportName = ReportLabel.STATUS;

    /**Instantiate an object report for each test. */
    @BeforeEach
    void makeObjectReport() {
        objectReport = new ObjectReport(objectReportName);
    }

    /** Check ObjectReport name is correct. */
    @DisplayName("Check ObjectReport name")
    @Test
    public void testObjectReportName() {
        Assertions.assertEquals(true,
            this.objectReport.getName() == this.objectReportName.getValue(),
            "ObjectReport name should equal "
            + this.objectReportName.getValue());
    };

    /** Check ObjectReport is initialised. */
    @DisplayName("Initialise ObjectReport")
    @Test
    public void testObjectReportInitialised() {
        Assertions.assertEquals(false, this.objectReport == null,
        "ObjectReport should not be null");
    };

    /** Check ObjectReport has some default contents. */
    @DisplayName("Check report not empty")
    @Test
    public void testObjectReportInitialised2() {
        Assertions.assertEquals(true, this.objectReport.isEmpty(),
        "ObjectReport should not be empty");
    };

    /** Check ObjectReport has correct number of keys. */
    @DisplayName("Check correct number of keys")
    @Test
    public void testCorrectNumberOfKeys() {
        int numKeys = ReportCategory.values().length;
        Assertions.assertEquals(numKeys, this.objectReport.size(),
        "ObjectReport should have " + numKeys + " keys");
    }

    /** Set of report category options.
     * @return item from the stream
     */
    private static Stream<Arguments> objectReportInitialValues() {
        return Stream.of(
            Arguments.of(ReportCategory.STATUS),
            Arguments.of(ReportCategory.MESSAGE),
            Arguments.of(ReportCategory.ADVICE),
            Arguments.of(ReportCategory.STACKTRACE)
        );
    }

    /** Check ObjectReport has correct initialise entries.
     * @param key item from objectReportInitialValues stream
     */
    @DisplayName("Check correct initial entries")
    @ParameterizedTest
    @MethodSource("objectReportInitialValues")
    public void testInitialEntries(final ReportCategory key) {
        ArrayList<String> realValues = this.objectReport.get(key);
        Assertions.assertEquals(0, realValues.size(),
         "there should no items in the values");
    }

    /** Test variables.
     * @return item from the stream
     */
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

    /** Check ObjectReport updates correctly.
     * @param category item[0] from updatingObjectReportValues1 stream
     * @param newValue item[1] from updatingObjectReportValues1 stream
     */
    @DisplayName("Updating previously empty values correctly")
    @ParameterizedTest
    @MethodSource("updatingObjectReportValues1")
    public void testUpdatingValues2(final ReportCategory category,
    final String newValue) {
        this.objectReport.updateValues(category, newValue);
        ArrayList<String> realValues = this.objectReport.get(category);
        Assertions.assertEquals(newValue, realValues.get(0),
        "The new value was not corrected added");
    }

    /** Test variables.
    * @return item from the stream
    */
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

    /** Check ObjectReport updates correctly.
     * @param category item[0] from updatingObjectReportValues2 stream
     * @param newValue item[1] from updatingObjectReportValues2 stream
     * @param newValue2 item[2] from updatingObjectReportValues2 stream
    */
    @DisplayName("Updating previously full values correctly")
    @ParameterizedTest
    @MethodSource("updatingObjectReportValues2")
    public void testUpdatingValues3(
        final ReportCategory category,
        final String newValue,
        final String newValue2
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

    /** Check ObjectReport updates correctly.
     * @param category item[0] from updatingObjectReportValues2 stream
     * @param newValue item[1] from updatingObjectReportValues2 stream
     * @param newValue2 item[2] from updatingObjectReportValues2 stream
     */
    @DisplayName("Updating previously full values correctly with clearing")
    @ParameterizedTest
    @MethodSource("updatingObjectReportValues2")
    public void testUpdatingValues4(
        final ReportCategory category,
        final String newValue,
        final String newValue2
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

    /** Check ObjectReport clears correctly.
     * @param category item[0] from updatingObjectReportValues1 stream
     * @param newValue item[1] from updatingObjectReportValues1 stream
    */
    @DisplayName("Clearing previously present values correctly")
    @ParameterizedTest
    @MethodSource("updatingObjectReportValues1")
    public void testClearingValues(final ReportCategory category,
    final String newValue) {
        this.objectReport.updateValues(category, newValue);
        ArrayList<String> realValues = this.objectReport.get(category);
        this.objectReport.clearValues(category);
        ArrayList<String> realValues2 = this.objectReport.get(category);
        Assertions.assertEquals(0, realValues.size(),
         "there should no items in the values");
    }

    /** Check ObjectReport conversion to string. */
    @DisplayName("Checking string output")
    @Test
    public void testConversionToString() {
        String update = "Experiment Aborted";
        this.objectReport.updateValues(ReportCategory.STATUS,
                                        update);
        String output = this.objectReport.toString();

        String expectedOutput = ""; /* "<span style="
                                + "\"display:inline-block;"
                                + "margin-left:40px;\">"
                                + "<u>"
                                + "Status"
                                + ":</u></span><br/>";
                                */
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
