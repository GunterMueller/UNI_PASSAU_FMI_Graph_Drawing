/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 1999, 2000 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Xerces" and "Apache Software Foundation" must
 *    not be used to endorse or promote products derived from this
 *    software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation and was
 * originally based on software copyright (c) 1999, International
 * Business Machines, Inc., http://www.apache.org.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

package org.visnacom.dom;


import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import org.visnacom.util.Arguments;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.apache.xerces.readers.MIME2Java;
import java.util.*;
import java.awt.Rectangle;
import java.awt.Point;
import java.awt.Dimension;

//import CPG.View.*;

/**
 * A sample DOM writer. This sample program illustrates how to traverse a DOM
 * tree in order to print a document that is parsed.
 *  
 */
public class DOMWriter {

	//
	// Constants
	//

	/** Default parser name. */
	private final String DEFAULT_PARSER_NAME = "dom.wrappers.DOMParser";

	private boolean setValidation = false; //defaults

	private boolean setNameSpaces = true;

	private boolean setSchemaSupport = true;

	private boolean setSchemaFullSupport = false;

	private boolean setDeferredDOM = true;

	private static LinkedList edges = new LinkedList();

	private static LinkedList parent = new LinkedList(),
			ids = new LinkedList(), views = new LinkedList();

	private static String currentMode = "", source = "",
			target = "";

	private static int x, y, width, height;

	private static String id = "";

	private static boolean change = false;

	private static boolean directed = false;

	private static LinkedList cPoints = new LinkedList();

	private static Stack stack = new Stack();

	private static HashMap nodes = new HashMap();

	private static HashMap edgeIds = new HashMap(),
			ctrlPoints = new HashMap(), parMap = new HashMap();

	//
	// Data
	//

	/** Default Encoding */
	private static String PRINTWRITER_ENCODING = "UTF8";

	private String MIME2JAVA_ENCODINGS[] = { "Default", "UTF-8", "US-ASCII",
			"ISO-8859-1", "ISO-8859-2", "ISO-8859-3", "ISO-8859-4",
			"ISO-8859-5", "ISO-8859-6", "ISO-8859-7", "ISO-8859-8",
			"ISO-8859-9", "ISO-2022-JP", "SHIFT_JIS", "EUC-JP", "GB2312",
			"BIG5", "EUC-KR", "ISO-2022-KR", "KOI8-R", "EBCDIC-CP-US",
			"EBCDIC-CP-CA", "EBCDIC-CP-NL", "EBCDIC-CP-DK", "EBCDIC-CP-NO",
			"EBCDIC-CP-FI", "EBCDIC-CP-SE", "EBCDIC-CP-IT", "EBCDIC-CP-ES",
			"EBCDIC-CP-GB", "EBCDIC-CP-FR", "EBCDIC-CP-AR1", "EBCDIC-CP-HE",
			"EBCDIC-CP-CH", "EBCDIC-CP-ROECE", "EBCDIC-CP-YU", "EBCDIC-CP-IS",
			"EBCDIC-CP-AR2", "UTF-16" };

	/** Print writer. */
	protected PrintWriter out;

	/** Canonical output. */
	protected static boolean canonical = false;

	public DOMWriter(String encoding, boolean canonical)
			throws UnsupportedEncodingException {
		out = new PrintWriter(new OutputStreamWriter(System.out, encoding));
		
		// resets static members
		edges.clear();
		nodes.clear();
		ids.clear();
		change = false;
		stack.clear();
		parent.clear();
		views.clear();
		cPoints.clear();
		currentMode = "";
		source = "";
		target = "";
		directed = false;
		id = "";
		edgeIds.clear();
		ctrlPoints.clear();
	} // <init>(String,boolean)

	//
	// Constructors
	//

	/** Default constructor. */
	public DOMWriter(boolean canonical) throws UnsupportedEncodingException {

		this(PRINTWRITER_ENCODING, canonical);
	}

	public String getWriterEncoding() {
		return (PRINTWRITER_ENCODING);
	}// getWriterEncoding

	public void setWriterEncoding(String encoding) {
		if (encoding.equalsIgnoreCase("DEFAULT"))
			PRINTWRITER_ENCODING = "UTF8";
		else if (encoding.equalsIgnoreCase("UTF-16"))
			PRINTWRITER_ENCODING = "Unicode";
		else
			PRINTWRITER_ENCODING = MIME2Java.convert(encoding);
	}// setWriterEncoding

