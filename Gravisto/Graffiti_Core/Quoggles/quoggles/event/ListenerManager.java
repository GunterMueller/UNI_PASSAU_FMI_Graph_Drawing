package quoggles.event;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Holds a list of listeners.
 */
public class ListenerManager implements RepChangeListener {

    private List listeners = new ArrayList(1);


    /**
     * Adds a listener. It is not added several times.
     * 
     * @param listener
     */
    public void addListener(RepChangeListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    /**
     * Removes a listener. Nothing happens if the given listener has not been
     * registered yet.
     * 
     * @param listener
     */
    public void removeListener(RepChangeListener listener) {
        listeners.remove(listener);
    }

    /**
     * Calls the method in all registered listeners.
     * 
     * @see quoggles.event.RepChangeListener#repChanged(quoggles.event.RepChangeEvent)
     */
    public void repChanged(RepChangeEvent event) {
        for (Iterator it = listeners.iterator(); it.hasNext();) {
            RepChangeListener listener = (RepChangeListener)it.next();
            listener.repChanged(event);
        }
    }
}
