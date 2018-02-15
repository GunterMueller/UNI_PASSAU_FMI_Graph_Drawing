// =============================================================================
//
//   AbstractParametrizable.java
//
//   Copyright (c) 2001-2009, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugin;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import org.graffiti.plugin.parameter.Parameter;

/**
 * @author brunner
 * @version $Revision$ $Date$
 */
public abstract class AbstractParametrizable implements Parametrizable {

    /** The parameters this <code>Parametrizable</code> can use. */
    protected Parameter<?>[] parameters;

    private Parameter<?>[] cachedParameters;

    private Parameter<?>[] defaultParameters;

    private Parameter<?>[] userParameters;

    /** The saved user preferences for this <code>Parametrizable</code>. */
    private Preferences prefs = Preferences.userNodeForPackage(getClass())
            .node(getClass().getSimpleName());

    /**
     * @see org.graffiti.plugin.Parametrizable#setParameters(org.graffiti.plugin.parameter.Parameter[])
     */
    public final void setParameters(Parameter<?>[] params) {
        setParameters(params, true);
    }

    private void setParameters(Parameter<?>[] params, boolean informAlgorithm) {
        if (cachedParameters == null) {
            getParameters();
        }
        Parameter<?>[] copy = copyParameters(params);

        if (params != null && cachedParameters != null && copy != null) {
            for (int i = 0; i < params.length; i++) {
                cachedParameters[i].setObjectValue(copy[i].getValue());
            }
        }

        if (informAlgorithm) {
            setAlgorithmParameters(params);
        }
    }

    protected void setAlgorithmParameters(Parameter<?>[] params) {
        // can be implemented by subclasses
    }

    /**
     * @see org.graffiti.plugin.Parametrizable#getParameters()
     */
    public final Parameter<?>[] getParameters() {
        if (cachedParameters == null) {
            cachedParameters = getAlgorithmParameters();
            // an algorithm might still override getAlgorithmParameters()
            // and simply return null - prevent that
            if (cachedParameters == null) {
                cachedParameters = new Parameter<?>[] {};
            }

            defaultParameters = copyParameters(cachedParameters);

            if (defaultParameters == null) {
                defaultParameters = new Parameter<?>[] {};
            }

            setParameters(getUserParameters(), false);
        }

        return copyParameters(cachedParameters);
    }

    protected Parameter<?>[] getAlgorithmParameters() {
        if (parameters == null) {
            parameters = new Parameter<?>[] {};
        }
        return parameters;
    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#getDefaultParameters()
     */
    public Parameter<?>[] getDefaultParameters() {
        if (defaultParameters == null) {
            getParameters();
        }
        return copyParameters(defaultParameters);
    }

    /**
     * Return the user's default parameters;
     * 
     * @return the user's default parameters
     */
    public Parameter<?>[] getUserParameters() {
        Parameter<?> param;
        if (userParameters == null) {
            userParameters = copyParameters(defaultParameters);
            try {
                String[] keys = prefs.keys();
                userParameters = copyParameters(defaultParameters);
                int index;
                for (String key : keys) {
                    index = -1;
                    for (int i = 0; i < defaultParameters.length; i++) {
                        if (key.compareTo(defaultParameters[i].getName()) == 0) {
                            index = i;
                            break;
                        }
                    }
                    if (index == -1) {
                        continue;
                    }

                    param = byteArrayToParam(prefs.getByteArray(key,
                            paramToByteArray(defaultParameters[index])));

                    if (param != null) {
                        userParameters[index] = param;
                    }
                }

            } catch (BackingStoreException bse) {
                bse.printStackTrace();
            }
        }
        addDependenciesToCopy(defaultParameters, userParameters);
        return copyParameters(userParameters);
    }

    /**
     * Save the currently set parameters as user defaults.
     */
    public void saveUserParameters() {
        saveUserParameters(cachedParameters);
    }

    /**
     * Save the given parameters as user defaults.
     */
    public void saveUserParameters(Parameter<?>[] params) {
        userParameters = copyParameters(params);
        byte[] paramBytes;
        for (Parameter<?> param : params) {
            paramBytes = paramToByteArray(param);
            if (paramBytes != null) {
                prefs.putByteArray(param.getName(), paramBytes);
            }
        }
    }

    private static void addDependenciesToCopy(Parameter<?>[] original,
            Parameter<?>[] copy) {
        for (int i = 0; i < original.length; i++) {
            Parameter<?> parent = original[i].getDependencyParent();
            if (parent != null) {
                for (int j = 0; j < original.length; j++) {
                    if (original[j] == parent) {
                        copy[i].setDependency(copy[j], original[i]
                                .getDependencyValue());
                    }
                }
            }
        }
    }

    public static Parameter<?>[] copyParameters(Parameter<?>[] params) {
        if (params == null)
            return null;

        Parameter<?>[] copy = new Parameter<?>[params.length];
        for (int i = 0; i < params.length; i++) {
            if (params[i].canCopy()) {
                copy[i] = params[i].copy();
            } else {
                copy[i] = params[i];
            }
        }
        addDependenciesToCopy(params, copy);
        return copy;
    }

    private byte[] paramToByteArray(Parameter<?> param) {
        if (!param.canCopy())
            return null;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(param);
            oos.flush();
            return baos.toByteArray();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return null;
    }

    private Parameter<?> byteArrayToParam(byte[] byteArray) {
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(byteArray);
            ObjectInputStream ois = new ObjectInputStream(bais);
            return (Parameter<?>) ois.readObject();
        } catch (IOException ioe) {
            System.out.println("Couldn't load user prefs");
        } catch (ClassNotFoundException ex) {
        }
        System.err.println("Error loading user prefs");
        return null;
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
