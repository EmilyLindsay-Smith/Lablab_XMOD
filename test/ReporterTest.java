package xmod.status;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.api.Test;

import java.util.stream.Stream;
import java.util.*;

public class ReporterTest{
    Reporter reporter;

    @BeforeEach
    void makeReporter(){
        reporter = new Reporter();
    }

    @DisplayName("Initialise Reporter")
    @Test
    public void test_reporterInitialised(){
        Assertions.assertEquals(false, this.reporter.status == null, "Reporter should not be null"); 
    };

    @DisplayName("Check status not empty")
    @Test
    public void test_reporterInitialised2(){
        Assertions.assertEquals(false, this.reporter.status.isEmpty(), "Reporter should not be empty"); 
    };

    @DisplayName("Check correct number of keys")
    @Test
    public void test_correctNumberOfKeys(){
        Assertions.assertEquals(6, this.reporter.status.size(), "Reporter should have 6 keys"); 
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
    public void test_initialEntries(String key, String value){
        ArrayList<String> realValues = this.reporter.status.get(key);
        Assertions.assertEquals(1, realValues.size(), "there should only be one item in the values");
        String realValue = realValues.get(0);
        Assertions.assertEquals(value, realValue, "Expected value and real value do not match");
    }

    @DisplayName("Check correct empty initial entries")
    @ParameterizedTest
    @ValueSource(strings = {"Connection", "Font", "Monitors"})
    public void test_emptyInitialEntries(String key){
        ArrayList<String> realValues = this.reporter.status.get(key);
        Assertions.assertEquals(0, realValues.size(), "There should be no initial values");
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
    public void test_updatingValues(String category, String newValue){
        this.reporter.updateValues(category, newValue, false);
        ArrayList<String> realValues = this.reporter.status.get(category);
        Assertions.assertEquals(newValue, realValues.get(0), "The new value was not corrected added");
    }

    @DisplayName("Updating previously empty values correctly - replacing")
    @ParameterizedTest
    @MethodSource("updatingReporterValues1")
    public void test_updatingValues2(String category, String newValue){
        this.reporter.updateValues(category, newValue, true);
        ArrayList<String> realValues = this.reporter.status.get(category);
        Assertions.assertEquals(newValue, realValues.get(0), "The new value was not corrected added");
    }

    private static Stream<Arguments> updatingReporterValues2(){
        return Stream.of(
            Arguments.of(ReportLabel.TMS, Responses.FILE_LOAD_SUCCESS),
            Arguments.of(ReportLabel.AUDIO, Responses.FILE_LOAD_FAILURE)      
        );
    }

    @DisplayName("Updating previously present values correctly - adding")
    @ParameterizedTest
    @MethodSource("updatingReporterValues2")
    public void test_updatingValues3(String category, String newValue){
        String originalValue = this.reporter.status.get(category).get(0);
        this.reporter.updateValues(category, newValue, false);
        ArrayList<String> realValues = this.reporter.status.get(category);
        Assertions.assertEquals(originalValue, realValues.get(0), "The original value was not preserved");
        Assertions.assertEquals(newValue, realValues.get(1), "The new value was not corrected added");
    }

    @DisplayName("Updating previously present values correctly - replacing")
    @ParameterizedTest
    @MethodSource("updatingReporterValues2")
    public void test_updatingValues4(String category, String newValue){
        String originalValue = this.reporter.status.get(category).get(0);
        this.reporter.updateValues(category, newValue, true);
        ArrayList<String> realValues = this.reporter.status.get(category);
        Assertions.assertEquals(newValue, realValues.get(0), "The new value was not corrected added");
    }

    @DisplayName("Checking not present categories")
    @ParameterizedTest
    @ValueSource(strings={"AUDIO", "AuDio", "Tms", "tms", "", "Test"})
    public void test_absentCategories(String category){
        String newValue = "testValue";
        this.reporter.updateValues(category, newValue, true);
        Boolean categoryPresent = this.reporter.status.containsKey(category);
        Assertions.assertEquals(false, categoryPresent, "The category should not be present");
    }

    @DisplayName("Checking string output")
    @Test
    public void test_conversionToString(){
        String output = this.reporter.convertToString();

        String expectedOutput  = "<span style=\"font-weight:bold\">"+ ReportLabel.STATUS + "</span><br/>";
        expectedOutput = expectedOutput + "<p style=\"display:inline-block;margin-left:40px;\">";
        expectedOutput = expectedOutput + Responses.WELCOME + "<br/><br/>";
        expectedOutput = expectedOutput + "</p>";
        expectedOutput = expectedOutput +"<span style=\"font-weight:bold\">"+ ReportLabel.TMS + "</span><br/>";
        expectedOutput = expectedOutput + "<p style=\"display:inline-block;margin-left:40px;\">";
        expectedOutput = expectedOutput + Responses.NO_FILE_SELECTED + "<br/><br/>";
        expectedOutput = expectedOutput + "</p>";
        expectedOutput = expectedOutput +"<span style=\"font-weight:bold\">"+ ReportLabel.AUDIO + "</span><br/>";
        expectedOutput = expectedOutput + "<p style=\"display:inline-block;margin-left:40px;\">";
        expectedOutput = expectedOutput + Responses.NO_FILE_SELECTED + "<br/><br/>";
        expectedOutput = expectedOutput + "</p>";   
        Assertions.assertEquals(expectedOutput, output, "The conversion to string is not correct");
                
    }

}