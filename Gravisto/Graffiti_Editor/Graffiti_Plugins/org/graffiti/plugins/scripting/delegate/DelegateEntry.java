package org.graffiti.plugins.scripting.delegate;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.graffiti.plugins.scripting.Scope;
import org.graffiti.plugins.scripting.exceptions.FieldTypeException;
import org.graffiti.plugins.scripting.exceptions.ScriptingException;
import org.graffiti.plugins.scripting.reflect.FieldDesc;
import org.graffiti.plugins.scripting.reflect.FunctionDesc;
import org.graffiti.plugins.scripting.reflect.MemberDesc;

public final class DelegateEntry {
    private Map<String, FunctionDelegateFactory> methodFactories;
    private Map<String, FieldDelegateFactory<?>> fieldFactories;
    private ConstructorDelegateFactory constructorFactory;

    public DelegateEntry(Class<?> wrapperClass, boolean useFieldsAndConstructors) {
        methodFactories = new HashMap<String, FunctionDelegateFactory>();
        createMethods(wrapperClass);
        if (useFieldsAndConstructors) {
            fieldFactories = new HashMap<String, FieldDelegateFactory<?>>();
            constructorFactory = new ConstructorDelegateFactory();
            createFields(wrapperClass);
            createConstructors(wrapperClass);
        }
    }

    private void createMethods(Class<?> wrapperClass) {
        for (Method method : wrapperClass.getMethods()) {
            ScriptedMethod sm = method.getAnnotation(ScriptedMethod.class);
            if (sm == null) {
                continue;
            }
            String methodName = sm.name();
            if (methodName.length() == 0) {
                methodName = method.getName();
            }
            FunctionDelegateFactory factory = methodFactories.get(methodName);
            if (factory == null) {
                factory = new FunctionDelegateFactory(methodName);
                methodFactories.put(methodName, factory);
            }
            factory.add(method);
        }
    }

    @SuppressWarnings("unchecked")
    private void createFields(Class<?> wrapperClass) {
        Class<?> superClass = wrapperClass.getSuperclass();
        if (ObjectDelegate.class.isAssignableFrom(superClass)) {
            createFields(superClass.asSubclass(ObjectDelegate.class));
        }
        for (final Field field : wrapperClass.getDeclaredFields()) {
            final ScriptedField sf = field.getAnnotation(ScriptedField.class);
            if (sf == null) {
                continue;
            }
            final FieldAccess fieldAccess = sf.access();
            field.setAccessible(true);
            final String[] fieldNames = sf.names().length == 0 ? new String[] { field
                    .getName() }
                    : sf.names();
            // final String fieldName = sf.name().length() == 0
            // ? field.getName()
            // : sf.name();
            FieldDelegateFactory factory = null;
            if (FieldDelegate.class.isAssignableFrom(field.getType())) {
                if (fieldAccess != FieldAccess.Auto)
                    throw new RuntimeException("Explicit field wrapper \""
                            + fieldNames[0] + "\" must have FieldAccess Auto.");
                factory = new FieldDelegateFactory(field) {
                    @Override
                    public FieldDelegate create(ObjectDelegate thisObject)
                            throws ScriptingException {
                        try {
                            FieldDelegate fw = (FieldDelegate) field
                                    .get(thisObject);
                            if (fw == null) {
                                fw = createFieldDelegate(thisObject, field);
                                field.set(thisObject, fw);
                            }
                            return fw;
                        } catch (IllegalAccessException e) {
                            throw new ScriptingException(e);
                        }
                    }
                };
            } else {
                factory = new FieldDelegateFactory(field) {
                    @Override
                    public FieldDelegate create(final ObjectDelegate thisObject)
                            throws ScriptingException {
                        return new FieldDelegate(field.getType()) {
                            @Override
                            public Object get() throws ScriptingException {
                                if (!fieldAccess.canGet())
                                    return super.get();
                                try {
                                    return field.get(thisObject);
                                } catch (IllegalAccessException e) {
                                    throw new ScriptingException(e);
                                }
                            }

                            @Override
                            public void set(Object value)
                                    throws ScriptingException {
                                if (!fieldAccess.canSet()) {
                                    super.set(value);
                                } else if (!accepts(value))
                                    throw new FieldTypeException(fieldNames[0]);
                                else {
                                    try {
                                        field.set(thisObject, value);
                                    } catch (IllegalAccessException e) {
                                        throw new ScriptingException(e);
                                    }
                                }
                            }
                        };
                    }
                };
            }
            for (String fieldName : fieldNames) {
                fieldFactories.put(fieldName, factory);
            }
        }
    }

