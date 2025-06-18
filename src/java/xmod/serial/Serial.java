package xmod.serial;

import xmod.constants.Actions;
import xmod.constants.Operations;

import xmod.utils.Utils;
import xmod.status.ObjectReport;
import xmod.status.ReportCategory;
import xmod.status.ReportLabel;
import xmod.status.Responses;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortEvent;
import com.fazecast.jSerialComm.SerialPortDataListener;

// For reporting errors to GUI
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;


/** Serial class manages communication with control box via serial port.
 * Uses jSerialComm's SerialPort class
 *
 * @author ELS
 * @version 1.0
 * @since 2025-06-09
 * KNOWN BUGS:
 */


public class Serial extends Thread {

    /** PCS to handle sending updates. */
    private PropertyChangeSupport pcs;
    /** Is application connected to the controller box. */
    private Boolean serialConnected = false;
    /** Serial port connection to controller box. */
    private SerialPort serialPort = null;
    /** duration of waiting for control box bytesAvailable() > 0. */
    private static final int PAUSE_DURATION = 40;
    /** duration of pause being looking for serial to connect to. */
    private static final int WAIT_DURATION = 1000;
    /** Port Parameters: Baud Rate. */
    private static final int BAUD_RATE = 19200;
    /** Port Parameters: Data bits. */
    private static final int DATA_BITS = 8;
    /** Port Parameters: Stop bots. */
    private static final int STOP_BITS = SerialPort.ONE_STOP_BIT;
    /** Port Parameters: Parity. */
    private static final int PARITY = SerialPort.NO_PARITY;
    /** time in ms to wait for data from control box. */
    private static final int TIMEOUT = 0;

    /** High -low byte separator. */
    private static final int HIGH_LOW_BYTE_SEPARATOR = 256;
    /** Second in Milliseconds. */
    private static final int SECONDS_IN_MILLISECONDS = 1000;
    /** Number of attempts to automatically reconnect to serial port. */
    private static final int NUM_REATTEMPTS = 5;

    /** Advice on reconnecting to controller box via serial port. */
    private final String updateAdv = "Please check connection then press the "
                    + Operations.CHECK_CONNECTION + " button to reconnect";

    // Constants for commands to send to the control box.
    // Note variable names are same as method names in control box software;
    /** Send timeouts for each trial to controller box.  */
    private static final int MAIL_TOUT = Integer.parseInt("01", 16);
    /** Send external interrupt trigger for each trial to controller box.  */
    private static final int ENABLE_EXT_INT0 = Integer.parseInt("02", 16);
    /** Request info about control box: source to controller box.  */
    private static final int GET_SOURCE = Integer.parseInt("07", 16);
    /** Request info about control box: source to controller box.  */
    private static final int GET_VERSION = Integer.parseInt("08", 16);
    /** Request info about control box: source to controller box.  */
    private static final int GET_CREATED = Integer.parseInt("09", 16);
    /** Request info about control box: source to controller box.  */
    private static final int GET_MODIFIED = Integer.parseInt("0A", 16);
    /** Request info about control box: source to controller box.  */
    private static final int GET_BOXES = Integer.parseInt("0B", 16);
    /** Request info about control box: source to controller box.  */
    private static final int GET_KEYS = Integer.parseInt("0C", 16);
    /** Send request to turn off monitors to controller box.  */
    private static final int ADJUST_OFF = Integer.parseInt("0F", 16);
    /** Send request to turn on monitors  to controller box.  */
    private static final int ADJUST_ON = Integer.parseInt("12", 16);
    /** Request flashing leds to controller box.  */
    private static final int FLASH_LED = Integer.parseInt("10", 16);
    /** Send experiment start to controller box.  */
    private static final int CROSSMODEL = Integer.parseInt("20", 16);


    /**Constructor to connect to serial port. */
    public Serial() {
        pcs = new PropertyChangeSupport(this);

        new Thread(new Runnable() {
            public void run() {
                connectToPort(NUM_REATTEMPTS);
            };
        }, "SerialPortConnection").start();

    }

