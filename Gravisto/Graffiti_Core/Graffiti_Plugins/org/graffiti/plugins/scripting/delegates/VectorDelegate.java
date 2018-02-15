package org.graffiti.plugins.scripting.delegates;

import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.graffiti.plugins.scripting.DefaultDocumentation;
import org.graffiti.plugins.scripting.Scope;
import org.graffiti.plugins.scripting.delegate.FieldDelegate;
import org.graffiti.plugins.scripting.delegate.ObjectDelegate;
import org.graffiti.plugins.scripting.delegate.ScriptedConstructor;
import org.graffiti.plugins.scripting.delegate.ScriptedField;
import org.graffiti.plugins.scripting.delegate.ScriptedMethod;
import org.graffiti.plugins.scripting.delegate.ScriptingDelegate;
import org.graffiti.plugins.scripting.delegate.Unwrappable;
import org.graffiti.plugins.scripting.exceptions.IllegalScriptingArgumentException;
import org.graffiti.plugins.scripting.exceptions.ScriptingException;
import org.graffiti.plugins.scripting.reflect.DocumentedDelegate;
import org.graffiti.util.Callback;

/**
 * @scripted A vector of double values.
 * @author Andreas Glei&szlig;ner
 */
@DocumentedDelegate(DefaultDocumentation.class)
public class VectorDelegate extends ObjectDelegate implements
        Unwrappable<Point2D> {
    private class IndexableWrapper extends FieldDelegate<Number> {
        private int index;

        public IndexableWrapper(int index) {
            super(Number.class);
            this.index = index;
        }

        @Override
        public Object get() {
            return VectorDelegate.this.get(index);
        }

        @Override
        public void set(Number value) {
            put(index, value.doubleValue());
        }
    };

    @ScriptedField
    protected IndexableWrapper x = new IndexableWrapper(0);

    @ScriptedField
    protected IndexableWrapper width = new IndexableWrapper(0);

    @ScriptedField
    protected IndexableWrapper y = new IndexableWrapper(1);

    @ScriptedField
    protected IndexableWrapper height = new IndexableWrapper(1);

    @ScriptedField
    protected IndexableWrapper z = new IndexableWrapper(2);

    @ScriptedField
    protected IndexableWrapper depth = new IndexableWrapper(2);

    private TreeMap<Integer, Double> map;

    public VectorDelegate(Scope scope) {
        super(scope);
        map = new TreeMap<Integer, Double>();
    }

    public VectorDelegate(Scope scope, Map<Integer, Double> map) {
        this(scope);
        this.map.putAll(map);
    }

    @ScriptedConstructor("Vector")
    public VectorDelegate(Scope scope, Number... values) {
        this(scope);
        for (int i = 0; i < values.length; i++) {
            map.put(i, values[i].doubleValue());
        }
    }

    @ScriptedConstructor("Vector")
    public VectorDelegate(Scope scope, Object[] array)
            throws ScriptingException {
        this(scope);
        if (array.length != 1)
            throw new IllegalScriptingArgumentException("Vector");
        try {
            array = (Object[]) array[0];
            for (int i = 0; i < array.length; i++) {
                map.put(i, (Double) array[i]);
            }
        } catch (Exception e) {
            throw new IllegalScriptingArgumentException("Vector");
        }
    }

    @ScriptedMethod
    public VectorDelegate add(VectorDelegate... others) {
        return calc(others, new Callback<Double, Double[]>() {
            public Double call(Double[] t) {
                double sum = 0.0;
                for (Double v : t) {
                    sum += v;
                }
                return sum;
            }
        });
    }

    @ScriptedMethod
    public VectorDelegate sub(VectorDelegate... others) {
        return calc(others, new Callback<Double, Double[]>() {
            public Double call(Double[] t) {
                double sum = t[0];
                for (int i = 1; i < t.length; i++) {
                    sum -= t[i];
                }
                return sum;
            }
        });
    }

    @ScriptedMethod
    public VectorDelegate multiply(final Number factor) {
        return calc(new VectorDelegate[0], new Callback<Double, Double[]>() {
            public Double call(Double[] t) {
                return t[0] * factor.doubleValue();
            }
        });
    }

    @ScriptedMethod
    public Double norm() {
        double len = 0.0;
        for (Double v : map.values()) {
            len += v * v;
        }
        return Math.sqrt(len);
    }

    @ScriptedMethod
    public VectorDelegate normalize() {
        return multiply(1 / norm());
    }

    protected VectorDelegate calc(VectorDelegate[] others,
            Callback<Double, Double[]> function) {
        int othersCount = others.length;

        @SuppressWarnings("unchecked")
        Map<Integer, Double>[] maps = new Map[othersCount + 1];
        maps[0] = map;
        int i = 1;
        for (VectorDelegate other : others) {
            maps[i] = other.map;
            i++;
        }
        return new VectorDelegate(scope, calc(maps, function));
    }

    protected Map<Integer, Double> calc(Map<Integer, Double>[] maps,
            Callback<Double, Double[]> function) {
        Set<Integer> indices = new HashSet<Integer>();
        int mapCount = maps.length;
        for (Map<Integer, Double> map : maps) {
            indices.addAll(map.keySet());
        }
        HashMap<Integer, Double> result = new HashMap<Integer, Double>();
        Double[] values = new Double[mapCount];
        for (Integer index : indices) {
            int i = 0;
            for (Map<Integer, Double> map : maps) {
                Double v = map.get(index);
                values[i] = v == null ? 0.0 : v;
                i++;
            }
            result.put(index, function.call(values));
        }
        return result;
    }

    @Override
    public Object get(int index) {
        Double value = map.get(index);
        if (value == null)
            return ScriptingDelegate.UNDEFINED;
        else
            return value;
    }

    @Override
    public boolean has(int index) {
        return map.containsKey(index);
    }

    @Override
    public Set<Integer> getIndices() {
        return map.keySet();
    }

    @Override
    public void put(int index, Object value) {
        map.put(index, (Double) value);
    }

    @Override
    @ScriptedMethod
    public VectorDelegate clone() {
        return new VectorDelegate(scope, map);
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer("[Vector ");
        Iterator<Double> iter = map.values().iterator();
        if (iter.hasNext()) {
            buffer.append(iter.next());
        }
        while (iter.hasNext()) {
            buffer.append(", ").append(iter.next());
        }
        return buffer.append(']').toString();
    }

    public Point2D unwrap() {
        Double x = map.get(0);
        Double y = map.get(1);
        return new Point2D.Double(x == null ? 0.0 : x, y == null ? 0.0 : y);
    }
}
