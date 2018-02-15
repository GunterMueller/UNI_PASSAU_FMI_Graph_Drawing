// =============================================================================
//
// FunctionManager.java
//
//   Copyright (c) 2004 Graffiti Team, Uni Passau
//
// =============================================================================
// $Id: FunctionManager.java 5766 2010-05-07 18:39:06Z gleissner $

// NOTE: This class is based on class civquest.swing.FunctionManager, 
// which is part of project CivQuest at www.sourceforge.net/projects/civquest
// Last cvs-version inside CivQuest: 1.27 of 2004-04-10

package org.graffiti.plugins.modes.advanced;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import javax.swing.event.MouseInputAdapter;
import javax.swing.event.MouseInputListener;

/**
 * Keeps track of all functions a FunctionComponent offers, including all
 * bindings for these functions.
 */
public class FunctionManager {
    /**
     * Maps Function-objects to their BindingInfo-objects. Each Function
     * administrated by this FunctionManager is a key in this Map.
     */
    private Map<Function, BindingInfo> functions = new HashMap<Function, BindingInfo>();

    /**
     * The functions this FunctionManager administrates all belong to this
     * FunctionComponent
     */
    private FunctionComponent functionComponent;

    private PrePostMouseListener prePostMouseListener;

    /***************************************************************************
     * ****** Prefixes for event-strings. CAUTION WHEN CHANGING THEM: They may
     * ******* NOT interfere with the legal Strings for
     **************************************************************************/

    /** String-prefix marking mouse-events like "button x pressed" */
    private static final String MOUSE_PREFIX = "mouse";

    /** String-prefix marking mouse-moved-events. */
    private static final String MOUSE_MOVED_PREFIX = "movedMouse";

    /** String-prefix marking mouse-entered-events */
    private static final String MOUSE_ENTERED_PREFIX = "enteredMouse";

    /** String-prefix marking mouse-exited-events */
    private static final String MOUSE_EXITED_PREFIX = "exitedMouse";

    /** String-prefix marking popup-trigger-events */
    private static final String MOUSE_POPUP_PREFIX = "popupTrigger";

    /**
     * Constructs a new FunctionManager, associated to the given
     * FunctionComponent.
     * 
     * @param functionComponent
     *            any FunctionComponent
     */
    public FunctionManager(FunctionComponent functionComponent) {
        this.functionComponent = functionComponent;
        prePostMouseListener = new PrePostMouseListener(functionComponent);
    }

    public void addFunction(String event, String functionName)
            throws InvalidInputEventException, NoSuchFunctionActionException,
            InvalidParameterException {

        addFunction(event, functionName, new HashMap<String, Object>());
    }

    public void addFunction(String event, String functionName,
            String paramOneKey, Object paramOneValue)
            throws InvalidInputEventException, NoSuchFunctionActionException,
            InvalidParameterException {

        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put(paramOneKey, paramOneValue);
        addFunction(event, functionName, paramMap);
    }

    public void addFunction(String event, String functionName,
            String paramOneKey, Object paramOneValue, String paramTwoKey,
            Object paramTwoValue) throws InvalidInputEventException,
            NoSuchFunctionActionException, InvalidParameterException {

        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put(paramOneKey, paramOneValue);
        paramMap.put(paramTwoKey, paramTwoValue);
        addFunction(event, functionName, paramMap);
    }

    public void addFunction(String event, String functionName,
            String paramOneKey, Object paramOneValue, String paramTwoKey,
            Object paramTwoValue, String paramThreeKey, Object paramThreeValue)
            throws InvalidInputEventException, NoSuchFunctionActionException,
            InvalidParameterException {

        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put(paramOneKey, paramOneValue);
        paramMap.put(paramTwoKey, paramTwoValue);
        paramMap.put(paramThreeKey, paramThreeValue);
        addFunction(event, functionName, paramMap);
    }

    public void addFunction(String event, String functionName,
            Map<String, Object> parameters) throws InvalidInputEventException,
            NoSuchFunctionActionException, InvalidParameterException {

        Set<String> eventSet = new HashSet<String>();
        eventSet.add(event);
        addFunction(eventSet, functionName, parameters);
    }

    /**
     * Adds a new Function with the given properties to this FunctionManager.
     * Afterwards, the given JComponent will execute the FunctionAction assigned
     * to the new Function whenever one of the given events happens.
     * 
     * @param events
     *            Set of event-Strings
     * @param functionName
     *            name of the new Function
     * @param parameters
     *            parameters for the new FunctionAction
     * @throws InvalidInputEventException
     *             if one of the given events has invalid syntax
     * @throws NoSuchFunctionActionException
     *             if the FunctionAction assigned to the new function can't be
     *             found
     */
    public void addFunction(Set<String> events, String functionName,
            Map<String, Object> parameters) throws InvalidInputEventException,
            NoSuchFunctionActionException, InvalidParameterException {

        // Construct function
        Function function = new Function(functionName);
        function.addBindings(events);
        function.setParameters(parameters);

        BindingInfo bindingInfo = new BindingInfo();
        functions.put(function, bindingInfo);

        // Fetch FunctionAction assigned to the function, test if it exists
        FunctionAction functionAction = FunctionManager.getFunctionAction(
                functionName, functionComponent);

        if (functionAction == null)
            throw new NoSuchFunctionActionException(functionName);

        function.setAction(functionAction);

        checkParameters(function);

        // Parameters where loaded before the FunctionAction and saved only
        // inside the Function-object up to now. However, we need
        // them inside the FunctionAction-object.
        Set<String> predefinedParamKeys = function.putParametersIntoAction();
        if (!predefinedParamKeys.isEmpty()) {
            System.err.println("Warning: Parameters " + predefinedParamKeys
                    + " to function " + function.getName() + " may interfere "
                    + "with built-in swing-keys!!!");
        }

        // Construct information within bindingInfo
        setBindings(function, bindingInfo);
    }