    /**
     * Identify available serial ports and select correct port.
     */
    private void selectPort() {
        //Set serialPort and serialConnected
        this.serialPort = null;
        this.serialConnected = false;
        //Search for port
        SerialPort[] availablePorts = SerialPort.getCommPorts();
        int numPorts = availablePorts.length;
        if (numPorts > 0) {
            for (int i = 0; i < numPorts; i++) {
                String portName = availablePorts[i].getSystemPortName();
                if (portName.contains("usbserial")) {
                    this.serialPort = availablePorts[i];
                    addPortListener();
                    updateStatus(Responses.SERIAL_CONNECTED, "", "", "");
                    return;
                }
            }
        }
        // No Update sent via PCS as this is handled within the connectToPort()
    }


    /**
     * Add data listener to serial port to listen for disconnection.
     */
    private void addPortListener() {
        this.serialPort.addDataListener(new SerialPortDataListener() {
            @Override
            public int getListeningEvents() {
                return SerialPort.LISTENING_EVENT_PORT_DISCONNECTED;
            }

            @Override
            public void serialEvent(final SerialPortEvent serialPortEvent) {
                if (serialPortEvent.getEventType()
                    == SerialPort.LISTENING_EVENT_PORT_DISCONNECTED) {
                    close(); // close port
                    notifyDisconnect();
                    connectToPort(NUM_REATTEMPTS);
                }
            }
        });
    }

    /**
     * Connect to serial port and set up timeouts and parameters.
     * Doesn't not try multiple times if no connection established
     */
    private void connectToPort() {
        connectToPort(0);
    }



    /**
     * Connect to serial port and set up timeouts and parameters.
     * @param repeatInt integer: number of items to check
     * If no port available, try again
     */
    private void connectToPort(final int repeatInt) {
        selectPort();
        if (this.serialPort == null) { // if port not identified yet
            if (repeatInt > 0) {
                String updateMsg = "Retrying in "
                    + (this.WAIT_DURATION / this.SECONDS_IN_MILLISECONDS)
                    + "s ... ("
                    + repeatInt + "automatic connection attempts remaining...)";
                updateStatus(Responses.SERIAL_UNCONNECTED,
                    updateMsg, "", "");
                Utils.pause(this.WAIT_DURATION);
                int decrementedRepeatInt = repeatInt - 1;
                connectToPort(decrementedRepeatInt);
                return;
             } else {
                String updateMsg = "Failed to connect to the controller box";

                updateStatus(Responses.SERIAL_UNCONNECTED,
                    updateMsg, this.updateAdv, "");

                return;
            }
        }

        // Open the port if not open
        if (!this.serialPort.isOpen()) {
            this.serialPort.openPort();
        }
        // If opened, set parameters and timeouts
        if (this.serialPort.isOpen()) {
            this.serialPort.setComPortParameters(
                this.BAUD_RATE,
                this.DATA_BITS,
                this.STOP_BITS,
                this.PARITY);
            this.serialPort.setComPortTimeouts(
                SerialPort.TIMEOUT_READ_BLOCKING
                | SerialPort.TIMEOUT_WRITE_BLOCKING,
                this.TIMEOUT, 0);
            this.serialConnected = true; //only update here as mark of success
         } else {
            String errorMessage = "Serial Port Unavailable: "
            + "Could not open connection to control box.";
            if (repeatInt > 0) {
                String updateMsg = "Retrying in "
                    + (this.WAIT_DURATION / this.SECONDS_IN_MILLISECONDS)
                    + " s ... ("
                    + repeatInt + " automatic connection attempts left...)";

                updateStatus(Responses.SERIAL_UNCONNECTED,
                    updateMsg, "", "");
                Utils.pause(this.WAIT_DURATION);
                int decrementedRepeatInt = repeatInt - 1;
                connectToPort(decrementedRepeatInt);
                return;
             } else {
                String updateMsg = "Failed to open connection "
                                    + " to the controller box";
                updateStatus(Responses.SERIAL_UNCONNECTED,
                    updateMsg, this.updateAdv, "");

                return;
            }
        }
    }

