// =============================================================================
//
//   Function.java
//
//   Copyright (c) 2001-2009, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.tools.benchmark.sampler;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public abstract class Function {
    private static final Map<String, Function> map = new HashMap<String, Function>();

    static {
        map.put("floor", new UnaryFunction() {
            @Override
            public double calc(double arg) {
                return Math.floor(arg);
            }
        });
        map.put("round", new UnaryFunction() {
            @Override
            public double calc(double arg) {
                return Math.round(arg);
            }
        });
        map.put("ceil", new UnaryFunction() {
            @Override
            public double calc(double arg) {
                return Math.floor(arg);
            }
        });
        map.put("max", new Function() {
            @Override
            public double eval(double... args) {
                if (args.length == 0)
                    return Double.NEGATIVE_INFINITY;
                double val = Double.NEGATIVE_INFINITY;
                for (double arg : args) {
                    val = Math.max(val, arg);
                }
                return val;
            }
        });
        map.put("min", new Function() {
            @Override
            public double eval(double... args) {
                if (args.length == 0)
                    return Double.POSITIVE_INFINITY;
                double val = Double.POSITIVE_INFINITY;
                for (double arg : args) {
                    val = Math.min(val, arg);
                }
                return val;
            }
        });
        map.put("sqrt", new UnaryFunction() {
            @Override
            public double calc(double arg) {
                return Math.sqrt(arg);
            }
        });
        map.put("log", new UnaryFunction() {
            @Override
            public double calc(double arg) {
                return Math.log(arg);
            }
        });
        map.put("abs", new UnaryFunction() {
            @Override
            public double calc(double arg) {
                return Math.abs(arg);
            }
        });
        map.put("#+", new BinaryFunction() {
            @Override
            protected double calc(double first, double second) {
                return first + second;
            }
        });
        map.put("#-", new BinaryFunction() {
            @Override
            protected double calc(double first, double second) {
                return first - second;
            }
        });
        map.put("#*", new BinaryFunction() {
            @Override
            protected double calc(double first, double second) {
                return first * second;
            }
        });
        map.put("#/", new BinaryFunction() {
            @Override
            protected double calc(double first, double second) {
                return first / second;
            }
        });
        map.put("#^", new BinaryFunction() {
            @Override
            protected double calc(double first, double second) {
                return Math.pow(first, second);
            }
        });
        map.put("#u-", new UnaryFunction() {
            @Override
            protected double calc(double arg) {
                return -arg;
            }
        });
    }

    public static Function get(String name) {
        return map.get(name);
    }

    public abstract double eval(double... args);
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
