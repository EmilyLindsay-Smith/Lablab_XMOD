package xmod.utils;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

import xmod.status.ObjectReport;
import xmod.constants.Actions;

public class TestListener implements PropertyChangeListener {
    private Boolean receivedUpdate;

    private ObjectReport latestUpdate;

    public TestListener(){
        this.receivedUpdate = false;
    }

    public void propertyChange(PropertyChangeEvent evt){
        String actionType = (String) evt.getPropertyName();
        if (actionType == Actions.UPDATE){
            this.receivedUpdate = true;
            this.latestUpdate = (ObjectReport) evt.getNewValue();
        }
    }

    public Boolean checkUpdate(){
        return this.receivedUpdate;
    }

    public ObjectReport getUpdateReport(){
        return this.latestUpdate;
    }
}