	public boolean isValidJavaEncoding(String encoding) {
		for (int i = 0; i < MIME2JAVA_ENCODINGS.length; i++)
			if (encoding.equals(MIME2JAVA_ENCODINGS[i]))
				return (true);

		return (false);
	}// isValidJavaEncoding

	/** Prints the resulting document tree. */
	public void print(String parserWrapperName, String uri, boolean canonical) {
		try {
			DOMParserWrapper parser = (DOMParserWrapper) Class.forName(
					parserWrapperName).newInstance();

			parser.setFeature(
					"http://apache.org/xml/features/dom/defer-node-expansion",
					setDeferredDOM);
			parser.setFeature("http://xml.org/sax/features/validation",
					setValidation);
			parser.setFeature("http://xml.org/sax/features/namespaces",
					setNameSpaces);
			parser.setFeature(
					"http://apache.org/xml/features/validation/schema",
					setSchemaSupport);
			parser
					.setFeature(
							"http://apache.org/xml/features/validation/schema-full-checking",
							setSchemaFullSupport);

			Document document = parser.parse(uri);
			DOMWriter writer = new DOMWriter(canonical);
			writer.print(document);
		} catch (Exception e) {
			//e.printStackTrace(System.err);
		}

	} // print(String,String,boolean)

	/** Prints the specified node, recursively. */
	public void print(Node node) {

		// is there anything to do?
		if (node == null) {
			return;
		}

		int type = node.getNodeType();
		switch (type) {
		// print document
		case Node.DOCUMENT_NODE: {

		}

		// print element with attributes
		case Node.ELEMENT_NODE: {

			processNode(node);

			NodeList children = node.getChildNodes();
			if (children != null) {
				int len = children.getLength();
				for (int i = 0; i < len; i++) {
					print(children.item(i));
				}
			}
			break;
		}

		// handle entity reference nodes
		case Node.ENTITY_REFERENCE_NODE: {
			if (canonical) {
				NodeList children = node.getChildNodes();
				if (children != null) {
					int len = children.getLength();
					for (int i = 0; i < len; i++) {
						print(children.item(i));
					}
				}
			} else {
				out.print('&');
				out.print(node.getNodeName());
				out.print(';');
			}
			break;
		}

		// print cdata sections...not used
		case Node.CDATA_SECTION_NODE: {
			if (canonical) {
				out.print(normalize(node.getNodeValue()));
			} else {
				out.print("<![CDATA[");
				out.print(node.getNodeValue());
				out.print("]]>");
			}
			break;
		}

		//		 print text
		case Node.TEXT_NODE: {
			processText(node);
//			out.print(normalize(node.getNodeValue()));
			break;
		}

		// print processing instruction
		case Node.PROCESSING_INSTRUCTION_NODE: {
			out.print("<?");
			out.print(node.getNodeName());
			String data = node.getNodeValue();
			if (data != null && data.length() > 0) {
				out.print(' ');
				out.print(data);
			}
			out.println("?>");
			break;
		}
		}

		if (type == Node.ELEMENT_NODE || type == Node.TEXT_NODE) {
			if (node.getNodeName().equals("node")
					&& node.getParentNode().getNodeName().equals("graph")) {
				change = false;
			} else if (node.getNodeName().equals("edge")
					&& node.getParentNode().getNodeName().equals("graph")) {
				// reads edge info
				LinkedList l = new LinkedList();
				l.add(new String(source));
				l.add(new String(target));
				l.add(new String(id));
				edges.add(l);
			} else if (node.getNodeName().equals("view")) {
				// stores info concerning view
				List l = (List) views.getLast();
				l.add(new HashMap(nodes));
				l.add(new HashMap(ctrlPoints));
			} else if (node.getNodeName().equals("graph")) {
				// next nested graph
				stack.pop();
			} else if (node.getNodeName().equals("node") && change
					&& node.getParentNode().getNodeName().equals("view")) {
				// stores node info
				Rectangle rec = new Rectangle(new Point(x, y), new Dimension(
						width, height));
				nodes.put(id, rec);
			} else if (node.getNodeName().equals("edge")
					&& node.getParentNode().getNodeName().equals("view")) {
				// stores control point info
				cPoints.addFirst(new String(target));
				cPoints.addFirst(new String(source));
				ctrlPoints.put(id, new LinkedList(cPoints));
				cPoints.clear();
			}
		}
	} // print(Node)

