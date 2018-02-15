package org.graffiti.plugin.parameter;

public class StringSelectionParameter extends AbstractSingleParameter<String> {

    /**
     * 
     */
    private static final long serialVersionUID = -7896867467836918169L;

    private String[] params;

    private int index = 0;

    public StringSelectionParameter(String[] params, String name,
            String description) {
        super(name, description);
        this.params = params;
    }

    public String getSelectedValue() {
        return params[index];
    }

    @Override
    public String getValue() {
        return params[index];
    }

    @Override
    public void setValue(String value) {
        for (int i = 0; i < params.length; i++) {
            if (params[i].equals(value)) {
                index = i;
                return;
            }
        }
    }

    public int getSelectedIndex() {
        return index;
    }

    public void setSelectedValue(int index) {
        this.index = index;
    }

    public int numOfParams() {
        return params.length;
    }

    public String[] getParams() {
        return params.clone();
    }

}
