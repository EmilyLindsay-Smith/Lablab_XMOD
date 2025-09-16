package xmod.view;

import xmod.constants.Actions;
import xmod.constants.Operations;
import xmod.status.Responses;

import java.util.ArrayList;

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
import javax.swing.JTabbedPane;

import java.awt.Component;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import javax.swing.filechooser.FileSystemView;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.awt.Color;
import java.awt.Font;

/** MainWindow is the main GUI for the application.
 *
 * @author ELS
 * @version 2.1
 * @since 2025-09-15
 */

public class MainWindow {
    // Components
    /** Main frame. */
    private JFrame f;
    /** Panel for tabs. */
    private JTabbedPane tabPanel;
    /** Header panel. */
    private JPanel header;
    /** Title. */
    private JLabel title;
    /** Subtitle. */
    private JLabel subtitle;

    /** Main Panel. */
    private JPanel mainPanel;
    /** Tool Panel. */
    private JPanel toolPanel;
    /** Analytics Panel. */
    private JPanel analyticsPanel;

    // Main Panel.
    /** Main Panel text panel. */
    private JTextPane mainPanelText;
    /** Main Panel scroll panel. */
    private JScrollPane mainPanelScrollPane;
    /** Main Panel button panel. */
    private JPanel mainButtonPane;
    /** Main Panel button list. */
    private ArrayList<JButton> mainButtonList;
    /** Run Experiment Button. */
    private JButton buttonRunExp;
    /** Load TMS button. */
    private JButton buttonLoadTMS;

    // Tool Panel */
    /** Tool Panel text panel. */
    private JTextPane toolPanelText;
    /** Tool Panel scroll panel. */
    private JScrollPane toolPanelScrollPane;
    /** Tool Panel button panel. */
    private JPanel toolButtonPane;
    /** Tool Panel button list. */
    private ArrayList<JButton> toolButtonList;
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
    /** Button to trigger Test Experiment. */
    private JButton buttonTestSystem;

   //Analytics Panel
    /** Analytics Panel text. */
    private JTextPane analyticsPanelText;
    /** Analytics Panel scroll panel. */
    private JScrollPane analyticsPanelScrollPane;
    /** Analytics Panel button panel. */
    private JPanel analyticsButtonPane;
    /** Analytics Panel button list. */
    private ArrayList<JButton> analyticsButtonList;
    /**This is the official Oxford University Blue. */
    private static final Color OXFORD_BLUE = new Color(0, 33, 71);
    /** Background Color. */
    private Color bg = OXFORD_BLUE;
    /** Foreground color. */
    private Color fg = Color.WHITE;
    // Font Settings
    /** Current font. */
    private String currentFontName;
    /** Current font size. */
    private int currentSize = 18;
    /** Current font style. */
    private static int currentStyle = Font.PLAIN;

    /** Property Change Support. */
    private PropertyChangeSupport pcs;

    /**
     * Constructor.
     */
    public MainWindow() {
        this.generateWindowContents();
        this.setAppearance();


        // Set up listener to respond to button
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
        // Make frame visible to user
        this.f.setVisible(true);
    };
    /** Generates window components and layout. */
    private void generateWindowContents() {
        // Generate JFrame
        this.f = new JFrame("XMOD");
        this.f.setLayout(new BorderLayout());

        // Generate Header
        this.header = new JPanel();
        this.header.setLayout(new BorderLayout());

        this.title = new JLabel("XMOD 2.1", SwingConstants.CENTER);
        this.subtitle = new JLabel("Oxford's Language and Brain Lab",
                                    SwingConstants.CENTER);

        this.header.add(this.title, BorderLayout.NORTH);
        this.header.add(this.subtitle, BorderLayout.SOUTH);
        this.f.add(this.header, BorderLayout.NORTH);

        // Generate Tabs
        this.tabPanel = new JTabbedPane(SwingConstants.TOP);
        this.f.add(this.tabPanel);

        // Generate Main Tab
        this.mainPanel = new JPanel();
        this.tabPanel.addTab("Main", this.mainPanel);

        // Generate Second Tab
        this.toolPanel = new JPanel();
        this.tabPanel.addTab("Tools", this.toolPanel);

        // Generate Third Tab
        this.analyticsPanel = new JPanel();
        this.tabPanel.addTab("Analytics", this.analyticsPanel);
        //

        createMainPanel();
        setPanelAppearance(this.mainButtonPane, this.mainButtonList,
                            this.mainPanelText);
        createToolPanel();
        setPanelAppearance(this.toolButtonPane, this.toolButtonList,
                            this.toolPanelText);
        createAnalyticsPanel();
        setPanelAppearance(this.analyticsButtonPane, this.analyticsButtonList,
                            this.analyticsPanelText);

    }

