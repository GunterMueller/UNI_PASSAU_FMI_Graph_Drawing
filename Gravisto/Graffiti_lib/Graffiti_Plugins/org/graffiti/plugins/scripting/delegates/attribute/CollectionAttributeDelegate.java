package org.graffiti.plugins.scripting.delegates.attribute;

import java.util.Map;
import java.util.SortedMap;

import org.graffiti.attributes.Attribute;
import org.graffiti.attributes.AttributeNotFoundException;
import org.graffiti.attributes.CollectionAttribute;
import org.graffiti.plugins.scripting.Scope;
import org.graffiti.plugins.scripting.delegate.ObjectDelegate;
import org.graffiti.plugins.scripting.delegate.ScriptedMethod;
import org.graffiti.plugins.scripting.delegates.attribute.handlers.AttributeHandler;
import org.graffiti.plugins.scripting.exceptions.ScriptingException;
import org.graffiti.plugins.scripting.reflect.BlackBoxMemberDesc;
import org.graffiti.plugins.scripting.reflect.MemberDesc;

public class CollectionAttributeDelegate extends ObjectDelegate {
    private final CollectionAttribute attribute;

    public CollectionAttributeDelegate(Scope scope,
            CollectionAttribute attribute) {
        super(scope);
        this.attribute = attribute;
    }

    @Override
    public void addDynamicMemberInfo(SortedMap<String, MemberDesc> map) {
        super.addDynamicMemberInfo(map);
        for (Map.Entry<String, Attribute> entry : attribute.getCollection()
                .entrySet()) {
            String name = entry.getKey();
            map.put(name, new BlackBoxMemberDesc(name));
        }
    }

    @Override
    public Object get(String name) throws ScriptingException {
        try {
            return AttributeHandler.get(scope, attribute.getAttribute(name));
        } catch (AttributeNotFoundException e) {
            return super.get(name);
        }
    }

    @Override
    public void put(String name, Object value) throws ScriptingException {
        try {
            AttributeHandler.set(value, attribute.getAttribute(name));
        } catch (AttributeNotFoundException e) {
            super.put(name, value);
        }
    }

    @ScriptedMethod
    public String getPath() {
        return attribute.getPath();
    }

    @Override
    public String toString() {
        return "[Attribute \"" + attribute.getPath() + "\"]";
    }
}
