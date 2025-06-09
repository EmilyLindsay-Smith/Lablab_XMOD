package xmod.utils;

import java.io.File;

import java.nio.file.Path;
import java.nio.file.Paths;

import java.io.StringWriter;
import java.io.PrintWriter;

/**
* Class of static methods that are useful tools for the project.
* @author ELS
*/
public final class Utils {
    private Utils() { }; // Private Constructor
    /**
    * Pauses current thread for specified number of milliseconds.
    * @param ms integer giving the number of milliseconds to sleep
    */
    public static void pause(final int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    };

    /**
     * Retrieves stack trace as a string.
     * @param e Exception thrown
     * @return string representation of stack trace
     */
    public static String getStackTrace(final Exception e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        String stringStackTrace = sw.toString();
        return stringStackTrace;
    }
}
