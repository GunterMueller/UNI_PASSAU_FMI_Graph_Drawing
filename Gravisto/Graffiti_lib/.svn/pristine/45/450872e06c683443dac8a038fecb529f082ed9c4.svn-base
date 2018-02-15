/*
 * Created on 05.07.2005
 *
 */
package org.visnacom.controller;

import javax.swing.JDialog;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import java.awt.*;

import java.io.*;
import javax.swing.*;

import javax.swing.text.html.*;
import java.net.URL;

/**
 * @author F. Pfeiffer
 * 
 * This class implements the online help dialog.
 */
public class OnlineHelp extends JDialog implements HyperlinkListener {

	/**
	 * Constructor. Opens help dialog.
	 *  
	 */
	public OnlineHelp() {
		super();

		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

		ClassLoader cl = this.getClass().getClassLoader();

		// gets url of contents page
		URL helpURL = cl.getResource("./onlinehelp/index.html");

		//makes editor pane
		JEditorPane editorPane = new JEditorPane();
		editorPane.setEditable(false);
		editorPane.addHyperlinkListener(this);
		try {
			editorPane.setPage(helpURL);
		} catch (IOException e) {
			System.err.println("No such file");
		}

		// makes dialog scrollable
		JScrollPane editorScrollPane = new JScrollPane(editorPane);
		editorScrollPane.setPreferredSize(new Dimension(400, 500));
		editorScrollPane.setMinimumSize(new Dimension(100, 100));

		panel.add(editorScrollPane);
		getContentPane().add(panel);
		setSize(700, 600);
		this.setLocation(200, 100);

		// displays dialog
		this.setVisible(true);
	}

	/**
	 * 
	 * @see javax.swing.event.HyperlinkListener#hyperlinkUpdate(javax.swing.event.HyperlinkEvent)
	 */
	public void hyperlinkUpdate(HyperlinkEvent arg0) {
		if (arg0.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
			JEditorPane pane = (JEditorPane) arg0.getSource();
			if (arg0 instanceof HTMLFrameHyperlinkEvent) {
				HTMLFrameHyperlinkEvent evt = (HTMLFrameHyperlinkEvent) arg0;
				HTMLDocument doc = (HTMLDocument) pane.getDocument();
				doc.processHTMLFrameHyperlinkEvent(evt);
			} else {
				try {
					pane.setPage(arg0.getURL());
				} catch (Throwable t) {
					t.printStackTrace();
				}
			}
		}
	}

}