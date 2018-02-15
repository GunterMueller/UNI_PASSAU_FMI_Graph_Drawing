package org.graffiti.plugin.gui;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeListener;

import javax.swing.Action;
import javax.swing.JOptionPane;

/**
 * Wrapper class for actions to add exception handling to method
 * <tt>actionPerformed</tt>
 * 
 * @see javax.swing.Action
 * @see java.awt.event.ActionListener#actionPerformed(ActionEvent)
 * 
 * @author Harald
 * @version $Revision$ $Date$
 */
public class ExceptionHandlingAction implements Action {
    /**
     * The delegate of this wrapper.
     */
    private final Action delegate;

    /**
     * Creates an action with exception handling.
     * 
     * @param delegate
     *            the action used for delegating method calls.
     */
    public ExceptionHandlingAction(Action delegate) {
        this.delegate = delegate;
    }

    /**
     * Delegates to the wrapped action instance.
     */
    public Object getValue(String key) {
        return delegate.getValue(key);
    }

    /**
     * Delegates to this wrapper's delegate action and catches all exceptions
     * thrown while executing it. Shows a message dialog with the exceptions
     * detail message.
     */
    public void actionPerformed(ActionEvent event) {
        try {
            delegate.actionPerformed(event);
        } catch (Exception e) {
            e.printStackTrace();
            StringBuffer s = new StringBuffer(e.getClass().getSimpleName());
            if (e.getMessage() != null) {
                s.append(": ").append(e.getMessage());
            } else {
                s.append(" without detail message.");
            }
            JOptionPane.showMessageDialog(null, s.toString(),
                    "Error while processing Algorithm",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Delegates to the wrapped action instance.
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        delegate.addPropertyChangeListener(listener);
    }

    /**
     * Delegates to the wrapped action instance.
     */
    public boolean isEnabled() {
        return delegate.isEnabled();
    }

    /**
     * Delegates to the wrapped action instance.
     */
    public void putValue(String key, Object value) {
        delegate.putValue(key, value);
    }

    /**
     * Delegates to the wrapped action instance.
     */
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        delegate.removePropertyChangeListener(listener);
    }

    /**
     * Delegates to the wrapped action instance.
     */
    public void setEnabled(boolean b) {
        delegate.setEnabled(b);
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