    static void checkParameters(Function function)
            throws InvalidParameterException {

        FunctionAction action = function.getAction();
        Map<String, Set<Object>> validParameters = action.getValidParameters();

        Iterator<String> iterator = function.getParameterIterator();
        while (iterator.hasNext()) {
            String currName = iterator.next();

            if (!validParameters.containsKey(currName))
                throw new InvalidParameterException("Function "
                        + function.getName() + " has no parameter "
                        + "with name " + currName);

            Set<Object> validValues = validParameters.get(currName);
            Object currValue = function.getParameterValue(currName);

            if (!validValues.contains(currValue))
                throw new InvalidParameterException(currValue
                        + " is not a valid value for parameter " + currName
                        + " in function " + function.getName());
        }
    }

    public void activateAllFunctions(JComponent jComponent) {
        activateAllKeyBindings(jComponent);
        activateAllMouseBindings(jComponent);
    }

    public void activateAllKeyBindings(JComponent component) {
        for (Function function : functions.keySet()) {
            Function currFunction = function;
            BindingInfo currBindingInfo = functions.get(currFunction);

            Iterator<KeyStroke> keyStrokeIterator = currBindingInfo
                    .getKeyStrokeIterator();
            while (keyStrokeIterator.hasNext()) {
                KeyStroke currKeyStroke = keyStrokeIterator.next();

                // Update input-map
                InputMap inputMap = component.getInputMap();
                Object inputValue = inputMap.get(currKeyStroke);
                if (inputValue == null) {
                    inputMap.put(currKeyStroke, currKeyStroke);
                } else if (inputValue != currKeyStroke) {
                    System.out.println("FunctionManager.activateAllKeyBindings"
                            + " says: Warning while processing " + currFunction
                            + ":Might overwrite " + " existing bindings!");
                    inputMap.put(currKeyStroke, currKeyStroke);
                } else {
                    // Nothing to be done here
                }

                // Update action-map
                ActionMap actionMap = component.getActionMap();
                Object actionValue = actionMap.get(currKeyStroke);
                PrePostAction prePostAction = null;
                if (actionValue == null) {
                    prePostAction = new PrePostAction(functionComponent);
                    actionMap.put(currKeyStroke, prePostAction);
                } else if (!(actionValue instanceof PrePostAction)) {
                    System.out.println("FunctionManager.activateAllKeyBindings"
                            + " says: Warning while processing " + currFunction
                            + ":Might overwrite " + " existing bindings!");
                    prePostAction = new PrePostAction(functionComponent);
                    actionMap.put(currKeyStroke, prePostAction);
                } else {
                    prePostAction = (PrePostAction) actionValue;
                }

                prePostAction.addFunctionAction(currFunction.getAction());
            }

        }
    }

    public void activateAllMouseBindings(JComponent jComponent) {
        jComponent.addMouseListener(prePostMouseListener);
        jComponent.addMouseMotionListener(prePostMouseListener);
    }

    public void deactivateAllFunctions(JComponent jComponent) {
        deactivateAllKeyBindings(jComponent);
        deactivateAllMouseBindings(jComponent);
    }

    public void deactivateAllKeyBindings(JComponent component) {
        for (Function currFunction : functions.keySet()) {
            BindingInfo currBindingInfo = functions.get(currFunction);

            Iterator<KeyStroke> keyStrokeIterator = currBindingInfo
                    .getKeyStrokeIterator();
            while (keyStrokeIterator.hasNext()) {
                KeyStroke currKeyStroke = keyStrokeIterator.next();

                // Update input-map
                InputMap inputMap = component.getInputMap();
                Object inputValue = inputMap.get(currKeyStroke);

                if (inputValue != null && inputValue != currKeyStroke) {
                    System.out.println("FunctionManager.deactivateAllKey"
                            + "Bindings says: Warning: This input-"
                            + "map doesn't look like I expect it.");
                }

                inputMap.put(currKeyStroke, null);

                // Update action-map
                ActionMap actionMap = component.getActionMap();
                Object actionValue = actionMap.get(currKeyStroke);

                if (actionValue != null
                        && !(actionValue instanceof PrePostAction)) {
                    System.out.println("FunctionManager.deactivateAllKey"
                            + "Bindings says: Warning: This input-"
                            + "map doesn't look like I expect it.");
                }

                actionMap.put(currKeyStroke, null);
            }

        }
    }

    public void deactivateAllMouseBindings(JComponent jComponent) {
        jComponent.removeMouseListener(prePostMouseListener);
        jComponent.removeMouseMotionListener(prePostMouseListener);
    }

    private void setBindings(Function function, BindingInfo bindingInfo)
            throws InvalidInputEventException {
        FunctionAction currAction = function.getAction();

        Iterator<String> bindingIterator = function.getBindingIterator();
        while (bindingIterator.hasNext()) {
            String currBindingString = (bindingIterator.next());
            // System.out.println("Assigning " + currBindingString
            // + " to " + function.getName());
            setOneBinding(currBindingString, currAction, function.getName(),
                    bindingInfo);
        }
    }

    /**
     * Set up (within the given JComponent) one binding "if the given event
     * happens, execute the given FunctionAction", where the FunctionAction is
     * associated to a Function with the given name.
     * 
     * @param event
     *            any valid input-event, see the texinfo-docs for a definition
     *            of valid input-events
     * @param action
     *            FunctionAction associated to the function with the given name
     * @param name
     *            name of a function
     * @throws InvalidInputEventException
     *             if the event-String has invalid syntax
     */
    private void setOneBinding(String event, FunctionAction action,
            String name, BindingInfo bindingInfo)
            throws InvalidInputEventException {

        // Get first token; it determines how we must handle the binding
        String firstToken = "";
        StringTokenizer tokenizer = new StringTokenizer(event);
        if (tokenizer.hasMoreTokens()) {
            firstToken = tokenizer.nextToken();
        }

        if (firstToken.equals(MOUSE_PREFIX)) {
            // Binding needs a mouse-listener - set it up
            addMouseListener(event, name, action, bindingInfo);

        } else if (firstToken.equals(MOUSE_MOVED_PREFIX)
                || firstToken.equals(MOUSE_ENTERED_PREFIX)
                || firstToken.equals(MOUSE_EXITED_PREFIX)) {
            // Binding consists of modifier-keys + a mouse-moved/entered/
            // exited-event
            addMouseMotionAreaListener(event, name, action, bindingInfo);
        } else if (firstToken.equals(MOUSE_POPUP_PREFIX)) {
            addMousePopupTriggerListener(event, name, action, bindingInfo);
        } else {
            // Anything without one of the prefixes defined in this class
            // is considered to be a KeyStroke-event as defined by the
            // Java-API
            addKeyStrokeBinding(event, name, action, bindingInfo);
        }
    }

