package org.graffiti.plugins.scripting.reflect;

import org.graffiti.plugins.scripting.ScriptingPlugin;

public class HelpFormatter {
    protected String getFunctionDescription(String name, boolean isInternal,
            String fullDescription, String membersSummary) {
        return String.format(ScriptingPlugin
                .getString(isInternal ? "FunctionInfo.descriptionFormat.method"
                        : "FunctionInfo.descriptionFormat.function"), name,
                expand(fullDescription), membersSummary);
    }

    protected String getObjectDescription(String name, String fullDescription,
            String membersSummary) {
        return String.format(ScriptingPlugin
                .getString("ObjectInfo.descriptionFormat"), name,
                expand(fullDescription), membersSummary);
    }

    protected String getConstructorDescription(String name,
            String fullDescription, String membersSummary) {
        return String.format(ScriptingPlugin
                .getString("ConstructorInfo.descriptionFormat"), name,
                expand(fullDescription), membersSummary);
    }

    protected String getFunctionMembersSummary(String name) {
        return String.format(ScriptingPlugin
                .getString("FunctionInfo.membersSummary.no"), name);
    }

    protected String getFunctionMembersSummary(String name, String summary) {
        return String.format(ScriptingPlugin
                .getString("FunctionInfo.membersSummary"), name, summary);
    }

    protected String getObjectMembersSummary(String name) {
        return String.format(ScriptingPlugin
                .getString("ObjectInfo.membersSummary.no"), name);
    }

    protected String getObjectMembersSummary(String name, String summary) {
        return String.format(ScriptingPlugin
                .getString("ObjectInfo.membersSummary"), name, summary);
    }

    protected String getConstructorMembersSummary(String name) {
        return String.format(ScriptingPlugin
                .getString("ConstructorInfo.membersSummary.no"), name);
    }

    protected String getConstructorMembersSummary(String name, String summary) {
        return String.format(ScriptingPlugin
                .getString("ConstructorInfo.membersSummary"), name, summary);
    }

    private String expand(String s) {
        return (s == null || s.length() == 0) ? "" : s + "\n";
    }
}
