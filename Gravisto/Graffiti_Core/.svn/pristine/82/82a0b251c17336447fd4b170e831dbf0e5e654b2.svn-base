package org.graffiti.plugins.scripting.delegates;

import org.graffiti.plugin.view.interactive.SlotMap;
import org.graffiti.plugins.scripting.DelegateWrapperUtil;
import org.graffiti.plugins.scripting.Scope;
import org.graffiti.plugins.scripting.delegate.ObjectDelegate;
import org.graffiti.plugins.scripting.exceptions.ScriptingException;

/**
 * @author Andreas Glei&szlig;ner
 */
public class SlotListDelegate extends ObjectDelegate {
    private SlotMap slots;

    public SlotListDelegate(Scope scope, SlotMap slots) {
        super(scope);
        this.slots = slots;
    }

    public void setSlots(SlotMap slots) {
        this.slots = slots;
    }

    @Override
    public Object get(String name) throws ScriptingException {
        Object object = DelegateWrapperUtil.get(slots, name, scope);
        if (object != null)
            return object;
        object = super.get(name);
        if (object == UNDEFINED)
            return null;
        else
            return object;
    }

    @Override
    public void put(String name, Object value) {
        DelegateWrapperUtil.put(slots, name, value);
    }

    @Override
    public String toString() {
        return "[SlotMap]";
    }
}
