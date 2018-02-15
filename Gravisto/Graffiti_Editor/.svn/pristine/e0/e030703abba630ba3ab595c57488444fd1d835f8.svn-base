package org.graffiti.plugins.scripting.reflect;

import org.graffiti.plugins.scripting.Scope;
import org.graffiti.plugins.scripting.delegate.FunctionDelegate;

public class FunctionDesc extends EvaluatingDesc {
    private final boolean isInternal;

    public FunctionDesc(FunctionDelegate delegate, boolean isInternal) {
        super(delegate.getName(), delegate.getClass(), delegate.getReturnType());
        this.isInternal = isInternal;
        setDescriptions(delegate.getThisObject().getClass());
    }

    public FunctionDesc(String name, Class<?> declaringClass,
            Class<?> returnType) {
        super(name, FunctionDelegate.class, returnType);
        this.isInternal = !(Scope.class.isAssignableFrom(declaringClass));

        setDescriptions(declaringClass);
        // Return types...
    }

    @Override
    protected String formatDescription(HelpFormatter helpFormatter,
            String membersSummary) {
        return helpFormatter.getFunctionDescription(name, isInternal,
                fullDescription, membersSummary);
    }

    @Override
    public String formatMemberSummary(HelpFormatter helpFormatter,
            String nameHint) {
        if (nameHint == null) {
            nameHint = name;
        }
        if (summary.length() == 0)
            return helpFormatter.getFunctionMembersSummary(nameHint);
        else
            return helpFormatter.getFunctionMembersSummary(nameHint, summary);
    }
}
