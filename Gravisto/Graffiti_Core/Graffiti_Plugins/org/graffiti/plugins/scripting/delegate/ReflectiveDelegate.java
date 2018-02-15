package org.graffiti.plugins.scripting.delegate;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;

import org.graffiti.plugins.scripting.DefaultDocumentation;
import org.graffiti.plugins.scripting.exceptions.ScriptingException;
import org.graffiti.plugins.scripting.reflect.DocumentedDelegate;
import org.graffiti.plugins.scripting.reflect.HelpFormatter;
import org.graffiti.plugins.scripting.reflect.MemberDesc;

/**
 * @author Andreas Glei&szlig;ner
 */
@DocumentedDelegate(DefaultDocumentation.class)
public abstract class ReflectiveDelegate extends ScriptingDelegate {
    protected final DelegateEntry entry;

    protected Map<String, FunctionDelegate> methods;

    protected ReflectiveDelegate() {
        methods = new HashMap<String, FunctionDelegate>();
        entry = DelegateManager.getEntry(this);
    }

    public void delete(String name) throws ScriptingException {
    }

    public void delete(int index) throws ScriptingException {
    }

    public Object get(String name) throws ScriptingException {
        FunctionDelegate functionDelegate = entry
                .getMethod(name, methods, this);
        if (functionDelegate != null)
            return functionDelegate;
        return ScriptingDelegate.UNDEFINED;
    }

    public Object get(int index) throws ScriptingException {
        return ScriptingDelegate.UNDEFINED;
    }

    public String getClassName() {
        return getClass().getCanonicalName();
    }

    public void addDynamicMemberInfo(SortedMap<String, MemberDesc> map) {
    }

    public Set<Integer> getIndices() {
        return Collections.emptySet();
    }

    public boolean has(String name) {
        return methods.containsKey(name);
    }

    public boolean has(int index) {
        return false;
    }

    public void put(String name, Object value) throws ScriptingException {
    }

    public void put(int index, Object value) throws ScriptingException {
    }

    public abstract MemberDesc getMemberInfo();

    /**
     * @scripted Prints a description and all members of this object.
     */
    @ScriptedMethod
    public String help() {
        return getMemberInfo().getDescription(new HelpFormatter());
    }
}