    /** Sets the colours, fonts and borders of the components. */
    private void setAppearance() {
        this.currentFontName = title.getFont().getFontName();
        //Frame Settings
        this.f.setSize(1000, 750);
        this.f.setLocationRelativeTo(null); //window appears in centre of screen

        this.f.getRootPane().setBorder(BorderFactory.createEmptyBorder(
                                                                20, 5, 20, 5));
        this.f.getRootPane().setBackground(this.bg);
        this.f.getContentPane().setBackground(this.bg);
        // Header Settings
        this.header.setBorder(BorderFactory.createEmptyBorder(30, 60, 30, 60));
        this.header.setBackground(this.bg);
        this.title.setFont(new Font(this.currentFontName, Font.BOLD, (
                                this.currentSize + 10)));
        this.title.setForeground(this.fg);
        this.subtitle.setFont(new Font(this.currentFontName, Font.BOLD,
                                    (this.currentSize + 8)));
        this.subtitle.setForeground(this.fg);

        // Tab Panel Settings
        this.tabPanel.setFont(new Font(this.currentFontName, Font.PLAIN, (
                                this.currentSize)));


        for (int i = 0; i < this.tabPanel.getTabCount(); i++) {
            tabPanel.setBackgroundAt(i, this.bg);
            tabPanel.setForegroundAt(i, this.fg);
            Component panel = tabPanel.getComponentAt(i);
            panel.setBackground(this.bg);
        }
        // Define first panel as selected one
        this.tabPanel.setSelectedIndex(0);
        this.tabPanel.setBackgroundAt(0, this.fg);
        this.tabPanel.setForegroundAt(0, this.bg);
        // Change tab this.bg/this.fg if selected
        this.tabPanel.addChangeListener(new ChangeListener() {
            public void stateChanged(final ChangeEvent e) {
                int selected = tabPanel.getSelectedIndex();
                tabPanel.setBackgroundAt(selected, fg);
                tabPanel.setForegroundAt(selected, bg);

                for (int i = 0; i < tabPanel.getTabCount(); i++) {
                    if (i != selected) {
                        tabPanel.setBackgroundAt(i, bg);
                        tabPanel.setForegroundAt(i, fg);
                    }
                }
            }
        });


    }

    private void createMainPanel() {
        this.mainPanel.setLayout(new BorderLayout());
        this.mainPanelText = createTextPane("Welcome to XMOD");
        this.mainPanelScrollPane = new JScrollPane(this.mainPanelText,
                                        JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                                        JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        this.buttonLoadTMS = new JButton(Operations.LOAD_TMS);
        this.buttonRunExp = new JButton(Operations.RUN_EXP);

        this.mainButtonList = new ArrayList<JButton>();
        this.mainButtonList.add(this.buttonLoadTMS);
        this.mainButtonList.add(this.buttonRunExp);

        addListener(this.mainButtonList);
        this.mainButtonPane = new JPanel(new GridLayout(
                                        this.mainButtonList.size(), 1, 5, 5));
        for (JButton button: this.mainButtonList) {
            this.mainButtonPane.add(button);
        }
        this.mainPanel.add(this.mainButtonPane, BorderLayout.WEST);
        this.mainPanel.add(this.mainPanelScrollPane, BorderLayout.CENTER);

    }

    /**
     * Set up the panel appearance.
     * @param buttonPane panel of buttons
     * @param buttonList list of buttons
     * @param panelText text panel
     */
    private void setPanelAppearance(final JPanel buttonPane,
                                    final ArrayList<JButton> buttonList,
                                    final JTextPane panelText) {

        if (null != buttonPane) {
            buttonPane.setBorder(BorderFactory.createEmptyBorder(
                                                    0, 15, 0, 15));
            buttonPane.setBackground(this.bg);
            }

        if (null != buttonList) {
            for (JButton button: buttonList) {
                button.setFont(new Font(this.currentFontName,
                                            Font.PLAIN, this.currentSize));
                button.setBackground(this.fg);
                button.setForeground(this.bg);
            }
        }
        if (null != panelText) {
            panelText.setBorder(BorderFactory.createEmptyBorder(30, 30,
                                                                 30, 30));
            panelText.setFont(new Font(this.currentFontName, Font.PLAIN,
                                        this.currentSize - 2));
            panelText.setBackground(this.fg);
            panelText.setForeground(this.bg);
        }
    }


    private JTextPane createTextPane(final String initialText) {
        JTextPane textPanel = new JTextPane();
        textPanel.setContentType("text/html");
        textPanel.setEditable(false);
        textPanel.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES,
                                    true); // ensure font can be updated
        textPanel.setText(initialText);
        return textPanel;
    }

