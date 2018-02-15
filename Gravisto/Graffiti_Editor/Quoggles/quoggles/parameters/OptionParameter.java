package quoggles.parameters;

import org.graffiti.plugin.XMLHelper;
import org.graffiti.plugin.parameter.AbstractSingleParameter;
import org.graffiti.util.GeneralUtils;

/**
 *
 */
public class OptionParameter extends AbstractSingleParameter {
    
    protected Object[] options;
    
    protected int optionNr = -1;
    
    protected boolean isEditable = false;
    
    
    /**
     * Constructor for OptionParameter. Selects first option.
     * @param opts
     * @param name
     * @param description
     */
    public OptionParameter(Object[] opts, String name, String description) {
        super(opts[0], name, description);
        optionNr = 0;
        options = opts;
    }

    /**
     * Constructor for OptionParameter. Selects option with index 
     * <code>optionNr</code>.
     * 
     * @param opts
     * @param optionNr
     * @param name
     * @param description
     */
    public OptionParameter(Object[] opts, int optionNr, 
        String name, String description) {
        
        super(opts[optionNr], name, description);
        this.optionNr = optionNr;
        options = opts;
    }

    /**
     * Constructor for OptionParameter. Selects first option. Can choose 
     * whether or not values can be added later on.
     *
     * @param opts
     * @param editable
     * @param name
     * @param description
     */
    public OptionParameter(Object[] opts, boolean editable, 
        String name, String description) {

        super(opts[0], name, description);
        optionNr = 0;
        options = opts;
        isEditable = editable;
    }

    /**
     * Constructor for OptionParameter. Selects option with index 
     * <code>optionNr</code>. Can choose whether or not values can be added 
     * later on.
     * 
     * @param opts
     * @param optionNr
     * @param editable
     * @param name
     * @param description
     */
    public OptionParameter(Object[] opts, int optionNr, boolean editable, 
        String name, String description) {
        
        super(opts[optionNr], name, description);
        this.optionNr = optionNr;
        options = opts;
        isEditable = editable;
    }


    /**
     * Returns true if new values can be added to this parameter later on.
     * 
     * @return
     */
    public boolean isEditable() {
        return isEditable;
    }

    /**
     * @see quoggles.parameters.AbstractParameter#setValue(java.lang.Object)
     */
    public void setValue(Object val) throws IllegalArgumentException {
        super.setValue(val);
        
        optionNr = -1;
        for (int i = 0; i < options.length; i++) {
            if (val.equals(options[i]))  {
                optionNr = i;
                return;
            }
        }
        
        // value not contained in options
        if (!isEditable) {
            throw new IllegalArgumentException("Passed unknown value to non-" +
                "editable OptionParameter.");
        }

//        if (isEditable) {
//            // value not saved in parameter add it
//            Object[] newOptions = new Object[options.length + 1];
//            System.arraycopy(options, 0, newOptions, 0, options.length);
//            options = newOptions;
//            optionNr = options.length - 1;
//            options[optionNr] = val;
//        } else {
//            throw new IllegalArgumentException("Passed unknown value to non-" +
//                "editable OptionParameter.");
//        }
    }
    
//    public void setOptionNr(int nr) {
//        optionNr = nr;
//        setValue(options[nr]);
//    }
    
    public int getOptionNr() {
        return optionNr;
    }
    
    public void setOptions(Object[] opts) {
        options = opts;
        optionNr = -1;
    }

    /**
     * Returns the options array.
     * 
     * @return the options array.
     */
    public Object[] getOptions() {
        return options;
    }

    /**
     * @see org.graffiti.plugin.parameter.Parameter#toXMLString()
     */
    public String toXMLString() {
        StringBuffer valString = new StringBuffer();
        valString.append("<options>" + XMLHelper.getDelimiter());
        for (int i = 0; i < options.length; i++) {
            valString.append(XMLHelper.spc(6) + "<option type=\\\"" + 
            options[i].getClass().getName() + "\\\">" +
            GeneralUtils.XMLify(options[i].toString()) +
                "</option>" + XMLHelper.getDelimiter());
        }
        valString.append(XMLHelper.spc(6) + "</options>" + 
            XMLHelper.getDelimiter() + XMLHelper.spc(4) +
            "<properties selectedOption=\\\"" + optionNr);
        if (optionNr < 0) {
            valString.append("\\\" userValue=\\\"" + getValue().toString());
        }
        valString.append("\\\" editable=\\\"" + 
            (isEditable ? "true" : "false") + "\\\"/>");
        
        return getStandardXML(valString.toString());
    }
}
