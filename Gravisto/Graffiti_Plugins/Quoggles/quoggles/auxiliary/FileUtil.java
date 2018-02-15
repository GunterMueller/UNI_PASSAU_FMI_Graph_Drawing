package quoggles.auxiliary;

import java.awt.Point;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.crimson.jaxp.DocumentBuilderFactoryImpl;
import org.apache.crimson.tree.TextNode;
import org.graffiti.attributes.Attribute;
import org.graffiti.attributes.CollectionAttribute;
import org.graffiti.attributes.StringAttribute;
import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;
import org.graffiti.graphics.CoordinateAttribute;
import org.graffiti.graphics.GraphicAttributeConstants;
import org.graffiti.plugin.Displayable;
import org.graffiti.plugin.XMLHelper;
import org.graffiti.plugin.editcomponent.ValueEditComponent;
import org.graffiti.plugin.io.InputSerializer;
import org.graffiti.plugin.io.ParserException;
import org.graffiti.plugin.parameter.BooleanParameter;
import org.graffiti.plugin.parameter.DoubleParameter;
import org.graffiti.plugin.parameter.IntegerParameter;
import org.graffiti.plugin.parameter.ObjectParameter;
import org.graffiti.plugin.parameter.Parameter;
import org.graffiti.plugin.parameter.StringParameter;
import org.graffiti.plugins.editcomponents.defaults.DoubleEditComponent;
import org.graffiti.plugins.editcomponents.defaults.IntegerEditComponent;
import org.graffiti.plugins.editcomponents.defaults.StringEditComponent;
import org.graffiti.plugins.ios.gml.gmlReader.GmlReader;
import org.graffiti.util.InstanceCreationException;
import org.graffiti.util.InstanceLoader;
import org.graffiti.util.Pair;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import quoggles.QAssign;
import quoggles.QAuxiliary;
import quoggles.boxes.IBox;
import quoggles.constants.QConstants;
import quoggles.exceptions.BoxCreationFailedException;
import quoggles.exceptions.LoadFailedException;
import quoggles.parameters.OptionParameter;
import quoggles.parameters.VECParameter;
import quoggles.querygraph.BoxAttribute;
import quoggles.representation.BoxRepresentation;
import quoggles.stdboxes.connectors.OneOneConnector_Rep;
import quoggles.stdboxes.subquery.SubQuery_Box;

/**
 * Auxiliary class holding public ststic methods concerning the use of files.
 */
public class FileUtil {

    /**
     * Calls <code>getQueryFromSomeFile(true)</code>.
     * 
     * @see getQueryFromSomeFile(boolean)
     */
    public static final Pair getQueryFromSomeFile()
        throws FileNotFoundException, LoadFailedException {
            
        return getQueryFromSomeFile(true);
    }

