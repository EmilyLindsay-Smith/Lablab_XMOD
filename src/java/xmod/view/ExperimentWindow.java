package xmod.view;

import xmod.constants.Actions;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

import java.awt.BorderLayout;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import java.awt.Color;
import java.awt.Font;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Arrays;

/** ExperimentWindow is the GUI shown during the experiment.
 *
 * @author ELS
 * @version 2.0
 * @since 2025-06-09
 * NOTES:
 * Many of the integers used in setting borders etc are magic numbers
 * so adjust these cautiously
 */

public class ExperimentWindow implements KeyListener {
    /** Text to show trial item. */
    protected JLabel text;
    /** Frame for window. */
    protected JFrame f;

    /** PCs for communicaiton with main xmod. */
    private PropertyChangeSupport pcs;
    /** Graphics Environment for window sizing. */
    private GraphicsEnvironment g;
     /** Graphics Device for window sizing. */
    private GraphicsDevice device;

    /** Central text area. */
    private JTextArea textArea;
    /** Font name. Protected so FontWindow can access. */
    protected String currentFontName;;
    /** Font size. Protected so FontWindow can access.*/
    protected int currentSize;
    /** Font style.Protected so FontWindow can access. */
    protected int defaultStyle = Font.PLAIN;
    /** Fonts available on device. Protected so FontWindow can access.*/
    protected String[] fonts;
    /** Max font size. */
    protected static final int MAX_FONT_SIZE = 200;
    /** Min font size. */
    protected static final int MIN_FONT_SIZE = 40;

    /**
     * Constructor.
     */
    public ExperimentWindow() {
        // Define screen
        setScreen();
        //Set up window contents
        this.f = new JFrame();
        // Add listeners
        this.pcs = new PropertyChangeSupport(this);
        this.f.addKeyListener(this);

        // Set font name and size
        this.currentFontName = ScreenWords.getDefaultFont();
        this.currentSize = ScreenWords.getDefaultSize();

        //Set window contents
        this.generateWindowContents();

    }

    private void setScreen() {
        this.g = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] gs = this.g.getScreenDevices();
        this.device = this.g.getDefaultScreenDevice();
        for (int i = 0; i < gs.length; i++) {
            if (gs[i] != this.g.getDefaultScreenDevice()) {
                this.device = gs[i];
                break;
            }
        }
    }

    /**
     * Sets up the window components.
     */
    public void generateWindowContents() {
        // Get fonts
        this.fonts = GraphicsEnvironment.getLocalGraphicsEnvironment()
                            .getAvailableFontFamilyNames();

        // Add text to centre
        this.text = new JLabel("", SwingConstants.CENTER);
        this.text.setText(ScreenWords.getWords()[0]);
        this.text.setFont(new Font(this.currentFontName,
        this.defaultStyle, this.currentSize));
        this.text.setForeground(Color.WHITE);
        this.f.setLayout(new BorderLayout());

        this.f.add(text, BorderLayout.CENTER);
        this.f.getContentPane().setBackground(Color.BLACK);
        this.f.pack();
    }

    /**
     * Change Font and fontsize.
     * @param fontname string name of font to change to
     * @param fontsize integer size of font to change to
     */
    public void changeFont(final String fontname, final int fontsize) {
        if (null == fontname || fontsize <= 0) {
            return;
        }
        if (Arrays.asList(this.fonts).contains(fontname)
                && fontsize <= this.MAX_FONT_SIZE) {
            this.currentFontName = fontname;
            this.currentSize = fontsize;
            this.text.setFont(new Font(this.currentFontName,
                this.defaultStyle, this.currentSize));
            this.f.repaint();
        }
        return;
    }

    /** Updates the text.
     * @param newText new text to show
     */
    public void updateText(final String newText) {
        if (newText == "") {
            return;
        }
        this.text.setText(newText);
        return;
    }


    /**
     * Makes the experiment window full screen and visible.
     */
    public void show() {
        //Make full-screen
        //this.f.setLocationRelativeTo(null);
        this.f.setResizable(false);
        this.device.setFullScreenWindow(f);
        this.f.setVisible(true);
    }

    /**
     * Makes the experiment screen invisible.
     */
    public void hide() {
        this.device.setFullScreenWindow(null);
        this.f.setVisible(false);
    }

    /**
     * Used in Xmod.java to allow the controller to listen to property changes.
     * @param l listener
     */
    public void addObserver(final PropertyChangeListener l) {
        pcs.addPropertyChangeListener(Actions.ABORT_EXPERIMENT, l);
    }

    /**
     * Listens for the escape key to tell the controller to abort.
     */
    @Override
    public void keyReleased(final KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_ESCAPE) {
            pcs.firePropertyChange(Actions.ABORT_EXPERIMENT, false, true);
        }
    }
    /**
     * Null; but required in order to implement KeyListener.
     */
    @Override
    public void keyPressed(final KeyEvent e) { };

    /**
     * Null; but required in order to implement KeyListener.
     */
    @Override
    public void keyTyped(final KeyEvent e) { };
}