	/**
	 * Preocesses XML text.
	 * @param node XML-Node to be processed.
	 */
	private static void processText(Node node) {
		// operations due to attributes
		if (currentMode.equals("x")) {
			x = Integer.parseInt(normalize(node.getNodeValue()));
			change = true;
		} else if (currentMode.equals("y")) {
			y = Integer.parseInt(normalize(node.getNodeValue()));
			change = true;
		} else if (currentMode.equals("width")) {
			width = Integer.parseInt(normalize(node.getNodeValue()));
			change = true;
		} else if (currentMode.equals("height")) {
			height = Integer.parseInt(normalize(node.getNodeValue()));
			change = true;
		} else if (currentMode.equals("poly")) {
			cPoints.add(normalize(node.getNodeValue()));
		} 
		currentMode = "";
	}

	/**
	 * Processes an XML-Node.
	 * @param node XML-Node to be processed.
	 */
	private static void processNode(Node node) {
		if (node.getNodeName().equals("node")) {
			Attr attrs[] = sortAttributes(node.getAttributes());

			for (int i = 0; i < attrs.length; i++) {
				Attr attr = attrs[i];
				String name = attr.getName();
				String value = attr.getValue();
				
				// sets id
				if (name.equals("id")) {
					id = value;
				}
			}
			
			// sets parent relation
			if (node.getParentNode().getNodeName().equals("graph")) {
				ids.add(id);
				if (stack.isEmpty()) {
					parent.add(null);
					parMap.put(id, null);
				} else {
					parent.add(stack.peek());
					parMap.put(id, stack.peek());
				}
			}
		} else if (node.getNodeName().equals("graph")) {
			Attr attrs[] = sortAttributes(node.getAttributes());
			for (int i = 0; i < attrs.length; i++) {
				Attr attr = attrs[i];
				String name = attr.getName();
				String value = attr.getValue();
				boolean val = false;
				
				// sets edge attribute
				if (value.equals("directed")) {
					val = true;
				}
				if (name.equals("edgedefault")) {
					directed = directed || val;
				}
			}
			stack.push(id);
			change = false;
		} else if (node.getNodeName().equals("data")) {
			Attr attrs[] = sortAttributes(node.getAttributes());

			for (int i = 0; i < attrs.length; i++) {

				Attr attr = attrs[i];
				String value = attr.getValue();

				// sets current mode
				if (value.equals("x")) {
					currentMode = "x";
				} else if (value.equals("y")) {
					currentMode = "y";
				} else if (value.equals("width")) {
					currentMode = "width";
				} else if (value.equals("height")) {
					currentMode = "height";
				} else if (value.equals("poly")) {
					currentMode = "poly";
				} 
			}
		} else if (node.getNodeName().equals("edge")) {
			Attr attrs[] = sortAttributes(node.getAttributes());

			for (int i = 0; i < attrs.length; i++) {

				Attr attr = attrs[i];
				String name = attr.getName();
				String value = attr.getValue();

				// sets edge attributes
				if (name.equals("source")) {
					source = value;
				} else if (name.equals("target")) {
					target = value;
				} else if (name.equals("id")) {
					id = value;
				}

			}
		} else if (node.getNodeName().equals("view")) {
			// makes view
			cPoints = new LinkedList();
			nodes = new HashMap();
			ctrlPoints = new HashMap();
			LinkedList newView = new LinkedList();
			newView.add(new LinkedList());
			views.add(newView);
		}
	}

	/**
	 * Gets the a hashmap relating nodes and coordinates.
	 * @return HashMap with nodes.
	 */
	public HashMap getNodes() {
		return nodes;
	}

	/**
	 * Gets a list of edges.
	 * @return List of loaded edges.
	 */
	public List getEdges() {
		return edges;
	}

	/**
	 * Gets a list modelling parent relations.
	 * @return List modelling parent relations.
	 */
	public List getParent() {
		return parent;
	}

	/**
	 * Gets a hashmap for parent relation.
	 * @return Hashmap for parent relation.
	 */
	public HashMap getParMap() {
		return parMap;
	}

	/**
	 * Gets a list with node ids.
	 * @return List with node ids.
	 */
	public List getIds() {
		return ids;
	}

	/**
	 * Gets a list with the views.
	 * @return List with the views.
	 */
	public List getViews() {
		return views;
	}

	/**
	 * Checks if edges in grapg are directed.
	 * @return True iff edges are directed.
	 */
	public boolean getEdgeDefault() {
		return directed;
	}


