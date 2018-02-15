package org.graffiti.plugins.tools.demos;

/**
 * Temporary class for demonstration purposes, which will be removed once there
 * is a real plugin employing the new tool system.
 * 
 * @author Andreas Glei&szlig;ner
 */
class ExampleTool2 extends AbstractExampleTool {
    public ExampleTool2() {
        super("asdfghj");
        setName("Idle Tool");
    }

    @Override
    protected boolean isDefaultMode() {
        return false;
    }
}
