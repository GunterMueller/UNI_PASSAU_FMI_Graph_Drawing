package org.graffiti.plugins.scripting.delegates;

import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.graffiti.plugin.view.interactive.PopupMenuItem;
import org.graffiti.plugin.view.interactive.SlotMap;
import org.graffiti.plugins.scripting.Scope;
import org.graffiti.plugins.scripting.delegate.FieldAccess;
import org.graffiti.plugins.scripting.delegate.FieldDelegate;
import org.graffiti.plugins.scripting.delegate.ObjectDelegate;
import org.graffiti.plugins.scripting.delegate.ScriptedConstructor;
import org.graffiti.plugins.scripting.delegate.ScriptedField;
import org.graffiti.plugins.scripting.delegate.Unwrappable;

/**
 * @author Andreas Glei&szlig;ner
 */
public final class MenuDelegate extends ObjectDelegate implements
        Unwrappable<PopupMenuItem> {
    private SortedMap<Integer, MenuDelegate> children;

    @ScriptedField
    protected FieldDelegate<String> id = new FieldDelegate<String>(String.class) {
        @Override
        public String get() {
            return menuItem.getId();
        }

        @Override
        public void set(String value) {
            menuItem.setId(value);
        }
    };

    @ScriptedField
    protected FieldDelegate<String> label = new FieldDelegate<String>(
            String.class) {
        @Override
        public String get() {
            return menuItem.getLabel();
        }

        @Override
        public void set(String value) {
            menuItem.setLabel(value);
        }
    };

    private PopupMenuItem menuItem;

    @ScriptedField(access = FieldAccess.Get)
    protected SlotListDelegate out;

    @ScriptedConstructor("Menu")
    public MenuDelegate(Scope scope) {
        super(scope);

        children = new TreeMap<Integer, MenuDelegate>();
        SlotMap slots = new SlotMap();
        out = new SlotListDelegate(scope, slots);
        menuItem = new PopupMenuItem();
        menuItem.setSlots(slots);
    }

    @Override
    public Object get(int index) {
        MenuDelegate child = children.get(index);
        if (child == null) {
            child = new MenuDelegate(scope);
            children.put(index, child);
            menuItem.add(index, child.menuItem);
        }
        return child;
    }

    @Override
    public Set<Integer> getIndices() {
        return children.keySet();
    }

    @Override
    public boolean has(int index) {
        return children.containsKey(index);
    }

    @Override
    public String toString() {
        return "[Menu]";
    }

    public PopupMenuItem unwrap() {
        return menuItem;
    }
}
