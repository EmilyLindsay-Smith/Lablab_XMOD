package xmod.view;

import xmod.constants.*; // For Actions and Operations
import xmod.status.*; // For Responses.NO_FILE_SELECTED

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Collections;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import javax.swing.filechooser.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.awt.Color;
import java.awt.Font;

/**
 * This class creates the main GUI for the application
 */
public class MainWindow{
    /** GUI Components */
    public JFrame f;
    public JPanel header;
    public JLabel title;
    public JLabel subtitle;
    public JTextPane text;
    public JScrollPane scrollPane;
    public JPanel leftButtonPane;
    public JPanel rightButtonPane;
    
    /** List of buttons to facilitate for loops */
    ArrayList<JButton> buttonListA;
    ArrayList<JButton> buttonListB;

    /** Buttons */
    JButton button_RunExp;
    JButton button_LoadTMS;
    JButton button_MonitorOn;
    JButton button_MonitorOff;
    JButton button_CheckConnection;
    JButton button_ControllerInfo;
    JButton button_CheckFont;

    PropertyChangeSupport pcs;

    /**This is the official Oxford University Blue */
    public static final Color OXFORD_BLUE = new Color(0, 33, 71);

    /** Font settings */
    public String current_font_name;
    public int current_size = 18;
    public static int default_style = Font.PLAIN;

    /**
     * Constructor
     */
    public MainWindow(){
        // Generate all the GUI Components and layout
        this.generateWindowContents();
        // Sets the colours, fonts and borders of the components
        this.setAppearance();

        //Set up listener to respond to button changes 
        pcs = new PropertyChangeSupport(this);
        this.f.addWindowListener(new WindowAdapter(){
            public void windowClosing(WindowEvent windowEvent){
                pcs.firePropertyChange(Actions.OPERATION, "", Operations.CLOSE_XMOD);
            }
        });
        // Set closing operation to hide and send property change to main Xmod class to handle shutdown
        this.f.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

    }

    /**
     * Generates the components and their layout
     */
    private void generateWindowContents(){
        // Main JFrame
        this.f = new JFrame();
        this.f.setLayout(new BorderLayout());

        //Set up Header
        this.header = new JPanel();
        this.header.setLayout(new BorderLayout());
    
        this.title = new JLabel("XMOD 2.0", SwingConstants.CENTER);
        this.subtitle = new JLabel("Oxford's Language and Brain Lab", SwingConstants.CENTER);
       
        this.header.add(this.title, BorderLayout.NORTH);
        this.header.add(this.subtitle, BorderLayout.SOUTH);
        
        //Set up Central text pane
        this.text = new JTextPane();
        this.text.setContentType("text/html");
        this.text.setEditable(false);
        this.text.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, true); // ensure font can be set
        this.scrollPane = new JScrollPane(this.text, 
                                            JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                                            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        //Left Hand Button Pane
        this.leftButtonPane = new JPanel(new GridLayout(2,1,5,5)); //rows, cols, hgap, vgap
       
        this.button_RunExp = new JButton(Operations.RUN_EXP);
        this.button_LoadTMS = new JButton(Operations.LOAD_TMS);
        
        //Right Hand Button Pane
        this.rightButtonPane = new JPanel(new GridLayout(5,1,5,5)); //rows, cols, hgap, vgap
        
        this.button_MonitorOn = new JButton(Operations.MONITOR_ON);
        this.button_MonitorOff = new JButton(Operations.MONITOR_OFF);
        this.button_CheckConnection = new JButton(Operations.CHECK_CONNECTION);
        this.button_ControllerInfo = new JButton(Operations.CONTROLLER_INFO);
        this.button_CheckFont = new JButton(Operations.CHECK_FONT);
      
        //Add buttons to buttonListA and buttonListB
        createButtonLists();

        // Add listeners to send property change notification to main Xmod class
        addListener(this.buttonListA); //left hand buttons
        addListener(this.buttonListB); //right hand buttons 

        // Place buttons on the screen 
        for (JButton button: buttonListA){
            this.leftButtonPane.add(button);
        }
        for (JButton button: buttonListB){
            this.rightButtonPane.add(button);
        }

        //Add all contents to Frame
        this.f.add(this.header, BorderLayout.NORTH);
        this.f.add(this.scrollPane, BorderLayout.CENTER);
        this.f.add(this.leftButtonPane, BorderLayout.WEST);
        this.f.add(this.rightButtonPane, BorderLayout.EAST);
        
    }

