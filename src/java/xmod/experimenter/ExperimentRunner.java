package xmod.experimenter;

import xmod.audio.AudioPlayer;
import xmod.constants.Actions;

import xmod.serial.Serial;
import xmod.serial.SerialBytesReceivedException;

import xmod.status.ObjectReport;
import xmod.status.ReportCategory;
import xmod.status.ReportLabel;
import xmod.status.Responses;
import xmod.view.ExperimentWindow;

import xmod.utils.Utils;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * ExperimentRunner class controls running the experiment.
 * @author ELS
 * @version 2.1
 * @since 2025-09-16
 * BUGS:

 */

public class ExperimentRunner implements PropertyChangeListener {
    /** Serial port connection to controller box. */
    private Serial serialPort;
    /** Experiment window to display the visual trial items. */
    private ExperimentWindow expWindow;
    /** Audio Player to play the audio file. */
    private AudioPlayer audioPlayer;
    /** Experiment Loader to load and parse the tms file. */
    private ExperimentLoader expLoader;
    /** ExperimentResulter to parse the results and print to file. */
    private ExperimentResulter expResulter;
    /** PCS to listen for updates from ExperimentLoader. */
    private PropertyChangeSupport pcs;

    /** Full set of reactions from controller box in bytes. */
    private static final int REACTION_SET = 36;
    /** Experiment Length. */
    private int expLength;
    /** Whether the experiment is loaded. */
    private Boolean experimentLoaded = false;
    /** Whether the experiment is currently running. */
    //private Boolean running = false;
    private AtomicBoolean running;
    /** Whether this object has run the experiment previously */
    private AtomicBoolean previouslyRun;
    /** Whether update has been sent to XMOD about file load failure. */
    private Boolean xmodUpdate = false;
    /** File location */
    private String expFile;
    /** timeout from RT start. */
    private int[] tReactionTimeout;
    /** offset from start to start recording RT. */
    private int[] tMonitorOn;
    /** time to turn monitor off from bleep. */
    private int[] tMonitorOff;
    /**  contains code text fields. */
    private String[] codingArray;
    /**  array of visual trial items. */
    private String[] screenItems;

    /**
     * Constructor.
     * @param aSerialPort Serial object for communication to controller box
     * @param aExpWindow the ExperimentWindow shown to participants
     * @param aAudioPlayer the AudioPlayer object to play the audio file
     */

    public ExperimentRunner(final Serial aSerialPort,
                            final ExperimentWindow aExpWindow,
                            final AudioPlayer aAudioPlayer) {
        this.pcs = new PropertyChangeSupport(this);

        this.serialPort = aSerialPort;
        this.expWindow = aExpWindow;
        this.audioPlayer = aAudioPlayer;
        this.expLoader = new ExperimentLoader();
        this.expLoader.addObserver(this);
        this.running = new AtomicBoolean(); // initial value false
        this.previouslyRun = new AtomicBoolean();// initial value false
    }

    /**
     * Creates ExperimentLoader object and loads the experiment.
     * Sends update to Xmod.
     *  @param filename tms filepath
     */
    public void setUpExperiment(final String filename) {
        this.expFile = filename;
        Boolean nextStep = false;
        Boolean loaded = this.expLoader.loadFile(this.expFile);
        // Load TMS File
        if (loaded) {
            this.expLoader.parseFile();
        }
        // Extract key info and set up resulter
        if (this.expLoader.getTMSLoaded()) {
            this.experimentLoaded = true;
            this.expLength = this.expLoader.getScreenItems().length;

            this.tReactionTimeout = this.expLoader.getTReactionTimeout();
            this.tMonitorOn = this.expLoader.getTMonitorOn();
            this.tMonitorOff = this.expLoader.getTMonitorOff();
            this.codingArray = this.expLoader.getCodingArray();
            this.screenItems = this.expLoader.getScreenItems();
            int[] tReactionOffset = this.expLoader.getTReactionOffset();
            String codehead = this.expLoader.getCodehead();
            this.expResulter = new ExperimentResulter(filename,
                                                        this.expLength,
                                                        tReactionOffset,
                                                        codehead,
                                                        this.codingArray,
                                                        this.screenItems
                                                    );
            this.expResulter.addObserver(this);
            updateStatus(Responses.FILE_LOAD_SUCCESS + filename,
                        "Total number of trials: " + this.expLength,
                        "", "", ReportLabel.TMS);
            return;
        } else {
            this.experimentLoaded = false;
            // If error message has already been sent to xmod
            if (this.xmodUpdate) {
                this.xmodUpdate = false;
                return;
            } else {
                updateStatus(Responses.FILE_LOAD_FAILURE + filename,
                "File load failure not recorded",
                "Please try again with a valid .tms file", "",
                ReportLabel.TMS);
                return;
            }

        }

    }

    /** Returns true if experiment loaded.
     * @return this.experimentLoaded;
     */
    public Boolean isExperimentLoaded() {
        return this.experimentLoaded;
    }

    /** Reload the experiment if previously loaded. */
    private void reloadExperiment(){
        setUpExperiment(this.expFile);
        return;
    }

    /** Sets the previouslyRun flag.
     * Needed to tell if exp needs reloading before running
     * @param flag boolean for the running flag
     * e.g. false to abort experiment
     */
    public void setPreviouslyRun(final Boolean flag) {
        //this.running = flag;
        this.previouslyRun.set(flag);
        return;
    }

    /** Sets the running flag.
     * Useful to abort experiment.
     * @param flag boolean for the running flag
     * e.g. false to abort experiment
     */
    public void setRunning(final Boolean flag) {
        //this.running = flag;
        this.running.set(flag);
        return;
    }
    /** Listen for updates from ExperimentLoader.
     * @param evt property change event
     */
    public void propertyChange(final PropertyChangeEvent evt) {
        String actionType = (String) evt.getPropertyName();
        if  (actionType == Actions.UPDATE) {
            ObjectReport report = (ObjectReport) evt.getNewValue();
            pcs.firePropertyChange(Actions.UPDATE, null, report);
        }
    }