    /**
     * Displays a file chooser dialog. The file should contain a graph in GML
     * format with the nodes having attributes that specify boxes (in XML).<p>
     * If no exception is thrown, a <code>Pair</code> is returned consisting
     * of the loaded graph and the file from which the graph was loaded.
     * 
     * @param useGraphic decides whether any manipulations / loadings of 
     * graphical representations should be done
     * 
     * @return <code>Pair</code> (<code>Graph</code> / <code>File</code>)
     * 
     * @throws FileNotFoundException thrown if the file chosen could not be
     * found
     * @throws LoadFailedException thrown if anything went wrong during the
     * loading process
     */
    public static final Pair getQueryFromSomeFile(boolean useGraphic)
        throws FileNotFoundException, LoadFailedException {

        int returnVal = QAuxiliary.fChooser.showOpenDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File loadFile = QAuxiliary.fChooser.getSelectedFile();
            return new Pair(getQueryFromFile(loadFile, useGraphic), loadFile);
        } else {
            return null;
        }
    }

    /**
     * Calls <code>getQueryFromFile(true)</code>.
     * 
     * @see getQueryFromFile(boolean)
     */
    public static final Graph getQueryFromFile(File loadFile)
        throws FileNotFoundException, LoadFailedException {
        
        return getQueryFromFile(loadFile, true);
    }

    /**
     * The given file should contain a graph in GML
     * format with the nodes having attributes that specify boxes (in XML).<p>
     * If no exception is thrown, the loaded graph is returned.
     * 
     * @param useGraphic decides whether any manipulations / loadings of 
     * graphical representations should be done
     * 
     * @return <code>Pair</code> (<code>Graph</code> / <code>File</code>)
     * 
     * @throws FileNotFoundException thrown if the file chosen could not be
     * found
     * @throws LoadFailedException thrown if anything went wrong during the
     * loading process
     */
    public static final Graph getQueryFromFile(File loadFile, boolean useGraphic)
        throws FileNotFoundException, LoadFailedException {

        if(!loadFile.exists()) {
            throw new FileNotFoundException("The file " +
                loadFile.getPath() + " could not be found.");
        }
                
        // save assignment if nothing is loaded
        boolean[] assignedRowsCopy = QAssign.getAssignedRowsCopy();
        boolean[] assignedBEPRowsCopy = QAssign.getAssignedBEPRowsCopy();
        QAssign.resetAssignedRows();

        InputSerializer is = new GmlReader();
        Graph newQueryGraph = null;
        
        try {
        
        try {
            newQueryGraph = is.read(new FileInputStream(loadFile));
        } catch(ParserException pe) {
            throw new LoadFailedException("Could not read query from " +
                "file " + loadFile.getPath() + ": " + 
                pe.getLocalizedMessage());
        } catch(IOException ioe) {
            throw new LoadFailedException("IO failed with file " +
                loadFile.getPath() + ": " + 
                ioe.getLocalizedMessage());
        }
                
        if (newQueryGraph == null) {
            throw new LoadFailedException("Reader returned \"null\" at " +
                "file " + loadFile.getPath() + ".");
        }
            
        // convert XML attributes to BoxAttributes
        DocumentBuilderFactory dbf = new DocumentBuilderFactoryImpl();
        DocumentBuilder docBuilder = null;
        try {
            docBuilder = dbf.newDocumentBuilder();
        } catch (ParserConfigurationException pce) {
            throw new LoadFailedException(pce);
        }
        
        Collection nodesToRemove = new ArrayList(0);
        for (Iterator it = newQueryGraph.getNodesIterator(); it.hasNext();) {
            Node node = (Node)it.next();
            for (Iterator atIt = ((CollectionAttribute)node
                .getAttribute("")).getCollection().values().iterator();
                atIt.hasNext(); ) {
                        
                Attribute attr = (Attribute)atIt.next();
                String attrId = attr.getId();
                if (attrId.startsWith(QConstants.XML_PREFIX)) {
                    try {
                        boolean removeNode = convertAttribute
                            (attr, node, docBuilder, useGraphic);
                        if (removeNode) {
                            nodesToRemove.add(node);
                            break;
                        }
                    } catch (InstanceCreationException ice) {
                        JOptionPane.showMessageDialog(null, 
                            "Error creating boxes: " + ice.getLocalizedMessage(),
                            "Error:", JOptionPane.ERROR_MESSAGE);
                    } catch (BoxCreationFailedException bcfe) {
                        JOptionPane.showMessageDialog(null, 
                            "Error creating boxes: " + bcfe.getLocalizedMessage(),
                            "Error:", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        }
        
        for (Iterator it = nodesToRemove.iterator(); it.hasNext();) {
            Node node = (Node)it.next();
            newQueryGraph.deleteNode(node);
        }
        
        } catch (Exception ex) {
            // reset
            QAssign.setAssignedRows(assignedRowsCopy);
            QAssign.setAssignedBEPRows(assignedBEPRowsCopy);
            throw new LoadFailedException(ex.getMessage());
        }
        
        return newQueryGraph;
    }
    
    /**
     * Interpret XML attribute values and create BoxAttributes.
     * 
     * @param attr
     * @param node
     * @param docBuilder
     * @param useGraphic decides whether any manipulations / loadings of 
     * graphical representations should be done
     */
    private static boolean convertAttribute(Attribute attr, Node node, 
        DocumentBuilder docBuilder, boolean useGraphic) 
        throws BoxCreationFailedException, InstanceCreationException {

        String realId = attr.getId().substring(QConstants.XML_PREFIX.length());
        node.removeAttribute(attr.getPath().substring(1));
        String attrValue = ((StringAttribute)attr).getString();
                        
        attrValue = attrValue.replaceAll("\\\\\"", "\"");
        
        StringReader stringReader = new StringReader(attrValue);
        InputSource inputSource = new InputSource(stringReader);
        Document document = null;
        try {
            document = docBuilder.parse(inputSource);
        } catch (SAXException e2) {
            throw new BoxCreationFailedException(e2);
        } catch (IOException e2) {
            throw new BoxCreationFailedException(e2);
        }

        Element attrNode = document.getDocumentElement();

        // get classname and create attribute
        String classname = attrNode.getAttribute("classname");
        if (classname.indexOf("BoxAttribute") < 0) {
            throw new BoxCreationFailedException
                ("Only BoxAttributes expected.");
        }
            
        BoxAttribute newAttr = new BoxAttribute(realId);
        
        // read the attribute's value
        Element valueNode = (Element)attrNode
            .getElementsByTagName("value").item(0);
        Element boxNode = (Element)valueNode
            .getElementsByTagName("box").item(0);

        classname = boxNode.getAttribute("classname");
        String inputBoxName = boxNode.getAttribute("name");
        IBox box = null;
        if (inputBoxName != null && !"".equals(inputBoxName)) {
            box = (IBox)InstanceLoader.createInstance(classname, inputBoxName);
        } else {
            box = (IBox)InstanceLoader.createInstance(classname);
        }
        box.setNode(node);
//        if (box instanceof NormalOutput_Box) {
//            ((NormalOutput_Box)box).setLastRowNumber(-1);
//        }
        
        if (useGraphic) {
            Element coordNode = (Element)valueNode
                .getElementsByTagName("geometry").item(0);
            int x = 0;
            int y = 0;
            int w = 0;
            int h = 0;
            try {
                x = Integer.parseInt(coordNode.getAttribute("x"));
                y = Integer.parseInt(coordNode.getAttribute("y"));
                w = Integer.parseInt(coordNode.getAttribute("w"));
                h = Integer.parseInt(coordNode.getAttribute("h"));
            } catch (NumberFormatException nfe) {
                throw new BoxCreationFailedException
                    ("Could not parse coordinates: " + nfe);
            }
            if (x == 0 && y == 0) {
                return true;
            }
            BoxRepresentation boxRep = 
                box.getGraphicalRepresentation().getRepresentation();
            boxRep.setLocation(x, y);
            ((CoordinateAttribute)node.getAttribute
                (GraphicAttributeConstants.COORD_PATH)).setCoordinate
                    (new Point(x + boxRep.getWidth()/2 + QConstants.shiftX, 
                        y + boxRep.getHeight()/2));
            boxRep.setSize(w, h);
            String fromTL = coordNode.getAttribute("fromTopLeft");
            try {
                if (fromTL != null && !"".equals(fromTL)) {
                    boolean fromTopLeft = Boolean.valueOf(fromTL).booleanValue();
                    ((OneOneConnector_Rep.MyBoxRepresentation)boxRep)
                        .setDrawFromTL(fromTopLeft);
                }
            } catch (ClassCastException cce) {
                // does not matter; this box does not need this information then
            }
        }
            
        Element paramsNode = (Element)valueNode
            .getElementsByTagName("parameters").item(0);

        if (paramsNode != null) {
            NodeList params = paramsNode.getElementsByTagName("parameter");
            Parameter[] parameters = new Parameter[params.getLength()];
            // used for saving the filename to load sub query objectparameter
            String lastString = null;
            for (int i = 0; i < params.getLength(); i++) {
                Element paramNode = (Element)params.item(i);
                classname = paramNode.getAttribute("classname");
                String parName = paramNode.getAttribute("name");
                String parDesc = paramNode.getAttribute("description");
                Element paramValueNode = 
                    (Element)paramNode.getElementsByTagName("value").item(0);
                
                // TODO move that inside the parameters?!
                if (classname.endsWith("OptionParameter")) {
                    // load options
                    Element optionsNode = (Element)paramValueNode
                        .getElementsByTagName("options").item(0);
        
                    NodeList options = optionsNode.getElementsByTagName("option");
                    Object[] opts = new Object[options.getLength()];
                    for (int opt = 0; opt < options.getLength(); opt++) {
                        String cName = ((Element)options.item(opt)).getAttribute("type");
                        String optionString = ((TextNode)options.item(opt)
                            .getFirstChild()).getData();
                        try {
                            opts[opt] = InstanceLoader.createInstance
                                (cName, optionString);
                        } catch (InstanceCreationException ice) {
                            System.out.println("Warning: could not instantiate " +
                                cName + " with String. Used String instead.");
                            opts[opt] = optionString;
                        }
                    }
                    
                    Element propertiesNode = (Element)paramValueNode
                        .getElementsByTagName("properties").item(0);
                    boolean editbl = propertiesNode
                        .getAttribute("editable").equals("true");
                    int selOpt = new Integer(propertiesNode
                        .getAttribute("selectedOption")).intValue();
                    
                    if (selOpt < 0) {
                        // must be editable; user entered and selected value
                        // TODO could check if editable?
                        String userValue = 
                            propertiesNode.getAttribute("userValue");
                        
                        Object[] userOpts = new Object[options.getLength() + 1];
                        System.arraycopy(opts, 0, userOpts, 0, opts.length);
                        userOpts[userOpts.length-1] = userValue;
                        
                        parameters[i] = new OptionParameter
                            (userOpts, userOpts.length - 1, editbl, 
                             parName, parDesc);
                    } else {
                        parameters[i] = new OptionParameter
                            (opts, selOpt, editbl, parName, parDesc);
                    }
                    
                
                } else if (classname.endsWith("StringParameter")) {
                    String strValue = ((TextNode)paramValueNode
                        .getFirstChild()).getData();
                    if (XMLHelper.useIndentation) strValue = strValue.trim();
                    parameters[i] = new StringParameter
                        (strValue, parName, parDesc);
                    lastString = strValue;
                
                } else if (classname.endsWith("IntegerParameter")) {
                    String strValue = ((TextNode)paramValueNode
                        .getFirstChild()).getData();
                    if (XMLHelper.useIndentation) strValue = strValue.trim();
                    int intValue = Integer.parseInt(strValue);
                    parameters[i] = new IntegerParameter
                        (intValue, parName, parDesc);
                
                } else if (classname.endsWith("DoubleParameter")) {
                    String strValue = ((TextNode)paramValueNode
                        .getFirstChild()).getData();
                    if (XMLHelper.useIndentation) strValue = strValue.trim();
                    double doubleValue = Double.parseDouble(strValue);
                    parameters[i] = new DoubleParameter
                        (doubleValue, parName, parDesc);
                
                } else if (classname.endsWith("BooleanParameter")) {
                    String strValue = ((TextNode)paramValueNode
                        .getFirstChild()).getData();
                    if (XMLHelper.useIndentation) strValue = strValue.trim();
                    Boolean boolValue = new Boolean(strValue);
                    parameters[i] = new BooleanParameter
                        (boolValue, parName, parDesc);
                
                } else if (classname.endsWith("ObjectParameter")) {
                    if (box instanceof SubQuery_Box) {
                        String fileName = lastString;
                        try {
                            boolean[] assignedRowsCopy = QAssign.getAssignedRowsCopy();
                            boolean[] assignedBEPRowsCopy = QAssign.getAssignedBEPRowsCopy();
////////                            QAssign.resetAssignedRows();

                            Graph graph = FileUtil.getQueryFromFile
                                (new File(fileName), false);
                            QAssign.setAssignedRows(assignedRowsCopy);
                            QAssign.setAssignedBEPRows(assignedBEPRowsCopy);
                            parameters[i] = new ObjectParameter
                                (graph, parName, parDesc);
                        } catch (IOException ioe) {
                            // cannot help ...
                        }
                    }
                
                } else if (classname.endsWith("VECParameter")) {
                    // TODO treat all types of displayables
                	String strValue = ((TextNode)paramValueNode
                        .getFirstChild()).getData();
                    if (XMLHelper.useIndentation) strValue = strValue.trim();

                    int spacePos = strValue.indexOf(" ");
                    String vecClassName = strValue.substring(0, spacePos);
                    int spacePos2 = strValue.lastIndexOf(" ");
                    String dispClassName = strValue.substring(spacePos + 1, spacePos2);
                    String valueStr = strValue.substring(spacePos2 + 1);
                    ValueEditComponent vec = null;
                    String name = "autogen";
                    String desc = "automatically generated";
                    if (vecClassName.endsWith("IntegerEditComponent")) {
                    	Displayable disp = new IntegerParameter
							(new Integer(valueStr), name, desc);
                    	vec = new IntegerEditComponent(disp);
                    } else if (vecClassName.endsWith("DoubleEditComponent")) {
                    	Displayable disp = new DoubleParameter
							(new Double(valueStr).doubleValue(), name, desc);
                    	vec = new DoubleEditComponent(disp);
                    } else if (vecClassName.endsWith("SpinnerEditComponent")) {
                    	Displayable disp = null;
                    	if (dispClassName.endsWith("IntegerParameter") || dispClassName.endsWith("IntegerAttribute")) {
                        	disp = new IntegerParameter
								(new Integer(valueStr).intValue(), name, desc);
                        	vec = new IntegerEditComponent(disp);
                    	} else if (dispClassName.endsWith("DoubleParameter") || dispClassName.endsWith("DoubleAttribute")) {
                        	disp = new DoubleParameter
								(new Double(valueStr).doubleValue(), name, desc);
                        	vec = new DoubleEditComponent(disp);
                    	}
                    } else { //if (vecClassName.endsWith("StringEditComponent")) {
                    	Displayable disp = new StringParameter
							(valueStr, name, desc);
                    	vec = new StringEditComponent(disp);
                    }
                    
                    parameters[i] = new VECParameter(vec, parName, parDesc);
                
                } else {
                    // TODO implement for all parameter types or do sth different!
//                    parameters[i] = (Parameter)InstanceLoader
//                        .createInstance(classname);
                }
            }
            box.setParameters(parameters, false);
        }

        newAttr.setIBox(box);
        node.addAttribute(newAttr, "");
        
        return false;
    }
}
