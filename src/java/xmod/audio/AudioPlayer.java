package xmod.audio;

import xmod.constants.Actions;
import xmod.status.ObjectReport;
import xmod.status.ReportCategory;
import xmod.status.ReportLabel;
import xmod.status.Responses;
import xmod.utils.Utils;
//import java.io.File;

import java.nio.file.Files;
//import java.nio.file.Path;
import java.nio.file.Paths;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

// For reporting errors to GUI
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * AudioPlayer class loads and plays the audio file.
 * @author ELS
 * @version 2.0
 * @since 2025-02-13
 * NOTES:
 */

public class AudioPlayer extends Thread {
    /** PCS to handle sending updates. */
    private PropertyChangeSupport pcs;
    /** Audio file. */
    private String audioFilePath;
    /** Buffer size. */
    private static final int BUFFER_SIZE = 4096;
    /** Whether the audio has been successfully loaded. */
    private Boolean audioLoaded = false;
    /** Whether the audio is currently playing. */
    private Boolean playAudio = false;
    /** SourceDataLine. */
    private SourceDataLine sourceDataLine;
    /** AudioInputStream. */
    private AudioInputStream audioStream;
    /** InputStream. */
    private InputStream inputStream;
    /** InputStream. */
    private InputStream originalInputStream;
    /** AudioFormat. */
    private AudioFormat audioFormat;
    /** Dataline info. */
    private DataLine.Info info;

    /**
     * AudioLoader Constructor.
     */
    public AudioPlayer() {
        pcs = new PropertyChangeSupport(this);
    }

    /**
     * Loads audio file.
     * @param audioFile name of audio file
     */
    public void loadAudio(final String audioFile) {
        if (null != audioFile && isAudioValid(audioFile)) {
            this.audioFilePath = audioFile;
            setUpPlayer();
        }
    }
    /**
     * Validates audio file - checks if filename ends in .wav and file exists.
     * @param audioFile
     * @return boolean if valid
     */
    private Boolean isAudioValid(final String audioFile) {
        if (null == audioFile) {
            return false;
        }
        return (Utils.fileHasExtension(audioFile, "wav")
                && Utils.fileExists(audioFile));
    }

     /**
     * Loads audio file.
     * ELS CHECK LIKELY EXCEPTIONS TO CATCH THEM
     */
    private void setUpPlayer() {
        if (null == this.audioFilePath || !isAudioValid(this.audioFilePath)) {
            return;
        }
        try {
            this.originalInputStream = Files.newInputStream(
                                                Paths.get(this.audioFilePath));
        } catch (IOException e) {
            String stackTrace = Utils.getStackTrace(e);
            updateStatus(Responses.FILE_LOAD_FAILURE,
                        "Could not load audio file due to I/O error",
                            "", stackTrace);
            return;
        } catch (SecurityException e) {
            String stackTrace = Utils.getStackTrace(e);
            updateStatus(Responses.FILE_LOAD_FAILURE,
                        "Could not load audio file due to SecurityException",
                        "", stackTrace);
            return;
        } catch (IllegalArgumentException e) {
            String stackTrace = Utils.getStackTrace(e);
            updateStatus(Responses.FILE_LOAD_FAILURE,
                        "Could not load audio file due to IllegalArgument",
                        "", stackTrace);
            return;
        } catch (UnsupportedOperationException e) {
            String stackTrace = Utils.getStackTrace(e);
            updateStatus(Responses.FILE_LOAD_FAILURE,
                        "Could not load audio file due to UnsupportedOperation",
                        "", stackTrace);
            return;
        }
        this.inputStream = new BufferedInputStream(this.originalInputStream);


        try {
            this.audioStream = AudioSystem.getAudioInputStream(
                                                            this.inputStream);
        } catch (UnsupportedAudioFileException e) {
            String stackTrace = Utils.getStackTrace(e);
            updateStatus(Responses.FILE_LOAD_FAILURE,
                        "Could not load audio file due to "
                        + "unsupported audio file error",
                        "", stackTrace);
            return;
        } catch (IOException e) {
            String stackTrace = Utils.getStackTrace(e);
            updateStatus(Responses.FILE_LOAD_FAILURE,
                        "Could not load audio file due to I/O Exception",
                        "", stackTrace);
            return;
        }

        this.audioFormat = this.audioStream.getFormat();
        this.info = new DataLine.Info(SourceDataLine.class, this.audioFormat);

        this.audioLoaded = true;
        updateStatus(Responses.FILE_LOAD_SUCCESS + this.audioFilePath,
                    "Audio Duration: " + this.getAudioLength(),
                    "", "");
    }

    /** Returns audioLoaded flag.
     * @return true if loaded
     */
    public Boolean isAudioLoaded() {
        return this.audioLoaded;
    }

