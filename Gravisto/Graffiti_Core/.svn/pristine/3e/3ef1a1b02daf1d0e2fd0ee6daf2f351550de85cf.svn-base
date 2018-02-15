package org.graffiti.plugins.scripting.reflect;

import java.util.SortedMap;
import java.util.TreeMap;

import org.graffiti.core.Bundle;
import org.graffiti.plugins.scripting.ScriptingRegistry;
import org.graffiti.plugins.scripting.delegate.DelegateManager;
import org.graffiti.plugins.scripting.delegate.ReflectiveDelegate;
import org.graffiti.plugins.scripting.delegate.ScriptingDelegate;

/**
 * Abstract base class of descriptions of an entity wrapping an object for
 * scripting, which is intended for reflection, interactive help and code
 * completion in the scripting system. This object contains lazily created
 * {@code MemberDesc} objects describing the members of the described entity.
 * The term 'wrapped object' may be misleading as there does not necessarily
 * exist a real object corresponding to the described entity. It is used in
 * order to depict that all entities belong to the first stage of the wrapping
 * process for scripting (most of the entities are instances of
 * {@link ScriptingDelegate}). See {@link ScriptingRegistry} for an overview.
 * 
 * @author Andreas Glei&szlig;ner
 * @see ScriptingRegistry
 */
public abstract class MemberDesc {
    /**
     * Name of the wrapped object.
     */
    protected final String name;

    /**
     * Type of the wrapping entity. May be null.
     */
    protected final Class<?> type;

    /**
     * The members, which are lazily created.
     */
    protected SortedMap<String, MemberDesc> members;

    /**
     * Detailed description readable by the user.
     */
    protected String fullDescription;

    /**
     * Summary readable by the user.
     */
    protected String summary;

    /**
     * The wrapped object. May be null.
     */
    protected Object thisHint;

    /**
     * Constructs a description for an entity with the specified name and of the
     * specified type.
     * 
     * @param name
     *            the name of the described entity.
     */
    protected MemberDesc(String name, Class<?> type) {
        this.name = name;
        this.type = type;
        setDescriptions(type);
    }

    /**
     * Returns a user readable form of this description formatted by the
     * specified formatter.
     * 
     * @param helpFormatter
     *            the formatter used to format the description to return.
     * @return a user readable form of this description formatted by the
     *         specified formatter.
     */
    public final String getDescription(HelpFormatter helpFormatter) {
        getMembers();
        StringBuffer buffer = new StringBuffer();
        for (MemberDesc member : members.values()) {
            buffer.append(member.formatMemberSummary(helpFormatter, null))
                    .append("\n");
        }
        return formatDescription(helpFormatter, buffer.toString());
    }

    /**
     * Returns a user readable form of this description formatted by the
     * specified formatter.
     * 
     * @param helpFormatter
     *            the formatter used to format the description to return.
     * @param membersSummary
     *            string containing a summary of all members.
     * @return a user readable form of this description formatted by the
     *         specified formatter.
     */
    protected abstract String formatDescription(HelpFormatter helpFormatter,
            String membersSummary);

    public abstract String formatMemberSummary(HelpFormatter helpFormatter,
            String nameHint);

    public SortedMap<String, MemberDesc> getMembers() {
        if (members == null) {
            members = new TreeMap<String, MemberDesc>();
            if (ReflectiveDelegate.class.isAssignableFrom(type)) {
                members = DelegateManager.getMembers(type
                        .asSubclass(ReflectiveDelegate.class), thisHint);
                if (thisHint != null) {
                    ((ReflectiveDelegate) thisHint)
                            .addDynamicMemberInfo(members);
                }
            } else {
                members = new TreeMap<String, MemberDesc>();
            }
        }
        return members;
    }

    private void setDescriptions(Class<?> clazz) {
        if (clazz == null || clazz.equals(Object.class)) {
            if (summary == null) {
                summary = "";
                fullDescription = "";
            }
            return;
        }
        DocumentedDelegate dd = clazz.getAnnotation(DocumentedDelegate.class);
        if (dd != null) {
            Bundle bundle = Bundle.getBundle(dd.value());

            summary = bundle.getString("doc." + clazz.getCanonicalName());
            fullDescription = bundle.getString("doc."
                    + clazz.getCanonicalName() + ".full");

            if (summary == null) {
                summary = "";
            }
            if (fullDescription == null || fullDescription.length() == 0) {
                fullDescription = summary;
            }
        }
        setDescriptions(clazz.getSuperclass());
    }

    public String getName() {
        return name;
    }

    public String getSummary() {
        return summary;
    }
}
