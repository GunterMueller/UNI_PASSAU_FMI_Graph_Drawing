package quoggles.auxiliary.attributes;

import org.graffiti.plugin.GenericPluginAdapter;

/**
 * This class provides the additional attribute types for the QUOGGLES system.
 */
public class QuogglesAttributesPlugin
    extends GenericPluginAdapter
{
    //~ Constructors ===========================================================

    /**
     * Constructs a new <code>QuogglesAttributesPlugin</code>.
     */
    public QuogglesAttributesPlugin()
    {
        super();
        this.attributes = new Class[2];
        attributes[0] = CollAttribute.class;
        attributes[1] = ObjectAttribute.class;
    }
}

//------------------------------------------------------------------------------
//   end of file
//------------------------------------------------------------------------------
