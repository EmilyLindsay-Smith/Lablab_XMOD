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
import javax.swing.JTabbedPane;

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

    private JFrame f;
    private JTabbedPane tabPanel;
    /** Header panel. */
    private JPanel header;
    /** Title. */
    private JLabel title;
    /** Subtitle. */
    private JLabel subtitle;
    /** Central text pane. */
    private JTextPane text;
    private JPanel mainPanel;
    private JPanel secondPanel;


    /**This is the official Oxford University Blue. */
    private static final Color OXFORD_BLUE = new Color(0, 33, 71);

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
    };
    /** Generates window components and layout */
    private void generateWindowContents() {
        this.f = new JFrame("XMOD");
        this.f.setLayout(new BorderLayout());

        // Set closing operation to hide and send property change to main
        // Xmod class to handle shutdown
        this.f.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

                //Set up Header
        this.header = new JPanel();
        this.header.setLayout(new BorderLayout());

        this.title = new JLabel("XMOD 2.0", SwingConstants.CENTER);
        this.subtitle = new JLabel("Oxford's Language and Brain Lab",
                                    SwingConstants.CENTER);

        this.header.add(this.title, BorderLayout.NORTH);
        this.header.add(this.subtitle, BorderLayout.SOUTH);
        this.f.add(this.header, BorderLayout.NORTH);


        this.tabPanel = new JTabbedPane(SwingConstants.TOP);
        this.mainPanel = new JPanel();
        this.mainPanel.add(new JLabel("Main Panel"));

        this.secondPanel = new JPanel();
        this.secondPanel.add(new JLabel("Second Panel"));

        this.tabPanel.addTab("Main", this.mainPanel);
        this.tabPanel.addTab("Second", this.secondPanel);
        this.f.add(tabPanel);
        this.f.setVisible(true);
    }

        /** Sets the colours, fonts and borders of the components. */
    private void setAppearance() {
        this.currentFontName = title.getFont().getFontName();
        Color bg = OXFORD_BLUE;
        Color fg = Color.WHITE;
        //Frame Settings
        this.f.setSize(1000, 750);
        this.f.setLocationRelativeTo(null); //window appears in centre of screen

        this.f.getRootPane().setBorder(BorderFactory.createEmptyBorder(
                                                                20, 5, 20, 5));
        this.f.getRootPane().setBackground(bg);
        this.f.getContentPane().setBackground(bg);
        this.f.getContentPane().setForeground(bg);

        // Header Settings
        this.header.setBorder(BorderFactory.createEmptyBorder(30, 60, 30, 60));
        this.header.setBackground(bg);

        this.title.setFont(new Font(this.currentFontName, Font.BOLD, (
                                this.currentSize + 10)));
        this.title.setForeground(fg);
        this.subtitle.setFont(new Font(this.currentFontName, Font.BOLD,
                                    (this.currentSize + 8)));
        this.subtitle.setForeground(fg);

        this.tabPanel.setBackground(bg);
        this.tabPanel.setFont(new Font(this.currentFontName, Font.PLAIN, (
                                this.currentSize)));
        this.tabPanel.setForeground(fg);

        /*
        this.tabPanel.setBackgroundAt(0, fg);
        this.tabPanel.setForegroundAt(0, bg);
        this.tabPanel.setBackgroundAt(1, fg);
        this.tabPanel.setForegroundAt(1, bg);
        */
        this.tabPanel.addChangeListener(new ChangeListener(){
            public void stateChanged(ChangeEvent e){
                int selected = tabPanel.getSelectedIndex();
                tabPanel.setBackgroundAt(selected, fg);
                tabPanel.setForegroundAt(selected, bg);
                for (int i=0; i < tabPanel.getTabCount(); i++){
                    if (i != selected){
                        tabPanel.setBackgroundAt(i, bg);
                        tabPanel.setForegroundAt(i, fg);
                    }
                }
            }
        });

        this.mainPanel.setBackground(bg);
        this.mainPanel.setFont(new Font(this.currentFontName, Font.PLAIN, (
                                this.currentSize)));
        this.mainPanel.setForeground(fg);
        this.secondPanel.setBackground(bg);
        this.secondPanel.setFont(new Font(this.currentFontName, Font.PLAIN, (
                                this.currentSize)));
        this.secondPanel.setForeground(fg);
        // Text Settings
        /*
        this.text.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        this.text.setFont(new Font(this.currentFontName, Font.PLAIN,
                                    this.currentSize - 2));
        */
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

    public void updateText(final String newText) {};
    public String chooseFile() {return "";};

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
