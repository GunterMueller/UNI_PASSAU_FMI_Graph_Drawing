package org.graffiti.plugins.scripting.reflect;

import org.graffiti.plugins.scripting.delegate.ObjectDelegate;

/**
 * Description of object delegates for reflection, interactive help and code
 * completion in the scripting system.
 * 
 * @author Andreas Glei&szlig;ner
 */
public class ObjectDesc extends MemberDesc {
    /**
     * Constructs a description of the specified delegate.
     * 
     * @param delegate
     *            the delegate to describe.
     */
    public ObjectDesc(ObjectDelegate delegate) {
        super(delegate.toString(), delegate.getClass());
        thisHint = delegate;
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
