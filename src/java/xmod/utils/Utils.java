package xmod.utils;

import java.io.File;

import java.io.StringWriter;
import java.io.PrintWriter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
/**
* Class of static methods that are useful tools for the project.
* @author ELS
* @version 2.0
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
     * @param extension expected extension e.g. "wav"
     * @return boolean true if filename has expected extension
     */
    public static Boolean fileHasExtension(final String filename,
                                            final String extension) {
        String expectedExtension = extension;

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

    /**
     * Get datestamp.
     * @return string of formatted date
     */
    public static String getDate() {
            LocalDate date = LocalDate.now();
            DateTimeFormatter format = DateTimeFormatter.ofPattern("dd.MM.yy");
            String formattedDate = date.format(format);
            return formattedDate;
    }

    /**
     * Get timestamp.
     * @return string of formatted time
     */
    public static String getTime() {
        LocalTime time = LocalTime.now();
        DateTimeFormatter format = DateTimeFormatter.ofPattern("HH.mm.ss");
        String formattedTime = time.format(format);
        return formattedTime;
    }

    /** Gets wav filename from tms file name.
     * @param tmsFilename name of tms file
     * @return name of wavfile
     */
    public static String getWavFromTMS(final String tmsFilename) {
        String location = getParent(tmsFilename);
        String bareFileName = getBareName(tmsFilename);
        if (location == "" || bareFileName == "") {
            return "";
        }
        String wavFile = mergePaths(location, bareFileName, ".wav");
        return wavFile;
    }

    /**
     * Gets parent directory of a file.
     * @param filename
     * @return parent directory
     */
    public static String getParent(final String filename) {
        if (null == filename) {
            return "";
        }

        File childDirectory = new File(new File(filename).getAbsolutePath());
        File parentDirectory = new File(childDirectory.getParent());

        if (parentDirectory.exists() && parentDirectory.isDirectory()) {
            return parentDirectory.toString();
        } else {
            return "";
        }
    }

    /**
     * Gets filename without path or extension.
     * @param filename
     * @return filename without path or extension
     */
    public static String getBareName(final String filename) {
        if (null == filename || "" == filename) {
            return "";
        }
        try {
            File tmsFile = new File(filename);
            String tmsName = tmsFile.getName();
            int dotIndex = tmsName.lastIndexOf('.');
            String fileBareName = tmsName.substring(0, dotIndex);
            return fileBareName;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * Merges filename and path.
     * @param path path to directory where the file should be
     * @param bareFileName name of the file without any extensions etc
     * @param ext extension to use for the file
     * @return string representation of file path
     */
    public static String mergePaths(final String path,
                                    final String bareFileName,
                                    final String ext) {
        String cleanPath = path;
        String cleanBareFileName = bareFileName;
        String cleanExt = ext;

        char fileSeparator = File.separatorChar;
        // remove any file separator at end of path
        if (cleanPath.charAt(cleanPath.length() - 1) == fileSeparator) {
            cleanPath = cleanPath.substring(0, cleanPath.length() - 1);
        }
        //Remove file separator at start of barefileName
        if (cleanBareFileName.charAt(0) == fileSeparator) {
            cleanBareFileName = cleanBareFileName.substring(1);
        }

        //Remove file separator at end of barefileName
        if (cleanBareFileName.charAt(cleanBareFileName.length() - 1)
                == fileSeparator) {
            cleanBareFileName = cleanBareFileName.substring(0,
                                           cleanBareFileName.length() - 1);
        }

        // remove . at end of bareFileName
        if (cleanBareFileName.charAt(cleanBareFileName.length() - 1) == '.') {
            cleanBareFileName = cleanBareFileName.substring(0,
                                            cleanBareFileName.length() - 1);
        }

        //remove . at start of extension
        if (cleanExt.charAt(0) == '.') {
            cleanExt = cleanExt.substring(1);
        }
        String newFileName = cleanPath + fileSeparator
                            + cleanBareFileName + '.' + cleanExt;
        return newFileName;
    }

}
