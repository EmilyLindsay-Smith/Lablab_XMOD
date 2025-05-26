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

     /**
     * Constructor
     */
    Xmod(MainWindow mainWindow){
        //Initialise window variables
        this.mainWindow = mainWindow;
    }

     /** Main function to run xmod */
    public static void main(String[] args){
        SwingUtilities.invokeLater(new Runnable(){
            public void run(){
                // Instantiate GUI Windows
                MainWindow mainWindow = new MainWindow();
                Xmod t = new Xmod(mainWindow);
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