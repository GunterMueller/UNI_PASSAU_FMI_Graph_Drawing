package org.graffiti.plugins.scripting.delegate;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ScriptedField {
    public String[] names() default {};

    public FieldAccess access() default FieldAccess.Auto;
}
