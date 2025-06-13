package xmod.view;

import xmod.constants.Actions;
import xmod.constants.Operations;
import xmod.status.Responses;

import java.util.ArrayList;
import java.util.Collections;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;


import java.awt.BorderLayout;
import java.awt.GridLayout;

import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.filechooser.FileSystemView;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.awt.Color;
import java.awt.Font;

/**
 * This class creates the main GUI for the application.
 */
public class MainWindow {
    // GUI Components. */
    /** JFrame. */
    public JFrame f;
    /** Header panel. */
    public JPanel header;
    /** Title. */
    public JLabel title;
    /** Subtitle. */
    public JLabel subtitle;
    /** Central text pane. */
    public JTextPane text;
    /** Central text pane scroller. */
    public JScrollPane scrollPane;
    /** Left hand side buttons. */
    public JPanel leftButtonPane;
    /** Right hand side buttons. */
    public JPanel rightButtonPane;

    // List of buttons to facilitate for loops. */
    /** Left hand button list. */
    private ArrayList<JButton> buttonListA;
    /** Right hand butotn list. */
    private ArrayList<JButton> buttonListB;

    // Buttons. */
    /** Run Experiment button. */
    private JButton buttonRunExp;
    /** Load TMS button. */
    private JButton buttonLoadTMS;
    /** Turn monitors on button. */
    private JButton buttonMonitorOn;
    /** Turn monitors off button. */
    private JButton buttonMonitorOff;
    /** Check connection to serial port button. */
    private JButton buttonCheckConnection;
    /** Retrieve info about controller box via serial port button. */
    private JButton buttonControllerInfo;
    /** Button to trigger FontWindow GUI to adjust font for experiments. */
    private JButton buttonCheckFont;

    /** Property Change Support. */
    private PropertyChangeSupport pcs;

    /**This is the official Oxford University Blue. */
    public static final Color OXFORD_BLUE = new Color(0, 33, 71);

    /** Font settings. */
    public String currentFontName;
    public int currentSize = 18;
    public static int currentStyle = Font.PLAIN;

    /**
     * Constructor.
     */
    public MainWindow() {
        // Generate all the GUI Components and layout
        this.generateWindowContents();
        // Sets the colours, fonts and borders of the components
        this.setAppearance();

        //Set up listener to respond to button changes
        pcs = new PropertyChangeSupport(this);
        this.f.addWindowListener(new WindowAdapter() {
            public void windowClosing(final WindowEvent windowEvent) {
                pcs.firePropertyChange(Actions.OPERATION,
                        "", Operations.CLOSE_XMOD);
            }
        });
        // Set closing operation to hide and send property change to main
        // Xmod class to handle shutdown
        this.f.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

    }

    /**
     * Generates the components and their layout.
     */
    private void generateWindowContents() {
        // Main JFrame
        this.f = new JFrame();
        this.f.setLayout(new BorderLayout());

        //Set up Header
        this.header = new JPanel();
        this.header.setLayout(new BorderLayout());

        this.title = new JLabel("XMOD 2.0", SwingConstants.CENTER);
        this.subtitle = new JLabel("Oxford's Language and Brain Lab",
                                    SwingConstants.CENTER);

        this.header.add(this.title, BorderLayout.NORTH);
        this.header.add(this.subtitle, BorderLayout.SOUTH);

        //Set up Central text pane
        this.text = new JTextPane();
        this.text.setContentType("text/html");
        this.text.setEditable(false);
        this.text.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES,
                                        true); // ensure font can be set
        this.scrollPane = new JScrollPane(this.text,
                                        JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                                        JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        //Left Hand Button Pane //rows, cols, hgap, vgap
        this.leftButtonPane = new JPanel(new GridLayout(2, 1, 5, 5));

        this.buttonRunExp = new JButton(Operations.RUN_EXP);
        this.buttonLoadTMS = new JButton(Operations.LOAD_TMS);

        //Right Hand Button Pane //rows, cols, hgap, vgap
        this.rightButtonPane = new JPanel(new GridLayout(5, 1, 5, 5));

        this.buttonMonitorOn = new JButton(Operations.MONITOR_ON);
        this.buttonMonitorOff = new JButton(Operations.MONITOR_OFF);
        this.buttonCheckConnection = new JButton(Operations.CHECK_CONNECTION);
        this.buttonControllerInfo = new JButton(Operations.CONTROLLER_INFO);
        this.buttonCheckFont = new JButton(Operations.CHECK_FONT);

        //Add buttons to buttonListA and buttonListB
        createButtonLists();

        // Add listeners to send property change notification to main Xmod class
        addListener(this.buttonListA); //left hand buttons
        addListener(this.buttonListB); //right hand buttons

        // Place buttons on the screen
        for (JButton button: buttonListA) {
            this.leftButtonPane.add(button);
        }
        for (JButton button: buttonListB) {
            this.rightButtonPane.add(button);
        }

        //Add all contents to Frame
        this.f.add(this.header, BorderLayout.NORTH);
        this.f.add(this.scrollPane, BorderLayout.CENTER);
        this.f.add(this.leftButtonPane, BorderLayout.WEST);
        this.f.add(this.rightButtonPane, BorderLayout.EAST);

    }