    /**
     * Getter for this.serialConnected.
     * @return this.serialConnected
     */

    public Boolean isSerialConnected() {
        return this.serialConnected;
    }

    /** Wrapper method to send commands to controller box and handle errors.
     * @param command integer command to send to the controller box
     * @param description string to use to describe what didn't happen to user
     */
    private void sendCommand(final int command, final String description) {
        if (this.serialConnected) {
            try {
                this.send(command);
            } catch (Exception e) {
            String stackTrace = Utils.getStackTrace(e);
            updateStatus("", "Could not " + description + " due to error",
                         "", stackTrace);

            }
        } else {
            String updateMsg = "Could not " + description
                                + " because not connected to serial port";
            updateStatus("", updateMsg, this.updateAdv, "");
        }
        return;
    }
    /**
     * Connects to usb serial port and should light up the LEDs 3 times.
     */
    public void checkConnection() {
        if (this.serialConnected) {
            sendCommand(this.FLASH_LED, "flash LEDs");
        } else {
            new Thread(new Runnable() {
                public void run() {
                    connectToPort(NUM_REATTEMPTS);
                };
            }, "SerialPortConnection").start();
        }
        return;
    }

    /**
     * Requests information about the control box.
     * @return String representation of all control box info
     */
    public String getControllerInfo() {
        String serialInfo = "Controller Info: <br/>";
        for (int j = this.GET_SOURCE; j <= this.GET_KEYS; j++) {
            try {
            sendCommand(j, "retrieve controller info");
            //try {
                serialInfo = serialInfo + "<br/>" + receive();
            } catch (Exception e) {
                String stackTrace = Utils.getStackTrace(e);
                updateStatus("", "Could not receive controller info",
                        "", stackTrace);
                return "";
            }
        }
        return serialInfo;
    }

    /**
     * Requests that control box turn on the monitors of the test computers.
     */
    public void turnOnMonitor() {
        sendCommand(this.ADJUST_ON, "turn on monitors");
        return;
    }

    /**
     * Requests that control box turn off the monitors of the test computers.
     */
    public void turnOffMonitor() {
        sendCommand(this.ADJUST_OFF, "turn off monitors");
        return;
    }

    /**
     * Sends timeout, on and off bytes to controller.
     * Note this does not use the sendCommand method to reduce latency
     * @param tReactionTimeoutByte : byte for timeout timings
     * @param tMonitorOnByte byte for monitor on timings
     * @param tMonitorOffByte byte for monitor off timings
     */
    public void sendTrialTimings(final int tReactionTimeoutByte,
                                final int tMonitorOnByte,
                                final int tMonitorOffByte) {
        try {

            int tOnLowByte = tMonitorOnByte % this.HIGH_LOW_BYTE_SEPARATOR;
            int tOnHighByte = tMonitorOnByte / this.HIGH_LOW_BYTE_SEPARATOR;
            int tOffLowByte = tMonitorOffByte % this.HIGH_LOW_BYTE_SEPARATOR;
            int tOffHighByte = tMonitorOffByte / this.HIGH_LOW_BYTE_SEPARATOR;
            int tOutLowByte = tReactionTimeoutByte
                                % this.HIGH_LOW_BYTE_SEPARATOR;
            int tOutHighByte = tReactionTimeoutByte
                                / this.HIGH_LOW_BYTE_SEPARATOR;

            // tell controller two timeout-bytes are coming;
            send(this.MAIL_TOUT);
            // send low byte of tReactionTimeout
            sendWithoutFlush(tOutLowByte);
            // send high byte of tReactionTimeout - integer division
            sendWithoutFlush(tOutHighByte);
            // tell controller tOn and tOff times are coming for crossmodal exp
            send(this.CROSSMODEL);
            // send low byte of tOn;
            sendWithoutFlush(tOnLowByte);
            // send high byte of tOn;- integer division
            sendWithoutFlush(tOnHighByte);
            // send low byte of tOff
            sendWithoutFlush(tOffLowByte);
            // send high byte of tOff - integer division
            sendWithoutFlush(tOffHighByte);
            // tell controller to enable external interrupr
            // this isaudio trigger to start exp run
            send(this.ENABLE_EXT_INT0);
        } catch (Exception e) {
            String stackTrace = Utils.getStackTrace(e);
            updateStatus("", "Could not send trial timings", "", stackTrace);
        }
        return;
    }

