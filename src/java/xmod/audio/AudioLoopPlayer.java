package xmod.audio;

import xmod.constants.Actions;
import xmod.status.ObjectReport;
import xmod.status.ReportCategory;
import xmod.status.ReportLabel;
import xmod.status.Responses;
import xmod.utils.Utils;

import java.io.IOException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
// For reporting errors to GUI
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * AudioLoopPlayer class loads and plays the audio file on repeat.
 * @author ELS
 * @version 2.0
 * @since 2026-02-02
 * NOTES:
 */

public class AudioLoopPlayer extends Thread {
    /** PCS to handle sending updates. */
    private PropertyChangeSupport pcs;
    /** Boolean for running continous test audio. */
    private Boolean loopRunning;
    /** Test audio clip. */
    private Clip clip;

    /** Constructor */
    public AudioLoopPlayer() {
        pcs = new PropertyChangeSupport(this);
    }

    /** Play Audio Repeatedly.
     * @param filename name of wav file to play
    */
    public void loopAudio(final String filename) {
        if (this.loopRunning != null && this.loopRunning) {
            this.loopRunning = false;
            return;
        }
        this.loopRunning = true;
        new Thread(new Runnable() {
            public void run() {
            loop(filename);
            return;
            }
        }, "LOOP PLAYER").start();

    }

    private void loop(final String filename) {
        try {
            //AudioInputStream loopInputStream = AudioSystem.getAudioInputStream(
            //new File(filename));
            AudioInputStream loopInputStream = AudioSystem.getAudioInputStream(
                getClass().getResource(filename)
            );
            this.clip = AudioSystem.getClip();
            this.clip.open(loopInputStream);
            this.clip.loop(Clip.LOOP_CONTINUOUSLY);
        } catch (UnsupportedOperationException e) {
            String stackTrace = Utils.getStackTrace(e);
            updateStatus(Responses.FILE_LOAD_FAILURE,
                        "Could not load test audio due to UnsupportedOperation",
                        "", stackTrace);
            this.loopRunning = false;
            return;
        } catch (LineUnavailableException e) {
            String stackTrace = Utils.getStackTrace(e);
            updateStatus(Responses.AUDIO_ERROR,
                    "Could not play test audio due to error",
                    "", stackTrace);
            this.loopRunning = false;
            return;
        } catch (IOException e) {
            String stackTrace = Utils.getStackTrace(e);
            updateStatus(Responses.AUDIO_ERROR,
                        "Could not play audio file due to error",
                        "", stackTrace);
            this.loopRunning = false;
            return;
        } catch (UnsupportedAudioFileException e) {
            String stackTrace = Utils.getStackTrace(e);
            updateStatus(Responses.FILE_LOAD_FAILURE,
                        "Could not load audio file due to "
                    + "unsupported audio file error",
                    "", stackTrace);
            this.loopRunning = false;
            return;
        }

        while (this.loopRunning) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();  //set the flag back to true
            }
        }
        this.clip.stop();
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
        ObjectReport report = new ObjectReport(ReportLabel.AUDIO_LOOP);
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