    /**
     * Adds buttons to lists.
     */
    private void createButtonLists() {
        // Left hand side buttons
        this.buttonListA = new ArrayList<JButton>();
        this.buttonListA.add(this.buttonLoadTMS);
        this.buttonListA.add(this.buttonRunExp);

        // Right hand side buttons
        this.buttonListB = new ArrayList<JButton>();
        this.buttonListB.add(this.buttonCheckConnection);
        this.buttonListB.add(this.buttonControllerInfo);
        this.buttonListB.add(this.buttonCheckFont);
        this.buttonListB.add(this.buttonMonitorOn);
        this.buttonListB.add(this.buttonMonitorOff);
    }

    /** Sets the colours, fonts and borders of the components. */
    private void setAppearance() {
        this.currentFontName = title.getFont().getFontName();

        //Frame Settings
        this.f.setSize(1000, 750);
        this.f.setLocationRelativeTo(null); //window appears in centre of screen

        this.f.getRootPane().setBorder(BorderFactory.createEmptyBorder(
                                                                20, 5, 20, 5));
        this.f.getRootPane().setBackground(OXFORD_BLUE);

        // Header Settings
        this.header.setBorder(BorderFactory.createEmptyBorder(30, 60, 30, 60));
        this.header.setBackground(OXFORD_BLUE);

        title.setFont(new Font(this.currentFontName, Font.BOLD, (
                                this.currentSize + 10)));
        title.setForeground(Color.WHITE);
        subtitle.setFont(new Font(this.currentFontName, Font.BOLD,
                                    (this.currentSize + 8)));
        subtitle.setForeground(Color.WHITE);

        // Text Settings
        this.text.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        this.text.setFont(new Font(this.currentFontName, Font.PLAIN,
                                    this.currentSize - 2));

        // Left Button Pane
        this.leftButtonPane.setBorder(BorderFactory.createEmptyBorder(
                                                                0, 15, 0, 15));
        this.leftButtonPane.setBackground(OXFORD_BLUE);
        // Right Button Pane
        this.rightButtonPane.setBorder(BorderFactory.createEmptyBorder(
                                                                0, 15, 0, 15));
        this.rightButtonPane.setBackground(OXFORD_BLUE);
        //Buttons
        ArrayList<ArrayList<JButton>> buttonLists =
                                        new ArrayList<ArrayList<JButton>>();
        Collections.addAll(buttonLists, this.buttonListA, this.buttonListB);

        for (ArrayList<JButton> buttonList : buttonLists) {
            for (JButton button: buttonList) {
                button.setFont(new Font(this.currentFontName,
                                         Font.PLAIN, this.currentSize));
            }
        }
    }

    /** Adds listeners to the buttons to respond to button clicks.
     * @param buttonList list of buttons to add listeners to
     */
    private void addListener(final ArrayList<JButton> buttonList) {
        ActionListener listener = e -> handleOperation(e.getActionCommand());
        for (JButton button : buttonList) {
            button.addActionListener(listener);
        }
    }

    /** Makes the main window visible. */
    public void show() {
        f.setVisible(true);
    }

    /** Makes the main window visible. */
    public void hide() {
        f.setVisible(false);
    }

    /** Popup to select a file.
     * @return string filename of chosen file
     * Note this is called in Xmod.java when a button is called
     * but method is in this class as it relates to the main window components
     */
    public String chooseFile() {
        JFileChooser fileChooser = new JFileChooser(FileSystemView
                                    .getFileSystemView().getHomeDirectory());
        // Ensure only .tms files can be selected
        fileChooser.setAcceptAllFileFilterUsed(false);
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                                                "TMS Files", "tms");
        fileChooser.addChoosableFileFilter(filter);
        int choice = fileChooser.showOpenDialog(null);
        if (choice == JFileChooser.APPROVE_OPTION) {
            return fileChooser.getSelectedFile().getAbsolutePath();
        } else {
            return Responses.NO_FILE_SELECTED;
        }
    }

    /**
     * Used in Xmod.java to allow the controller to listen to property changes.
     * i.e. so the buttons can trigger different actions
     * @param l property change listner
     */
    public void addObserver(final PropertyChangeListener l) {
        pcs.addPropertyChangeListener(Actions.OPERATION, l);
    }
    /**
     * Handles sending property changes.
     * @param operation name of button pressed
     */
    private void handleOperation(final String operation) {
       pcs.firePropertyChange(Actions.OPERATION, "", operation);
    }
}
