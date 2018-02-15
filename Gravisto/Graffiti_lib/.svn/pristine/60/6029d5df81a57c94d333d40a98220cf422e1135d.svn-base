package org.graffiti.plugins.tools.scripted;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Map;

import javax.swing.ImageIcon;

import org.graffiti.editor.GraffitiEditor;
import org.graffiti.plugin.tool.Tool;
import org.graffiti.plugin.view.interactive.InteractiveView;
import org.graffiti.plugin.view.interactive.ViewFamily;
import org.graffiti.util.VoidCallback;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * SAX2 event handler for parsing XML files that specify scripted tools.
 * 
 * @author Andreas Glei&szlig;ner
 * @see ScriptedTool
 * @see ScriptedToolLoader
 */
class ToolParser<T extends InteractiveView<T>> extends DefaultHandler {
    private Class<?> pluginClass;
    private ViewFamily<T> viewFamily;
    private VoidCallback<Tool<T>> callback;
    private Locator locator;
    private LinkedList<String> locales;

    private Tool<T> currentTool;
    private String currentLocale;
    private Map<String, String> names;
    private Map<String, String> descriptions;
    private StringBuffer textBuffer;

    public ToolParser(Class<?> pluginClass, ViewFamily<T> viewFamily,
            VoidCallback<Tool<T>> callback) {
        this.pluginClass = pluginClass;
        this.viewFamily = viewFamily;
        this.callback = callback;
        textBuffer = new StringBuffer();
        names = new HashMap<String, String>();
        descriptions = new HashMap<String, String>();
        createLocales();

    }

    private void createLocales() {
        locales = new LinkedList<String>();
        locales.addFirst("");
        Locale locale = Locale.getDefault();

        String language = locale.getLanguage();
        boolean l = language.length() != 0;

        String country = locale.getCountry();
        boolean c = country.length() != 0;

        String variant = locale.getVariant();
        boolean v = variant.length() != 0;

        StringBuffer buffer = new StringBuffer();
        if (l) {
            buffer.append(language);
            locales.addFirst(language);
        }
        if (c || (l && v)) {
            buffer.append("_");
            if (c) {
                buffer.append(country);
                locales.addFirst(buffer.toString());
            }
        }
        if (v && (l || c)) {
            buffer.append("_").append(variant);
            locales.addFirst(buffer.toString());
        }
    }

    @Override
    public void startElement(String uri, String localName, String name,
            Attributes attributes) throws SAXException {
        if (localName.equals("tool")) {
            String id = attributes.getValue("id");
            if (id == null)
                throw new SAXParseException("Tool tag misses id attribute",
                        locator);
            else if (!Tool.ID_PATTERN.matcher(id).matches())
                throw new SAXParseException(
                        "Tool tag has invalid id attribute", locator);
            String sourcePath = attributes.getValue("source");
            if (sourcePath == null)
                throw new SAXParseException("Tool tag misses source attribute",
                        locator);
            String language = attributes.getValue("language");
            if (language == null)
                throw new SAXParseException(
                        "Tool tag misses language attribute", locator);
            String source = readSource(sourcePath);
            currentTool = new ScriptedTool<T>(viewFamily, id, source, language);
            String imagePath = attributes.getValue("image");
            if (imagePath != null) {
                try {
                    currentTool.setIcon(new ImageIcon(pluginClass
                            .getResource(imagePath)));
                } catch (Exception e) {
                    currentTool.setIcon(new ImageIcon(GraffitiEditor.class
                            .getResource("images/errorTool.png")));
                }
            }
            names = new HashMap<String, String>();
            descriptions = new HashMap<String, String>();
        } else if (localName.equals("name") || localName.equals("description")) {
            currentLocale = attributes.getValue("locale");
            if (currentLocale == null) {
                currentLocale = "";
            }
            textBuffer = new StringBuffer();
        }

    }

    @Override
    public void endElement(String uri, String localName, String name)
            throws SAXException {
        if (localName.equals("name")) {
            names.put(currentLocale, textBuffer.toString());
        } else if (localName.equals("description")) {
            descriptions.put(currentLocale, textBuffer.toString());
        } else if (localName.equals("tool")) {
            setName();
            setDescription();
            callback.call(currentTool);
            currentTool = null;
        }
    }

    private void setName() {
        for (String locale : locales) {
            String value = names.get(locale);
            if (value != null) {
                currentTool.setName(value);
                return;
            }
        }
        currentTool.setName("");
    }

    private void setDescription() {
        for (String locale : locales) {
            String value = descriptions.get(locale);
            if (value != null) {
                currentTool.setDescription(value);
                return;
            }
        }
        currentTool.setDescription("");
    }

    @Override
    public void characters(char[] ch, int start, int length)
            throws SAXException {
        if (textBuffer == null)
            return;
        textBuffer.append(ch, start, length);
    }

    @Override
    public void ignorableWhitespace(char[] ch, int start, int length)
            throws SAXException {
    }

    @Override
    public void setDocumentLocator(Locator locator) {
        this.locator = locator;
        super.setDocumentLocator(locator);
    }

    private String readSource(String path) throws SAXParseException {
        try {
            Reader reader = new InputStreamReader(pluginClass
                    .getResourceAsStream(path));
            char[] array = new char[1024];
            StringBuffer buffer = new StringBuffer();
            int size;
            while ((size = reader.read(array)) > 0) {
                buffer.append(array, 0, size);
            }
            return buffer.toString();
        } catch (IOException e) {
            throw new SAXParseException(null, locator, e);
        }
    }
}