    /**
     * Adds the needed (according to the given event-String) MouseListener to
     * the given JComponent. For more information on the syntax of the
     * event-String, have a look into the texinfo-docs.
     * 
     * @param event
     *            String describing the mouse-event
     * @param functionName
     *            function assigned to the currently processed binding
     * @param action
     *            action assigned to functionName
     * @throws InvalidInputEventException
     *             if the event-String has invalid syntax
     */
    private void addMouseListener(String event, String functionName,
            FunctionAction action, BindingInfo bindingInfo)
            throws InvalidInputEventException {

        // check syntax - after this call, we can assume that the syntax of
        // the event is ok (throws InvalidInputEventException else)
        checkMouseSyntax(event, functionName);

        // number of "basic"-events for current event
        int number = 1;
        // the modifier-keys which have to be pressed while the event happens
        // (bitmask specified in Java-API, class InputEvent)
        int pressedMod = 0;
        // the modifier-keys which must not be pressed while the event happens
        // (bitmask specified in Java-API, class InputEvent)
        int releasedMod = 0;
        // the event-type; one of the MOUSE_PRESSED, _RELEASED, _CLICKED -
        // constants of class MouseEvent
        int eventType = 0;
        // the mouse-button; one of the MouseEvent.BUTTON(1|2|3) - constants
        int button = -1;

        StringTokenizer tokenizer = new StringTokenizer(event);

        // Skip MOUSE_START_STRING
        tokenizer.nextToken();

        String currToken;

        // <number>x
        final int NUMBER = 0;
        // modifier-keys
        final int MOD = 1;
        // event-type like pressed
        final int EVENT_TYPE = 2;
        // button
        final int BUTTON = 3;

        // which part of the String is processed currently?
        int state = NUMBER;

        while (tokenizer.hasMoreTokens()) {
            currToken = tokenizer.nextToken();

            if (state == NUMBER) {
                // read number of basic-events

                // System.out.println("currToken=" + currToken + ";");

                if (Pattern.matches("\\A[0-9]*x\\z", currToken)) {
                    // is given explicitely (if omitted, 1 is assumed)

                    // the last character is the "x"
                    String numberString = currToken.substring(0, currToken
                            .length() - 1);

                    // convert - as the syntax is ok, no error should occur here
                    number = Integer.parseInt(numberString);

                    // go to reading the modifier-keys
                    state = MOD;
                    continue;
                } else {
                    // number of basic-events is not given explicitely, so
                    // we must check for currToken marking a modifier-key
                    state = MOD;
                }
            }

            if (state == MOD) {
                // (try to) read one modifier-key

                // get bit-mask for currToken
                Integer mask = getKeyModifierMask(currToken);

                if (mask == null) {
                    // currToken is no modifier-key => currToken marks the
                    // event-type
                    state = EVENT_TYPE;

                } else if (currToken.startsWith("not_")) {
                    // currToken is a not_x - String, adjust releasedMod
                    releasedMod = releasedMod | mask.intValue();
                    continue;
                } else {
                    // currToken is a x - String, adjust pressedMod
                    pressedMod = pressedMod | mask.intValue();
                    continue;
                }
            }

            if (state == EVENT_TYPE) {
                // read event-type

                if (currToken.equals("pressed")) {
                    eventType = MouseEvent.MOUSE_PRESSED;
                } else if (currToken.equals("released")) {
                    eventType = MouseEvent.MOUSE_RELEASED;
                } else if (currToken.equals("clicked")) {
                    eventType = MouseEvent.MOUSE_CLICKED;
                } else {
                    assert false : "Syntax-check should prevent this"
                            + " currToken = " + currToken;
                }

                state = BUTTON;
                continue;
            }

            if (state == BUTTON) {
                // read button

                if (currToken.equals("button1")) {
                    button = MouseEvent.BUTTON1;
                } else if (currToken.equals("button2")) {
                    button = MouseEvent.BUTTON2;
                } else if (currToken.equals("button3")) {
                    button = MouseEvent.BUTTON3;
                } else {
                    assert false : "Syntax-check should prevent this"
                            + " currToken = " + currToken;
                }

                break;
            }
        }
        /*
         * System.out.println("After reading: number = " + number + ";
         * pressedMod = " + pressedMod + "; releasedMod = " + releasedMod + ";
         * eventType = " + eventType + "; button = " + button);
         */

        // construct MouseListener
        MouseListener mouseListener = null;

        if (eventType == MouseEvent.MOUSE_PRESSED) {
            mouseListener = new PressedConfigMouseAdapter(action, number,
                    pressedMod, releasedMod, button);

        } else if (eventType == MouseEvent.MOUSE_RELEASED) {
            mouseListener = new ReleasedConfigMouseAdapter(action, number,
                    pressedMod, releasedMod, button);
        } else if (eventType == MouseEvent.MOUSE_CLICKED) {
            mouseListener = new ClickedConfigMouseAdapter(action, number,
                    pressedMod, releasedMod, button);
        } else {
            assert false : "Syntax-check should prevent this";
        }

        // Save mouseListener - useful if we ever support enabling/disabling
        // functions at any time
        bindingInfo.addMouseListener(mouseListener);

        // PrePostMouseListener registers at a given JComponent later and
        // passes events to the mouseListeners we add here
        prePostMouseListener.addMouseListener(mouseListener);
    }

