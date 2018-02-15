// =============================================================================
//
//   ParameterConverter.java
//
//   Copyright (c) 2001-2009, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.tools.benchmark;

import java.util.HashMap;

import org.graffiti.plugin.parameter.BooleanParameter;
import org.graffiti.plugin.parameter.DoubleParameter;
import org.graffiti.plugin.parameter.FloatParameter;
import org.graffiti.plugin.parameter.IntegerParameter;
import org.graffiti.plugin.parameter.Parameter;
import org.graffiti.plugin.parameter.ProbabilityParameter;
import org.graffiti.plugin.parameter.StringParameter;
import org.graffiti.plugin.parameter.StringSelectionParameter;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public abstract class ParameterConverter {
    private static final HashMap<Class<?>, ParameterConverter> classMap = new HashMap<Class<?>, ParameterConverter>();

    private static final HashMap<String, ParameterConverter> hintMap = new HashMap<String, ParameterConverter>();

    static {
        ParameterConverter booleanConverter = new ParameterConverter() {
            @Override
            protected void convert(Parameter<?> parameter, String value) {
                parameter.setObjectValue(Boolean.valueOf(value));
            }
        };

        ParameterConverter doubleConverter = new ParameterConverter() {
            @Override
            protected void convert(Parameter<?> parameter, String value) {
                parameter.setObjectValue(Double.valueOf(value));
            }
        };

        ParameterConverter floatConverter = new ParameterConverter() {
            @Override
            protected void convert(Parameter<?> parameter, String value) {
                parameter.setObjectValue(Float.valueOf(value));
            }
        };

        ParameterConverter intConverter = new ParameterConverter() {
            @Override
            protected void convert(Parameter<?> parameter, String value) {
                parameter.setObjectValue(Integer.valueOf(value));
            }
        };

        ParameterConverter longConverter = new ParameterConverter() {
            @Override
            protected void convert(Parameter<?> parameter, String value) {
                parameter.setObjectValue(Long.valueOf(value));
            }
        };

        ParameterConverter stringConverter = new ParameterConverter() {
            @Override
            protected void convert(Parameter<?> parameter, String value) {
                parameter.setObjectValue(value);
            }
        };

        ParameterConverter stringSelectionConverter = new ParameterConverter() {
            @Override
            protected void convert(Parameter<?> parameter, String value) {
                StringSelectionParameter slp = (StringSelectionParameter) parameter;
                try {
                    slp.setSelectedValue(Integer.valueOf(value));
                } catch (NumberFormatException e) {
                    slp.setValue(value);
                }
            }
        };

        classMap.put(BooleanParameter.class, booleanConverter);
        classMap.put(DoubleParameter.class, doubleConverter);
        classMap.put(FloatParameter.class, floatConverter);
        classMap.put(IntegerParameter.class, intConverter);
        classMap.put(StringParameter.class, stringConverter);
        classMap.put(StringSelectionParameter.class, stringSelectionConverter);
        classMap.put(ProbabilityParameter.class, doubleConverter);

        hintMap.put("boolean", booleanConverter);
        hintMap.put("double", doubleConverter);
        hintMap.put("float", floatConverter);
        hintMap.put("int", intConverter);
        hintMap.put("long", longConverter);
        hintMap.put("string", stringConverter);
    }

    public static boolean apply(Parameter<?> parameter, String value,
            String typeHint) {
        if (typeHint != null && typeHint.equals("null")) {
            parameter.setObjectValue(null);
            return true;
        }

        Class<?> paramClass = parameter.getClass();
        while (Parameter.class.isAssignableFrom(paramClass)) {
            ParameterConverter converter = classMap.get(paramClass);
            if (converter != null) {
                converter.convert(parameter, value);
                return true;
            }
            paramClass = paramClass.getSuperclass();
        }
        if (typeHint == null)
            return false;
        ParameterConverter converter = hintMap.get(typeHint);
        if (converter != null) {
            converter.convert(parameter, value);
            return true;
        } else {
            try {
                Class<?> clazz = Class.forName(typeHint);
                if (clazz.isEnum()) {
                    parameter.setObjectValue(getEnumValue(clazz, value));
                    return true;
                }
            } catch (ClassNotFoundException e) {
            }
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    private static Object getEnumValue(Class<?> clazz, String value) {
        Class enumClass = clazz;
        return Enum.valueOf(enumClass, value);
    }

    protected abstract void convert(Parameter<?> parameter, String value);
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