	/** Returns a sorted list of attributes. */
	protected static Attr[] sortAttributes(NamedNodeMap attrs) {

		int len = (attrs != null) ? attrs.getLength() : 0;
		Attr array[] = new Attr[len];
		for (int i = 0; i < len; i++) {
			array[i] = (Attr) attrs.item(i);
		}
		for (int i = 0; i < len - 1; i++) {
			String name = array[i].getNodeName();
			int index = i;
			for (int j = i + 1; j < len; j++) {
				String curName = array[j].getNodeName();
				if (curName.compareTo(name) < 0) {
					name = curName;
					index = j;
				}
			}
			if (index != i) {
				Attr temp = array[i];
				array[i] = array[index];
				array[index] = temp;
			}
		}

		return (array);

	} // sortAttributes(NamedNodeMap):Attr[]

	public void print(String filename, String def_parser) {
		String argv[] = new String[1];
		argv[0] = filename;
		Arguments argopt = new Arguments();
		argopt
				.setUsage(new String[] {
						"usage: java dom.DOMWriter (options) uri ...",
						"",
						"options:",
						"  -n | -N  Turn on/off namespace [default=on]",
						"  -v | -V  Turn on/off validation [default=off]",
						"  -s | -S  Turn on/off Schema support [default=on]",
						"  -f | -F  Turn on/off Schema full consraint checking  [default=off]",
						"  -d | -D  Turn on/off deferred DOM [default=on]",
						"  -c       Canonical XML output.",
						"  -h       This help screen.",
						"  -e       Output Java Encoding.",
						"           Default encoding: UTF-8" });

		// is there anything to do?
		if (argv.length == 0) {
			argopt.printUsage();
			System.exit(1);
		}

		// vars
		String parserName = def_parser;
		boolean canonical = false;
		String encoding = "UTF8"; // default encoding

		argopt.parseArgumentTokens(argv, new char[] { 'p', 'e' });

		int c;
		String arg = null;
		while ((arg = argopt.getlistFiles()) != null) {

			outer: while ((c = argopt.getArguments()) != -1) {
				switch (c) {
				case 'c':
					canonical = true;
					break;
				case 'e':
					encoding = argopt.getStringParameter();
					if (encoding != null && isValidJavaEncoding(encoding))
						setWriterEncoding(encoding);
					else {
						printValidJavaEncoding();
						System.exit(1);
					}
					break;
				case 'v':
					setValidation = true;
					break;
				case 'V':
					setValidation = false;
					break;
				case 'N':
					setNameSpaces = false;
					break;
				case 'n':
					setNameSpaces = true;
					break;
				case 'p':
					parserName = argopt.getStringParameter();
					break;
				case 'd':
					setDeferredDOM = true;
					break;
				case 'D':
					setDeferredDOM = false;
					break;
				case 's':
					setSchemaSupport = true;
					break;
				case 'S':
					setSchemaSupport = false;
					break;
				case 'f':
					setSchemaFullSupport = true;
					break;
				case 'F':
					setSchemaFullSupport = false;
					break;
				case '?':
				case 'h':
				case '-':
					argopt.printUsage();
					System.exit(1);
					break;
				case -1:
					break outer;
				default:
					break;
				}
			}
			print(parserName, arg, canonical);

		}
	}

	//
	// Main
	//

	/** Normalizes the given string. */
	protected static String normalize(String s) {
		StringBuffer str = new StringBuffer();

		int len = (s != null) ? s.length() : 0;
		for (int i = 0; i < len; i++) {
			char ch = s.charAt(i);
			switch (ch) {
			case '<': {
				str.append("&lt;");
				break;
			}
			case '>': {
				str.append("&gt;");
				break;
			}
			case '&': {
				str.append("&amp;");
				break;
			}
			case '"': {
				str.append("&quot;");
				break;
			}
			case '\'': {
				str.append("&apos;");
				break;
			}
			case '\r':
			case '\n': {
				if (canonical) {
					str.append("&#");
					str.append(Integer.toString(ch));
					str.append(';');
					break;
				}
				// else, default append char
			}
			default: {
				str.append(ch);
			}
			}
		}

		return (str.toString());

	} // normalize(String):String

	private void printValidJavaEncoding() {
		System.err.println("    ENCODINGS:");
		System.err.print("   ");
		for (int i = 0; i < MIME2JAVA_ENCODINGS.length; i++) {
			System.err.print(MIME2JAVA_ENCODINGS[i] + " ");
			if ((i % 7) == 0) {
				System.err.println();
				System.err.print("   ");
			}
		}

	} // printJavaEncoding()

}