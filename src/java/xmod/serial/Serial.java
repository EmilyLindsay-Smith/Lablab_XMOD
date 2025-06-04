package xmod.serial;

import xmod.utils.*;
import com.fazecast.jSerialComm.*;

// For reporting errors to GUI
import java.beans.PropertyChangeSupport;
import java.beans.PropertyChangeListener;

/**
 * Serial class uses jSerialComm's SerialPort class to manage all the communication with the control box via serial port 
 * @author ELS
 * @version 1.0
 * @since 2024-2-25
 * BUGS:
 *  
 *  */

public class Serial
{
    public Boolean serialConnected = false;
    public PropertyChangeSupport pcs;
 
    private SerialPort serialPort = null;
    private int PAUSE_DURATION = 40; //duration of waiting for control box bytesAvailable() > 0
    private int WAIT_DURATION = 1000; //duration of pause being looking for serial to connect to
    //Port Parameters
    private final int BaudRate = 19200;
    private final int DataBits = 8;
    private final int StopBits = SerialPort.ONE_STOP_BIT;
    private final int Parity = SerialPort.NO_PARITY;
    private final int timeout = 0; //2000; //time in ms to wait for data from control box 
    
    //Constants for commands to send to the control box; variable names are same as method names in control box software;

    private final int MAIL_TOUT = Integer.parseInt("01", 16);
    private final int ENABLE_EXT_INT0 = Integer.parseInt("02", 16);
    private final int GET_SOURCE = Integer.parseInt("07", 16);
    private final int GET_VERSION = Integer.parseInt("08", 16);
    private final int GET_CREATED = Integer.parseInt("09", 16);
    private final int GET_MODIFIED = Integer.parseInt("0A", 16);
    private final int GET_BOXES = Integer.parseInt("0B", 16);
    private final int GET_KEYS = Integer.parseInt("0C", 16);
    private final int ADJUST_OFF = Integer.parseInt("0F", 16);
    private final int ADJUST_ON = Integer.parseInt("12", 16);
    private final int FLASH_LED = Integer.parseInt("10", 16);
    private final int CROSSMODEL = Integer.parseInt("20", 16);


    /**Constructor to connect to serial port */
    public Serial(){
        connectToPort();

        pcs = new PropertyChangeSupport(this);
    }

    /**
     * Identify available serial ports and select correct port
     */
    private void selectPort(){
        SerialPort [] availablePorts = SerialPort.getCommPorts(); //from jSerialComm
        int numPorts = availablePorts.length;
        if (numPorts > 0){
            for (int i = 0; i < numPorts; i++){
                String name = availablePorts[i].getSystemPortName();
                if (name.contains("usbserial")){
                    this.serialPort = availablePorts[i];
                    addPortListener();
                    return;
                }
            }
        }
        this.serialPort = null;
        this.serialConnected = false;
        // No Port Available - report this?
    }


    /**
     * Add data listener to serial port to listen for disconnection
     */
    private void addPortListener(){
        this.serialPort.addDataListener(new SerialPortDataListener(){
            @Override
            public int getListeningEvents(){
                return SerialPort.LISTENING_EVENT_PORT_DISCONNECTED;
            }

            @Override
            public void serialEvent(SerialPortEvent serialPortEvent){
                if (serialPortEvent.getEventType() == SerialPort.LISTENING_EVENT_PORT_DISCONNECTED){
                    close(); // close port 
                    this.pcs.firePropertyChange(Actions.ERROR, "", "Serial Port Disconnected");
                    connectToPort(5);
                }
            }
        });
    }

