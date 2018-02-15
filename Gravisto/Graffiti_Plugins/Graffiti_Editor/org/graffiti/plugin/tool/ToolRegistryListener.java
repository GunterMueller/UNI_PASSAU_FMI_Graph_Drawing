package org.graffiti.plugin.tool;

/**
 * Classes implementing {@code ToolRegistryListener} are interested in changes
 * to the tool list.
 * 
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 * @see ToolRegistry#addListener(ToolRegistryListener)
 * @see ToolRegistry#registerTool(Tool)
 * @see ToolRegistry#registerToolFactory(ToolFactory)
 */
public interface ToolRegistryListener {
    /**
     * Is called when the specified tool has been registered.
     * 
     * @param tool
     *            the tool that has been registered.
     * @see ToolRegistry#registerTool(Tool)
     */
    public void toolRegistered(Tool<?> tool);

    /**
     * Is called when the specified tool factory has been registered.
     * 
     * @param toolFactory
     *            the tool factory that has been registered.
     * @see ToolRegistry#registerTool(Tool)
     */
    public void toolFactoryRegistered(ToolFactory toolFactory);
}