    /** Send updates to main Xmod.java.
     * @param newStatus status
     * @param newMessage message
     * @param newAdvice advice
     * @param newStackTrace any stack trace
     * @param reportName ReportLabel for which section the report is for
     */
    private void updateStatus(final String newStatus,
                                final String newMessage,
                                final String newAdvice,
                                final String newStackTrace,
                                final ReportLabel reportName) {
        ObjectReport report = new ObjectReport(reportName);
        if  (newStatus != "") {
            report.updateValues(ReportCategory.STATUS, newStatus);
        }
        if  (newMessage != "") {
            report.updateValues(ReportCategory.MESSAGE, newMessage);
        }
        if  (newAdvice != "") {
            report.updateValues(ReportCategory.ADVICE, newAdvice);
        }
        if  (newStackTrace != "") {
            report.updateValues(ReportCategory.STACKTRACE, newStackTrace);
        }
        pcs.firePropertyChange(Actions.UPDATE, null, report);
        return;
    }

    /**
     * Generates thread to run experiment in.
     *  */
    public void runExperiment() {
        new Thread(new Runnable() {
            public void run() {
                if (!running.get()) {
                    try {
                      runMethod();
                    } catch (Exception e) {
                        String stackTrace = Utils.getStackTrace(e);
                        updateStatus(Responses.EXPERIMENT_ABORTED,
                        "Experiment aborted due to error", "",
                        stackTrace, ReportLabel.STATUS);
                        setRunning(false);
                    }
                } else {
                    updateStatus("", "Experiment already in progress",
                    "", "", ReportLabel.STATUS);
                }
                return;
            };
        }, "EXPERIMENT RUNNER").start();
    }

    /**
     * Runs the experiment.
     */
    public void runMethod() { //throws SerialBytesReceivedException {
        // CHECK EVERYTHING IS READY

        if  (!this.experimentLoaded) {
            updateStatus(Responses.EXPERIMENT_NOT_READY,
                        "Cannot begin experiment as no experiment loaded",
                        "Please load a valid TMS file",
                        "", ReportLabel.STATUS);
            return;
        }

        if (!this.audioPlayer.isAudioLoaded()) {
            updateStatus(Responses.EXPERIMENT_NOT_READY,
                        "Cannot begin experiment as audio file not loaded",
                        "Please check audio file exists",
                        "", ReportLabel.STATUS);
            return;
        }

        if  (!this.serialPort.isSerialConnected()) {
            updateStatus(Responses.EXPERIMENT_NOT_READY,
                        "Cannot begin experiment as not connected"
                        + " to controller box",
                        "Please check serial port connection",
                        "", ReportLabel.STATUS);
            return;
        }
        // Should reload exp including new ExperimentResulter if run previously
        // If no
        if (this.previouslyRun.get()) {
            try {
                setUpExperiment(this.expFile);
            } catch (NullPointerException e) {
                updateStatus(Responses.EXPERIMENT_NOT_READY,
                        "Experiment not loaded correctly",
                        "Please try again",
                        "", ReportLabel.STATUS);
                return;
            }
        }
        // Set flag to true
        setPreviouslyRun(true);
        setRunning(true);
        updateStatus(Responses.EXPERIMENT_RUNNING,
                        "", "Press ESC key to abort", "",
                        ReportLabel.STATUS);
        this.audioPlayer.playAudio();
        this.expWindow.updateText("");
        this.expWindow.show();
        this.serialPort.turnOffMonitor();

        for (int trialIndex = 0; trialIndex < this.expLength; trialIndex++) {
            // to faciliate aborting the experiment
            if  (!this.running.get()) {
                System.out.println("Quit running");
                break;
            }
            //Show Window and screen item
            this.expWindow.updateText(this.screenItems[trialIndex]);
            this.serialPort.sendTrialTimings(
                                this.tReactionTimeout[trialIndex],
                                this.tMonitorOn[trialIndex],
                                this.tMonitorOff[trialIndex]
                                );
            // complete set of reactions is 36 bytes
            try {
                byte[] reaction = this.serialPort.receiveChunk(
                                                            this.REACTION_SET
                                                            );
                // collects button pressed and reaction time for all 16 boxes
                this.expResulter.collectTrialResults(reaction, trialIndex);
            } catch (SerialBytesReceivedException e) {
                this.running.set(false);
                break; // quit the experiment running
            }
        }
        endExperiment();
    }


    /** Exit the experiment. */
    public void endExperiment() {
        this.audioPlayer.stopAudio();
        this.expResulter.printResults();
        //If experiment not aborted
        if (this.running.get()) {
            //tells main Xmod instance experiment is finished
            //so it can change window views etc
            this.pcs.firePropertyChange(Actions.FINISH_EXPERIMENT, false, true);
            setRunning(false);
        } else {
            // If experiment aborted by user
            updateStatus(Responses.EXPERIMENT_ABORTED,
                        "Experiment aborted by user<br/>"
                        + "Results to date printed to file in "
                        + this.expResulter.getResultsFile(),
                        "", "", ReportLabel.STATUS);
        }
        this.expWindow.hide();
    }
    /**
     * To allow main Xmod instance to listen for updates.
     * @param l listener
     */
    public void addObserver(final PropertyChangeListener l) {
        pcs.addPropertyChangeListener(Actions.FINISH_EXPERIMENT, l);
        pcs.addPropertyChangeListener(Actions.UPDATE, l);
    }
}