    /**
     * Adds buttons to lists
     */
    private void createButtonLists(){
        // Left hand side buttons
        this.buttonListA = new ArrayList<JButton>();
        this.buttonListA.add(this.button_LoadTMS);
        this.buttonListA.add(this.button_RunExp);

        // Right hand side buttons
        this.buttonListB = new ArrayList<JButton>();
        this.buttonListB.add(this.button_CheckConnection);
        this.buttonListB.add(this.button_ControllerInfo);
        this.buttonListB.add(this.button_CheckFont);
        this.buttonListB.add(this.button_MonitorOn);
        this.buttonListB.add(this.button_MonitorOff);
    }

    /** Sets the colours, fonts and borders of the components */
    private void setAppearance(){
        this.current_font_name = title.getFont().getFontName();

        //Frame Settings
        this.f.setSize(1000, 750);
        this.f.setLocationRelativeTo(null); // window appears in centre of screen

        this.f.getRootPane().setBorder(BorderFactory.createEmptyBorder(20,5,20,5));
        this.f.getRootPane().setBackground(OXFORD_BLUE);

        // Header Settings
        this.header.setBorder(BorderFactory.createEmptyBorder(30,60,30,60));
        this.header.setBackground(OXFORD_BLUE);

        title.setFont(new Font(this.current_font_name, Font.BOLD, (this.current_size + 10)));
        title.setForeground(Color.WHITE);
        subtitle.setFont(new Font(this.current_font_name, Font.BOLD, (this.current_size + 8)));
        subtitle.setForeground(Color.WHITE);

        // Text Settings
        this.text.setBorder(BorderFactory.createEmptyBorder(30,30,30,30));
        this.text.setFont(new Font(this.current_font_name, Font.PLAIN, this.current_size -2 ));

        // Left Button Pane
        this.leftButtonPane.setBorder(BorderFactory.createEmptyBorder(0,15,0,15));
        this.leftButtonPane.setBackground(OXFORD_BLUE);
        // Right Button Pane
        this.rightButtonPane.setBorder(BorderFactory.createEmptyBorder(0,15,0,15));
        this.rightButtonPane.setBackground(OXFORD_BLUE);
        //Buttons
        ArrayList<ArrayList<JButton>> buttonLists = new ArrayList<ArrayList<JButton>>();
        Collections.addAll(buttonLists, this.buttonListA, this.buttonListB);

        for (ArrayList<JButton> buttonList : buttonLists){
            for (JButton button: buttonList){
                button.setFont(new Font(this.current_font_name, Font.PLAIN, this.current_size));
            }
        }
    }

    /** Adds listeners to the buttons to respond to button clicks */ 
    private void addListener(ArrayList<JButton> buttonList){
        ActionListener listener = e -> handleOperation(e.getActionCommand());
        for(JButton button : buttonList){
            button.addActionListener(listener); 
        }
    }
  
    /** Makes the main window visible */
    public void show(){
        f.setVisible(true);
    }

    /** Makes the main window visible */
    public void hide(){
        f.setVisible(false);
    }

    /** Popup to select a file
     * @return string filename of chosen file
     * Note this is called in Xmod.java when a button is called, but method is in this class as it relates to the main window components
     */
    public String chooseFile(){
        JFileChooser fileChooser = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
        // Ensure only .tms files can be selected
        fileChooser.setAcceptAllFileFilterUsed(false);
        FileNameExtensionFilter filter = new FileNameExtensionFilter("TMS Files", "tms");
        fileChooser.addChoosableFileFilter(filter);
        int choice = fileChooser.showOpenDialog(null);
        if (choice == JFileChooser.APPROVE_OPTION){
            return fileChooser.getSelectedFile().getAbsolutePath();
        }else{
            return Responses.NO_FILE_SELECTED;
        }
    }

    /**
     * Used in Xmod.java to allow the controller to listen to property changes in the view
     * i.e. so the buttons can trigger different actions
     */
    public void addObserver(PropertyChangeListener l){
        pcs.addPropertyChangeListener(Actions.OPERATION, l);
    }
    private void handleOperation(String operation){
       pcs.firePropertyChange(Actions.OPERATION, "", operation);
    }
}