package xmod.serial;

import xmod.constants.*;
import xmod.utils.*;
import com.fazecast.jSerialComm.*;

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
 *
 *  */
public class Serial {
    public PropertyChangeSupport pcs;

    private Boolean serialConnected = false;
    private SerialPort serialPort = null;
    //duration of waiting for control box bytesAvailable() > 0
    private static final int PAUSE_DURATION = 40;
     //duration of pause being looking for serial to connect to
    private static final int WAIT_DURATION = 1000;
    //Port Parameters
    private static final int BAUD_RATE = 19200;
    private static final int DATA_BITS = 8;
    private static final int STOP_BITS = SerialPort.ONE_STOP_BIT;
    private static final int PARITY = SerialPort.NO_PARITY;
    //time in ms to wait for data from control box
    private static final int TIMEOUT = 0;

    //Constants for commands to send to the control box;
    // variable names are same as method names in control box software;
    private static final int MAIL_TOUT = Integer.parseInt("01", 16);
    private static final int ENABLE_EXT_INT0 = Integer.parseInt("02", 16);
    private static final int GET_SOURCE = Integer.parseInt("07", 16);
    private static final int GET_VERSION = Integer.parseInt("08", 16);
    private static final int GET_CREATED = Integer.parseInt("09", 16);
    private static final int GET_MODIFIED = Integer.parseInt("0A", 16);
    private static final int GET_BOXES = Integer.parseInt("0B", 16);
    private static final int GET_KEYS = Integer.parseInt("0C", 16);
    private static final int ADJUST_OFF = Integer.parseInt("0F", 16);
    private static final int ADJUST_ON = Integer.parseInt("12", 16);
    private static final int FLASH_LED = Integer.parseInt("10", 16);
    private static final int CROSSMODEL = Integer.parseInt("20", 16);


