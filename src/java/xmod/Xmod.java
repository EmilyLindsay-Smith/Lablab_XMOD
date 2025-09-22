package xmod;

import xmod.audio.AudioPlayer;
import xmod.view.FontWindow;
import xmod.view.ExperimentWindow;
import xmod.view.MainWindow;

import xmod.constants.Actions;
import xmod.constants.Operations;

import xmod.experimenter.ExperimentRunner;

import xmod.serial.Serial;

import xmod.utils.Utils;

import xmod.status.ObjectReport;
import xmod.status.Reporter;
import xmod.status.ReportCategory;
import xmod.status.ReportLabel;
import xmod.status.Responses;

import java.awt.EventQueue;
import javax.swing.SwingUtilities;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

/** Xmod is the controller class for the whole application.
 *
 * @author ELS
 * @version 2.1
 * @since 2025-09-16
 */

public class Xmod implements PropertyChangeListener {
    //Windows
    /** Main GUI Window. */
    private MainWindow mainWindow;
    /** Experiment GUI Window. */
    private ExperimentWindow expWindow;
    /** Font GUI Window. */
    private FontWindow fontWindow;
    // Objects.
    /** Reporter to manage status. */
    private Reporter reporter;
    /** SerialPort for communication to Controller Box. */
    private Serial serialPort;
    /** ExperimentRunner to manage experiment. */
    private ExperimentRunner experimentRunner;
    /** AudioPlayer to play audio file. */
    private AudioPlayer audioPlayer;
     /**
     * Constructor.
     * @param aMainWindow MainWindow object
     * @param aExpWindow ExpWindow object
     * @param aFontWindow FontWindow object
     * @param aReporter Reporter Object
     * @param aSerialPort Serial Object
     * @param expRunner ExperimentRunner Object
     * @param aAudioPlayer AudioPLayer Object
     */
    Xmod(final MainWindow aMainWindow,
        final ExperimentWindow aExpWindow,
        final FontWindow aFontWindow,
        final Reporter aReporter,
        final Serial aSerialPort,
        final ExperimentRunner expRunner,
        final AudioPlayer aAudioPlayer) {

        //Initialise window variables
        this.mainWindow = aMainWindow;
        this.expWindow = aExpWindow;
        this.fontWindow = aFontWindow;
        // Initialise Objects
        this.reporter = aReporter;
        this.serialPort = aSerialPort;
        this.experimentRunner = expRunner;
        this.audioPlayer = aAudioPlayer;
        // Set MainWindow Report
        updateWindowText();
    }

