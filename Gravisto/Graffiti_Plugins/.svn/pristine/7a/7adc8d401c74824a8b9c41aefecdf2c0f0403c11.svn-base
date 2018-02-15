package org.graffiti.plugins.scripting.delegates;

import java.lang.reflect.Field;
import java.util.SortedMap;

import org.graffiti.core.Bundle;
import org.graffiti.plugin.view.Grid;
import org.graffiti.plugin.view.GridParameter;
import org.graffiti.plugins.grids.GridRegistry;
import org.graffiti.plugins.scripting.DefaultDocumentation;
import org.graffiti.plugins.scripting.DelegateWrapperUtil;
import org.graffiti.plugins.scripting.Scope;
import org.graffiti.plugins.scripting.delegate.DelegateFactory;
import org.graffiti.plugins.scripting.delegate.ObjectDelegate;
import org.graffiti.plugins.scripting.delegate.ScriptedConstructor;
import org.graffiti.plugins.scripting.delegate.Unwrappable;
import org.graffiti.plugins.scripting.exceptions.IllegalScriptingArgumentException;
import org.graffiti.plugins.scripting.exceptions.ScriptingException;
import org.graffiti.plugins.scripting.reflect.BlackBoxMemberDesc;
import org.graffiti.plugins.scripting.reflect.DocumentedDelegate;
import org.graffiti.plugins.scripting.reflect.MemberDesc;

/**
 * Delegate, which represents a grid.
 * 
 * @author Andreas Glei&szlig;ner
 * @scripted The grid.
 */
@DocumentedDelegate(DefaultDocumentation.class)
public class GridDelegate extends ObjectDelegate implements Unwrappable<Grid> {
    public static class Factory extends DelegateFactory<GridDelegate, Grid> {
        public Factory(Scope scope) {
            super(scope, GridDelegate.class);
        }

        @Override
        public GridDelegate create(Grid grid) {
            return new GridDelegate(scope, grid);
        }
    }

    private Grid grid;

    public GridDelegate(Scope scope, Grid grid) {
        super(scope);
        this.grid = grid;
    }

    @ScriptedConstructor("Grid")
    public GridDelegate(Scope scope, String className)
            throws ScriptingException {
        super(scope);
        try {
            grid = findClass(className).newInstance();
        } catch (Exception e) {
            throw new IllegalScriptingArgumentException("Grid");
        }
    }

    @Override
    public void addDynamicMemberInfo(SortedMap<String, MemberDesc> map) {
        super.addDynamicMemberInfo(map);
        Class<? extends Grid> gridClass = grid.getClass();
        Bundle bundle = Bundle.getBundle(gridClass);

        for (Field field : grid.getClass().getDeclaredFields()) {
            String name = field.getName();
            String summary = null;
            if (bundle != null) {
                summary = bundle.getString(String.format(Grid.NAME_PATTERN,
                        name))
                        + ".";
            }
            BlackBoxMemberDesc desc = summary == null ? new BlackBoxMemberDesc(
                    name) : new BlackBoxMemberDesc(name, summary, summary);
            map.put(name, desc);
        }
    }

    private Class<? extends Grid> findClass(String className) {
        for (Class<? extends Grid> gridClass : GridRegistry.get().getGrids()) {
            if (className.equals(gridClass.getSimpleName())
                    || className.equals(gridClass.getName())
                    || className.equals(gridClass.getSimpleName()))
                return gridClass;
        }
        try {
            return Class.forName(className).asSubclass(Grid.class);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    @Override
    public Object get(String name) throws ScriptingException {
        for (Field field : grid.getClass().getDeclaredFields()) {
            if (field.getName().equals(name)
                    && field.getAnnotation(GridParameter.class) != null) {
                try {
                    field.setAccessible(true);
                    return DelegateWrapperUtil.wrap(field.get(grid), scope);
                } catch (IllegalAccessException e) {
                }
            }
        }
        return super.get(name);
    }

    @Override
    public void put(String name, Object value) throws ScriptingException {
        for (Field field : grid.getClass().getDeclaredFields()) {
            if (field.getName().equals(name)
                    && field.getAnnotation(GridParameter.class) != null) {
                try {
                    field.setAccessible(true);
                    field.set(grid, DelegateWrapperUtil.unwrap(value, field
                            .getType()));
                } catch (IllegalAccessException e) {
                }
            }
        }
        super.put(name, value);
    }

    @Override
    public String toString() {
        return "[Grid " + grid.getClass().getSimpleName() + "]";
    }

    public Grid unwrap() {
        return grid;
    }

    // public VectorDelegate getOrigin
}
