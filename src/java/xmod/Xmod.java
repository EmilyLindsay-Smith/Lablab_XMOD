package xmod;

import xmod.view.*;
import xmod.constants.*;
import xmod.status.*;

import java.awt.event.*;
import javax.swing.*;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

public class Xmod implements PropertyChangeListener{
    /*Windows*/
    MainWindow mainWindow;
    /*Objects*/
    Reporter reporter;
     /**
     * Constructor
     */
    Xmod(MainWindow mainWindow, Reporter reporter){
        //Initialise window variables
        this.mainWindow = mainWindow;

        // Initialise Objects
        this.reporter = reporter;
        // Set MainWindow Report
        updateWindowText(); 
    }

     /** Main function to run xmod */
    public static void main(String[] args){
        SwingUtilities.invokeLater(new Runnable(){
            public void run(){
                // Instantiate GUI Windows
                MainWindow mainWindow = new MainWindow();

                //Instantiate Objects
                Reporter reporter = new Reporter();
                Xmod t = new Xmod(mainWindow, reporter);
                // Add observers to respond to buttons/key strokes
                mainWindow.addObserver(t);
                // Show main Window
                mainWindow.show();
            }    
        });
    }

    /**
     * Handles actions on receiving a property change event
     */
    public void propertyChange(PropertyChangeEvent evt){
        String actionType = (String) evt.getPropertyName();
        if (actionType == Actions.OPERATION){
            String action = (String) evt.getNewValue();
            switch(action){
                case Operations.RUN_EXP : operationRunExp(); break;
                case Operations.LOAD_TMS: operationLoadTMS(); break;
                //case Operations.LOAD_WAV: operationLoadWAV(); break;
                case Operations.MONITOR_ON: operationMonitorOn(); break;
                case Operations.MONITOR_OFF: operationMonitorOff(); break;
                case Operations.CHECK_CONNECTION: operationCheckConnection(); break;
                case Operations.CONTROLLER_INFO : operationControllerInfo(); break;
                case Operations.CHECK_FONT: operationCheckFont(); break;
                case Operations.CLOSE_XMOD: operationCloseXmod();
                default: break;
           }
        }
    }

    private void operationLoadTMS(){};
    private void operationRunExp(){};
    private void operationMonitorOn(){};
    private void operationMonitorOff(){};
    private void operationCheckConnection(){};
    private void operationControllerInfo(){};
    private void operationCheckFont(){};
 
    /******************** METHODS RELATED TO UPDATING THE WINDOWS/TEXT/FONT ************/
    
    /**
     * Updates the Reporter
     */
    private void updateStatus(String ReportLabel, String ReportMessage, Boolean replace){
        this.reporter.updateValues(ReportLabel, ReportMessage, replace);
        return;
    }


    /**
     * Updates the central text box on MainWindow
     */
    private void updateWindowText(){
        String newText = this.reporter.convertToString();
        this.mainWindow.text.setText(newText);
        this.mainWindow.f.repaint();
    }

    /******************** METHODS RELATED TO SHUTTING DOWN THE APPLICATION ************/

   /**
     * Handles cleanup on shutting down application
     */
    private void operationCloseXmod(){
        System.out.println("Closing Xmod...");
        System.exit(0);
        System.out.println("XmodClosed...");
    }
}