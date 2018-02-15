//=============================================================================
//
//   SliderEditComponent.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
//=============================================================================
//$Id: SliderEditComponent.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.editcomponents.yagi;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.ParseException;

import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.DefaultFormatterFactory;

import org.graffiti.plugin.Displayable;
import org.graffiti.plugin.editcomponent.AbstractValueEditComponent;
import org.graffiti.plugin.editcomponent.ValueEditComponent;
import org.graffiti.plugin.parameter.LimitableParameter;

/**
 * Builds the infrastructure for an edit component for numbers. This edit
 * component can be extended by a slider if there are lower and upper bounds for
 * the value range.
 */
public abstract class SliderEditComponent extends AbstractValueEditComponent
        implements ChangeListener, PropertyChangeListener {

    /** The default dimension of the panel. */
    protected final static Dimension DEFAULT_DIM = new Dimension(100, 38);

    /** The textfield of this component. */
    protected JFormattedTextField textField;

    /** The slider of this component. */
    protected JSlider slider;

    /** The verifier of this textField. */
    protected Verifier verifier;

    /** The JPanel containing the textField and the slider. */
    protected JPanel panel;

    /**
     * Indicates if an <code>propertyChange</code> is currently changing the
     * slider value. This is needed to avoid <code>stateChange</code> from
     * changing the slider again.
     */
    protected boolean changeActive = false;

    /** The min value of the slider. */
    protected double sliderMin = 0;

    /** The max value of the slider. */
    protected double sliderMax = 0;

    /** The min value of the textField. */
    protected double textFieldMin = 0;

    /** The max value of the textField. */
    protected double textFieldMax = 0;

    /** The number of steps of the slider. */
    protected final int STEP_COUNT = 100;

    /**
     * Constructs a new SliderEditComponent, referencing the given displayable.
     * 
     * @param disp
     *            the displayable (e.g., an attribute) this VEC belongs to
     */
    protected SliderEditComponent(Displayable<?> disp) {
        super(disp);

        // create the text field to contain the number value
        this.textField = new JFormattedTextField();
        this.textField.setHorizontalAlignment(SwingConstants.RIGHT);
        this.textField.setValue(disp.getValue());
        this.textField.addPropertyChangeListener(this);

        // the formatter is responsible for converting the
        // object value into a string and vice versa, the
        // verifier ensures the bounds
        verifier = new Verifier();
        textField.setFormatterFactory(new DefaultFormatterFactory(
                getFormatter()));
        textField.setInputVerifier(verifier);

        // panel containing the text field and the slider
        panel = new JPanel(new GridLayout(0, 1, 0, 0));
        panel.add(textField);
    }

    /**
     * Constructs a new SliderEditComponent, referencing the given displayable.
     * 
     * @param disp
     *            the displayable (e.g., an attribute) this VEC belongs to
     */
    public SliderEditComponent(LimitableParameter<? extends Number> disp) {
        this((Displayable<?>) disp);

        double n_min = -Double.MAX_VALUE;
        if (disp.getMin() != null) {
            n_min = disp.getMin().doubleValue();
        }
        double n_max = Double.MAX_VALUE;
        if (disp.getMax() != null) {
            n_max = disp.getMax().doubleValue();
        }
        double s_min = -Double.MAX_VALUE;
        if (disp.getSliderMin() != null) {
            s_min = disp.getSliderMin().doubleValue();
        }
        double s_max = Double.MAX_VALUE;
        if (disp.getSliderMax() != null) {
            s_max = disp.getSliderMax().doubleValue();
        }

        createSlider();
        changeActive = true;
        setLimits(s_min, s_max, n_min, n_max);
    }

    /**
     * Has this component a slider.
     * 
     * @return <code>true</code> if there is a slider, <code>false</code>
     *         otherwise.
     */
    protected boolean hasSlider() {
        return slider != null;
    }

    /**
     * Returns the formatter to convert a string value into an object and vice
     * versa. This formatter also ensures the correct format of the input text.
     * 
     * @return Formatter used to convert the values.
     */
    protected JFormattedTextField.AbstractFormatter getFormatter() {
        return new Formatter();
    }

    /**
     * Convert the double value into the needed object type.
     * 
     * @param value
     *            Value to convert.
     * @return Corresponding object type.
     */
    protected abstract Number doubleToNumber(double value);

    /**
     * Check the string format and convert it into the corresponding Number
     * object.
     * 
     * @param value
     *            Text to parse.
     * @return Parsed Number object.
     */
    protected abstract Number stringToNumber(String value)
            throws ParseException;

    /**
     * Create a new slider for this component.
     */
    protected void createSlider() {
        // the slider has by default a maximum value of
        // STEP_COUNT, therefor a transformation is necessary
        // to convert the slider value into the text field value
        slider = new JSlider(0, STEP_COUNT);
        slider.setOrientation(SwingConstants.HORIZONTAL);

        panel.add(slider);
        panel.setMinimumSize(DEFAULT_DIM);
        panel.setPreferredSize(DEFAULT_DIM);
        panel.setMaximumSize(DEFAULT_DIM);
    }

    /**
     * Convert the slider value into the correct value according to the current
     * sliderMin and sliderMax values.
     * 
     * @param value
     *            Value to convert.
     * @return Converted value.
     */
    protected double convertSliderValueToValue(int value) {
        double alpha = value / (double) STEP_COUNT;
        double setValue = (1 - alpha) * sliderMin + alpha * sliderMax;

        return Math.round(setValue * 100d) / 100d;
    }

    /**
     * Convert the current value into the aquivalent slider value according to
     * the current sliderMin and sliderMax values.
     * 
     * @param value
     *            Value to convert.
     * @return Converted value.
     */
    protected int convertValueToSliderValue(double value) {
        double proportion = (value - sliderMin) / (sliderMax - sliderMin);
        return (int) (proportion * STEP_COUNT);
    }

    /**
     * Reacts on changes of the slider.
     * 
     * @param event
     *            The event describing the change.
     */
    public void stateChanged(ChangeEvent event) {

        // don't call propertyChanged again
        if (this.changeActive) {
            this.changeActive = false;
            return;
        }

        JSlider source = (JSlider) event.getSource();
        double setValue = convertSliderValueToValue(source.getValue());

        Number value = doubleToNumber(setValue);
        if (!source.getValueIsAdjusting()) {
            // done adjusting
            // update textfield value
            textField.setValue(value);
        } else {
            // value is adjusting; just set the text
            textField.setText(value.toString());
        }
    }

    /**
     * Listen to the text field. This method detects when the value of the text
     * field changes.
     * 
     * @param event
     *            The event describing the change.
     */
    public void propertyChange(PropertyChangeEvent event) {

        // textfield value changed
        if ("value".equals(event.getPropertyName())
                && textField.getInputVerifier().verify(textField)) {

            if (hasSlider()) {
                Object value = event.getNewValue();
                if (value.equals(EMPTY_STRING) && showEmpty) {
                    changeActive = true;
                    slider.setValue(0);
                    return;
                }
                if (showEmpty) {
                    changeActive = true;
                    setShowEmpty(false);
                }
                if (value != null) {
                    changeActive = true;
                    double val = ((Number) value).doubleValue();

                    if (val < sliderMin) {
                        slider.setValue(0);
                    } else if (val > sliderMax) {
                        slider.setValue(STEP_COUNT);
                    } else {
                        slider.setValue(convertValueToSliderValue(val));
                    }
                }
            }

            fireVECChanged();
        }
    }

    /**
     * Returns the <code>JComponent</code> for editing this edit component.
     * 
     * @return the <code>JComponent</code> for editing this edit component.
     * @see org.graffiti.plugin.editcomponent.ValueEditComponent#getComponent()
     */
    public JComponent getComponent() {
        return this.panel;
    }

    /**
     * Sets the current value of the attribute in the corresponding
     * <code>JComponent</code>.
     * 
     * @see org.graffiti.plugin.editcomponent.ValueEditComponent
     *      #setEditFieldValue()
     */
    @Override
    protected void setDispEditFieldValue() {
        if (!this.showEmpty) {
            this.textField.setValue(this.displayable.getValue());
        } else {
            this.textField.setValue(EMPTY_STRING);
        }
    }

    /**
     * Sets the value of the displayable specified in the
     * <code>JComponent</code>.
     * 
     * @see org.graffiti.plugin.editcomponent.ValueEditComponent#setValue()
     */
    @Override
    protected void setDispValue() {
        if (showEmpty)
            return;
        Object value = textField.getValue();
        if (!(displayable.getValue()).equals(value)) {
            @SuppressWarnings("unchecked")
            Displayable<Object> displayable = (Displayable<Object>) this.displayable;
            displayable.setValue(value);
        }
    }

    /**
     * Specifies whether this component should allow editing.
     * 
     * @param enabled
     *            <code>true</code>, if the component should allow editing,
     *            <code>false</code> if not
     * @see org.graffiti.plugin.editcomponent.ValueEditComponent
     *      #setEnabled(boolean)
     */
    @Override
    public void setEnabled(boolean enabled) {
        if (hasSlider()) {
            this.slider.setEnabled(enabled);
        }
        this.textField.setEnabled(enabled);
    }

    /**
     * Returns if this component allows editing.
     * 
     * @see org.graffiti.plugin.editcomponent.ValueEditComponent#isEnabled()
     * @return <code>true</code>, if the component allows editing,
     *         <code>false</code> if not
     */
    @Override
    public boolean isEnabled() {
        return true;
    }

    /**
     * Specifies if the component shows a value or a dummy (e.g. "---").
     * 
     * @param showEmpty
     *            <code>true</code>, if the component should display a dummy,
     *            <code>false</code> if not
     * @see org.graffiti.plugin.editcomponent.ValueEditComponent
     *      #setShowEmpty(boolean)
     */
    @Override
    public void setShowEmpty(boolean showEmpty) {
        this.showEmpty = showEmpty;
        if (!this.changeActive) {
            this.setEditFieldValue();
        } else {
            changeActive = false;
        }
    }

    /**
     * Returns this component's textField.
     * 
     * @return the textField
     */
    public JTextField getTextField() {
        return this.textField;
    }

    /**
     * Sets the minima and maxima of the slider and the textfield. Enables and
     * updates the slider.
     * 
     * @param sliderMin
     *            the minimum of the slider
     * @param sliderMax
     *            the maximum of the slider
     * @param textFieldMin
     *            the minimum of the textfield
     * @param textFieldMax
     *            the maximum of the textfield
     */
    public void setLimits(double sliderMin, double sliderMax,
            double textFieldMin, double textFieldMax) {
        if (!hasSlider()) {
            createSlider();
        }

        this.sliderMin = sliderMin;
        this.sliderMax = sliderMax;
        this.textFieldMin = textFieldMin;
        this.textFieldMax = textFieldMax;

        // set the slider
        double dispValue = ((Number) this.textField.getValue()).doubleValue();
        int value;
        if (dispValue < this.sliderMin) {
            value = 0;
        } else if (dispValue > this.sliderMax) {
            value = this.STEP_COUNT;
        } else {
            value = convertValueToSliderValue(dispValue);
        }

        this.slider.setValue(value);
        this.slider.addChangeListener(this);
    }

    @Override
    public boolean isValid() {
        // return verifier.verify(textField);

        double currentValue = 0;
        try {
            currentValue = Double.parseDouble(textField.getText());
        } catch (NumberFormatException e) {
            textField.selectAll();
            return false;
        }

        if (currentValue < textFieldMin) {
            textField.selectAll();
            return false;
        }
        if (currentValue > textFieldMax) {
            textField.selectAll();
            return false;
        }
        return true;

    }

    @Override
    public String getErrorMessageOfInvalidParameter() {
        // return verifier.verify(textField);

        double currentValue = 0;
        try {
            currentValue = Double.parseDouble(textField.getText());
        } catch (NumberFormatException e) {
            textField.selectAll();
            return "has to be a numeric expression";
        }

        if (currentValue < textFieldMin) {
            textField.selectAll();
            return "has to be at least " + textFieldMin;
        }
        if (currentValue > textFieldMax) {
            textField.selectAll();
            return "may be at most " + textFieldMax;
        }
        return "";
    }

    /**
     * Handles the conversion from Object to String and vice versa.
     */
    protected class Formatter extends JFormattedTextField.AbstractFormatter {

        /**
         * 
         */
        private static final long serialVersionUID = 9119416761068509665L;

        /**
         * Converts a String to an Object.
         * 
         * @param text
         *            The String to convert.
         * @return The Double-Object of the String.
         * @throws ParseException
         *             if the String can't be converted.
         */
        @Override
        public Object stringToValue(String text) throws ParseException {
            if (text.equals(ValueEditComponent.EMPTY_STRING))
                return text;
            try {
                return stringToNumber(text);
            } catch (NumberFormatException nfe) {
                throw new ParseException(
                        text + " can not be parsed to Number.", -1);
            }
        }

        /**
         * Converts an Object to a String.
         * 
         * @param value
         *            The Object to convert.
         * @return The String representing the Object.
         */
        @Override
        public String valueToString(Object value) {
            return value.toString();
        }
    }

    /**
     * This class is needed for validation of user input.
     */
    protected class Verifier extends InputVerifier {

        /**
         * Checks if the input of a component is valid.
         * 
         * @param input
         *            the component that is to be verified
         * @return <code>true</code>, if the input is valid, <code>false</code>
         *         if not
         */
        @Override
        public boolean verify(JComponent input) {
            if (input instanceof JFormattedTextField && !showEmpty) {
                JFormattedTextField ftf = (JFormattedTextField) input;

                double value = 0;
                try {
                    value = Double.parseDouble(ftf.getText());
                } catch (NumberFormatException e) {
                    Toolkit.getDefaultToolkit().beep();
                    ftf.selectAll();
                    return false;
                }
                if (hasSlider()
                        && (value < textFieldMin || value > textFieldMax)) {
                    // value too small/too big

                    Toolkit.getDefaultToolkit().beep();
                    ftf.selectAll();
                    return false;
                }
                try {
                    ftf.commitEdit();
                } catch (ParseException e) {
                    Toolkit.getDefaultToolkit().beep();
                    ftf.selectAll();
                    return false;
                }
            }
            return true;
        }

        /**
         * Calls verify(input) to ensure that the input is valid.
         * 
         * @param input
         *            the component that is to be verified
         */
        @Override
        public boolean shouldYieldFocus(JComponent input) {
            return verify(input);
        }
    }
}
// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
