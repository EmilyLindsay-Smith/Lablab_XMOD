package xmod;

import xmod.view.MainWindow;

import xmod.constants.Actions;
import xmod.constants.Operations;

import xmod.serial.Serial;

import xmod.status.ObjectReport;
import xmod.status.Reporter;
import xmod.status.ReportLabel;


import javax.swing.SwingUtilities;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

public class Xmod implements PropertyChangeListener {
    /** Windows.*/
    private MainWindow mainWindow;
    // Objects.
    /** Reporter to manage status. */
    private Reporter reporter;
    /** SerialPort for communication to Controller Box. */
    private Serial serialPort;
     /**
     * Constructor.
     * @param aMainWindow MainWindow object
     * @param aReporter Reporter Object
     * @param aSerialPort Serial Object
     */
    Xmod(final MainWindow aMainWindow,
        final Reporter aReporter,
        final Serial aSerialPort) {

        //Initialise window variables
        this.mainWindow = aMainWindow;

        // Initialise Objects
        this.reporter = aReporter;
        this.serialPort = aSerialPort;
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

                //Instantiate Objects
                Reporter reporter = new Reporter();
                Serial serialPort = new Serial();
                Xmod t = new Xmod(mainWindow, reporter, serialPort);
                // Add observers to respond to buttons/key strokes/error reports
                mainWindow.addObserver(t);
                serialPort.addObserver(t);
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

    private void operationLoadTMS() { };
    private void operationRunExp() { };

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
        this.mainWindow.text.setText(newText);
        this.mainWindow.f.repaint();
    }

    /* ******* METHODS RELATED TO SHUTTING DOWN THE APPLICATION ************/

   /**
     * Handles cleanup on shutting down application.
     */
    private void operationCloseXmod() {
        System.out.println("Closing Xmod...");
        System.exit(0);
        System.out.println("XmodClosed...");
    }
}