    /**
     * Connect to serial port and set up timeouts and parameters without cycling if no connection
     */
    private void connectToPort(){
        connectToPort(0);
    }
    /**
     * Connect to serial port and set up timeouts and parameters
     * @param repeatInt integer: number of items to check
     * If no port available, try again
     */
    private void connectToPort(int repeatInt){
        selectPort();
        if (this.serialPort == null){
            this.pcs.firePropertyChange(Actions.ERROR, "", "Serial Port Unavailable: Could not connect to control box");
            if (repeatInt > 0){
                Utils.pause(this.WAIT_DURATION);
                connectToPort(--repeatInt); //pre-increment reduces repeatInt by 1 before calling method
                return;
            }
            return;
        }
    
        // Open the port if not open
        if (!this.serialPort.isOpen()){
            this.serialPort.openPort();
        }
        // If opened, set parameters and timeouts
        if (this.serialPort.isOpen()){
            this.serialPort.setComPortParameters(this.BaudRate, this.DataBits, this.StopBits, this.Parity);
            this.serialPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING |SerialPort.TIMEOUT_WRITE_BLOCKING, this.timeout, 0);
            this.serialConnected = true;
            System.out.println("Serial Port successfully set up");
        }else{
            this.pcs.firePropertyChange(Actions.ERROR, "", "Serial Port Unavailable: Could not open connection to control box");
            if (repeatInt > 0){
                Utils.pause(this.WAIT_DURATION);
                connectToPort(--repeatInt); //pre-increment reduces repeatInt by 1 before calling method
            }
        }
    }

    /**
     * Connects to usb serial port and should light up the LEDs 3 times
     */
    public void checkConnection(){
        if (this.serialConnected){
            try{
                this.send(this.FLASH_LED);
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        return;
    }

    /**
     * Requests information about the control box
     */
    public String getControllerInfo(){
        String serialInfo = "Controller Info: <br/>";
        try{
            for (int j = this.GET_SOURCE; j <= this.GET_KEYS; j++){
                send(j);
                serialInfo = serialInfo + "<br/>" + receive();
            }
            return serialInfo;
        }catch(Exception e){
            e.printStackTrace();
        }
        this.pcs.firePropertyChange(Actions.ERROR, "", "Serial Port Unavailable: Could not retrieve controller info");
        return "";
    }

    /**
     * Requests that control box turn on the monitors of the test computers
     */
    public void turnOnMonitor(){
        try{
            send(this.ADJUST_ON);
        }catch(Exception e){
            System.out.println("Exception occurred: " + e.getMessage());
        }
    }

    /**
     * Requests that control box turn off the monitors of the test computers
     */
    public void turnOffMonitor(){
        try{
            send(this.ADJUST_OFF);
        }catch(Exception e){
            System.out.println("Exception occurred: " + e.getMessage());
        }
    }

    /**
     * Sends timeout, on and off bytes to controller
     */
    public void sendTrialTimings(int T_reactionTimeout_Byte, int T_monitorOn_Byte, int T_monitorOff_Byte){
        try{
            int T_on_lowByte = T_monitorOn_Byte % 256;
            int T_on_highByte = T_monitorOn_Byte / 256;
            int T_off_lowByte = T_monitorOff_Byte % 256;
            int T_off_highByte = T_monitorOff_Byte / 256;
            int T_out_lowByte = T_reactionTimeout_Byte % 256;
            int T_out_highByte = T_reactionTimeout_Byte / 256;

            send(this.MAIL_TOUT); //(&h01)); // tell controller two timeout-bytes are coming;
             sendWithoutFlush(T_out_lowByte); // send low byte of T_reactionTimeout
             sendWithoutFlush(T_out_highByte); // send high byte of T_reactionTimeout - integer division
             
             send(this.CROSSMODEL); //chr(&h20)); // tell controller T_monitorOn and T_monitorOff times are coming for a crossmodal exp
             sendWithoutFlush(T_on_lowByte); // send low byte of T_monitorOn;
             sendWithoutFlush(T_on_highByte); // send high byte of T_monitorOn; - integer division
             sendWithoutFlush(T_off_lowByte); // send low byte of T_monitorOff
             sendWithoutFlush(T_off_highByte); // send high byte of T_monitorOff - integer division
             
             send(this.ENABLE_EXT_INT0); //chr(&h02)); // tell controller to enable external interrupr - audio trigger to start exp run
        }catch(Exception e){
            System.out.println("Exception occurred while sending trial timings to controller box: " + e.getMessage());
        }
        return;
    }

/**
     * Flushes IO buffers and Sends data to the serial port
     * @param  message integer representation of the byte command to the control box
     */
    private void send(int message)
    {
        // Tell serialPort to flush buffers
        this.serialPort.flushIOBuffers(); //command from jSerialComm
        //Send message
        sendWithoutFlush(message);
        return;
    }

    /**
     * Sends data to the serial port
     * @param  message integer representation of the byte command to the control box
     */
    private void sendWithoutFlush(int message)
    {
        //Create Message in Bytes
        byte[] sendByte = new byte[1];
        sendByte[0] = (byte) message;
        //Send message to serialPort
        int sent = this.serialPort.writeBytes(sendByte, 1); // command for jSerialComm;
        Utils.pause(this.PAUSE_DURATION);
        return;
    }

    /**
     * Receives the response from the serial port; must be used in conjunction with send command
     * @return output string containing response from control box
     */
    public String receive()
    {
        //Wait til serialPort ready to send response
        while (this.serialPort.bytesAvailable() == 0){
            Utils.pause(this.PAUSE_DURATION); //from Utils
        }
        // Receive info from Serial
        //byte[] inbuffer = new byte[this.serialPort.bytesAvailable()]; // sets up buffer to size of incoming message
        byte[] inbuffer = new byte[this.serialPort.bytesAvailable()]; // sets up buffer to size of incoming message
        int received = this.serialPort.readBytes(inbuffer, inbuffer.length); //returns number of bytes received
        //Check no error has occured based on error response from jSerialComm's readBytes()
        if (received == -1){
            System.out.println("Reading error has occurred");
        }
        if (received == -2){
            System.out.println("Buffer-size error has occurred");
        }
        //Write input to string
        String output = "";
        for (int i = 0; i<received; i++){
            output = output + (char) inbuffer[i];
        }
        return output;
    }

    /**
     * Receives the response from the serial port; must be used in conjunction with send command
     * @param chunkSize size of chunk to receive
     * @return output string containing response from control box
     */
    public byte[] receiveChunk(int chunkSize)
    {
        // Receive info from Serial
        byte[] inbuffer = new byte[chunkSize]; // sets up buffer to size of incoming message
        int received = this.serialPort.readBytes(inbuffer, inbuffer.length); //returns number of bytes received
        //Check no error has occured based on error response from jSerialComm's readBytes()
        if (received == -1){
            System.out.println("Reading error has occurred");
        }
        if (received == -2){
            System.out.println("Buffer-size error has occurred");
        }
        return inbuffer;
    }

        /**
     * Closes the serial port connection if open
     */
    public void close(){
        if (this.serialConnected && this.serialPort.isOpen()){
            this.serialPort.closePort();
            this.serialConnected = false;
            System.out.println("Closing the port...");
            return;
        }
        //Otherwise
        System.out.println("No port to close....");
    }

    /**
     * Used in Xmod.java to allow the controller to listen to property changes in the view
     * i.e. so the buttons can trigger different actions
     */
    public void addObserver(PropertyChangeListener l){
        pcs.addPropertyChangeListener(Actions.ERROR, l);
    }
    private void handleOperation(String errorMessage){
       pcs.firePropertyChange(Actions.ERROR, "", errorMessage);
    }
}