    private FieldDelegate<?> createFieldDelegate(ObjectDelegate thisObject,
            Field field) {
        try {
            return (FieldDelegate<?>) field.getType().getConstructor(
                    ObjectDelegate.class, Field.class).newInstance(thisObject,
                    field);
        } catch (Exception e) {
            return null;
        }
    }

    private void createConstructors(Class<?> wrapperClass) {
        for (Constructor<?> constructor : wrapperClass.getConstructors()) {
            ScriptedConstructor sc = constructor
                    .getAnnotation(ScriptedConstructor.class);
            if (sc == null) {
                continue;
            }
            constructorFactory.add(constructor);
            constructorFactory.setName(sc.value());
        }
    }

    public FunctionDelegate getMethod(String name,
            Map<String, FunctionDelegate> methods, ReflectiveDelegate thisObject) {
        FunctionDelegate delegate = methods.get(name);
        if (delegate != null)
            return delegate;
        FunctionDelegateFactory factory = methodFactories.get(name);
        if (factory == null)
            return null;
        delegate = factory.create(thisObject);
        methods.put(name, delegate);
        return delegate;
    }

    public Collection<FunctionDelegate> createMethods(Scope thisObject) {
        LinkedList<FunctionDelegate> list = new LinkedList<FunctionDelegate>();
        for (FunctionDelegateFactory factory : methodFactories.values()) {
            list.add(factory.create(thisObject));
        }
        return list;
    }

    public FieldDelegate<?> getField(String name,
            Map<String, FieldDelegate<?>> fields, ObjectDelegate thisObject)
            throws ScriptingException {
        FieldDelegate<?> delegate = fields.get(name);
        if (delegate != null)
            return delegate;
        FieldDelegateFactory<?> factory = fieldFactories.get(name);
        if (factory != null) {
            delegate = factory.create(thisObject);
        } else {
            delegate = thisObject.createField(name);
        }
        if (delegate == null)
            return null;
        else {
            fields.put(name, delegate);
            return delegate;
        }
    }

    public ConstructorDelegate createConstructor(Scope scope) {
        return constructorFactory.create(scope);
    }

    public SortedMap<String, MemberDesc> getMembers(Object thisHint) {
        SortedMap<String, MemberDesc> result = new TreeMap<String, MemberDesc>();
        for (Map.Entry<String, FunctionDelegateFactory> entry : methodFactories
                .entrySet()) {
            String name = entry.getKey();
            List<Method> methods = entry.getValue().getMethods();
            if (methods.isEmpty()) {
                continue;
            }
            // TODO: intersect return types from all methods
            Method method = methods.get(0);
            result.put(name, new FunctionDesc(name, method.getDeclaringClass(),
                    method.getReturnType()));
        }
        for (Map.Entry<String, FieldDelegateFactory<?>> entry : fieldFactories
                .entrySet()) {
            String name = entry.getKey();
            Field field = entry.getValue().getField();
            Class<?> returnType = field.getType();
            if (FieldDelegate.class.isAssignableFrom(returnType)) {
                try {
                    returnType = null;
                    returnType = ((FieldDelegate<?>) field.get(thisHint))
                            .getType();
                } catch (Exception e) {
                }
                if (returnType == null) {
                    returnType = Object.class;
                }
            }
            result.put(name, new FieldDesc(name, field.getDeclaringClass(),
                    returnType));
        }
        return result;
    }
}
