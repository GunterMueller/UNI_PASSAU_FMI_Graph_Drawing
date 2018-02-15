package org.graffiti.plugins.scripting.reflect;

import org.graffiti.plugins.scripting.delegate.FieldDelegate;

public class FieldDesc extends EvaluatingDesc {
    public FieldDesc(String name, Class<?> declaringClass, Class<?> returnType) {
        super(name, FieldDelegate.class, returnType);
        setDescriptions(declaringClass);
    }

    @Override
    protected String formatDescription(HelpFormatter helpFormatter,
            String membersSummary) {
        return helpFormatter.getObjectDescription(name, fullDescription,
                membersSummary);
    }

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
