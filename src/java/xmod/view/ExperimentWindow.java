package xmod.view;

import xmod.constants.Actions;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import java.awt.BorderLayout;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import java.awt.Color;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.KeyEvent;
import java.util.Arrays;

public class ExperimentWindow implements KeyListener {
    /** Text to show trial item. */
    public JLabel text;
    /** Frame for window. */
    public JFrame f;

    /** For user-initiated abort. */
    private Boolean abort = false;
    /** PCs for communicaiton with main xmod. */
    private PropertyChangeSupport pcs;
    /** Graphics Environment for window sizing. */
    private GraphicsEnvironment g;
     /** Graphics Device for window sizing. */
    private GraphicsDevice device;

    /** Central text area. */
    private JTextArea textArea;
    /** Font name. */
    private String currentFontName;;
    /** Font size. */
    private int currentSize;
    /** Font style. */
    private int defaultStyle = Font.PLAIN;
    /** Fonts available on device. */
    private String[] fonts;
    /** Max font size. */
    private static final int MAX_FONT_SIZE = 200;

    /**
     * Constructor.
     */
    public ExperimentWindow() {
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

        this.g = GraphicsEnvironment.getLocalGraphicsEnvironment();
        this.device = this.g.getDefaultScreenDevice();
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

    /**
     * Makes the experiment window full screen and visible.
     */
    public void show() {
        this.f.setVisible(true);
        //Make full-screen
        this.f.setResizable(false);
        this.device.setFullScreenWindow(f);
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
            abort = true;
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
