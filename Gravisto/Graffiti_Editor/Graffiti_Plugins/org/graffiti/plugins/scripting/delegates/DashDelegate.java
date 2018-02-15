package org.graffiti.plugins.scripting.delegates;

import org.graffiti.graphics.Dash;
import org.graffiti.plugins.scripting.DefaultDocumentation;
import org.graffiti.plugins.scripting.Scope;
import org.graffiti.plugins.scripting.delegate.DelegateFactory;
import org.graffiti.plugins.scripting.delegate.ObjectDelegate;
import org.graffiti.plugins.scripting.delegate.ScriptedConstructor;
import org.graffiti.plugins.scripting.delegate.Unwrappable;
import org.graffiti.plugins.scripting.exceptions.IllegalScriptingArgumentException;
import org.graffiti.plugins.scripting.exceptions.ScriptingException;
import org.graffiti.plugins.scripting.reflect.DocumentedDelegate;

/**
 * @scripted The dash.
 * @author Andreas Glei&szlig;ner
 */
@DocumentedDelegate(DefaultDocumentation.class)
public class DashDelegate extends ObjectDelegate implements Unwrappable<Dash> {
    public static class Factory extends DelegateFactory<DashDelegate, Dash> {
        public Factory(Scope scope) {
            super(scope, DashDelegate.class);
        }

        @Override
        public DashDelegate create(Dash dash) {
            return new DashDelegate(scope, dash);
        }
    }

    private float phase;
    private float[] array;

    public DashDelegate(Scope scope, Dash dash) {
        super(scope);
        phase = dash.getDashPhase();
        array = dash.getDashArray();
        if (array == null) {
            array = new float[0];
        }
    }

    @ScriptedConstructor("Dash")
    public DashDelegate(Scope scope, Number phase, Object[] values)
            throws ScriptingException {
        super(scope);
        this.phase = phase.floatValue();
        if (values.length != 1 || !(values[0] instanceof Object[]))
            throw new IllegalScriptingArgumentException("Dash");
        values = (Object[]) values[0];
        int len = values.length;
        array = new float[len];
        for (int i = 0; i < len; i++) {
            if (!(values[i] instanceof Number))
                throw new IllegalScriptingArgumentException("Dash");
            array[i] = ((Number) values[i]).floatValue();
        }
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer("[Dash phase=").append(phase)
                .append("; array=[");
        int len = array.length;
        if (len > 0) {
            buffer.append(array[0]);
        }
        for (int i = 1; i < len; i++) {
            buffer.append(", ").append(array[i]);
        }
        return buffer.append("]]").toString();
    }

    public Dash unwrap() {
        return new Dash(array, phase);
    }
}
