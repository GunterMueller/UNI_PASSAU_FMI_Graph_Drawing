/*
 * Created on 07.09.2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.graffiti.plugins.algorithms.brandeskoepf;

import java.awt.event.ActionEvent;

import org.graffiti.help.HelpContext;
import org.graffiti.plugin.actions.GraffitiAction;

/**
 * @author Florian
 * 
 *         TODO To change the template for this generated type comment go to
 *         Window - Preferences - Java - Code Generation - Code and Comments
 */
public class BKButtonAction extends GraffitiAction {

    /**
     * 
     */
    private static final long serialVersionUID = 4441544080234648987L;

    public BKButtonAction() {
        super(null, null);
    }

    @Override
    public boolean isEnabled() {
        return super.enabled;
    }

    @Override
    public HelpContext getHelpContext() {
        return null;
    }

    public void actionPerformed(ActionEvent e) {
        System.out.println("klicked on test toolbar button");
    }
}
