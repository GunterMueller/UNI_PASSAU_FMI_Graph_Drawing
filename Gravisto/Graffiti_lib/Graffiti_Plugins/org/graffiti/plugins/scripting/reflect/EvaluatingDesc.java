package org.graffiti.plugins.scripting.reflect;

import org.graffiti.core.Bundle;

public abstract class EvaluatingDesc extends MemberDesc {
    private final Class<?> returnType;

    public EvaluatingDesc(String name, Class<?> type, Class<?> returnType) {
        super(name, type);
        this.returnType = returnType;
    }

    protected void setDescriptions(Class<?> declaringClass) {
        DocumentedDelegate dd = null;

        do {
            dd = declaringClass.getAnnotation(DocumentedDelegate.class);
            if (dd != null) {
                break;
            }
            declaringClass = declaringClass.getSuperclass();
        } while (!declaringClass.equals(Object.class));

        if (dd != null) {
            Bundle bundle = Bundle.getBundle(dd.value());

            summary = bundle.getString("doc."
                    + declaringClass.getCanonicalName() + "." + name);
            fullDescription = bundle.getString("doc."
                    + declaringClass.getCanonicalName() + "." + name + ".full");

            if (summary == null) {
                summary = "";
            }
            if (fullDescription == null || fullDescription.length() == 0) {
                fullDescription = summary;
            }
        }
    }

    public Class<?> getReturnType() {
        return returnType;
    }
}
