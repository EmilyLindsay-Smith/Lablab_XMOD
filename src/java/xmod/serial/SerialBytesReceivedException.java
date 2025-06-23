package xmod.serial;

/** Custom Exception to stop parsing if errors occur. */
public class SerialBytesReceivedException extends Exception {
    /** Constructor.
    */
    SerialBytesReceivedException() {
        super("Error receiving bytes from serial port");
    }
}
