package xmod.view;

import xmod.constants.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import java.awt.Color;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;

import java.util.Arrays;

public class ExperimentWindow implements KeyListener{
    public JLabel text;
    public JFrame f;

    public Boolean abort = false;
    PropertyChangeSupport pcs;
    GraphicsEnvironment g;
    GraphicsDevice device;

    JTextArea textArea;
    public String current_font_name;;
    public int current_size;
    public static int default_style = Font.PLAIN;
    public String[] fonts;
    public int MAX_FONT_SIZE = 200;
    /**
     * Constructor
     */
    public ExperimentWindow(){
        //Set up window contents
        this.f = new JFrame();
        // Add listeners
        this.pcs = new PropertyChangeSupport(this);
        this.f.addKeyListener(this);
        
        // Set font name and size
        this.current_font_name = ScreenWords.getDefaultFont();
        this.current_size = ScreenWords.getDefaultSize();
        
        //Set window contents
        this.generateWindowContents();

        this.g = GraphicsEnvironment.getLocalGraphicsEnvironment();
        this.device = this.g.getDefaultScreenDevice();
    }

    /**
     * Sets up the window components
     */
    public void generateWindowContents(){
        // Get fonts
        this.fonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();

        // Add text to centre
        this.text = new JLabel("", SwingConstants.CENTER);
        this.text.setText(ScreenWords.getWords()[0]);
        this.text.setFont(new Font(this.current_font_name, this.default_style, this.current_size));
        this.text.setForeground(Color.WHITE);
        
        this.f.setLayout(new BorderLayout());
        this.f.add(text, BorderLayout.CENTER);

        this.f.getContentPane().setBackground(Color.BLACK);
    }

    /**
     * Change Font and fontsize
     * @param fontname string name of font to change to
     * @param fontsize integer size of font to change to
     */
    public void changeFont(String fontname, int fontsize){
        if (null == fontname || fontsize <= 0){
            return;
        }
        if (Arrays.asList(this.fonts).contains(fontname) && fontsize <= this.MAX_FONT_SIZE){
            this.current_font_name = fontname;
            this.current_size = fontsize;
            this.text.setFont(new Font(this.current_font_name, this.default_style, this.current_size));
            this.f.repaint();
        }
        return;
    }
    
    /**
     * Makes the experiment window full screen and visible
     */
    public void show(){
        this.f.setVisible(true);
        //Make full-screen
        this.f.setResizable(false);
        this.device.setFullScreenWindow(f); 
    }

    /**
     * Makes the experiment screen invisible
     */
    public void hide(){
        this.device.setFullScreenWindow(null);
        this.f.setVisible(false);
    }

    /**
     * Used in Xmod.java to allow the controller to listen to property changes in the view
     */
    public void addObserver(PropertyChangeListener l){
        pcs.addPropertyChangeListener(Actions.ABORT_EXPERIMENT, l);
    }

    /**
     * Listens for the escape key to tell the controller to abort the experiment
     */
    @Override
    public void keyReleased(KeyEvent e){
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_ESCAPE){
            pcs.firePropertyChange(Actions.ABORT_EXPERIMENT, false, true);
            abort = true;
        }
    }
    /**
     * Null; but required in order to implement KeyListener
     */
    @Override
    public void keyPressed(KeyEvent e){};
    /**
     * Null; but required in order to implement KeyListener
     */
    @Override
    public void keyTyped(KeyEvent e){};
}