    /**
     * Checks if the given mouse-event-String (an event starting with "mouse"),
     * bound to the given function, has correct syntax. Does nothing, if yes,
     * else an {@link InvalidInputEventException} is thrown.
     * 
     * @param event
     *            a mouse-event-String
     * @param functionName
     *            name of the function associated to the event (is needed for
     *            feeding the exception with some useful information on what
     *            went wrong exactly)
     * @throws InvalidInputEventException
     *             if the given mouse-event-String has incorrect syntax
     */
    private void checkMouseSyntax(String event, String functionName)
            throws InvalidInputEventException {

        // Set of legal mouse-event-Strings is a real subset of the set of
        // Strings given by this regexp. \\A means beginning of input,
        // \\z end of input, \\s any whitespace-character
        String pattern = "\\A" + MOUSE_PREFIX + "\\s*" + "([0-9]*x\\s*)?"
                + "((alt|alt_graph|shift|meta|ctrl|not_alt|not_alt_graph|"
                + "not_shift|not_meta|not_ctrl)\\s*)*"
                + "(pressed|released|clicked)\\s*" + "button[1-3]" + "\\z";

        // check syntax, ignore restrictions concerning modifier-keys
        if (!Pattern.matches(pattern, event))
            throw new InvalidInputEventException(event, functionName);

        checkModifierKeyRestrictions(event, functionName);

        // if everything is ok with the event-string, this method doesn't need
        // to do anything
    }

    /**
     * Adds (depending on the prefix-value of the event-String) a
     * MouseInputAdapter for a mouse-moved, mouse-entered or mouse-entered-event
     * to the given JComponent.
     * 
     * @param event
     *            String defining the event, starting with MOUSE_MOVED_PREFIX,
     *            MOUSE_ENTERED_PREFIX or MOUSE_EXITED_PREFIX
     * @param functionName
     *            name of the function assigned to the event
     * @param action
     *            action assigned to the function
     * @throws InvalidInputEventException
     *             if the syntax of the event-String is not valid
     */
    private void addMouseMotionAreaListener(String event, String functionName,
            FunctionAction action, BindingInfo bindingInfo)
            throws InvalidInputEventException {

        StringTokenizer tokenizer = new StringTokenizer(event);

        // Get XXX_PREFIX-string
        String prefix = null;

        if (tokenizer.hasMoreTokens()) {
            prefix = tokenizer.nextToken();
        }

        // check syntax - if incorrect, this method throws the
        // InvalidInputEventException
        checkMouseMotionAreaSyntax(event, functionName, prefix);

        // bitmask indicating which keys/mouse-buttons must be pressed
        int pressedModMask = 0;
        // bitmask indicating which keys/mouse-buttons must NOT be pressed
        int releasedModMask = 0;

        // Construct pressedModMask and releasedModMask
        while (tokenizer.hasMoreTokens()) {
            String currModifier = tokenizer.nextToken();
            Integer currMask = getKeyButtonModifierMask(currModifier);

            if (currMask == null) {
                System.err.println("FunctionManager.addMouseMotionListener:");
                System.err.println("currModifier = " + currModifier);
                System.err.println("Since we checked the syntax before, "
                        + "this shouldn't happen!");
                System.exit(-1);
            } else {
                if (currModifier.startsWith("not_")) {
                    // currToken is a not_x - String, adjust releasedMod
                    releasedModMask = releasedModMask | currMask.intValue();
                } else {
                    // currToken is a x - String, adjust pressedMod
                    pressedModMask = pressedModMask | currMask.intValue();
                }
            }
        }

        // a mouse-moved-event
        if (prefix.equals(MOUSE_MOVED_PREFIX)) {

            if (!buttonsInvolved(event)) {
                // if no button is pressed, a MouseMoved-event, but _not_ a
                // MouseDragged-event happens
                MouseInputListener listener = new MouseMovedConfigAdapter(
                        action, pressedModMask, releasedModMask);
                bindingInfo.addMouseMotionListener(listener);
                prePostMouseListener.addMouseMotionListener(listener);
            }

            MouseInputListener listener = new MouseDraggedConfigAdapter(action,
                    pressedModMask, releasedModMask);

            // Save mouseListener - useful if we ever support enabling/disabling
            // functions at any time
            bindingInfo.addMouseMotionListener(listener);

            // PrePostMouseListener registers at a given JComponent later and
            // passes events to the mouseListeners we add here
            prePostMouseListener.addMouseMotionListener(listener);
        } else if (prefix.equals(MOUSE_ENTERED_PREFIX)) {
            // a mouse-entered-event

            MouseInputListener mouseListener = new MouseEnteredConfigAdapter(
                    action, pressedModMask, releasedModMask);

            // Save mouseListener - useful if we ever support enabling/disabling
            // functions at any time
            bindingInfo.addMouseListener(mouseListener);

            // PrePostMouseListener registers at a given JComponent later and
            // passes events to the mouseListeners we add here
            prePostMouseListener.addMouseListener(mouseListener);
        } else if (prefix.equals(MOUSE_EXITED_PREFIX)) {
            // a mouse-exited-event
            MouseInputListener mouseListener = new MouseExitedConfigAdapter(
                    action, pressedModMask, releasedModMask);

            // Save mouseListener - useful if we ever support enabling/disabling
            // functions at any time
            bindingInfo.addMouseListener(mouseListener);

            // PrePostMouseListener registers at a given JComponent later and
            // passes events to the mouseListeners we add here
            prePostMouseListener.addMouseListener(mouseListener);
        } else {
            System.err.println("FunctionManager.addMouseMotionAreaListener:");
            System.err.println("This shouldn't happen!!! (probably the "
                    + "syntax-check is buggy...)");
            System.exit(-1);
        }
    }

    /**
     * Checks if the given String describes (syntactically correct) a
     * mouse-moved, mouse-entered or mouse-exited-event. has correct syntax.
     * Throws an InvalidInputEventException if not.
     * 
     * @param event
     *            String to be checked
     * @param functionName
     *            function assigned to the event - used by the exception to
     *            generate some senseful message on what went wrong
     * @param prefix
     *            MOUSE_MOVED_PREFIX, MOUSE_ENTERED_PREFIX or
     *            MOUSE_EXITED_PREFIX - for which type of event the String
     *            should be checked
     * @throws InvalidInputEventException
     *             if the given String has incorrect syntax
     */
    private void checkMouseMotionAreaSyntax(String event, String functionName,
            String prefix) throws InvalidInputEventException {

        // Set of legal mouse-motion-event-Strings is a real subset of the set
        // of Strings given by this regexp. \\A means beginning of input,
        // \\z end of input, \\s any whitespace-character
        String pattern = "\\A" + prefix
                + "(\\s*(alt|alt_graph|shift|meta|ctrl|not_alt|not_alt_graph|"
                + "not_shift|not_meta|not_ctrl|button[1-3]|not_button[1-3]))*"
                + "\\z";

        // check syntax, ignore restrictions concerning modifier-keys/buttons
        if (!Pattern.matches(pattern, event))
            throw new InvalidInputEventException(event, functionName);

        checkModifierKeyRestrictions(event, functionName);
        checkButtonRestrictions(event, functionName);
    }

