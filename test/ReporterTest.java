package xmod.status;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;

import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.api.Test;

import java.util.stream.Stream;
import java.util.*;


import xmod.status.ObjectReport;
import xmod.status.ReportCategory;
import xmod.status.ReportLabel;


public class ReporterTest{
    Reporter reporter;

    @BeforeEach
    void makeReporter(){
        reporter = new Reporter();
    }

    @DisplayName("Initialise Reporter")
    @Test
    public void test_reporterInitialised(){
        Assertions.assertEquals(false, this.reporter == null,
         "Reporter should not be null");
    };

    @DisplayName("Check status not empty")
    @Test
    public void test_reporterInitialised2(){
        Assertions.assertEquals(false, this.reporter.isEmpty(),
         "Reporter should not be empty");
    };

    @DisplayName("Check correct number of keys")
    @Test
    public void test_correctNumberOfKeys(){
        int numKeys = ReportLabel.values().length;
        Assertions.assertEquals(numKeys, this.reporter.size(),
         "Reporter should have " + numKeys + " keys");
    }

  private static Stream<Arguments> reporterInitialValues(){
        return Stream.of(
            Arguments.of(ReportLabel.STATUS, Responses.WELCOME),
            Arguments.of(ReportLabel.TMS, Responses.NO_FILE_SELECTED),
            Arguments.of(ReportLabel.AUDIO, Responses.NO_FILE_SELECTED)
        );
    }

    @DisplayName("Check correct initial entries")
    @ParameterizedTest
    @MethodSource("reporterInitialValues")
    public void test_initialEntries(ReportLabel key, String value){
        ArrayList<String> realValues = this.reporter.get(key)
                                        .get(ReportCategory.MESSAGE);
        Assertions.assertEquals(1, realValues.size(),
                                "there should only be one item in the values");
        String realValue = realValues.get(0);
        Assertions.assertEquals(value, realValue,
                                    "Expected value and real value do not match");
    }


    @DisplayName("Check correct empty initial entries")
    @ParameterizedTest
    @EnumSource(value = ReportLabel.class,
                names = {"CONNECTION", "FONT", "MONITORS"})
    public void test_emptyInitialEntries(ReportLabel key){
        //ObjectReport realValues = this.reporter.get(key);
        for(Map.Entry<ReportCategory, ArrayList<String>> e:
            this.reporter.get(key).entrySet()) {
                ArrayList<String> value = e.getValue(); // get values
                Assertions.assertEquals(0, value.size(),
                "There should be no initial values");
            }
    }

    private static Stream<Arguments> updatingReporterValues1(){
        return Stream.of(
            Arguments.of(ReportLabel.CONNECTION, Responses.SERIAL_UNCONNECTED),
            Arguments.of(ReportLabel.FONT, "Font changed to Helvetica"),
            Arguments.of(ReportLabel.MONITORS, Responses.MONITORS_ON)

        );
    }

    @DisplayName("Updating previously empty values correctly - adding")
    @ParameterizedTest
    @MethodSource("updatingReporterValues1")
    public void test_updatingValues(ReportLabel category, String newValue){
        //Create objectreport for new data
        ObjectReport reportUpdate = new ObjectReport(category);
        reportUpdate.updateValues(ReportCategory.STATUS, newValue);

        //Add to this.reporter
        this.reporter.updateValues(category, reportUpdate);
        //Check what is added
        ArrayList<String> realValues = this.reporter.get(category)
                                            .get(ReportCategory.STATUS);
        Assertions.assertEquals(newValue, realValues.get(0),
        "The new value was not corrected added");
    }