    private void createToolPanel() {
        this.toolPanel.setLayout(new BorderLayout());
        this.toolPanelText = createTextPane("XmodTools");
        this.toolPanelScrollPane = new JScrollPane(this.toolPanelText,
                                        JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                                        JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        this.buttonMonitorOn = new JButton(Operations.MONITOR_ON);
        this.buttonMonitorOff = new JButton(Operations.MONITOR_OFF);
        this.buttonCheckConnection = new JButton(Operations.CHECK_CONNECTION);
        this.buttonControllerInfo = new JButton(Operations.CONTROLLER_INFO);
        this.buttonCheckFont = new JButton(Operations.CHECK_FONT);
        this.buttonTestSystem = new JButton(Operations.TEST);

        this.toolButtonList = new ArrayList<JButton>();
        this.toolButtonList.add(this.buttonCheckFont);
        this.toolButtonList.add(this.buttonCheckConnection);
        this.toolButtonList.add(this.buttonControllerInfo);
        this.toolButtonList.add(this.buttonMonitorOn);
        this.toolButtonList.add(this.buttonMonitorOff);
        this.toolButtonList.add(this.buttonTestSystem);

        addListener(this.toolButtonList);
        this.toolButtonPane = new JPanel(new GridLayout(
                                        this.toolButtonList.size(), 1, 5, 5));
        for (JButton button: this.toolButtonList) {
            this.toolButtonPane.add(button);
        }
        this.toolPanel.add(this.toolButtonPane, BorderLayout.WEST);
        this.toolPanel.add(this.toolPanelScrollPane, BorderLayout.CENTER);
    }

    private void createAnalyticsPanel() {
        this.analyticsPanel.setLayout(new BorderLayout());
        this.analyticsPanelText = createTextPane("Analytics: Coming Soon!");
        this.analyticsPanelScrollPane = new JScrollPane(this.analyticsPanelText,
                                        JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                                        JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        this.analyticsPanel.add(this.analyticsPanelScrollPane,
                                        BorderLayout.CENTER);

    }

    /** Makes the main window visible. */
    public void show() {
        f.setVisible(true);
    }

    /** Makes the main window visible. */
    public void hide() {
        f.setVisible(false);
    }

    /** Repaints the frame so updates become visible to user. */
    public void repaint() {
        this.f.repaint();
    };

    /** Updates the text.
     * @param newText new text to show
     */
    public void updateMainText(final String newText) {
        if (newText == "") {
            return;
        }
        this.mainPanelText.setText(newText);
        this.mainPanelText.setCaretPosition(0);
        return;
    }

    /** Updates the text.
     * @param newText new text to show
     */
    public void updateToolText(final String newText) {
        if (newText == "") {
            return;
        }
        this.toolPanelText.setText(newText);
        this.toolPanelText.setCaretPosition(0);
        return;
    }


    /** Popup to select a TMS file.
     * @return string filename of chosen tms file
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

    /** Adds listeners to the buttons to respond to button clicks.
     * @param buttonList list of buttons to add listeners to
     */
    private void addListener(final ArrayList<JButton> buttonList) {
        ActionListener listener = e -> handleOperation(e.getActionCommand());
        for (JButton button : buttonList) {
            button.addActionListener(listener);
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
