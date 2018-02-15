package org.graffiti.plugins.scripting.delegate;

/**
 * Enumeration, which denotes if a field can be get or set from a script.
 * 
 * @see FieldDelegate
 * @see ScriptedField
 */
public enum FieldAccess {
    /**
     * The field can be get, but not set.
     */
    Get(true, false),

    /**
     * The field can be set, but not get.
     */
    Set(false, true),

    /**
     * The field can be get and set.
     */
    All(true, true),

    /**
     * The field cannot be accessed from the script.
     */
    None(false, false),

    /**
     * If the field is represented by {@code FieldDelegate}, it automatically
     * manages its accessibility. Else the field can be get, but not set.
     * 
     * @see FieldDelegate
     */
    Auto(true, false);

    /**
     * Denotes if the field can be get.
     */
    private boolean canGet;

    /**
     * Denotes if the field can be set.
     */
    private boolean canSet;

    /**
     * Constructs a {@code FieldAccess} enumeration member with the specified
     * field access permissions.
     * 
     * @param canGet
     *            denotes if the field can be get.
     * @param canSet
     *            denotes if the field can be set.
     */
    private FieldAccess(boolean canGet, boolean canSet) {
        this.canGet = canGet;
        this.canSet = canSet;
    }

    /**
     * Returns if the field can be get.
     * 
     * @return if the field can be get.
     */
    public boolean canGet() {
        return canGet;
    }

    /**
     * Returns if the field can be set.
     * 
     * @return if the field can be set.
     */
    public boolean canSet() {
        return canSet;
    }
}
