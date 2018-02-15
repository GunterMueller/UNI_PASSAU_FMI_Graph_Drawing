// =============================================================================
//
//   GraffitiInternalFrame.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: GraffitiInternalFrame.java 5768 2010-05-07 18:42:39Z gleissner $

package org.graffiti.editor;

import javax.swing.event.InternalFrameEvent;

import org.graffiti.plugin.view.View;
import org.graffiti.session.EditorSession;
import org.graffiti.util.MaximizeFrame;

/**
 * A specialized internal frame for the graffiti editor. A
 * <code>GraffitiInternalFrame</code> is always resizable, closeable,
 * maximizable and iconifyable.
 * 
 * @see javax.swing.JInternalFrame
 * @see MainFrame
 */
public class GraffitiInternalFrame extends MaximizeFrame {

    /**
     * 
     */
    private static final long serialVersionUID = 5822702649848810763L;

    /** The session this frame is in. */
    private EditorSession session;

    /** The view this frame contains. */
    private View view;

    /**
     * Constructs a new <code>GraffitiInternalFrame</code>.
     */
    public GraffitiInternalFrame() {
        super();
        this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        this.setMaximizable(true);
        this.setClosable(true);
        this.setResizable(true);
        this.setIconifiable(true);

        GraffitiSingleton.getInstance().addFrame(this);
    }

    /**
     * Constructor that sets the session, as well as the title.
     * 
     * @param session
     *            the session this frame is in.
     * @param view
     *            DOCUMENT ME!
     * @param title
     *            the title of this internal frame.
     */
    public GraffitiInternalFrame(EditorSession session, View view, String title) {
        this();
        this.session = session;
        this.view = view;
        setTitle(title);
    }

    /**
     * Returns the session this frame is opened in.
     * 
     * @return the session this frame is opened in.
     */
    public EditorSession getSession() {
        return session;
    }

    /**
     * Sets the title of this frame and its associated button and menu button.
     * 
     * @param title
     *            the new title of the frame.
     */
    @Override
    public void setTitle(String title) {
        String frameTitle = title + " - (" + session.getId() + ","
                + view.getId() + ")";

        super.setTitle(frameTitle);
    }

    /**
     * Returns the view of this frame.
     * 
     * @return the view of this frame.
     */
    public View getView() {
        return view;
    }

    /**
     * Own implementation of dispose to ensure the DesktopManager works as
     * expected. Since we omit the setSelected(false) the DesktopManager
     * automatically activates the next frame and if the last frame was
     * maximized, the newly activated frame will also be maximized. See bug
     * 6288609 in the official java bug database.
     * 
     * TODO: Remove this method, if the above mentioned bug is fixed in java.
     */
    @Override
    public void dispose() {
        if (isVisible()) {
            setVisible(false);
        }
        if (!isClosed) {
            firePropertyChange(IS_CLOSED_PROPERTY, Boolean.FALSE, Boolean.TRUE);
            isClosed = true;
        }
        fireInternalFrameEvent(InternalFrameEvent.INTERNAL_FRAME_CLOSED);
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
