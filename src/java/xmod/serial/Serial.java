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
 * @version 2.1
 * @since 2025-09-16
 * KNOWN BUGS:
 */


public class Serial extends Thread {

    /** PCS to handle sending updates. */
    private PropertyChangeSupport pcs;
    /** Is application connected to the controller box. */
    private Boolean serialConnected = false;
    /** True if trying to reconnect to controller box. */
    private Boolean tryingToConnect = false;
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
    // This needs to be 0 so it waits for as long as necessary
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
        connectRepeatedly();
    }

    /** Thread to try to connect to serial port multiple times.
     * Only starts new thread if one isn't currently running.
    */
    private void connectRepeatedly() {
        if (!this.tryingToConnect) {
            new Thread(new Runnable() {
                public void run() {
                    flipConnectFlag();
                    connectToPort(NUM_REATTEMPTS);
                    flipConnectFlag();
                    return;
                };
            }, "SerialPortConnection").start();
        }
    }

    /** Flips this.tryingToConnect to block concurrent threads.
     */
    private void flipConnectFlag() {
        this.tryingToConnect = !this.tryingToConnect;
        return;
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
                    // Turn off monitor immediately after connection to avoid
                    // confusing participants
                    turnOffMonitor();
                    return;
                }
            }
        }
        // No Update sent via PCS as this is handled within the connectToPort()
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
            return;
         } else {
            if (repeatInt > 0) {
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
     * @param sendSuccessUpdate send update via PCS if command sent successfully
     * @return true if sent successfully
     */
    private Boolean sendCommand(final int command, final String description,
                                final Boolean sendSuccessUpdate) {
        try {
            this.send(command);
            /*
            if (sendSuccessUpdate) {
                /*
                updateStatus(Responses.SERIAL_CONNECTED, "Sent command to "
                                + description, "", "");

            }
            */
            return true;
        } catch (SerialNotConnectedException e) {
            // StackTrace not passed on as
            updateStatus(Responses.SERIAL_UNCONNECTED, "Could not "
                            + description
                            + " as not connected to the serial port",
                            this.updateAdv, "");
            return false;
        }
    }
    /**
     * Connects to usb serial port and should light up the LEDs 3 times.
     * If not connected, will reattempt connection NUM_REATTEMPTS times.
     */
    public void checkConnection() {
        if (!this.serialConnected) {
            connectRepeatedly();
        } else {
            sendCommand(this.FLASH_LED, "flash LEDs", true);
        }
        return;
    }

    /**
     * Requests information about the control box.
     * @return String representation of all control box info or ""
     */
    public String getControllerInfo() {
        if (!this.serialConnected) {
            String updateMsg = "Could not retrieve controller info"
                                + " because not connected to serial port";
            updateStatus(Responses.SERIAL_UNCONNECTED, updateMsg,
                            this.updateAdv, "");
            return "";
        }

        String serialInfo = "Controller Info: <br/>";
        try {
            for (int j = this.GET_SOURCE; j <= this.GET_KEYS; j++) {
                Boolean sent = sendCommand(j, "retrieve controller info",
                                            false);
                if (sent) {
                    serialInfo = serialInfo + "<br/>" + receive();
                }
            }
        } catch (SerialBytesReceivedException e) {
            String updateMsg = "Could not retrieve controller info"
                                + " because of error receiving bytes";
            updateStatus("", updateMsg,
                            this.updateAdv, "");
            return "";
        }
        return serialInfo;
    }

    /**
     * Requests that control box turn on the monitors of the test computers.
     */
    public void turnOnMonitor() {
        sendCommand(this.ADJUST_ON, "turn on monitors", true);
        return;
    }

    /**
     * Requests that control box turn off the monitors of the test computers.
     */
    public void turnOffMonitor() {
        sendCommand(this.ADJUST_OFF, "turn off monitors", true);
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
        } catch (SerialNotConnectedException e) {
            String stackTrace = Utils.getStackTrace(e);
            updateStatus("", "Could not send trial timings as not connected "
                + "to serial port", "", stackTrace);
        }
        return;
    }

    /**
     * Flushes IO buffers and Sends data to the serial port.
     * @param  message integer representation of the command to the control box
     */
    private void send(final int message) throws SerialNotConnectedException {
        // Tell serialPort to flush buffers
        if (null == this.serialPort) {
            throw new SerialNotConnectedException();
        }
            this.serialPort.flushIOBuffers(); //command from jSerialComm
            //Send message
            sendWithoutFlush(message);
            return;
      }


    /**
     * Sends data to the serial port.
     * @param  message integer representation of the command to the control box
     */
    private void sendWithoutFlush(final int message)
                                            throws SerialNotConnectedException {
        if (null == this.serialPort) {
            throw new SerialNotConnectedException();
        }
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
    public String receive() throws SerialBytesReceivedException {
        //Wait til serialPort ready to send response
        while (this.serialPort.bytesAvailable() == 0) {
            Utils.pause(this.PAUSE_DURATION); //from Utils
        }

        int availableBytes = this.serialPort.bytesAvailable();
        if (availableBytes == -1) {
            String updateMsg = "Error (-1 bytes available) occurred while"
                + " receiving bytes from controller box";
            updateStatus("", updateMsg, "", "");
            throw new SerialBytesReceivedException();
        }
        byte[] received;
        try {
            received = receiveChunk(availableBytes);
        } catch (SerialBytesReceivedException e) {
            throw new SerialBytesReceivedException();
        }
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
    public byte[] receiveChunk(final int chunkSize)
        throws SerialBytesReceivedException {
        if (chunkSize < 0) {
            String updateMsg = "Error occurred while expected bytes from "
                    + "controller box; should have expected more than 0 bytes";
            updateStatus("", updateMsg, "", "");
            throw new SerialBytesReceivedException();
        }
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
            throw new SerialBytesReceivedException();
        }
        if (received == serialBuffersizeError) {
            String updateMsg = "Buffersize Error (-2) occurred while"
                + " receiving bytes from controller box";
            updateStatus("", updateMsg, "", "");
            throw new SerialBytesReceivedException();
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
     * Used in Xmod.java to allow the controller to listen for pcs.
     * @param l listener i.e. Xmod.java
     */
    public void addObserver(final PropertyChangeListener l) {
        pcs.addPropertyChangeListener(Actions.UPDATE, l);
    }
}

/** Custom Exception to stop parsing if errors occur. */
class SerialNotConnectedException extends Exception {
    /** Constructor.
    */
    SerialNotConnectedException() {
        super("Not connected to serial port");
    }

}