    /**
     * Checks the given event-String for the following restrictions: (1) if each
     * modifier-key is mentioned only once (2) if not_x and x do not both occur
     * in the string
     * 
     * @param event
     *            event-String to test
     * @param functionName
     *            name of the function the event-String is assigned to
     * @throws InvalidInputEventException
     *             if the check fails
     */
    private void checkModifierKeyRestrictions(String event, String functionName)
            throws InvalidInputEventException {

        int altOcc = getOccurrenceNumber(event, "alt");
        int altGraphOcc = getOccurrenceNumber(event, "alt_graph");
        int shiftOcc = getOccurrenceNumber(event, "shift");
        int metaOcc = getOccurrenceNumber(event, "meta");
        int ctrlOcc = getOccurrenceNumber(event, "ctrl");
        int notAltOcc = getOccurrenceNumber(event, "not_alt");
        int notAltGraphOcc = getOccurrenceNumber(event, "not_alt_graph");
        int notShiftOcc = getOccurrenceNumber(event, "not_shift");
        int notMetaOcc = getOccurrenceNumber(event, "not_meta");
        int notCtrlOcc = getOccurrenceNumber(event, "not_ctrl");

        // check restriction that any key-defining-String may occur max. once
        if (altOcc > 1 || altGraphOcc > 1 || shiftOcc > 1 || metaOcc > 1
                || ctrlOcc > 1 || notAltOcc > 1 || notAltGraphOcc > 1
                || notShiftOcc > 1 || notMetaOcc > 1 || notCtrlOcc > 1) {
            System.out.println("checkModifierKeyRestrictionsaltOcc: " + altOcc);
            throw new InvalidInputEventException(event, functionName);
        }

        // check restriction that x and not_x may not occur in the same event
        if ((altOcc == 1 && notAltOcc == 1)
                || (altGraphOcc == 1 && notAltGraphOcc == 1)
                || (shiftOcc == 1 && notShiftOcc == 1)
                || (metaOcc == 1 && notMetaOcc == 1)
                || (ctrlOcc == 1 && notCtrlOcc == 1))
            throw new InvalidInputEventException(event, functionName);
    }

    /**
     * Checks the given event-String for the following restrictions: (1) if each
     * button is mentioned max. once (2) if each not_buttonX and buttonX do not
     * occur both in the String
     * 
     * @param event
     *            event-String to test
     * @param functionName
     *            function assigned to the event-String
     * @throws InvalidInputEventException
     *             if the check fails
     */
    private void checkButtonRestrictions(String event, String functionName)
            throws InvalidInputEventException {

        int[] buttonOcc = new int[3];
        int[] notButtonOcc = new int[3];

        // determine how often each of the button-Strings occurs in the event
        for (int n = 0; n < buttonOcc.length; n++) {
            buttonOcc[n] = getOccurrenceNumber(event, "button" + (n + 1));
            notButtonOcc[n] = getOccurrenceNumber(event, "not_button" + (n + 1));
        }

        // check restriction that any button-String may occur max. once
        for (int n = 0; n < buttonOcc.length; n++) {
            if (buttonOcc[n] > 1 || notButtonOcc[n] > 1)
                throw new InvalidInputEventException(event, functionName);
        }

        // check restriction that x and not_x may not occur in the same event
        for (int n = 0; n < 3; n++) {
            if (buttonOcc[n] == 1 && notButtonOcc[n] == 1)
                throw new InvalidInputEventException(event, functionName);
        }
    }

    /**
     * Checks, if the given event-String contains mouse-buttons.
     * 
     * @param event
     *            event-String to check
     * @return if the event-String contains mouse-buttons
     */
    private boolean buttonsInvolved(String event) {
        return (getOccurrenceNumber(event, "button1") > 0)
                || (getOccurrenceNumber(event, "button2") > 0)
                || (getOccurrenceNumber(event, "button3") > 0);
    }

    /**
     * Returns how often the given token exists as a token (in the sense of the
     * one-parameter-constructor of class StringTokenizer) in the given String.
     * 
     * @param s
     *            any String
     * @param token
     *            any String
     * @return returns how often the given token exists as a token in the given
     *         String
     */
    private int getOccurrenceNumber(String s, String token) {
        StringTokenizer tokenizer = new StringTokenizer(s);

        int retValue = 0;
        while (tokenizer.hasMoreTokens()) {
            if (tokenizer.nextToken().equals(token)) {
                retValue++;
            }
        }

        return retValue;
    }

    private void addMousePopupTriggerListener(String event,
            String functionName, FunctionAction action, BindingInfo bindingInfo)
            throws InvalidInputEventException {

        checkMousePopupTriggerSyntax(event, functionName);

        MouseInputListener listener = new MousePopupTriggerAdapter(action);
        prePostMouseListener.addMouseListener(listener);

        // prePostMouseMotionListener.addMouseMotionListener(listener);
    }

    private void checkMousePopupTriggerSyntax(String event, String functionName)
            throws InvalidInputEventException {
        if (!Pattern.matches("\\A" + MOUSE_POPUP_PREFIX + "\\z", event))
            throw new InvalidInputEventException(event, functionName);
    }

    /**
     * Adds a new keystroke-binding for the given event, function-name and
     * action to the given JComponent.
     * 
     * @param event
     *            String defining a KeyStroke-event (syntax defined in the
     *            Java-API, class KeyStroke, method getKeyStroke(String s)
     * @param functionName
     *            name of the function we assign the event to
     * @param action
     *            FunctionAction assigned to the given function
     * @throws InvalidInputEventException
     *             if event-String has invalid syntax
     */
    private void addKeyStrokeBinding(String event, String functionName,
            FunctionAction action, BindingInfo bindingInfo)
            throws InvalidInputEventException {

        KeyStroke keyStroke = KeyStroke.getKeyStroke(event);

        if (keyStroke == null)
            throw new InvalidInputEventException(event, functionName);
        else {
            bindingInfo.addKeyStroke(keyStroke);
        }
    }

