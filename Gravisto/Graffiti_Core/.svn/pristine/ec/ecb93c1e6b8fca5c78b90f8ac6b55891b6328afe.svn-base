package org.graffiti.plugins.scripting.reflect;

import org.graffiti.plugins.scripting.delegate.BlackBoxDelegate;
import org.graffiti.plugins.scripting.delegate.ScriptingDelegate;

/**
 * Describes an entity that is either a {@code BlackBoxDelegate} or is not a
 * {@code ScriptingDelegate}.
 * 
 * @author Andreas Glei&szlig;ner
 * @see BlackBoxDelegate
 * @see ScriptingDelegate
 */
public class BlackBoxMemberDesc extends MemberDesc {
    /**
     * Constructs a description of an entity with the specified name.
     * 
     * @param name
     *            the name of the entity to describe.
     */
    public BlackBoxMemberDesc(String name) {
        super(name, null);
    }

    /**
     * Constructs a description of an entity with the specified name and of the
     * specified type
     * 
     * @param name
     *            the name of the entity to describe.
     * @param type
     *            the type of the entity to describe.
     */
    public BlackBoxMemberDesc(String name, Class<?> type) {
        super(name, type);
    }

    /**
     * Constructs a description of an entity with the specified name, user
     * readable description and summary.
     * 
     * @param name
     *            the name of the entity to describe.
     * @param fullDescription
     *            user readable extensive description of the entity.
     * @param summary
     *            user readable summary.
     */
    public BlackBoxMemberDesc(String name, String fullDescription,
            String summary) {
        super(name, null);
        this.fullDescription = fullDescription;
        this.summary = summary;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String formatDescription(HelpFormatter helpFormatter,
            String membersSummary) {
        return helpFormatter.getObjectDescription(name, fullDescription,
                membersSummary);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String formatMemberSummary(HelpFormatter helpFormatter,
            String nameHint) {
        if (nameHint == null) {
            nameHint = name;
        }
        if (summary.length() == 0)
            return helpFormatter.getObjectMembersSummary(nameHint);
        else
            return helpFormatter.getObjectMembersSummary(nameHint, summary);
    }
}