    /**Constructor to connect to serial port. */
    public Serial() {
        pcs = new PropertyChangeSupport(this);
        connectToPort();
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
                    pcs.firePropertyChange(Actions.UPDATE_CONNECTION, "",
                    "Connected");
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
                    connectToPort(5);
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
            String errorMessage = "Serial Port Unavailable: "
            + "Could not connect to control box.";
            if (repeatInt > 0) {
                errorMessage = errorMessage
                + "<br/>Retrying in " + (this.WAIT_DURATION / 1000) + "s ... ("
                + repeatInt + "automatic connection attempts remaining...)";
                pcs.firePropertyChange(Actions.ERROR, "", errorMessage);
                Utils.pause(this.WAIT_DURATION);
                //pre-increment reduces repeatInt by 1 before calling method
                connectToPort(--repeatInt);
                return;
             } else {
                errorMessage = errorMessage
                + "<br/>Please connect to the control box then"
                + "click the 'CHECK CONNECTION' button";
                pcs.firePropertyChange(Actions.ERROR, "", errorMessage);
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
            System.out.println("Serial Port successfully set up");
         } else {
            String errorMessage = "Serial Port Unavailable: "
            + "Could not open connection to control box.";
            if (repeatInt > 0) {
                errorMessage = errorMessage
                + "<br/>Retrying in "
                + (this.WAIT_DURATION / 1000) + "s ... ("
                + repeatInt
                + "automatic connection attempts remaining...)";

                pcs.firePropertyChange(Actions.ERROR, "", errorMessage);
                Utils.pause(this.WAIT_DURATION);
                //pre-increment reduces repeatInt by 1 before calling method
                connectToPort(--repeatInt);
                return;
             } else {
                errorMessage = errorMessage
                + "<br/>Please connect to the control box then"
                + " click the 'CHECK CONNECTION' button";
                pcs.firePropertyChange(Actions.ERROR, "", errorMessage);
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

    /**
     * Connects to usb serial port and should light up the LEDs 3 times.
     */
    public void checkConnection() {
        if (this.serialConnected) {
            try {
                this.send(this.FLASH_LED);
            } catch (Exception e) {
            String stackTrace = Utils.getStackTrace(e);
            String errorMessage = "Serial Port : Could not flash LEDs as <br/>"
            + stackTrace;
            pcs.firePropertyChange(Actions.ERROR, "", errorMessage);
            }
        }
        return;
    }

    /**
     * Requests information about the control box.
     * @return String representation of all control box info
     */
    public String getControllerInfo() {
        String serialInfo = "Controller Info: <br/>";
        try {
            for (int j = this.GET_SOURCE; j <= this.GET_KEYS; j++) {
                send(j);
                serialInfo = serialInfo + "<br/>" + receive();
            }
            return serialInfo;
        } catch (Exception e) {
            String stackTrace = Utils.getStackTrace(e);
            String errorMessage = "Serial Port: Could not retrieve controller "
            + " info because <br/>" + stackTrace;
            pcs.firePropertyChange(Actions.ERROR, "", errorMessage);
            return "";
        }
    }

    /**
     * Requests that control box turn on the monitors of the test computers.
     */
    public void turnOnMonitor() {
        try {
            send(this.ADJUST_ON);
        } catch (Exception e) {
            String stackTrace = Utils.getStackTrace(e);
            String errorMessage = "Serial Port: Could not turn on monitor <br/>"
            + stackTrace;
            pcs.firePropertyChange(Actions.ERROR, "", errorMessage);
        }
    }

    /**
     * Requests that control box turn off the monitors of the test computers.
     */
    public void turnOffMonitor() {
        try {
            send(this.ADJUST_OFF);
        } catch (Exception e) {
            String stackTrace = Utils.getStackTrace(e);
            String errorMessage = "Serial Port: Could not turn on monitor <br/>"
            + stackTrace;
            pcs.firePropertyChange(Actions.ERROR, "", errorMessage);
        }
    }

    /**
     * Sends timeout, on and off bytes to controller.
     * @param tReactionTimeoutByte : byte for timeout timings
     * @param tMonitorOnByte: byte for monitor on timings
     * @param tMonitorOffByte: byte for monitor off timings
     */
    public void sendTrialTimings(final int tReactionTimeoutByte,
                                final int tMonitorOnByte,
                                final int tMonitorOffByte) {
        try {
            static final int HIGH_LOW_BYTE_SEPARATOR = 256;
            int tOnLowByte = tMonitorOnByte % HIGH_LOW_BYTE_SEPARATOR;
            int tOnHighByte = tMonitorOnByte / HIGH_LOW_BYTE_SEPARATOR;
            int tOffLowByte = tMonitorOffByte % HIGH_LOW_BYTE_SEPARATOR;
            int tOffHighByte = tMonitorOffByte / HIGH_LOW_BYTE_SEPARATOR;
            int tOutLowByte = tReactionTimeoutByte % HIGH_LOW_BYTE_SEPARATOR;
            int tOutHighByte = tReactionTimeoutByte / HIGH_LOW_BYTE_SEPARATOR;

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
            String errorMessage = "Serial Port : Could not send timings to "
            + " control box because <br/>" + stackTrace;
            pcs.firePropertyChange(Actions.ERROR, "", errorMessage);
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
    private byte[] receiveChunk(final int chunkSize) {
        // Receive info from Serial
        // sets up buffer to size of incoming message
        byte[] inbuffer = new byte[chunkSize];
        //returns number of bytes received
        int received = this.serialPort.readBytes(inbuffer, inbuffer.length);
        //Check no error has occured (error response from jSerialComm)
        if (received == -1) {
            String errorMessage = "Serial Port : Reading error (-1)"
            + "has occured in receiving bytes from the control box";
            pcs.firePropertyChange(Actions.ERROR, "", errorMessage);
        }
        if (received == -2) {
            String errorMessage = "Serial Port : Buffersize error (-2)"
            + "has occured in receiving bytes from the control box";
            pcs.firePropertyChange(Actions.ERROR, "", errorMessage);
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
        pcs.firePropertyChange(Actions.UPDATE_CONNECTION, "", "Disconnected");
        pcs.firePropertyChange(Actions.ERROR, "",
        "Serial Port Disconnected <br/> Attempting to reconnect...");
        return;
    }

    /**
     * Used in Xmod.java to allow the controller to listen for pcs.
     * @param l listener i.e. Xmod.java
     */
    public void addObserver(final PropertyChangeListener l) {
        pcs.addPropertyChangeListener(Actions.ERROR, l);
        pcs.addPropertyChangeListener(Actions.UPDATE_CONNECTION, l);
    }
}