    /**
     * Returns the mask (specified by the Java API, class InputEvent) for the
     * given modifier key (syntax defined in the texinfo-docs). The mask of a
     * not_x-String is the mask of the associated x-String.
     * 
     * @param name
     *            name of the modifier-key
     * @return Integer-object with the mask, if name was a legal name of a
     *         modifier-key, null otherwise
     */
    private Integer getKeyModifierMask(String name) {
        if (name.startsWith("not_")) {
            name = name.substring(4, name.length());
        }

        if (name.equals("alt"))
            return new Integer(InputEvent.ALT_DOWN_MASK);
        else if (name.equals("alt_graph"))
            return new Integer(InputEvent.ALT_GRAPH_DOWN_MASK);
        else if (name.equals("meta"))
            return new Integer(InputEvent.META_DOWN_MASK);
        else if (name.equals("shift"))
            return new Integer(InputEvent.SHIFT_DOWN_MASK);
        else if (name.equals("ctrl"))
            return new Integer(InputEvent.CTRL_DOWN_MASK);
        else
            return null;
    }

    /**
     * Returns the mask (specified by the Java API, class InputEvent) for the
     * given button (syntax defined in the texinfo-docs). The mask of a
     * not_x-String is the mask of the associated x-String.
     * 
     * @param name
     *            name of the mouse-button
     * @return Integer-object with the mask, if name was a legal name of a
     *         mouse-button, null otherwise
     */
    private Integer getButtonModifierMask(String name) {
        if (name.startsWith("not_")) {
            name = name.substring(4, name.length());
        }

        if (name.equals("button1"))
            return new Integer(InputEvent.BUTTON1_DOWN_MASK);
        else if (name.equals("button2"))
            return new Integer(InputEvent.BUTTON2_DOWN_MASK);
        else if (name.equals("button3"))
            return new Integer(InputEvent.BUTTON3_DOWN_MASK);
        else
            return null;
    }

    /**
     * Returns the mask for the given modifier-key or mouse-button.
     * 
     * @param name
     *            name of the modifier-key/mouse-button
     * @return Integer-object with the mask, if name was a legal name of a
     *         mouse-button/modifier-key, null otherwise
     */
    private Integer getKeyButtonModifierMask(String name) {
        Integer keyMask = getKeyModifierMask(name);
        Integer buttonMask = getButtonModifierMask(name);

        if (keyMask != null)
            return keyMask;
        else if (buttonMask != null)
            return buttonMask;
        else
            return null;
    }

    /**
     * Implementations of this abstract class are added to JComponents by
     * addMouseListener. Each implementing class provides the actual
     * MouseAdapter-implementation for executing the action (function)
     * associated to a mousePressed, mouseReleased or mouseClicked-event.
     * <p>
     * The mouseXXX-methods (implemented by subclasses) perform the job of
     * checking all additional rules (like "execute FunctionAction only if event
     * happens twice"). They use {@link #happenedEvent} for doing this.
     */
    private abstract class ConfigMouseAdapter extends MouseAdapter {

        /**
         * FunctionAction associated to the function of the binding this
         * MouseAdapter is associated to
         */
        FunctionAction action;

        /** Action is executed only if the event happened <number> times */
        int number;

        /**
         * Action is only executed if the modifier-keys specified by this
         * bitmask are pressed (see doc of class InputEvent)
         */
        int pressedModMask;

        /**
         * Action is only executed if none of the modifier-keys specified by
         * this bitmask is pressed (see doc of class InputEvent)
         */
        int releasedModMask;

        /**
         * Action is executed only if the event which happened involved this
         * mouse-button. Allowed values: {@link MouseEvent#BUTTON1},
         * {@link MouseEvent#BUTTON2}, {@link MouseEvent#BUTTON3}
         */
        int button;

        /**
         * Constructs a new ConfigMouseAdapter and sets its variables.
         * 
         * @param action
         *            Action assigned to this ConfigMouseAdapter
         * @param number
         *            number of basic events needed for executing the Action
         * @param pressedModMask
         *            bitmask of pressed modifier-keys
         * @param releasedModMask
         *            bitmask of "forbidden" modifier-keys
         * @param button
         *            mouse-button that needs to be involved
         */
        private ConfigMouseAdapter(FunctionAction action, int number,
                int pressedModMask, int releasedModMask, int button) {
            this.action = action;
            this.number = number;
            this.pressedModMask = pressedModMask;
            this.releasedModMask = releasedModMask;
            this.button = button;
        }

        /**
         * Checks if the given MouseEvent satisfies the rules given by
         * {@link #number}, {@link #pressedModMask}, {@link #releasedModMask},
         * {@link #button}.
         * 
         * @param mouseEvent
         *            any MouseEvent
         * @return if the given MouseEvent satisfies all rules (see above)
         */
        protected boolean happenedEvent(MouseEvent mouseEvent) {

            // System.out.println("Called happenedEvent - "
            // + "mouseEvent.getModEx = " + mouseEvent.getModifiersEx());
            // System.out.println(mouseEvent.getClickCount() + "<->" + number);

            if (mouseEvent.getClickCount() != number)
                return false;

            if ((mouseEvent.getModifiersEx() & (pressedModMask | releasedModMask)) != pressedModMask)
                return false;

            // System.out.println(mouseEvent.getButton() + " <-> " + button);

            if (mouseEvent.getButton() != button)
                return false;

            // no check failed, so all rules are satisfied
            return true;
        }
    }

    /** ConfigMouseAdapter for mousePressed-events */
    private class PressedConfigMouseAdapter extends ConfigMouseAdapter {

        private PressedConfigMouseAdapter(FunctionAction action, int number,
                int pressedModMask, int releasedModMask, int button) {
            super(action, number, pressedModMask, releasedModMask, button);
        }

