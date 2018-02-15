/*
 * Created on 10.02.2005
 *
 */
package org.visnacom;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import org.visnacom.controller.GUIFrame;
import org.visnacom.controller.ViewPanel;


/**
 * @author F. Pfeiffer
 *
 * Class containing the main method.
 */
public class Visnacom {

	/**
	 * Main method. Makes GUI frame.
	 * @param args Command line arguments.
	 */
	public static void main(String[] args) {
		GUIFrame frame = new GUIFrame();
		WindowListener l = new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		};
		frame.addWindowListener(l);
    }

    /**
     *
     * @return the viewpanel for static set up of a testcase
     */
    public static ViewPanel main_test() {
        GUIFrame frame = new GUIFrame();
        WindowListener l = new WindowAdapter() {
                public void windowClosing(WindowEvent e) {
                    System.exit(0);
                }
            };
        frame.addWindowListener(l);
        ViewPanel panel = frame.main_test();
        return panel;
    }
}
