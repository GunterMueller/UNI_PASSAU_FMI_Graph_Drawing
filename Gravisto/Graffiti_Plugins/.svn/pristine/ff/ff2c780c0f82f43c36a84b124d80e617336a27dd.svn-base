package org.graffiti.plugins.tools.scripted;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.graffiti.plugin.GenericPlugin;
import org.graffiti.plugin.tool.Tool;
import org.graffiti.plugin.view.interactive.InteractiveView;
import org.graffiti.plugin.view.interactive.ViewFamily;
import org.graffiti.util.VoidCallback;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * {@code ScriptedToolLoader} creates scripted tools specified by XML files of
 * plugins.
 * 
 * @author Andreas Glei&szlig;ner
 */
public class ScriptedToolLoader {
    /**
     * Creates the scripted tools specified in the tools.xml file located in the
     * package of the specified plugin.
     * 
     * @param plugin
     *            plugin with the package containing the XML file "tools.xml",
     *            which specifies to scripted tools to create.
     * @param viewFamily
     *            the view family to support by the tools to create.
     * @return a list of the created tools.
     */
    public static <T extends InteractiveView<T>> List<Tool<T>> loadTools(
            GenericPlugin plugin, ViewFamily<T> viewFamily) {
        Class<?> pluginClass = plugin.getClass();
        final LinkedList<Tool<T>> list = new LinkedList<Tool<T>>();
        try {
            XMLReader reader = XMLReaderFactory.createXMLReader();
            ToolParser<T> toolParser = new ToolParser<T>(pluginClass,
                    viewFamily, new VoidCallback<Tool<T>>() {
                        public void call(Tool<T> tool) {
                            list.add(tool);
                        }
                    });
            reader.setContentHandler(toolParser);
            reader.parse(new InputSource(pluginClass
                    .getResourceAsStream("tools.xml")));
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return list;
    }
}