        @Override
        public void mousePressed(MouseEvent e) {
            if (happenedEvent(e)) {
                PositionInfo positionInfo = functionComponent.getPositionInfo();
                FunctionActionEvent event = new FunctionActionEvent(e
                        .getSource(), ActionEvent.ACTION_PERFORMED, null,
                        positionInfo.getMousePosition());

                action.actionPerformed(event);
            }
        }
    }

    /** ConfigMouseAdapter for mouseReleased-events */
    private class ReleasedConfigMouseAdapter extends ConfigMouseAdapter {

        private ReleasedConfigMouseAdapter(FunctionAction action, int number,
                int pressedModMask, int releasedModMask, int button) {
            super(action, number, pressedModMask, releasedModMask, button);
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            if (happenedEvent(e)) {
                PositionInfo positionInfo = functionComponent.getPositionInfo();
                FunctionActionEvent event = new FunctionActionEvent(e
                        .getSource(), ActionEvent.ACTION_PERFORMED, null,
                        positionInfo.getMousePosition());

                action.actionPerformed(event);
            }
        }
    }

    /** ConfigMouseAdapter for mouseClicked-events */
    private class ClickedConfigMouseAdapter extends ConfigMouseAdapter {

        private ClickedConfigMouseAdapter(FunctionAction action, int number,
                int pressedModMask, int releasedModMask, int button) {
            super(action, number, pressedModMask, releasedModMask, button);
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            if (happenedEvent(e)) {
                PositionInfo positionInfo = functionComponent.getPositionInfo();
                FunctionActionEvent event = new FunctionActionEvent(e
                        .getSource(), ActionEvent.ACTION_PERFORMED, null,
                        positionInfo.getMousePosition());

                action.actionPerformed(event);
            }
        }
    }

    /**
     * Implementations of this abstract class are added to JComponents and
     * provide the code for executing mouseMoved, mouseDragged, mouseEntered and
     * mouseExited-events.
     * <p>
     * The mouseXXX-methods (implemented by subclasses) perform the job of
     * checking the rules concerning modifier-keys (which may be pressed, which
     * not). They use {@link #happenedEvent} for doing this.
     */
    private abstract class ConfigMouseMotionAreaAdapter extends
            MouseInputAdapter {
        /**
         * Action associated to the function of the binding this adapter is
         * associated to
         */
        FunctionAction action;

        /**
         * Action is only executed if the modifier-keys specified by this
         * bitmask are pressed (see doc of class InputEvent)
         */
        int pressedModMask;

        /**
         * Action is only executed if none of the modifier-keys specified by
         * this bitmask is pressed (see doc of class InputEvent)
         */
        int releasedModMask;

        /**
         * Constructs a new ConfigMotionMouseAdapter and sets its variables.
         * 
         * @param action
         *            FunctionAction assigned to this ConfigMouseAdapter
         * @param pressedModMask
         *            bitmask of pressed modifier-keys
         * @param releasedModMask
         *            bitmask of "forbidden" modifier-keys
         */
        private ConfigMouseMotionAreaAdapter(FunctionAction action,
                int pressedModMask, int releasedModMask) {
            this.action = action;
            this.pressedModMask = pressedModMask;
            this.releasedModMask = releasedModMask;
        }

        /**
         * Checks if the given MouseEvent satisfies the rules given by
         * {@link #pressedModMask} and {@link #releasedModMask}.
         * 
         * @param mouseEvent
         *            any MouseEvent
         * @return if the given MouseEvent satisfies all rules (see above)
         */
        protected boolean happenedEvent(MouseEvent mouseEvent) {
            // System.out.println("Constants: Button1: "
            // + InputEvent.BUTTON1_MASK);
            // System.out.println(" Button1 down: "
            // // + InputEvent.BUTTON1_DOWN_MASK);

            // System.out.println("Called happenedEvent - "
            // + "mouseEvent.getModEx = "
            // + mouseEvent.getModifiersEx());

            // System.out.println((mouseEvent.getModifiersEx()
            // & (pressedModMask | releasedModMask)) + " <-> "
            // + pressedModMask);

            return ((mouseEvent.getModifiersEx() & (pressedModMask | releasedModMask)) == pressedModMask);
        }
    }

    /** ConfigMouseMotionAdapter for mouseMoved-events */
    private class MouseMovedConfigAdapter extends ConfigMouseMotionAreaAdapter {

        private MouseMovedConfigAdapter(FunctionAction action,
                int pressedModMap, int releasedModMap) {
            super(action, pressedModMap, releasedModMap);
        }

        @Override
        public void mouseMoved(MouseEvent e) {
            if (happenedEvent(e)) {
                PositionInfo positionInfo = functionComponent.getPositionInfo();
                FunctionActionEvent event = new FunctionActionEvent(e
                        .getSource(), ActionEvent.ACTION_PERFORMED, null,
                        positionInfo.getMousePosition());

                action.actionPerformed(event);
            }
        }
    }

    /** ConfigMouseMotionAdapter for mouseDragged-events */
    private class MouseDraggedConfigAdapter extends
            ConfigMouseMotionAreaAdapter {

        private MouseDraggedConfigAdapter(FunctionAction action,
                int pressedModMap, int releasedModMap) {
            super(action, pressedModMap, releasedModMap);
        }

        public void mouseDragged(MouseEvent e) {
            if (happenedEvent(e)) {
                PositionInfo positionInfo = functionComponent.getPositionInfo();
                FunctionActionEvent event = new FunctionActionEvent(e
                        .getSource(), ActionEvent.ACTION_PERFORMED, null,
                        positionInfo.getMousePosition());

                action.actionPerformed(event);
            }
        }
    }

    /** ConfigMouseMotionAreaAdapter for mouseEntered-events */
    private class MouseEnteredConfigAdapter extends
            ConfigMouseMotionAreaAdapter {

        private MouseEnteredConfigAdapter(FunctionAction action,
                int pressedModMap, int releasedModMap) {
            super(action, pressedModMap, releasedModMap);
        }

        public void mouseEntered(MouseEvent e) {

            if (happenedEvent(e)) {
                PositionInfo positionInfo = functionComponent.getPositionInfo();
                FunctionActionEvent event = new FunctionActionEvent(e
                        .getSource(), ActionEvent.ACTION_PERFORMED, null,
                        positionInfo.getMousePosition());

                action.actionPerformed(event);
            }
        }
    }