    /**
     * Flushes IO buffers and Sends data to the serial port.
     * @param  message integer representation of the command to the control box
     */
    private void send(final int message) {
        // Tell serialPort to flush buffers
        this.serialPort.flushIOBuffers(); //command from jSerialComm
        //Send message
        sendWithoutFlush(message);
        return;
    }

    /**
     * Sends data to the serial port.
     * @param  message integer representation of the command to the control box
     */
    private void sendWithoutFlush(final int message) {
        //Create Message in Bytes
        byte[] sendByte = new byte[1];
        sendByte[0] = (byte) message;
        //Send message to serialPort
        int sent = this.serialPort.writeBytes(sendByte, 1);
        Utils.pause(this.PAUSE_DURATION);
        return;
    }

    /**
     * Receives the response from the serial port.
     * must be used in conjunction with send command
     * @return output string containing response from control box
     */
    public String receive() {
        //Wait til serialPort ready to send response
        while (this.serialPort.bytesAvailable() == 0) {
            Utils.pause(this.PAUSE_DURATION); //from Utils
        }

        byte[] received = receiveChunk(this.serialPort.bytesAvailable());

        //Write input to string
        String output = "";

        for (int i = 0; i < received.length; i++) {
            output = output + (char) received[i];
        }
        return output;
    }

    /**
     * Receives the response from the serial port.
     * must be used in conjunction with send command
     * @param chunkSize size of chunk to receive
     * @return output string containing response from control box
     */
    public byte[] receiveChunk(final int chunkSize) {
        // Receive info from Serial
        // sets up buffer to size of incoming message
        byte[] inbuffer = new byte[chunkSize];
        //returns number of bytes received
        int received = this.serialPort.readBytes(inbuffer, inbuffer.length);
        //Check no error has occured (error response from jSerialComm)
        final int serialReadingError = -1;
        final int serialBuffersizeError = -1;
        if (received == serialReadingError) {
            String updateMsg = "Reading Error (-1) occurred while"
                + " receiving bytes from controller box";
            updateStatus("", updateMsg, "", "");
        }
        if (received == serialBuffersizeError) {
            String updateMsg = "Buffersize Error (-2) occurred while"
                + " receiving bytes from controller box";
            updateStatus("", updateMsg, "", "");
        }
        return inbuffer;
    }

    /**
     * Closes the serial port connection if open.
     */
    public void close() {
        if (this.serialConnected && this.serialPort.isOpen()) {
            this.serialPort.closePort();
            this.serialConnected = false;
            return;
        }
        return;
    }

    /**
     * Notify main Xmod of disconnection.
     */
    private void notifyDisconnect() {
        updateStatus(Responses.SERIAL_DISCONNECTED,
                         "Attempting to reconnect...", "", "");
        return;
    }

    /**
     * Send updates to main Xmod.java.
     * @param newStatus status
     * @param newMessage message
     * @param newAdvice advice
     * @param newStackTrace any stack trace
     */
    private void updateStatus(final String newStatus,
                                final String newMessage,
                                final String newAdvice,
                                final String newStackTrace) {
        ObjectReport report = new ObjectReport(ReportLabel.CONNECTION);
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
        pcs.firePropertyChange(Actions.UPDATE, null, report);
        return;
    }

    /**
     * Used in Xmod.java to allow the controller to listen for pcs.
     * @param l listener i.e. Xmod.java
     */
    public void addObserver(final PropertyChangeListener l) {
        pcs.addPropertyChangeListener(Actions.UPDATE, l);
    }
}