    @DisplayName("Updating previously full values correctly - no duplication")
    @ParameterizedTest
    @MethodSource("updatingReporterValues1")
    public void test_updatingValues2(ReportLabel category, String newValue){
        //Create objectreport for new data
        ObjectReport reportUpdate = new ObjectReport(category);
        reportUpdate.updateValues(ReportCategory.STATUS, newValue);

        //Add to this.reporter twice
        this.reporter.updateValues(category, reportUpdate);
        this.reporter.updateValues(category, reportUpdate);

        //Check what is added
        ArrayList<String> realValues = this.reporter.get(category)
                                            .get(ReportCategory.STATUS);
        Assertions.assertEquals(newValue, realValues.get(0),
        "The new value was not corrected added");
        Assertions.assertEquals(1, realValues.size(),
        "The newValue should only have one item in it");
    }

    @DisplayName("Updating previously full values correctly with second message")
    @ParameterizedTest
    @MethodSource("updatingReporterValues1")
    public void test_updatingValues3(ReportLabel category, String newValue){
        //Create objectreport for new data
        ObjectReport reportUpdate = new ObjectReport(category);
        reportUpdate.updateValues(ReportCategory.MESSAGE, newValue);
        ObjectReport reportUpdate2 = new ObjectReport(category);
        reportUpdate2.updateValues(ReportCategory.MESSAGE, "second message");

        //Add to this.reporter twice
        this.reporter.updateValues(category, reportUpdate);
        this.reporter.updateValues(category, reportUpdate2);

        //Check what is added
        ArrayList<String> realValues = this.reporter.get(category)
                                            .get(ReportCategory.MESSAGE);
        Assertions.assertEquals(2, realValues.size(),
            "The newValue should have two items in it");
        Assertions.assertEquals(newValue, realValues.get(0),
            "The first new value was not corrected added");
        Assertions.assertEquals("second message", realValues.get(1),
            "The second new value was not corrected added");
    }

    @DisplayName("Updating full values correctly with status change")
    @ParameterizedTest
    @MethodSource("updatingReporterValues1")
    public void test_updatingValues4(ReportLabel category, String newValue){
        System.out.println("TEST OF INTEREST");
        //Create objectreport for new data
        ObjectReport reportUpdate = new ObjectReport(category);
        reportUpdate.updateValues(ReportCategory.STATUS, "first status");
        reportUpdate.updateValues(ReportCategory.MESSAGE, newValue);
        ObjectReport reportUpdate2 = new ObjectReport(category);
        reportUpdate2.updateValues(ReportCategory.STATUS, "second status");
        reportUpdate2.updateValues(ReportCategory.MESSAGE, "second message");

        //Add to this.reporter twice
        this.reporter.updateValues(category, reportUpdate);
        this.reporter.updateValues(category, reportUpdate2);

        //Check what is added
        ArrayList<String> realValues = this.reporter.get(category)
                                            .get(ReportCategory.MESSAGE);
        Assertions.assertEquals(1, realValues.size(),
            "The newValue should have one item in it");
        Assertions.assertEquals("second message", realValues.get(0),
            "The second new value was not corrected added");
    }

    @DisplayName("Updating full values correctly without status change")
    @ParameterizedTest
    @MethodSource("updatingReporterValues1")
    public void test_updatingValue5(ReportLabel category, String newValue){
        //Create objectreport for new data
        ObjectReport reportUpdate = new ObjectReport(category);
        reportUpdate.updateValues(ReportCategory.STATUS, "first status");
        reportUpdate.updateValues(ReportCategory.MESSAGE, newValue);
        ObjectReport reportUpdate2 = new ObjectReport(category);
        reportUpdate2.updateValues(ReportCategory.STATUS, "first status");
        reportUpdate2.updateValues(ReportCategory.MESSAGE, "second message");

        //Add to this.reporter twice
        this.reporter.updateValues(category, reportUpdate);
        this.reporter.updateValues(category, reportUpdate2);

        //Check what is added
        ArrayList<String> realValues = this.reporter.get(category)
                                            .get(ReportCategory.MESSAGE);
        Assertions.assertEquals(2, realValues.size(),
            "The newValue should have two items in it");
        Assertions.assertEquals(newValue, realValues.get(0),
            "The first new value was not corrected added");
        Assertions.assertEquals("second message", realValues.get(1),
            "The second new value was not corrected added");
    }

}