    /** ConfigMouseMotionAreaAdapter for mouseExited-events */
    private class MouseExitedConfigAdapter extends ConfigMouseMotionAreaAdapter {

        private MouseExitedConfigAdapter(FunctionAction action,
                int pressedModMap, int releasedModMap) {
            super(action, pressedModMap, releasedModMap);
        }

        public void mouseExited(MouseEvent e) {

            if (happenedEvent(e)) {
                PositionInfo positionInfo = functionComponent.getPositionInfo();
                FunctionActionEvent event = new FunctionActionEvent(e
                        .getSource(), ActionEvent.ACTION_PERFORMED, null,
                        positionInfo.getMousePosition());

                action.actionPerformed(event);
            }
        }
    }

    private class MousePopupTriggerAdapter extends MouseInputAdapter {
        private FunctionAction action;

        private MousePopupTriggerAdapter(FunctionAction action) {
            this.action = action;
        }

        public void mousePressed(MouseEvent e) {
            if (e.isPopupTrigger()) {
                PositionInfo positionInfo = functionComponent.getPositionInfo();
                FunctionActionEvent event = new FunctionActionEvent(e
                        .getSource(), ActionEvent.ACTION_PERFORMED, null,
                        positionInfo.getMousePosition());

                action.actionPerformed(event);
            }
        }

        public void mouseReleased(MouseEvent e) {
            if (e.isPopupTrigger()) {
                PositionInfo positionInfo = functionComponent.getPositionInfo();
                FunctionActionEvent event = new FunctionActionEvent(e
                        .getSource(), ActionEvent.ACTION_PERFORMED, null,
                        positionInfo.getMousePosition());

                action.actionPerformed(event);
            }
        }

        public void mouseClicked(MouseEvent e) {
            if (e.isPopupTrigger()) {
                PositionInfo positionInfo = functionComponent.getPositionInfo();
                FunctionActionEvent event = new FunctionActionEvent(e
                        .getSource(), ActionEvent.ACTION_PERFORMED, null,
                        positionInfo.getMousePosition());

                action.actionPerformed(event);
            }
        }
    }

    /** ********************************************************************** */
    /** ********************** Static helper-methods ************************* */
    /** ********************************************************************** */

    /**
     * Returns the name of that first sub-component as specified by the
     * subComponentString. If subComponentString is built up like
     * "component1.component2...componentN.function", this function returns
     * "component1". If N=0 in the example (String names just a function), null
     * is returned.
     * 
     * @param subComponentString
     *            String naming a "path" of components, ending in a function
     * @return name of the first sub-component as described, null if no such
     *         component exists
     */
    public static String getDirectSubComponentName(String subComponentString) {
        // String[] parts = subComponentString.split(Function.SEPARATOR);

        String[] parts = subComponentString.split("\\.");

        if (parts.length <= 1)
            // String names just a function
            return null;
        else
            return parts[0];
    }

    public static FunctionComponent getProvidingComponent(
            FunctionComponent start, String function) {

        FunctionComponent currComponent = start;

        while (true) {
            String nextComponentName = FunctionManager
                    .getDirectSubComponentName(function);

            if (nextComponentName == null)
                return currComponent;
            else {
                function = cutSuperComponentPrefix(function, nextComponentName);
                currComponent = currComponent
                        .getSubComponent(nextComponentName);
            }
        }
    }

    /**
     * Adds the given prefix naming a super-component to all Functions stored in
     * the given Set.
     * 
     * @param functions
     *            Set of Functions, may not contain anything else
     * @param prefix
     *            prefix to add
     */
    public static void addSuperComponentPrefix(Set<Function> functions,
            String prefix) {
        for (Function function : functions) {
            function.addSuperComponentPrefix(prefix);
        }
    }

    /**
     * Cuts the given super-component-prefix from the given function-name.
     * Returns the result of that operation, if the function-name really starts
     * with the given prefix, null otherwise.
     * 
     * @param functionName
     *            function-name
     * @param prefix
     *            prefix of the function-name
     * @return function-name minus prefix, if it starts with the prefix, null
     *         otherwise
     */
    public static String cutSuperComponentPrefix(String functionName,
            String prefix) {
        prefix = prefix + Function.SEPARATOR;
        if (functionName.startsWith(prefix))
            return functionName.replaceFirst(prefix, "");
        else
            return null;
    }

    /**
     * Gets the FunctionAction assigned to the given function within the given
     * FunctionComponent. The function-name may contain subcomponent-prefixes,
     * in this case. The given FunctionComponent (or its sub-components will be
     * asked for the FunctionAction.
     * 
     * @param functionName
     *            name of a function as described
     * @param functionComponent
     *            any FunctionComponent
     * @return FunctionAction assigned to the given function, if it can be
     *         found, null otherwise
     */
    public static FunctionAction getFunctionAction(String functionName,
            FunctionComponent functionComponent) {

        FunctionComponent currComponent = functionComponent;

        // go downwards in the FunctionComponent-hierarchy using the prefixes of
        // the given function.
        while (true) {

            FunctionAction action = currComponent
                    .getFunctionAction(functionName);
            if (action != null)
                // currComponent knows the function, so we have a FunctionAction
                // to
                // return
                return action;
            else {
                String separator = Function.SEPARATOR;
                // Hack until I find a good way of splitting up a String
                // without interfering with regexps
                if (separator.equals(".")) {
                    separator = "\\.";
                }

                String[] parts = functionName.split(separator);

                if (parts.length <= 1)
                    // no component-prefix exists, so we can't search any sub-
                    // component, so we must give up here
                    return null;
                else {
                    currComponent = currComponent.getSubComponent(parts[0]);
                    if (currComponent == null)
                        // the sub-component specified by the prefix doesn't
                        // exist, so we must give up here
                        return null;
                    else {
                        // cut prefix
                        functionName = "";
                        for (int n = 1; n < parts.length; n++) {
                            // after the last part, no separator may be added
                            String suffix = (n < parts.length - 1 ? Function.SEPARATOR
                                    : "");
                            functionName += (parts[n] + suffix);
                        }
                    }
                }
            }
        }
    }
}