    /** Start thread to play audio file. */
    public void playAudio() {
        //If restarting, reload audio to start from the beginning.
        if (this.getState() == Thread.State.TERMINATED) {
            this.setUpPlayer();
        }
        if (this.playAudio) {
            updateStatus("", "Could not play audio as audio already playing",
                            "", "");
            return;
        }
        new Thread(new Runnable() {
        public void run() {
            play();
            return;
        };
        }, "AUDIO PLAYER").start();
    }
    /**
     * Plays audio file.
     */
    private void play() {
        if (!this.audioLoaded) {
            updateStatus("", "Could not play audio as not loaded",
                            "", "");
            return;
        }
        this.playAudio = true;
        try {
            this.sourceDataLine = (SourceDataLine) AudioSystem.getLine(
                                                                this.info);
        } catch (LineUnavailableException e) {
            String stackTrace = Utils.getStackTrace(e);
            updateStatus(Responses.AUDIO_ERROR,
                        "Could not play audio file due to error",
                        "", stackTrace);
            this.playAudio = false;
            return;
        }
        try {
            this.sourceDataLine.open(audioFormat);
        } catch (LineUnavailableException e) {
           String stackTrace = Utils.getStackTrace(e);
            updateStatus(Responses.AUDIO_ERROR,
                        "Could not play audio file due to error",
                        "", stackTrace);
            this.playAudio = false;
            return;
        } catch (IllegalStateException e) {
           String stackTrace = Utils.getStackTrace(e);
            updateStatus(Responses.AUDIO_ERROR,
                        "Could not play audio file due to error",
                        "", stackTrace);
            this.playAudio = false;
            return;
        } catch (SecurityException e) {
           String stackTrace = Utils.getStackTrace(e);
            updateStatus(Responses.AUDIO_ERROR,
                        "Could not play audio file due to error",
                        "", stackTrace);
            this.playAudio = false;
            return;
        }

        this.sourceDataLine.start();

        byte[] bufferBytes = new byte[this.BUFFER_SIZE];
        int readBytes = -1;
        try {
            while (this.playAudio
                && (readBytes = this.audioStream.read(bufferBytes)) != -1) {
                    this.sourceDataLine.write(bufferBytes, 0, readBytes);
                }
        } catch (IOException e) {
            String stackTrace = Utils.getStackTrace(e);
            updateStatus(Responses.AUDIO_ERROR,
                        "Could not play audio file due to error",
                        "", stackTrace);
            this.playAudio = false;
            return;
        } catch (IllegalArgumentException e) {
            String stackTrace = Utils.getStackTrace(e);
            updateStatus(Responses.AUDIO_ERROR,
                        "Could not play audio file due to error",
                        "", stackTrace);
            this.playAudio = false;
            return;
        } catch (ArrayIndexOutOfBoundsException e) {
            String stackTrace = Utils.getStackTrace(e);
            updateStatus(Responses.AUDIO_ERROR,
                        "Could not play audio file due to error",
                        "", stackTrace);
            this.playAudio = false;
            return;
        }
        stopAudio();
    }

    /**
     * Stops the audio playing.
     */
    public void stopAudio() {
        this.playAudio = false; // this stops playAudio()
        sourceDataLine.drain();
        try {
            sourceDataLine.close();
        } catch (SecurityException e) {
            String stackTrace = Utils.getStackTrace(e);
                updateStatus(Responses.AUDIO_ERROR,
                            "Could not stop audio file due to error",
                            "", stackTrace);
            return;
        }

        try {
            audioStream.close();
        } catch (IOException e) {
            String stackTrace = Utils.getStackTrace(e);
                updateStatus(Responses.AUDIO_ERROR,
                            "Could not stop audio file due to error",
                            "", stackTrace);
            return;
        }

    }

    /** Returns length of audio file in HH:MM:SS format.
     * @return audio file duration
     */
    public String getAudioLength() {
        if (null == this.audioLoaded) {
            return "No audio file has been loaded";
        }
        int timeDivisor = 60;
        int duration = this.getAudioLengthInSeconds();
        int seconds = duration % timeDivisor;
        int minutes = duration / timeDivisor;
        int hours = minutes / timeDivisor;
        minutes = minutes % timeDivisor;
        String minuteFormat = "";
        if (minutes < 10) {
            minuteFormat = "0";
        }
        String durationString = hours + ":" + minuteFormat
                                + minutes + ":" + seconds;
        return durationString;
    }

    /**
     * Calculates the duration in seconds.
     * Note: this truncates any fractional seconds rather than rounding down/up
     * @return audio length in seconds
     */
    public int getAudioLengthInSeconds() {
        if (null != this.audioStream && null != this.audioFormat) {
            long frameLength = this.audioStream.getFrameLength();
            float frameRate = this.audioFormat.getFrameRate();

            //calculate duration in sections
            // note the typecasting will just truncate the float
            return (int) (frameLength / frameRate);
        } else {
            return -1;
        }
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
        ObjectReport report = new ObjectReport(ReportLabel.AUDIO);
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
