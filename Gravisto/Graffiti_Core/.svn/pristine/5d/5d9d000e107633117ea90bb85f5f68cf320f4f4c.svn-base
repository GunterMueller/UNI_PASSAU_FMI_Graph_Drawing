package org.graffiti.plugins.scripting;

import java.util.Map;
import java.util.SortedMap;

import org.graffiti.plugins.scripting.delegate.ScriptedMethod;
import org.graffiti.plugins.scripting.reflect.DocumentedDelegate;
import org.graffiti.plugins.scripting.reflect.HelpFormatter;
import org.graffiti.plugins.scripting.reflect.MemberDesc;

/**
 * The scope of commands entered in a console.
 * 
 * @author Andreas Glei&szlig;ner
 */
@DocumentedDelegate(DefaultDocumentation.class)
class ConsoleScope extends Scope {
    /**
     * Constructs a console scope, which is a child of the specified parent.
     * 
     * @param parent
     *            the parent of the scope to construct.
     */
    public ConsoleScope(Scope parent) {
        super(parent);
    }

    /**
     * Prints the available functions and objects on the console.
     * 
     * @scripted Shows the available functions and objects.
     */
    @ScriptedMethod
    public String help() {
        SortedMap<String, MemberDesc> members = getMembers();
        HelpFormatter helpFormatter = new HelpFormatter();
        StringBuffer buffer = new StringBuffer("\n");
        for (Map.Entry<String, MemberDesc> entry : members.entrySet()) {
            buffer.append(
                    entry.getValue().formatMemberSummary(helpFormatter,
                            entry.getKey())).append("\n");
        }
        return buffer.toString();
    }

    @ScriptedMethod
    public Boolean registerClass(String className) {
        try {
            addNativeJavaClass(Class.forName(className));
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
