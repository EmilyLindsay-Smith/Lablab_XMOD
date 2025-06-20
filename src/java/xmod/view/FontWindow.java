package xmod.view;

import xmod.constants.Actions;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;

import java.awt.BorderLayout;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.awt.Color;
import java.awt.Font;


/** FontWindow is where the font size and family can be updated by the user.
 * It extends ExperimentWindow so that ExperimentWindow can be updated as well.
 * @author ELS
 * @version 2.0
 * @since 2025-06-09
 */

public class FontWindow extends ExperimentWindow {
    //inherits all the variables from ExperimentWindow plus the following
    /** Ok button. */
    private JButton okButton;
    /** Word switch button. */
    private JButton wordSwitchButton;
    /** Font chooser combobox. */
    private JComboBox<String> fontChooser;
    /** Size chooser combobox. */
    private JComboBox<Integer> sizeChooser;
    /** PCS for communication with main XMOD. */
    private PropertyChangeSupport pcs;
    /** Max number of rows to show. */
    static final Integer MAX_ROW_COUNT = 15;

    /** Original font before any changes. */
    private String originalFontName;
    /** Original font size before any changes. */
    private int originalSize;
    /** Current font. */
    private String currentFontName;
    /** Current font size.. */
    private int currentSize;


    /** Array of available words to display. */
    private String[] availableWords;

    /**
     * Constructor.
     */
    public FontWindow() {
        super(); // calls ExperimentWindow constructor
        pcs = new PropertyChangeSupport(this);

        // Handle closure behaviour
        // hide this window if closed by standard buttons
        this.f.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        this.f.addWindowListener(new WindowAdapter() {
            public void windowClosing(final WindowEvent windowEvent) {
                undoChanges(); // undo changes
            }
        });

        this.availableWords = ScreenWords.getWords();
        this.originalFontName = ScreenWords.getDefaultFont();
        this.originalSize = ScreenWords.getDefaultSize();

        // Create Componenets
        this.generateExtraWindowContents();
    }

    /**
     * Generates all the extra contents of the window.
     */
    private void generateExtraWindowContents() {
        this.createFontChooser();
        this.createSizeChooser();
        this.createOkButton();
        this.createWordSwitchButton();

        JPanel optionPanel = new JPanel();
        optionPanel.setBorder(BorderFactory.createEmptyBorder(30, 60, 30, 60));
        optionPanel.setLayout(new BorderLayout());

        optionPanel.add(this.fontChooser, BorderLayout.WEST);
        optionPanel.add(this.sizeChooser, BorderLayout.CENTER);
        optionPanel.add(this.okButton, BorderLayout.EAST);
        optionPanel.setBackground(Color.WHITE);

        this.f.getContentPane().add(this.wordSwitchButton, BorderLayout.SOUTH);

        this.f.getContentPane().add(optionPanel, BorderLayout.NORTH);
        return;
    }


    /** So main instance can list for call to update the font.
     * @param l listener
    */
    public void addObserver(final PropertyChangeListener l) {
        pcs.addPropertyChangeListener(Actions.UPDATE_FONT, l);
    }

    /**
     * Resets to original font and size if not closed by OK button.
     */
    public void undoChanges() {
        this.currentFontName = this.originalFontName;
        this.currentSize = this.originalSize;
        this.text.setFont(new Font(this.currentFontName, this.defaultStyle,
                                this.currentSize));
        this.fontChooser.setSelectedItem(this.currentFontName);
        this.sizeChooser.setSelectedItem(this.currentSize);
        this.f.repaint();
        return;
    }
    /**
     * Creates the font chooser drop down.
     */
    private void createFontChooser() {
        this.fontChooser = new JComboBox<String>(this.fonts);
        this.fontChooser.setBackground(Color.WHITE);
        this.fontChooser.setForeground(Color.RED);

        this.fontChooser.setSelectedItem(this.currentFontName);
        this.fontChooser.setLightWeightPopupEnabled(false);

        this.fontChooser.setMaximumRowCount(this.MAX_ROW_COUNT);

        this.fontChooser.addActionListener((e) -> {
            String newFontName = (String) this.fontChooser.getSelectedItem();
            this.currentFontName = newFontName;
            this.text.setFont(new Font(this.currentFontName,
                                this.defaultStyle, this.currentSize));
            this.f.repaint();
        });
    }

    /**
     * Creates the size chooser drop down.
     */
    private void createSizeChooser() {

        this.sizeChooser = new JComboBox<Integer>();
        for (int i = this.MIN_FONT_SIZE; i < this.MAX_FONT_SIZE; i++) {
            sizeChooser.addItem(i);
            }

        this.sizeChooser.setBackground(Color.WHITE);
        this.sizeChooser.setForeground(Color.RED);
        this.sizeChooser.setSelectedItem(this.currentSize);

        this.sizeChooser.setMaximumRowCount(this.MAX_ROW_COUNT);

        this.sizeChooser.addActionListener((e) -> {
            int selectedSize = (int) this.sizeChooser.getSelectedItem();
            this.currentSize = selectedSize;
            this.text.setFont(new Font(this.currentFontName,
                             this.defaultStyle, this.currentSize));
            this.f.repaint();
        });
        return;
    }

    /** creates the ok button. */
    private void createOkButton() {
        this.okButton = new JButton("OK");
        this.okButton.setBackground(Color.WHITE);
        this.okButton.setForeground(Color.RED);

        this.okButton.addActionListener((e) -> {
            this.originalFontName = this.currentFontName;
            this.originalSize = this.currentSize;
            pcs.firePropertyChange(Actions.UPDATE_FONT, false, true);
        });
        return;
    }

    /** creates the word switch button. */
    private void createWordSwitchButton() {
        this.wordSwitchButton = new JButton("Switch Word");
        this.wordSwitchButton.setBackground(Color.WHITE);
        this.wordSwitchButton.setForeground(Color.RED);

        this.wordSwitchButton.addActionListener((e) -> {
            String currentWord = this.text.getText();
            int index = ScreenWords.getIndex(currentWord, this.availableWords);
            int newIndex = index + 1;
            if (newIndex >= this.availableWords.length) {
                newIndex = 0;
            }
            this.text.setText(this.availableWords[newIndex]);
        });
        return;
    }

    /** Gets current font .
     * @return this.current_font_name
     */
    public String getCurrentFont(){
        return this.currentFontName;
    }

    /** Gets current font size.
     * @return this.current_size
     */
    public int getCurrentSize(){
        return this.currentSize;
    }
     /**
     * Listens for the escape key.
     * to tell the controller to close window nad update font
     */
    @Override
    public void keyReleased(final KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_ESCAPE) {
            System.out.println("Escape!");
            pcs.firePropertyChange(Actions.UPDATE_FONT, false, true);
        }
    }

    /**
     * Overrides ExperimentWindow show() to make visible.
     * using fullScreenWindow causes failure in JComboBox
     * to show dropdown menu due to internal calculations around window size
     */

    public void show() {
        this.f.setSize(1000, 1000);
        this.f.setLocationRelativeTo(null);
        this.f.setVisible(true);
    }

    /**
     * Overrides ExperimentWindow hide() as not using full screen mode.
     */
    public void hide() {
        this.f.setVisible(false);
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