     /** Main function to run xmod.
      * @param args default for main main
      */
    public static void main(final String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                // Instantiate GUI Windows
                MainWindow mainWindow = new MainWindow();
                ExperimentWindow expWindow = new ExperimentWindow();
                FontWindow fontWindow = new FontWindow();
                //Instantiate Objects
                Reporter reporter = new Reporter();
                Serial serialPort = new Serial();
                AudioPlayer audioPlayer = new AudioPlayer();
                ExperimentRunner experimentRunner = new ExperimentRunner(
                                                        serialPort,
                                                        expWindow,
                                                        audioPlayer);

                Xmod xmod = new Xmod(mainWindow, expWindow, fontWindow,
                                reporter, serialPort, experimentRunner,
                                audioPlayer);
                // Add observers to respond to buttons/key strokes/error reports
                mainWindow.addObserver(xmod);
                expWindow.addObserver(xmod);
                fontWindow.addObserver(xmod);
                serialPort.addObserver(xmod);
                experimentRunner.addObserver(xmod);
                audioPlayer.addObserver(xmod);
                // Show main Window
                mainWindow.show();
            }
        });
    }

    /**
     * Handles actions on receiving a property change event.
     * @param evt PropertyChangeEvent sent by the pcs
     */
    public void propertyChange(final PropertyChangeEvent evt) {
        String actionType = (String) evt.getPropertyName();
        if (actionType == Actions.OPERATION) {
            String action = (String) evt.getNewValue();
            switch (action) {
                case Operations.RUN_EXP:
                    operationRunExp(); break;
                case Operations.LOAD_TMS:
                    operationLoadTMS(); break;
                case Operations.MONITOR_ON:
                    operationMonitorOn(); break;
                case Operations.MONITOR_OFF:
                    operationMonitorOff(); break;
                case Operations.CHECK_CONNECTION:
                    operationCheckConnection(); break;
                case Operations.CONTROLLER_INFO:
                    operationControllerInfo(); break;
                case Operations.CHECK_FONT:
                    operationCheckFont(); break;
                case Operations.CLOSE_XMOD:
                    operationCloseXmod();
                case Operations.TEST:
                    operationTestSystem();
                default: break;
           }
        } else if (actionType == Actions.UPDATE) {
            // Send the ObjectReport to reporter to update the status
            ObjectReport report = (ObjectReport) evt.getNewValue();
            updateStatus(report);
            updateWindowText();
        } else if (actionType == Actions.UPDATE_FONT) {
            updateFont();
        } else if (actionType == Actions.ABORT_EXPERIMENT) {
            abortExperiment();
        }
    }

    /** Calls the file chooser and the the loadTMSFile method. */
    private void operationLoadTMS() {
        String filename = mainWindow.chooseFile();
        loadTMSFile(filename);
    };

    /** Loads TMS file.
     * @param filename name of tms file
     */
    private void loadTMSFile(final String filename) {
        // If no file selected
        if (filename == Responses.NO_FILE_SELECTED) {
            updateStatus(createReport(ReportLabel.TMS,
                        Responses.NO_FILE_SELECTED, "",
                        "Please select a .tms file to upload", ""));
            updateWindowText();
            return;
        }
        //If file selected
        this.experimentRunner.setUpExperiment(filename);
        // Check loaded
        if (this.experimentRunner.isExperimentLoaded()) {
            lookForWavFile(filename);
        }
        checkExperimentReady();
    }

    /** Looks for wav file to go with tms file.
     * @param filename name of tms file
     * calls loadAudio to load the audio file
     */
    private void lookForWavFile(final String filename) {
        String wavFile = Utils.getWavFromTMS(filename);
        if (wavFile == "") {
            ObjectReport report = createReport(
                ReportLabel.AUDIO, Responses.WAV_UNAVAILABLE,
                "Could not identify wav file name from tms file name",
                "", "");
            return;
        }
        if (!Utils.fileExists(wavFile)) {
            // if new file doesn't exist
            ObjectReport report = createReport(
                ReportLabel.AUDIO, Responses.WAV_UNAVAILABLE,
                "Please ensure .wav file is in the same folder as .tms file",
                "", "");
            updateStatus(report);
            updateWindowText();
            return;
        }
        loadAudio(wavFile);
        return;
    }

    /** Loads the audio file.
     * @param wavFile name of audio file
     */
    private void loadAudio(final String wavFile) {
        if (null != wavFile & wavFile.length() > 0) {
            this.audioPlayer.loadAudio(wavFile);
        }
        //AudioPlayer will handle sending updates to XMOD re status
        return;
    }

    private void checkExperimentReady() {
        Boolean ready = true;
        String updateAdv = "";
        ObjectReport report;
        if (!this.experimentRunner.isExperimentLoaded()) {
            updateAdv += "No experiment loaded <br/>";
            ready = false;
        }
        if (!this.audioPlayer.isAudioLoaded()) {
            updateAdv += "No audio file loaded <br/>";
            ready = false;
        }
        if (!this.serialPort.isSerialConnected()) {
            updateAdv += "No connection to controller box <br/>";
            ready = false;
        }
        if (ready) {
            report = createReport(
                ReportLabel.STATUS, Responses.EXPERIMENT_READY, "",
                "Click " + Operations.RUN_EXP + " to begin", "");
        } else {
            report = createReport(
                ReportLabel.STATUS, Responses.EXPERIMENT_NOT_READY, "",
                updateAdv, "");
        }
        this.reporter.clearValues(ReportLabel.STATUS);
        updateStatus(report);
        updateWindowText();
    }

    private void operationRunExp() {
        this.experimentRunner.runExperiment();
    };

    /** Calls serialPort to turn on the experiment monitors. */
    private void operationMonitorOn() {
        this.serialPort.turnOnMonitor();
     };

    /** Calls serialPort to turn on the experiment monitors. */
    private void operationMonitorOff() {
        this.serialPort.turnOffMonitor();
    };

    /** Checks connection to the controller box via the serial port.
     * If the connection exists, the LEDs on the box will flash three times.
     * If no connection, it will try to re-establish connection with the box.
     */
    private void operationCheckConnection() {
        this.serialPort.checkConnection();
    };

    /** Requests information about the controller box to display.
     * Wraps result in an ObjectReport and updates status to display to user.
     */
    private void operationControllerInfo() {
        String info = this.serialPort.getControllerInfo();
        if (info != "") {
            ObjectReport report = createReport(ReportLabel.CONNECTION, info,
                                                "", "", "");
            updateStatus(report);
            updateWindowText();
        }
    };

    /** Displays the fontWindow GUI. */
    private void operationCheckFont() {
        this.fontWindow.show();
        //Note fontWindow.hide() is triggered in updateFont()
    };

    /** Aborts the experiment. */
    private void abortExperiment() {
        this.experimentRunner.setRunning(false); // abort experiment runner
        ObjectReport abortReport =  createReport(ReportLabel.STATUS,
                                "Aborting experiment... please wait...",
                                "",
                                "",
                                "");
        updateStatus(abortReport);
        this.audioPlayer.stopAudio();
        this.mainWindow.show();
        this.expWindow.hide();
    }

    /** Tests the system. */
    private void operationTestSystem() {
        String testFile = "../test/testFiles/charlie_short.tms";
        loadTMSFile(testFile);
        operationRunExp();
        return;
    }

    /* ***** METHODS RELATED TO UPDATING THE WINDOWS/TEXT/FONT ************/

    /**
     * Updates the font in ExperimentWindow.
     */
    private void updateFont() {
        //Get the new font & size from fontWindow
        String newFont = this.fontWindow.getCurrentFont();
        int newSize = this.fontWindow.getCurrentSize();
        // Change the font & size for expWindow
        this.expWindow.changeFont(newFont, newSize);
        //Report the change
        ObjectReport report = createReport(ReportLabel.FONT, "Font updated",
        "New Font: " + newFont + " <br/>New font size: " + newSize + " pt",
        "", "");
        updateStatus(report);
        updateWindowText();
        // Return to the main window
        this.mainWindow.show();
        this.fontWindow.hide();
        return;
    }



     /**
     * Send updates to main Xmod.java.
     * @param reportLabel which section it is for
     * @param newStatus status
     * @param newMessage message
     * @param newAdvice advice
     * @param newStackTrace any stack trace
     * @return report
     */
    private ObjectReport createReport(final ReportLabel reportLabel,
                                final String newStatus,
                                final String newMessage,
                                final String newAdvice,
                                final String newStackTrace
                                ) {
        ObjectReport report = new ObjectReport(reportLabel);
        if (newStatus != "") {
            report.updateValues(ReportCategory.STATUS, newStatus);
        }
        if (newMessage != "") {
            report.updateValues(ReportCategory.MESSAGE, newMessage);
        }
        if (newAdvice != "") {
            report.updateValues(ReportCategory.ADVICE, newAdvice);
        }
        if (newStackTrace != "") {
            report.updateValues(ReportCategory.STACKTRACE, newStackTrace);
        }
        return report;
    }

    /**
     * Updates the Reporter.
     * @param report ObjectReport containing the updates
     */
    private void updateStatus(final ObjectReport report) {
      this.reporter.updateValues(ReportLabel.valueOf(report.getName()),
                                report);
      return;
    }

    /**
     * Updates the central text box on MainWindow.
     * Note use of EventQueue schedules task for Event Dispatch Thread
     */
    private void updateWindowText() {
        EventQueue.invokeLater(new Runnable() {
          @Override
            public void run() {
                StringBuilder mainNewText = new StringBuilder(
                                    reporter.printValues(ReportLabel.STATUS));
                mainNewText.append(reporter.printValues(ReportLabel.TMS));
                mainNewText.append(reporter.printValues(ReportLabel.AUDIO));

                String connectionNewText = reporter.printValues(
                                            ReportLabel.CONNECTION);
                String toolNewText = reporter.printValues(ReportLabel.FONT);

                mainWindow.updateMainText(mainNewText.toString());
                mainWindow.updateConnectionText(connectionNewText);
                mainWindow.updateToolText(toolNewText);
            }
        });
    }

    /* ******* METHODS RELATED TO SHUTTING DOWN THE APPLICATION ************/

   /**
     * Handles cleanup on shutting down application.
     */
    private void operationCloseXmod() {
        System.exit(0);
    }
}
