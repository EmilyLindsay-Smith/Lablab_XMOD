package xmod.utils;

import java.io.File;

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

    /**
     * Checks filename extension matches expected extension.
     * @param filename name of file
     * @param expectedExtension expected extension e.g. "wav"
     * @return boolean true if filename has expected extension
     */
    public static Boolean fileHasExtension(final String filename,
                                            final String expectedExtension) {
        if (null == filename || null == expectedExtension) {
            return false;
        }
        //If first char is '.', ignore this
        if (expectedExtension.charAt(0) == '.') {
            expectedExtension = expectedExtension.substring(1);
        }

        String fileExtension = getFileExtension(filename);
        return (fileExtension.equals(expectedExtension));
    }
    /**
     * Gets file extension.
     * @param filename
     * @return file extension or ""
     */
    public static String getFileExtension(final String filename) {
        if (null == filename) {
            return "";
        }
        int dotIndex = filename.lastIndexOf('.');
        // handle no extension or dot at end of filename so no extension
        if (dotIndex == -1 || dotIndex == filename.length() - 1) {
            return "";
        } else {
            return filename.substring(dotIndex + 1);
        }
    }

    /**
     * Checks file exists.
     * @param filename
     * @return boolean true if file exists
     */
    public static Boolean fileExists(final String filename) {
        if (null == filename) {
            return false;
        }
        File f = new File(filename);
        return (f.exists() && !f.isDirectory());
    }


}
