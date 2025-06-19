package xmod;

import xmod.view.MainWindow;
import xmod.view.ExperimentWindow;

import xmod.constants.Actions;
import xmod.constants.Operations;

import xmod.experimenter.ExperimentRunner;

import xmod.serial.Serial;

import xmod.status.ObjectReport;
import xmod.status.Reporter;
import xmod.status.ReportCategory;
import xmod.status.ReportLabel;
import xmod.status.Responses;


import javax.swing.SwingUtilities;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

public class Xmod implements PropertyChangeListener {
    //Windows
    /** Main GUI Window. */
    private MainWindow mainWindow;
    /** Experiment GUI Window. */
    private ExperimentWindow expWindow;
    // Objects.
    /** Reporter to manage status. */
    private Reporter reporter;
    /** SerialPort for communication to Controller Box. */
    private Serial serialPort;
    /** ExperimentRunner to manage experiment. */
    private ExperimentRunner experimentRunner;
     /**
     * Constructor.
     * @param aMainWindow MainWindow object
     * @param aReporter Reporter Object
     * @param aSerialPort Serial Object
     * @param expRunner ExperimentRunner Object
     */
    Xmod(final MainWindow aMainWindow,
        final Reporter aReporter,
        final Serial aSerialPort,
        final ExperimentRunner expRunner,
        final ExperimentWindow aExpWindow) {

        //Initialise window variables
        this.mainWindow = aMainWindow;
        this.expWindow = aExpWindow;
        // Initialise Objects
        this.reporter = aReporter;
        this.serialPort = aSerialPort;
        this.experimentRunner = expRunner;
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
                //Instantiate Objects
                Reporter reporter = new Reporter();
                Serial serialPort = new Serial();
                ExperimentRunner experimentRunner = new ExperimentRunner(
                                                        serialPort,
                                                        expWindow);
                Xmod t = new Xmod(mainWindow, reporter,
                                serialPort, experimentRunner, expWindow);
                // Add observers to respond to buttons/key strokes/error reports
                mainWindow.addObserver(t);
                serialPort.addObserver(t);
                experimentRunner.addObserver(t);
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
                default: break;
           }
        } else if (actionType == Actions.UPDATE) {
            // Send the ObjectReport to reporter to update the status
            ObjectReport action = (ObjectReport) evt.getNewValue();
            updateStatus(action);
            updateWindowText();
        }
    }

    private void operationLoadTMS() {
        String filename = mainWindow.chooseFile();
        // If no file selected
        if (filename == Responses.NO_FILE_SELECTED ) {
            updateStatus(createReport(ReportLabel.TMS,
                        Responses.NO_FILE_SELECTED, "",
                        "Please select a .tms file to upload", ""));
            updateWindowText();
            return;
        }
        //If file selected
        this.experimentRunner.setUpExperiment(filename);
        // Check loaded
        if(this.experimentRunner.isExperimentLoaded()){
            //lookForWavFile(filename);
        }
    };

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
        // OBJECT REPORT HERE
    };

    private void operationCheckFont() { };

    /* ***** METHODS RELATED TO UPDATING THE WINDOWS/TEXT/FONT ************/


     /**
     * Send updates to main Xmod.java.
     * @param reportLabel which section it is for
     * @param newStatus status
     * @param newMessage message
     * @param newAdvice advice
     * @param newStackTrace any stack trace
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
     */
    private void updateWindowText() {
        String newText = this.reporter.toString();
        this.mainWindow.updateText(newText);
        this.mainWindow.repaint();
    }

    /* ******* METHODS RELATED TO SHUTTING DOWN THE APPLICATION ************/

   /**
     * Handles cleanup on shutting down application.
     */
    private void operationCloseXmod() {
        System.exit(0);
    }
}
