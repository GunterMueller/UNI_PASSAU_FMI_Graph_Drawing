package org.graffiti.plugins.scripting.reflect;

import org.graffiti.plugins.scripting.delegate.ConstructorDelegate;

public class ConstructorDesc extends MemberDesc {
    public ConstructorDesc(ConstructorDelegate delegate) {
        super(delegate.getName(), delegate.getClass());
    }

    @Override
    protected String formatDescription(HelpFormatter helpFormatter,
            String membersSummary) {
        return helpFormatter.getConstructorDescription(name, fullDescription,
                membersSummary);
    }

    @Override
    public String formatMemberSummary(HelpFormatter helpFormatter,
            String nameHint) {
        if (nameHint == null) {
            nameHint = name;
        }
        if (summary.length() == 0)
            return helpFormatter.getConstructorMembersSummary(nameHint);
        else
            return helpFormatter
                    .getConstructorMembersSummary(nameHint, summary);
    }
}
