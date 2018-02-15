package org.graffiti.plugins.scripting.delegates.attribute;

import java.lang.reflect.Field;

import org.graffiti.plugins.scripting.delegate.ObjectDelegate;
import org.graffiti.plugins.scripting.delegates.DashDelegate;

public class LineModeAttributeDelegate extends
        AttributeFieldDelegate<DashDelegate> {
    public LineModeAttributeDelegate(ObjectDelegate delegate, Field field) {
        super(DashDelegate.class, delegate, field);
    }
}
