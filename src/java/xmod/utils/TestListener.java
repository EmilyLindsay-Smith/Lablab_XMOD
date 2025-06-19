package xmod.utils;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

import xmod.status.ObjectReport;
import xmod.constants.Actions;

public final class TestListener implements PropertyChangeListener {
    /** Whether update has been received. */
    private Boolean receivedUpdate;
    /** Latest update object. */
    private ObjectReport latestUpdate;

    /** Constructor. */
    public TestListener() {
        this.receivedUpdate = false;
    }

    /** Handles property change event.
     * @param evt property change event
     */
    public void propertyChange(final PropertyChangeEvent evt) {
        String actionType = (String) evt.getPropertyName();
        if (actionType == Actions.UPDATE) {
            this.receivedUpdate = true;
            this.latestUpdate = (ObjectReport) evt.getNewValue();
        }
    }

    /** Whether an update has been received.
     * @return boolean of update
    */
    public Boolean checkUpdate() {
        return this.receivedUpdate;
    }

    /** The latest update report.
     * @return latest report
     */
    public ObjectReport getUpdateReport() {
        return this.latestUpdate;
    }
}
