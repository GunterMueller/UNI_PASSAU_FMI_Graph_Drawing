package org.graffiti.plugins.tools.scripted;

import java.util.Map;

import org.graffiti.plugin.tool.ToolEnvironment;
import org.graffiti.plugin.view.interactive.InteractiveView;
import org.graffiti.plugin.view.interactive.Slot;
import org.graffiti.plugin.view.interactive.Trigger;
import org.graffiti.plugins.scripting.DelegateWrapperUtil;
import org.graffiti.plugins.scripting.Scope;
import org.graffiti.plugins.scripting.ViewScope;
import org.graffiti.plugins.scripting.delegate.ScriptedMethod;
import org.graffiti.plugins.scripting.delegates.SlotListDelegate;
import org.graffiti.plugins.scripting.exceptions.IllegalScriptingArgumentException;
import org.graffiti.plugins.scripting.exceptions.ScriptingException;

/**
 * Scope for scripts specifying tools.
 * 
 * @author Andreas Glei&szlig;ner
 * @see ScriptedTool
 */
class ToolScope<T extends InteractiveView<T>> extends Scope {
    /**
     * The environment of the scripted tool.
     */
    private ToolEnvironment<T> environment;

    /**
     * The scripted tool.
     */
    private ScriptedTool<T> tool;

    /**
     * Is {@code true} if the script is executed in response to the activation
     * of the tool. Is {@code false} if the script is executed to process a user
     * gesture.
     */
    private boolean isActivated;

    /**
     * Wraps the input slots.
     */
    private SlotListDelegate ins;

    /**
     * Wraps the output slots.
     */
    private SlotListDelegate out;

    public ToolScope(ViewScope<T> viewScope, ToolEnvironment<T> environment,
            ScriptedTool<T> tool) {
        super(viewScope);
        this.environment = environment;
        this.tool = tool;
        isActivated = false;
        ins = new SlotListDelegate(this, environment.getIn());
        out = new SlotListDelegate(this, environment.getOut());
        put("state", 0);
        put("ins", ins);
        put("out", out);
    }

    public void setEnvironment(ToolEnvironment<T> environment) {
        this.environment = environment;
        out.setSlots(environment.getOut());
        ins.setSlots(environment.getIn());
    }

    public void setActivated(boolean isActivated) {
        this.isActivated = isActivated;
    }

    @ScriptedMethod
    public void action(String actionId) {
        environment.execute(actionId);
    }

    @ScriptedMethod
    public boolean activated() {
        return isActivated;
    }

    @ScriptedMethod
    public boolean matches(String triggerId, Object... params)
            throws ScriptingException {
        Trigger trigger = environment.getTrigger(triggerId);
        if (trigger == null)
            throw new IllegalScriptingArgumentException("matches", "Trigger "
                    + triggerId + " not found.");
        Map<String, Slot<?>> parameters = trigger.createParameters();
        for (Object paramObj : params) {
            if (!(paramObj instanceof Object[]))
                throw new IllegalScriptingArgumentException("matches");
            Object[] param = (Object[]) paramObj;
            if (param.length != 2)
                throw new IllegalScriptingArgumentException("matches");
            Slot<?> slot = parameters.get(param[0]);
            Object value = param[1];
            DelegateWrapperUtil.put(environment.getParam(), slot, value);
        }
        return environment.matches(triggerId);
    }

    @ScriptedMethod
    public void reset() {
        tool.reset();
    }
}
