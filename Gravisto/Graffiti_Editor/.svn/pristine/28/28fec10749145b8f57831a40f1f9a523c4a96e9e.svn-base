package org.visnacom.controller;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;

import org.visnacom.model.BaseCompoundGraph;
import org.visnacom.sugiyama.*;
import org.visnacom.view.*;

import java.util.*;
import java.io.*;

/**
 * 
 * @author F. Pfeiffer
 * 
 * This is the class implementing the main frame for the application, as well as
 * the listeners. The GUI includes menubar, toolbar, preferences dialog and so
 * on.
 *  
 */

public class GUIFrame extends JFrame implements ActionListener,
		InternalFrameListener, ChangeListener {

	// Flags which represent the different graph editing modes
	private final static int TOG_CREATE = 0, TOG_MOVE = 1, TOG_LABEL = 2,
			TOG_INCLUDE = 3, TOG_COLL_EXP = 4;

	// variable for current look-and-feel setting
	private String LAF = "metal";

	// current scrolling speed
	private int scrollSpeed;

	// flag for the currently active mode
	private int togFlag = TOG_CREATE;

	// menu items
	private JMenuItem menuNew, menuNewView, menuOpen, menuSave, menuSaveAs,
			menuClose, menuExit, menuCut, menuPaste, menuCopy, menuDelete,
			menuSelectAll, menuRedraw, menuHelp, menuInfo, menuSession,
			menuPref;

	// toolbar buttons

	private JButton tbOpen, tbSave, tbNew, tbNewView, tbCut, tbCopy, tbSaveAs,
			tbPaste, nodeFillColor, nodeFrameColor, lineColor,
			clusterFillColor, clusterFrameColor, restoreDefault, withinColor,
			selectionColor, noCollColor, noExpandColor, prefOk, prefCancel;

	// used to choose the scrolling speed
	private JSpinner speedSpinner;

	// toggle buttons for the editing modes
	private JToggleButton togCreate, togMove, togLabel, togInclude, togCollExp;

	// button group for said toggle buttons
	private ButtonGroup group = new ButtonGroup();

	// buuton group for radio buttons in pref dialog (specifying algorithm)
	private ButtonGroup algGroup = new ButtonGroup();

	// button group for radio buttons in pref dialog (specifying animation)
	private ButtonGroup animGroup = new ButtonGroup();

	// radio buttons for pref dialog
	private JRadioButton metalLaF, motifLaF, winLaF, sysLaF, defAlg, sugiAlg,
			noAnimation, linearAnimation, directed, undirected, smooth,
			polyline;

	// counts the number of opened internal frames
	private int frameCounter = 0;

	// a list of all currently available internal frames
	private LinkedList iFrames;

	// hashmaps for the cut/copy/paste funtionality
	private HashMap copiedClusters, geoCoords, edges, copiedClusDepth;

	private LinkedList contracted, copiedNodes;

	// specifying the drawing colors
	private Color nFill = new Color(10, 150, 200, 200), nFrame = Color.black,
			lCol = Color.black, clusFill = Color.lightGray,
			clusFrame = Color.black, select = new Color(255, 204, 0),
			selected = Color.red, noExpansion = Color.RED,
			noContraction = Color.RED;

	// example color panels for pref dialog
	private JPanel nFillCol, nFrameCol, lineCol, clusFillCol, clusFrameCol,
			withinSel, selection, noColl, noExpand;

	// for reading image files
	private ClassLoader cl = this.getClass().getClassLoader();

	// standard desktop pane
	private JDesktopPane desk;

	// dialog for choosing preferences
	private JDialog prefDialog;

	// temporarily saves changes of preferences
	private Preferences tmpPrefs;

	/**
	 * 
	 * Standard constructor. Creates GUI.
	 * 
	 *  
	 */
	public GUIFrame() {
		super("VisnaCom");
		noAnimation = new JRadioButton();
		undirected = new JRadioButton();
		defAlg = new JRadioButton();
		smooth = new JRadioButton();
		iFrames = new LinkedList();
		copiedNodes = new LinkedList();
		copiedClusters = new HashMap();
		copiedClusDepth = new HashMap();
		Preferences prefs = new Preferences();
		tmpPrefs = new Preferences();

		// loads saved preferences
		try {
			FileInputStream fs = new FileInputStream("prefs.ser");
			ObjectInputStream is = new ObjectInputStream(fs);
			prefs = (Preferences) is.readObject();
			is.close();
		} catch (FileNotFoundException exc) {
			prefs = restoreDef();
		} catch (ClassNotFoundException exc) {
			System.err.println("Class not found: " + exc.toString());
		} catch (IOException exc) {
			System.err.println("Error while creating input stream: "
					+ exc.toString());
		}
		LAF = prefs.LAF;
		tmpPrefs = new Preferences(prefs);
		scrollSpeed = prefs.scrollSpeed;

		// sets look-and-feel
		if (LAF.equals("metal")) {
			try {
				UIManager.setLookAndFeel(UIManager
						.getCrossPlatformLookAndFeelClassName());
			} catch (Exception e) {
				System.err.println("Cannot set look and feel: " + e);
			}
		} else if (LAF.equals("motif")) {
			try {
				UIManager
						.setLookAndFeel("com.sun.java.swing.plaf.motif.MotifLookAndFeel");
				SwingUtilities.updateComponentTreeUI(this);
			} catch (Exception e) {
				System.err.println("Cannot set look and feel: " + e);

			}
		} else if (LAF.equals("win")) {
			try {
				UIManager
						.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
				SwingUtilities.updateComponentTreeUI(this);
			} catch (Exception e) {
				System.err.println("Cannot set look and feel: " + e);
			}
		} else if (LAF.equals("sys")) {
			try {
				UIManager.setLookAndFeel(UIManager
						.getSystemLookAndFeelClassName());
				SwingUtilities.updateComponentTreeUI(this);
			} catch (Exception e) {
				System.err.println("Cannot set look and feel: " + e);
			}
		}

		JPanel contentPane = new JPanel();

		// create toggle buttons
		togCreate = new JToggleButton(
				new ImageIcon("./images/megaCreate24.gif"));
		togMove = new JToggleButton(new ImageIcon("./images/megaMove24.gif"));
		togLabel = new JToggleButton(new ImageIcon("./images/labelTool24.gif"));
		togInclude = new JToggleButton(new ImageIcon("./images/include.GIF"));
		togCollExp = new JToggleButton(new ImageIcon("./images/contract.GIF"));

		// add toggle buttons to button group
		// (only one editing mode at a time)
		group.add(togCreate);
		group.add(togMove);
		group.add(togLabel);
		group.add(togInclude);
		group.add(togCollExp);

		// adds action listener
		togCreate.setSelected(true);
		togCreate.addActionListener(this);
		togMove.addActionListener(this);
		togLabel.addActionListener(this);
		togInclude.addActionListener(this);
		togCollExp.addActionListener(this);

		// creates menu bar
		JMenuBar menubar = new JMenuBar();
		menubar.add(createFileMenu());
		menubar.add(createEditMenu());
		menubar.add(createOptionMenu());
		menubar.add(createHelpMenu());
		setJMenuBar(menubar);

		// creates space for toggle buttons
		JPanel leftBar = new JPanel();
		leftBar.setMaximumSize(new Dimension(500, 1200));
		leftBar.setLayout(new BoxLayout(leftBar, BoxLayout.Y_AXIS));
		leftBar.setBorder(new SoftBevelBorder(BevelBorder.RAISED));

		// creates first internal frame, which is displayed when program
		// is started
		desk = new JDesktopPane();
		DefaultDesktopManager deskManager = new DefaultDesktopManager();
		desk.setDesktopManager(deskManager);
		ViewPanel firstIPanel = new ViewPanel(prefs, this);
		Dimension d = new Dimension(698, 434);
		firstIPanel.setPreferredSize(d);
		ViewFrame firstIFrame = new ViewFrame("Graph " + (frameCounter + 1),
				firstIPanel, TOG_CREATE);
		iFrames.add(firstIFrame);
		firstIFrame.setSize(300, 300);
		firstIFrame.addInternalFrameListener(this);
		firstIPanel.setBackground(Color.white);

		// adds scrollbar
		firstIFrame.getContentPane().add(firstIPanel);
		JScrollPane scroll = new JScrollPane(firstIPanel,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		firstIFrame.getContentPane().add(scroll, "Center");
		firstIFrame.setToggle(TOG_CREATE);
		this.desk.add(firstIFrame);

		// sets internal frame to maximum size
		firstIPanel.setViewport(scroll.getViewport());

		// adds JPanel as drawing space
		JPanel drawPanel = new JPanel();
		drawPanel.setMaximumSize(new Dimension(500, 1200));
		drawPanel.setLayout(new BoxLayout(drawPanel, BoxLayout.Y_AXIS));
		drawPanel.add(desk);

		// JPanel which will contain toolbar

		JPanel toolBar = new JPanel();
		toolBar.setMaximumSize(new Dimension(500, 1200));
		toolBar.setLayout(new BoxLayout(toolBar, BoxLayout.Y_AXIS));
		toolBar.setBorder(new SoftBevelBorder(BevelBorder.RAISED));

		// adds toolbar buttons

		toolBar.add(createToolBar());
		leftBar.add(togCreate);
		leftBar.add(togMove);
		leftBar.add(togLabel);
		leftBar.add(togInclude);
		leftBar.add(togCollExp);

		// specifies content pane
		contentPane.setLayout(new BorderLayout());
		contentPane.setBorder(BorderFactory.createLineBorder(Color.gray, 1));
		contentPane.add(toolBar, BorderLayout.NORTH);
		contentPane.add(leftBar, BorderLayout.WEST);
		contentPane.add(drawPanel, BorderLayout.CENTER);
		setContentPane(contentPane);
		this.setLocation(10, 10);
		this.setSize(800, 600);
		this.setVisible(true);
		firstIFrame.setVisible(true);
		try {
			firstIFrame.show();
			firstIFrame.setMaximum(true);
			firstIFrame.setSelected(true);
		} catch (Exception e) {
			System.err.println(e);
		}
	}

	/**
	 * 
	 * Triggered by closing internal frame.
	 * 
	 * @param e
	 *            The frame event.
	 *  
	 */
	public void internalFrameClosing(InternalFrameEvent e) {

	}

	/**
	 * 
	 * Triggered by closed internal frame.
	 * 
	 * @param e
	 *            The frame event.
	 *  
	 */
	public void internalFrameClosed(InternalFrameEvent e) {
		((ViewFrame) e.getSource()).closeFrame();
		iFrames.remove(e.getSource());
		if (iFrames.isEmpty()) {
			this.disableCutCopy();
			menuPaste.setEnabled(false);
			tbPaste.setEnabled(false);
		}
		setSaveEnabled();
	}

	/**
	 * 
	 * Triggered by openeded internal frame.
	 * 
	 * @param e
	 *            The frame event.
	 */
	public void internalFrameOpened(InternalFrameEvent e) {

	}

	/**
	 * 
	 * Triggered by iconified internal frame.
	 * 
	 * @param e
	 *            The frame event.
	 */
	public void internalFrameIconified(InternalFrameEvent e) {

	}

	/**
	 * 
	 * Triggered by deiconified internal frame.
	 * 
	 * @param e
	 *            The frame event.
	 */
	public void internalFrameDeiconified(InternalFrameEvent e) {

	}

	/**
	 * 
	 * Triggered by activated internal frame.
	 * 
	 * @param e
	 *            The frame event.
	 */
	public void internalFrameActivated(InternalFrameEvent e) {
		boolean paste = false;
		if (menuPaste.isEnabled()) {
			paste = true;
		}
		this.disableCutCopy();
		menuPaste.setEnabled(false);
		tbPaste.setEnabled(false);
		if (paste) {
			menuPaste.setEnabled(true);
			tbPaste.setEnabled(true);
		}
		this.getActiveFrame().getViewPanel().clearSelections();
		this.getActiveFrame().getViewPanel().setActive(true);
		this.getActiveFrame().getViewPanel().redraw();
	}

	/**
	 * 
	 * Triggered by deactivated internal frame.
	 * 
	 * @param e
	 *            The frame event.
	 */
	public void internalFrameDeactivated(InternalFrameEvent e) {
		((ViewFrame) e.getSource()).getViewPanel().setActive(false);
	}

	/**
	 * 
	 * Triggered by change in scroling spinner.
	 * 
	 * @param e
	 *            The frame event.
	 */
	public void stateChanged(ChangeEvent e) {
		scrollSpeed = ((Integer) (speedSpinner.getValue())).intValue();
	}

	/**
	 * 
	 * Handles action events such as pressing a button or choosing a menu item.
	 * 
	 * @param event
	 *            The action event.
	 */
	public void actionPerformed(ActionEvent event) {
		// information dialog
		if (event.getSource() == menuInfo) {
			JOptionPane
					.showMessageDialog(
							this,
							"VisnaCom\n" +
							"Visual Navigation of Compound Graphs\n" +
							"Version 1.0\n\n" +
							"Implemented by: \n" + 
							"Franz Pfeiffer\n" + 
							"Michael Proepster\n\n",
							"Information", JOptionPane.INFORMATION_MESSAGE);

			// exiting application
		} else if (event.getSource() == menuExit) {
			System.exit(0);

			// switching to system L-a-
		} else if (event.getSource() == sysLaF) {
			tmpPrefs.LAF = "sys";

			// switching to metal L-a-F
		} else if (event.getSource() == metalLaF) {
			tmpPrefs.LAF = "metal";

			// switching to motif L-a-F
		} else if (event.getSource() == motifLaF) {
			tmpPrefs.LAF = "motif";

			// switching to windows L-a-F
		} else if (event.getSource() == winLaF) {
			tmpPrefs.LAF = "win";

			// shows file chooser for opening file
		} else if (event.getSource() == menuOpen || event.getSource() == tbOpen) {
			String filename = "";
			JFileChooser fc = new JFileChooser(System.getProperty("user.dir"));
			if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
				filename = fc.getSelectedFile().getAbsolutePath();
			}
			if (!filename.equals("")) {
				createNewInternalFrame();
				ViewFrame vF = getActiveFrame();
				vF.loadFile(filename);
				this.setAllFrameTitles(getActiveFrame().getViewPanel()
						.getBaseGraph(), filename);
			}
			setSaveEnabled();

			// shows file chooser fo saving file
		} else if (getActiveFrame() != null
				&& (event.getSource() == menuSaveAs || event.getSource() == tbSaveAs)) {
			String filename = "";
			JFileChooser fc = new JFileChooser(System.getProperty("user.dir"));
			if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
				filename = fc.getSelectedFile().getAbsolutePath();
			}
			if (!filename.equals("") && getActiveFrame() != null) {
				getActiveFrame().saveFile(filename);
				this.setAllFrameTitles(getActiveFrame().getViewPanel()
						.getBaseGraph(), filename);
			}

			// saves graph and views
		} else if (getActiveFrame() != null
				&& (event.getSource() == menuSave || event.getSource() == tbSave)) {
			String filename = getActiveFrame().getFilename();
			if (getActiveFrame().saved()) {
				getActiveFrame().saveFile(filename);
			} else {
				JFileChooser fc = new JFileChooser(System
						.getProperty("user.dir"));
				if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
					filename = fc.getSelectedFile().getAbsolutePath();
				}
				if (!filename.equals("")) {
					getActiveFrame().saveFile(filename);
				}
			}

			if (!filename.equals("")) {
				this.setAllFrameTitles(getActiveFrame().getViewPanel()
						.getBaseGraph(), filename);
			}

			// creates new internal frame
		} else if (event.getSource() == menuNew || event.getSource() == tbNew) {
			createNewInternalFrame();
			setSaveEnabled();

			// opens additional view for current graph
		} else if (event.getSource() == menuNewView
				|| event.getSource() == tbNewView) {
			ViewFrame oldFrame = getActiveFrame();
			setSaveEnabled();
			if (oldFrame == null) {
				showError("Unavailable", "No currently active graph!");
			} else {
				createNewInternalFrame();
				ViewFrame newFrame = getActiveFrame();
				newFrame.newView(oldFrame);
				newFrame.setFilename(oldFrame.getFilename());
				this.frameCounter--;
			}

			// close frame
		} else if (event.getSource() == menuClose) {
			ViewFrame closed = getActiveFrame();
			closed.closeFrame();
			iFrames.remove(closed);
			if (iFrames.isEmpty()) {
				this.disableCutCopy();
				menuPaste.setEnabled(false);
				tbPaste.setEnabled(false);
			}
			closed.dispose();
			setSaveEnabled();

			// shows preferences dialog
		} else if (event.getSource() == menuPref) {
			createPrefDialog();

			// changes node color
		} else if (event.getSource() == nodeFillColor) {
			Color c = JColorChooser.showDialog(((Component) event.getSource())
					.getParent(), "Choose node fillcolor",
					getCurrentPrefs().nFill);
			if (c != null) {
				Color newTransCol = new Color(c.getRed(), c.getGreen(), c
						.getBlue(), 200);
				nFillCol.setBackground(newTransCol);
				tmpPrefs.nFill = newTransCol;
			}

			// changing cluster frame color
		} else if (event.getSource() == clusterFrameColor) {
			Color c = JColorChooser.showDialog(((Component) event.getSource())
					.getParent(), "Choose cluster framecolor",
					getCurrentPrefs().clusFrame);
			if (c != null) {
				clusFrameCol.setBackground(c);
				tmpPrefs.clusFrame = c;
			}

			// changing node frame color
		} else if (event.getSource() == nodeFrameColor) {
			Color c = JColorChooser.showDialog(((Component) event.getSource())
					.getParent(), "Choose node framecolor",
					getCurrentPrefs().nFrame);
			if (c != null) {
				nFrameCol.setBackground(c);
				tmpPrefs.nFrame = c;
			}

			//changing line color
		} else if (event.getSource() == lineColor) {
			Color c = JColorChooser.showDialog(((Component) event.getSource())
					.getParent(), "Choose line color", getCurrentPrefs().lCol);
			if (c != null) {
				lineCol.setBackground(c);
				tmpPrefs.lCol = c;
			}

			//changing cluster color
		} else if (event.getSource() == clusterFillColor) {
			Color c = JColorChooser.showDialog(((Component) event.getSource())
					.getParent(), "Choose cluster fillcolor",
					getCurrentPrefs().clusFill);
			if (c != null) {
				clusFillCol.setBackground(c);
				tmpPrefs.clusFill = c;
			}

			// changing color of markings for items within selection range
		} else if (event.getSource() == withinColor) {
			Color c = JColorChooser.showDialog(((Component) event.getSource())
					.getParent(), "Choose select-markings color",
					getCurrentPrefs().select);
			if (c != null) {
				withinSel.setBackground(c);
				tmpPrefs.select = c;
			}

			// changing color of markings for selected items
		} else if (event.getSource() == selectionColor) {
			Color c = JColorChooser.showDialog(((Component) event.getSource())
					.getParent(), "Choose cluster fillcolor",
					getCurrentPrefs().selected);
			if (c != null) {
				selection.setBackground(c);
				tmpPrefs.selected = c;
			}

			// chooses color for nodes that cannot be contracted
		} else if (event.getSource() == noCollColor) {
			Color c = JColorChooser.showDialog(((Component) event.getSource())
					.getParent(), "Choose no collapsing color",
					getCurrentPrefs().selected);
			if (c != null) {
				noColl.setBackground(c);
				tmpPrefs.noContraction = c;
			}

			// chooses color for nodes that cannot be expanded
		} else if (event.getSource() == noExpandColor) {
			Color c = JColorChooser.showDialog(((Component) event.getSource())
					.getParent(), "Choose no expansion color",
					getCurrentPrefs().selected);
			if (c != null) {
				noExpand.setBackground(c);
				tmpPrefs.noExpansion = c;
			}

			// setting editing mode to 'create'
		} else if (event.getSource() == togCreate) {
			changeEditMode(TOG_CREATE);

			// setting editing mode to 'move'
		} else if (event.getSource() == togMove) {
			changeEditMode(TOG_MOVE);

			// setting editing mode to 'collapse and expand'
		} else if (event.getSource() == togCollExp) {
			changeEditMode(TOG_COLL_EXP);

			// delete selected items
		} else if (event.getSource() == menuDelete) {
			ViewFrame active = getActiveFrame();
			active.delSelection();

			// copies selected items
		} else if (event.getSource() == menuCopy || event.getSource() == tbCopy) {
			copy();

			// pastes copied items
		} else if ((event.getSource() == menuPaste || event.getSource() == tbPaste)) {
			getActiveFrame().pasteNodes(copiedNodes, copiedClusters,
					contracted, geoCoords, edges, copiedClusDepth);
			getActiveFrame().drawAll();

			// cuts selected items
		} else if (event.getSource() == menuCut || event.getSource() == tbCut) {
			copy();
			getActiveFrame().delSelection();
			getActiveFrame().drawAll();
			//redraw according to selected algorithm

		} else if (event.getSource() == menuRedraw) {
			getActiveFrame().getViewPanel().redraw();

			// saves preferences to file
		} else if (event.getSource() == menuSession) {
			if (getCurrentPrefs() == null) {
				showError("Unavailable",
						"You need to activate an internal frame");
			} else {
				try {
					FileOutputStream fs = new FileOutputStream("prefs.ser");
					ObjectOutputStream os = new ObjectOutputStream(fs);
					Preferences curr = getCurrentPrefs();
					curr.LAF = this.LAF;
					curr.scrollSpeed = this.scrollSpeed;
					os.writeObject(curr);
					os.close();
					showInfo("Session saved",
							"Your preferences have been saved.");
				} catch (IOException exc) {
					System.err.println("Error while saving preferences: "
							+ exc.toString());
				}
			}

			// enables edge direction
		} else if (event.getSource() == directed) {
			tmpPrefs.edgeType = "directed";

			// disables edge direction
		} else if (event.getSource() == undirected) {
			tmpPrefs.edgeType = "undirected";

			// uses smooth curves for edges
		} else if (event.getSource() == smooth) {
			tmpPrefs.curveType = "smooth";

			// uses polylines for edges
		} else if (event.getSource() == polyline) {
			tmpPrefs.curveType = "polyline";

			// restores default preferences
		} else if (event.getSource() == restoreDefault) {
			tmpPrefs.setPrefs(restoreDef());
			scrollSpeed = getCurrentPrefs().scrollSpeed;
			nFillCol.setBackground(tmpPrefs.nFill);
			nFrameCol.setBackground(tmpPrefs.nFrame);
			lineCol.setBackground(tmpPrefs.lCol);
			clusFillCol.setBackground(tmpPrefs.clusFill);
			clusFrameCol.setBackground(tmpPrefs.clusFrame);
			withinSel.setBackground(tmpPrefs.select);
			selection.setBackground(tmpPrefs.selected);
			noColl.setBackground(tmpPrefs.noContraction);
			noExpand.setBackground(tmpPrefs.noExpansion);
			speedSpinner.setValue(new Integer(scrollSpeed));

			// merge/split mode
		} else if (event.getSource() == togInclude) {
			changeEditMode(TOG_INCLUDE);

			// mode for text labels
		} else if (event.getSource() == togLabel) {
			changeEditMode(TOG_LABEL);

			// changes layout algorithm to sugiyama
		} else if (event.getSource() == sugiAlg) {
			tmpPrefs.algorithm = "sugi";

			// changes layout algorithm to default
		} else if (event.getSource() == defAlg) {
			tmpPrefs.algorithm = "default";

			// selects all elements in frame
		} else if (event.getSource() == menuSelectAll) {
			this.getActiveFrame().getViewPanel().selectAll();

			//disables animation during expand/collapse
		} else if (event.getSource() == noAnimation) {
			tmpPrefs.animation = "none";

			// enables linear animation during expand/collapse
		} else if (event.getSource() == linearAnimation) {
			tmpPrefs.animation = "linear";

			// confirming preferences change
		} else if (event.getSource() == prefOk) {
			setNewPrefs();
			this.getActiveFrame().getViewPanel().getPrefs().setPrefs(tmpPrefs);
			prefDialog.dispose();

			// no change in preferences
		} else if (event.getSource() == prefCancel) {
			prefDialog.dispose();

			// opens online help

		} else if (event.getSource() == menuHelp) {
			OnlineHelp help = new OnlineHelp();
		}

	}

	/**
	 * 
	 * Sets new preferences for currently active panel, if these preferences
	 * 
	 * have been confirmed by pressing ok-button
	 *  
	 */
	private void setNewPrefs() {
		// sets new drawing style if necessary
		if (!tmpPrefs.algorithm.equals(getCurrentPrefs().algorithm)) {
			if (tmpPrefs.algorithm.equals("sugi")) {
				this.getActiveFrame().getViewPanel().getGeometry()
						.setDrawingStyle(
								new SugiyamaDrawingStyle(this.getActiveFrame()
										.getViewPanel().getGeometry()));
			} else {
				this.getActiveFrame().getViewPanel().getGeometry()
						.setDrawingStyle(
								new DefaultDrawingStyle(this.getActiveFrame()
										.getViewPanel().getGeometry()));
			}
		}

		// sets new animation style if necessary
		if (!tmpPrefs.animation.equals(getCurrentPrefs().animation)) {
			if (tmpPrefs.animation.equals("linear")) {
				this.getActiveFrame().getViewPanel().setAnimationStyle(
						new AffinLinearAnimation(this.getActiveFrame()
								.getViewPanel().getGeometry(), this
								.getActiveFrame().getViewPanel()));
			} else {
				this.getActiveFrame().getViewPanel().setAnimationStyle(
						new NoAnimation(this.getActiveFrame().getViewPanel()
								.getGeometry(), this.getActiveFrame()
								.getViewPanel()));
			}
		}

		// sets new LAF if necessary
		if (!tmpPrefs.LAF.equals(LAF)) {
			if (tmpPrefs.LAF.equals("sys")) {
				try {
					UIManager.setLookAndFeel(UIManager
							.getSystemLookAndFeelClassName());
					SwingUtilities.updateComponentTreeUI(this);
					LAF = "sys";
				} catch (Exception e) {
					System.err.println("Cannot set look and feel: " + e);
				}

				// switching to metal L-a-F
			} else if (tmpPrefs.LAF.equals("metal")) {
				try {
					UIManager
							.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
					SwingUtilities.updateComponentTreeUI(this);
					LAF = "metal";
				} catch (Exception e) {
					System.err.println("Cannot set look and feel: " + e);
				}

				// switching to motif L-a-F
			} else if (tmpPrefs.LAF.equals("motif")) {
				try {
					UIManager
							.setLookAndFeel("com.sun.java.swing.plaf.motif.MotifLookAndFeel");
					SwingUtilities.updateComponentTreeUI(this);
					LAF = "motif";
				} catch (Exception e) {
					System.err.println("Cannot set look and feel: " + e);
				}

				// switching to windows L-a-F
			} else if (tmpPrefs.LAF.equals("win")) {
				try {
					UIManager
							.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
					SwingUtilities.updateComponentTreeUI(this);
					LAF = "win";
				} catch (Exception e) {
					System.err.println("Cannot set look and feel: " + e);
				}
			}
		}
		getActiveFrame().getViewPanel().repaint();
	}

	/**
	 * 
	 * Creates the file menu.
	 * 
	 * @return File JMenu.
	 *  
	 */
	private JMenu createFileMenu() {
		// straightforward. loading icons, setting accelerators, mnemonics and
		// adding listeners
		JMenu ret = new JMenu("File");
		ret.setMnemonic('F');
		// new graph
		menuNew = new JMenuItem("New", new ImageIcon(cl
				.getResource("./images/New16.gif")));
		setCtrlAccelerator(menuNew, 'N');
		menuNew.setMnemonic('e');
		menuNew.addActionListener(this);
		ret.add(menuNew);

		// new view
		menuNewView = new JMenuItem("New View", new ImageIcon(cl
				.getResource("./images/newView16.gif")));
		setCtrlAccelerator(menuNewView, 'W');
		menuNewView.setMnemonic('v');
		menuNewView.addActionListener(this);
		ret.add(menuNewView);

		// load graph from file
		menuOpen = new JMenuItem("Open", new ImageIcon(cl
				.getResource("./images/Open16.gif")));
		setCtrlAccelerator(menuOpen, 'O');
		menuOpen.setMnemonic('p');
		menuOpen.addActionListener(this);
		ret.add(menuOpen);
		ret.addSeparator();

		// save graph to file

		menuSave = new JMenuItem("Save", new ImageIcon(cl
				.getResource("./images/Save16.gif")));
		setCtrlAccelerator(menuSave, 'S');
		menuSave.setMnemonic('a');
		menuSave.addActionListener(this);
		ret.add(menuSave);

		// save graph to file with given filename
		menuSaveAs = new JMenuItem("Save as", new ImageIcon(cl
				.getResource("./images/SaveAs16.gif")));
		setCtrlShiftAccelerator(menuSaveAs, 'S');
		menuSaveAs.setMnemonic('a');
		menuSaveAs.addActionListener(this);
		ret.add(menuSaveAs);
		ret.addSeparator();

		// closes internal frame
		menuClose = new JMenuItem("Close", new ImageIcon(cl
				.getResource("./images/Close.gif")));
		menuClose.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4,
				Event.CTRL_MASK));
		menuClose.setMnemonic('l');
		menuClose.addActionListener(this);
		ret.add(menuClose);

		// exits editor
		menuExit = new JMenuItem("Exit", 'x');
		setCtrlAccelerator(menuExit, 'E');
		menuExit.addActionListener(this);
		ret.add(menuExit);
		return ret;
	}

	/**
	 * 
	 * Creates edit menu. *
	 * 
	 * @return Edit JMenu.
	 *  
	 */
	private JMenu createEditMenu() {
		JMenu edit = new JMenu("Edit");
		edit.setMnemonic('E');

		// copy and delete
		menuCut = new JMenuItem("Cut", new ImageIcon(cl
				.getResource("./images/Cut16.gif")));
		setCtrlAccelerator(menuCut, 'X');
		menuCut.setMnemonic('u');
		menuCut.addActionListener(this);
		edit.add(menuCut);
		menuCut.setEnabled(false);

		// copy
		menuCopy = new JMenuItem("Copy", new ImageIcon(cl
				.getResource("./images/Copy16.gif")));
		setCtrlAccelerator(menuCopy, 'C');
		menuCopy.setMnemonic('o');
		menuCopy.addActionListener(this);
		edit.add(menuCopy);
		menuCopy.setEnabled(false);

		// paste
		menuPaste = new JMenuItem("Paste", new ImageIcon(cl
				.getResource("./images/Paste16.gif")));
		setCtrlAccelerator(menuPaste, 'V');
		menuPaste.setMnemonic('a');
		menuPaste.addActionListener(this);
		edit.add(menuPaste);
		menuPaste.setEnabled(false);
		edit.addSeparator();

		// delete
		menuDelete = new JMenuItem("Delete", new ImageIcon(cl
				.getResource("./images/Delete16.gif")));
		menuDelete
				.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
		menuDelete.setMnemonic('l');
		menuDelete.addActionListener(this);
		edit.add(menuDelete);

		// select all elements
		menuSelectAll = new JMenuItem("Select All");
		setCtrlAccelerator(menuSelectAll, 'A');
		menuSelectAll.setMnemonic('t');
		menuSelectAll.addActionListener(this);
		edit.add(menuSelectAll);
		edit.addSeparator();

		// redraw using current alg
		menuRedraw = new JMenuItem("ReDraw", new ImageIcon(cl
				.getResource("./images/Redraw16.gif")));
		setCtrlAccelerator(menuRedraw, 'R');
		menuRedraw.setMnemonic('D');
		menuRedraw.addActionListener(this);
		edit.add(menuRedraw);
		return edit;
	}

	/**
	 * 
	 * Creates option menu.
	 * 
	 * @return Option JMenu.
	 *  
	 */
	private JMenu createOptionMenu() {
		JMenu opt = new JMenu("Option");
		opt.setMnemonic('O');

		// displaying preferences
		menuPref = new JMenuItem("Preferences", new ImageIcon(cl
				.getResource("./images/Preferences16.gif")));
		setCtrlAccelerator(menuPref, 'P');
		menuPref.setMnemonic('R');
		menuPref.addActionListener(this);
		opt.add(menuPref);
		opt.addSeparator();

		// saving of preferences
		menuSession = new JMenuItem("Save Preferences", new ImageIcon(cl
				.getResource("./images/Save16.gif")));
		menuSession.setAccelerator(KeyStroke.getKeyStroke('S', Event.ALT_MASK));
		menuSession.setMnemonic('A');
		menuSession.addActionListener(this);
		opt.add(menuSession);
		return opt;
	}

	/**
	 * 
	 * Creates help menu.
	 * 
	 * @return Help JMenu.
	 *  
	 */
	private JMenu createHelpMenu() {
		JMenu help = new JMenu("Help");
		help.setMnemonic('H');

		// help
		menuHelp = new JMenuItem("Help", new ImageIcon(cl
				.getResource("./images/Help16.gif")));
		setCtrlAccelerator(menuHelp, 'H');
		menuHelp.setMnemonic('e');
		menuHelp.addActionListener(this);
		help.add(menuHelp);
		help.addSeparator();

		// info
		menuInfo = new JMenuItem("Info", new ImageIcon(
				"./images/Information16.GIF"));
		setCtrlAccelerator(menuInfo, 'I');
		menuInfo.setMnemonic('n');
		menuInfo.addActionListener(this);
		help.add(menuInfo);
		return help;
	}

	/**
	 * 
	 * Sets the Ctrl-accelerator for a menu.
	 * 
	 * @param mi
	 *            Menu which gets Ctrl-accelerator.
	 * 
	 * @param acc
	 *            Accelerator char.
	 *  
	 */
	private void setCtrlAccelerator(JMenuItem mi, char acc) {
		KeyStroke ks = KeyStroke.getKeyStroke(acc, Event.CTRL_MASK);
		mi.setAccelerator(ks);
	}

	/**
	 * 
	 * Sets the Ctrl-Shift-accelerator for a menu.
	 * 
	 * @param mi
	 *            Menu which gets Ctrl-Shift-accelerator.
	 * 
	 * @param acc
	 *            Accelerator char.
	 *  
	 */
	private void setCtrlShiftAccelerator(JMenuItem mi, char acc) {
		KeyStroke ks = KeyStroke.getKeyStroke(acc, Event.CTRL_MASK
				+ Event.SHIFT_MASK);
		mi.setAccelerator(ks);
	}

	/**
	 * 
	 * Creates the toolbar.
	 * 
	 * @return JPanel containing toolbar.
	 *  
	 */
	private JPanel createToolBar() {
		JPanel toolBar = new JPanel();
		toolBar.setLayout(new FlowLayout(FlowLayout.LEFT));
		JToolBar symbols = new JToolBar();

		// images for icons in toolbar
		ImageIcon tNew = new ImageIcon(cl.getResource("./images/New24.gif"));
		ImageIcon tNewView = new ImageIcon(cl
				.getResource("./images/newView24.gif"));
		ImageIcon tOpen = new ImageIcon(cl.getResource("./images/Open24.gif"));
		ImageIcon tSave = new ImageIcon(cl.getResource("./images/Save24.gif"));
		ImageIcon tCut = new ImageIcon(cl.getResource("./images/Cut24.gif"));
		ImageIcon tCopy = new ImageIcon(cl.getResource("./images/Copy24.gif"));
		ImageIcon tPaste = new ImageIcon(cl.getResource("./images/Paste24.gif"));
		ImageIcon tSaveAs = new ImageIcon(cl
				.getResource("./images/SaveAs24.gif"));

		// new graph
		tbNew = new JButton(tNew);
		tbNew.setToolTipText("Draw new compound graph");
		tbNew.addActionListener(this);

		// new view
		tbNewView = new JButton(tNewView);
		tbNewView.setToolTipText("Opens new view of basegraph");
		tbNewView.addActionListener(this);

		// open file
		tbOpen = new JButton(tOpen);
		tbOpen.setToolTipText("Open compound graph");
		tbOpen.addActionListener(this);

		// save to file
		tbSave = new JButton(tSave);
		tbSave.setToolTipText("Save compound graph");
		tbSave.addActionListener(this);

		// save to file with given name
		tbSaveAs = new JButton(tSaveAs);
		tbSaveAs.setToolTipText("Save compound graph as ...");
		tbSaveAs.addActionListener(this);

		// cut
		tbCut = new JButton(tCut);
		tbCut.setToolTipText("Cut a part of compound graph");
		tbCut.addActionListener(this);
		tbCut.setEnabled(false);

		// copy
		tbCopy = new JButton(tCopy);
		tbCopy.setToolTipText("Copy part of compound graph");
		tbCopy.addActionListener(this);
		tbCopy.setEnabled(false);

		// paste
		tbPaste = new JButton(tPaste);
		tbPaste.setToolTipText("Paste part of compound graph");
		tbPaste.addActionListener(this);
		tbPaste.setEnabled(false);

		// adding buttons to toolbar
		symbols.add(tbNew);
		symbols.add(tbNewView);
		symbols.add(tbOpen);
		symbols.addSeparator();
		symbols.add(tbSave);
		symbols.add(tbSaveAs);
		symbols.addSeparator();
		symbols.add(tbCut);
		symbols.add(tbCopy);
		symbols.add(tbPaste);
		symbols.addSeparator();
		symbols.setFloatable(false);
		toolBar.add(symbols);
		return toolBar;
	}

	/**
	 * 
	 * Gets the currently active internal frame.
	 * 
	 * @return The active ViewFrame.
	 *  
	 */
	private ViewFrame getActiveFrame() {
		Iterator it = iFrames.iterator();
		while (it.hasNext()) {
			ViewFrame iframe = (ViewFrame) it.next();
			if (iframe.isSelected()) {
				return iframe;
			}
		}
		return null;
	}

	/**
	 * 
	 * Sets the preferences to default values.
	 * 
	 * @return The default preferences.
	 *  
	 */
	private Preferences restoreDef() {
		Preferences prefs = new Preferences();
		prefs.clusFill = this.clusFill;
		prefs.clusFrame = this.clusFrame;
		prefs.lCol = this.lCol;
		prefs.nFill = this.nFill;
		prefs.nFrame = this.nFrame;
		prefs.select = this.select;
		prefs.selected = this.selected;
		prefs.noExpansion = this.noExpansion;
		prefs.noContraction = this.noContraction;
		prefs.algorithm = "default";
		prefs.animation = "none";
		prefs.edgeType = "undirected";
		prefs.curveType = "smooth";
		prefs.scrollSpeed = 100;
		prefs.LAF = "metal";
		noAnimation.setSelected(true);
		undirected.setSelected(true);
		defAlg.setSelected(true);
		smooth.setSelected(true);
		return prefs;
	}

	/**
	 * Enables or disables save buttons.
	 */
	private void setSaveEnabled() {
		if (iFrames.isEmpty()) {
			menuSave.setEnabled(false);
			tbSave.setEnabled(false);
			menuSaveAs.setEnabled(false);
			tbSaveAs.setEnabled(false);
		} else {
			menuSave.setEnabled(true);
			tbSave.setEnabled(true);
			menuSaveAs.setEnabled(true);
			tbSaveAs.setEnabled(true);
		}
	}

	/**
	 * 
	 * Enables the cut and copy buttons in the toolbar and the menu items.
	 *  
	 */
	public void enableCutCopy() {
		tbCut.setEnabled(true);
		tbCopy.setEnabled(true);
		menuCut.setEnabled(true);
		menuCopy.setEnabled(true);
		menuDelete.setEnabled(true);
	}

	/**
	 * 
	 * Enables the deletion buttons in the toolbar and the menu items.
	 *  
	 */
	public void enableDel() {
		menuDelete.setEnabled(true);
	}

	/**
	 * 
	 * Disables the cut and copy buttons in the toolbar and the menu items.
	 *  
	 */
	public void disableCutCopy() {
		tbCut.setEnabled(false);
		tbCopy.setEnabled(false);
		menuCut.setEnabled(false);
		menuCopy.setEnabled(false);
		menuDelete.setEnabled(false);
	}

	/**
	 * 
	 * Creates the dialog used to choose preferences.
	 *  
	 */
	private void createPrefDialog() {
		if (getCurrentPrefs() == null) {
			this.showError("Unavailable",
					"You need to active an internal frame");
		} else {
			tmpPrefs.setPrefs(getCurrentPrefs());
			ButtonGroup lafGroup = new ButtonGroup();
			prefDialog = new JDialog(this, "Select your preferences", true);

			// different dimensions dur to current L-a-F
			if (LAF.equals("win")) {
				prefDialog.setSize(370, 520);
			} else if (LAF.equals("metal")) {
				prefDialog.setSize(370, 510);
			} else if (LAF.equals("motif")) {
				prefDialog.setSize(370, 590);
			} else {
				prefDialog.setSize(370, 590);
			}
			prefDialog.setLocation(200, 30);

			// JPanels showing sample colors
			nFillCol = new JPanel(true);
			nFrameCol = new JPanel(true);
			lineCol = new JPanel(true);
			clusFillCol = new JPanel(true);
			clusFrameCol = new JPanel(true);
			withinSel = new JPanel(true);
			selection = new JPanel(true);
			noColl = new JPanel(true);
			noExpand = new JPanel(true);
			JTabbedPane jtb = new JTabbedPane();

			// making TabbedPane content
			JPanel dialogPanel = new JPanel();
			JPanel algPanel = new JPanel();
			dialogPanel.setLayout(new BorderLayout());
			algPanel.setLayout(new GridLayout(5, 1));
			JPanel algChooser = new JPanel();
			JPanel animChooser = new JPanel();
			JPanel directedChooser = new JPanel();
			JPanel curveChooser = new JPanel();
			JPanel scrollSpinner = new JPanel();
			algPanel.add(algChooser);
			algPanel.add(animChooser);
			algPanel.add(directedChooser);
			algPanel.add(curveChooser);
			algPanel.add(scrollSpinner);

			// setting borders for editing specs areas
			algChooser.setBorder(new TitledBorder("Choose Algorithm"));
			animChooser.setBorder(new TitledBorder("Choose Animation"));
			directedChooser.setBorder(new TitledBorder("Choose Edge Type"));
			curveChooser.setBorder(new TitledBorder("Choose Curve Type"));
			scrollSpinner.setBorder(new TitledBorder("Choose Scrolling Speed"));
			algGroup = new ButtonGroup();
			defAlg = new JRadioButton("Default", getCurrentPrefs().algorithm
					.equals("default"));
			sugiAlg = new JRadioButton("Sugiyama", getCurrentPrefs().algorithm
					.equals("sugi"));
			algGroup.add(defAlg);
			algGroup.add(sugiAlg);
			algChooser.add(defAlg);
			algChooser.add(sugiAlg);

			// adding action listeners
			defAlg.addActionListener(this);
			sugiAlg.addActionListener(this);
			animGroup = new ButtonGroup();
			noAnimation = new JRadioButton("None", getCurrentPrefs().animation
					.equals("none"));
			linearAnimation = new JRadioButton("Linear",
					getCurrentPrefs().animation.equals("linear"));
			animGroup.add(noAnimation);
			animGroup.add(linearAnimation);
			animChooser.add(noAnimation);
			animChooser.add(linearAnimation);

			// adding action listeners
			noAnimation.addActionListener(this);
			linearAnimation.addActionListener(this);

			// buttons for choosing edge type
			ButtonGroup directedGroup = new ButtonGroup();
			directed = new JRadioButton("Directed", getCurrentPrefs().edgeType
					.equals("directed"));
			undirected = new JRadioButton("Undirected",
					getCurrentPrefs().edgeType.equals("undirected"));
			directedGroup.add(undirected);
			directedGroup.add(directed);
			directedChooser.add(undirected);
			directedChooser.add(directed);

			// adding action listeners
			directed.addActionListener(this);
			undirected.addActionListener(this);

			// buttons for choosing curve type
			ButtonGroup curveGroup = new ButtonGroup();
			smooth = new JRadioButton("Smooth", (getCurrentPrefs().curveType)
					.equals("smooth"));
			polyline = new JRadioButton("Polyline",
					(getCurrentPrefs().curveType).equals("polyline"));
			curveGroup.add(smooth);
			curveGroup.add(polyline);
			curveChooser.add(smooth);
			curveChooser.add(polyline);

			// adding action listeners
			smooth.addActionListener(this);
			polyline.addActionListener(this);

			//JSpinner for choosing setting speed
			JLabel label = new JLabel("Scrolling speed in %");
			SpinnerModel sm = new SpinnerNumberModel(scrollSpeed, 1, 1000, 1);
			speedSpinner = new JSpinner(sm);
			speedSpinner.setPreferredSize(new Dimension(50, 20));
			scrollSpinner.add(label);
			scrollSpinner.add(speedSpinner);
			speedSpinner.addChangeListener(this);

			// chosing L-a-F
			JPanel lafPanel = new JPanel();
			jtb.addTab("L-a-F", dialogPanel);
			jtb.addTab("Editing specs", algPanel);
			lafPanel.setBorder(new TitledBorder("Choose Look-and-Feel"));
			metalLaF = new JRadioButton("Metal", LAF.equals("metal"));
			motifLaF = new JRadioButton("Motif", LAF.equals("motif"));
			winLaF = new JRadioButton("Windows", LAF.equals("win"));
			sysLaF = new JRadioButton("System", LAF.equals("sys"));
			metalLaF.addActionListener(this);
			motifLaF.addActionListener(this);
			winLaF.addActionListener(this);
			sysLaF.addActionListener(this);
			lafGroup.add(metalLaF);
			lafGroup.add(motifLaF);
			lafGroup.add(winLaF);
			lafGroup.add(sysLaF);
			lafPanel.add(metalLaF);
			lafPanel.add(motifLaF);
			lafPanel.add(winLaF);
			lafPanel.add(sysLaF);
			JPanel restoreDef = new JPanel();
			restoreDefault = new JButton("Restore Default");
			restoreDef.setBorder(new TitledBorder("Default Look-and-Feel"));
			restoreDef.add(restoreDefault);
			restoreDefault.addActionListener(this);
			JPanel colorPanel = new JPanel();
			colorPanel.setLayout(new GridLayout(9, 2));
			nodeFillColor = new JButton("Node fillcolor:");
			nodeFillColor.addActionListener(this);
			nodeFrameColor = new JButton("Node framecolor:");
			nodeFrameColor.addActionListener(this);
			lineColor = new JButton("Line color:");
			lineColor.addActionListener(this);
			clusterFillColor = new JButton("Cluster fillcolor:");
			clusterFillColor.addActionListener(this);
			clusterFrameColor = new JButton("Cluster framecolor:");
			clusterFrameColor.addActionListener(this);
			withinColor = new JButton("Select-markings color");
			withinColor.addActionListener(this);
			selectionColor = new JButton("Selected-markings color");
			selectionColor.addActionListener(this);
			noCollColor = new JButton("No-Collapsing color");
			noCollColor.addActionListener(this);
			noExpandColor = new JButton("No-Expansion color");
			noExpandColor.addActionListener(this);

			// choosing of node color
			colorPanel.add(nodeFillColor);
			JPanel nFillCol_Big = new JPanel(true);
			nFillCol_Big.setLayout(new GridLayout(3, 3));
			nFillCol_Big.add(new JPanel());
			nFillCol_Big.add(new JPanel());
			nFillCol_Big.add(new JPanel());
			nFillCol_Big.add(new JPanel());
			nFillCol_Big.add(nFillCol);
			nFillCol_Big.add(new JPanel());
			nFillCol_Big.add(new JPanel());
			nFillCol_Big.add(new JPanel());
			nFillCol_Big.add(new JPanel());
			colorPanel.add(nFillCol_Big);
			nFillCol.setBackground(getCurrentPrefs().nFill);

			// choosing of node frame color
			colorPanel.add(nodeFrameColor);
			JPanel nFrameCol_Big = new JPanel(true);
			nFrameCol_Big.setLayout(new GridLayout(3, 3));
			nFrameCol_Big.add(new JPanel());
			nFrameCol_Big.add(new JPanel());
			nFrameCol_Big.add(new JPanel());
			nFrameCol_Big.add(new JPanel());
			nFrameCol_Big.add(nFrameCol);
			nFrameCol_Big.add(new JPanel());
			nFrameCol_Big.add(new JPanel());
			nFrameCol_Big.add(new JPanel());
			nFrameCol_Big.add(new JPanel());
			colorPanel.add(nFrameCol_Big);
			nFrameCol.setBackground(getCurrentPrefs().nFrame);

			// choosing of edge color
			colorPanel.add(lineColor);
			JPanel lineCol_Big = new JPanel(true);
			lineCol_Big.setLayout(new GridLayout(3, 3));
			lineCol_Big.add(new JPanel());
			lineCol_Big.add(new JPanel());
			lineCol_Big.add(new JPanel());
			lineCol_Big.add(new JPanel());
			lineCol_Big.add(lineCol);
			lineCol_Big.add(new JPanel());
			lineCol_Big.add(new JPanel());
			lineCol_Big.add(new JPanel());
			lineCol_Big.add(new JPanel());
			colorPanel.add(lineCol_Big);
			lineCol.setBackground(getCurrentPrefs().lCol);

			// choosing of clusterColor
			colorPanel.add(clusterFillColor);
			JPanel clusFillCol_Big = new JPanel(true);
			clusFillCol_Big.setLayout(new GridLayout(3, 3));
			clusFillCol_Big.add(new JPanel());
			clusFillCol_Big.add(new JPanel());
			clusFillCol_Big.add(new JPanel());
			clusFillCol_Big.add(new JPanel());
			clusFillCol_Big.add(clusFillCol);
			clusFillCol_Big.add(new JPanel());
			clusFillCol_Big.add(new JPanel());
			clusFillCol_Big.add(new JPanel());
			clusFillCol_Big.add(new JPanel());
			colorPanel.add(clusFillCol_Big);
			clusFillCol.setBackground(getCurrentPrefs().clusFill);

			// choosing of cluster frame color
			colorPanel.add(clusterFrameColor);
			JPanel clusFrameCol_Big = new JPanel(true);
			clusFrameCol_Big.setLayout(new GridLayout(3, 3));
			clusFrameCol_Big.add(new JPanel());
			clusFrameCol_Big.add(new JPanel());
			clusFrameCol_Big.add(new JPanel());
			clusFrameCol_Big.add(new JPanel());
			clusFrameCol_Big.add(clusFrameCol);
			clusFrameCol_Big.add(new JPanel());
			clusFrameCol_Big.add(new JPanel());
			clusFrameCol_Big.add(new JPanel());
			clusFrameCol_Big.add(new JPanel());
			colorPanel.add(clusFrameCol_Big);
			clusFrameCol.setBackground(getCurrentPrefs().clusFrame);

			// choosing of color of items within selection range
			colorPanel.add(withinColor);
			JPanel within_Big = new JPanel(true);
			within_Big.setLayout(new GridLayout(3, 3));
			within_Big.add(new JPanel());
			within_Big.add(new JPanel());
			within_Big.add(new JPanel());
			within_Big.add(new JPanel());
			within_Big.add(withinSel);
			within_Big.add(new JPanel());
			within_Big.add(new JPanel());
			within_Big.add(new JPanel());
			within_Big.add(new JPanel());
			colorPanel.add(within_Big);
			withinSel.setBackground(getCurrentPrefs().select);

			// choosing of color of selected items
			colorPanel.add(selectionColor);
			JPanel sel_Big = new JPanel(true);
			sel_Big.setLayout(new GridLayout(3, 3));
			sel_Big.add(new JPanel());
			sel_Big.add(new JPanel());
			sel_Big.add(new JPanel());
			sel_Big.add(new JPanel());
			sel_Big.add(selection);
			sel_Big.add(new JPanel());
			sel_Big.add(new JPanel());
			sel_Big.add(new JPanel());
			sel_Big.add(new JPanel());
			colorPanel.add(sel_Big);
			selection.setBackground(getCurrentPrefs().selected);

			// choosing of color of items that cannot be contracted
			colorPanel.add(noCollColor);
			JPanel noColl_Big = new JPanel(true);
			noColl_Big.setLayout(new GridLayout(3, 3));
			noColl_Big.add(new JPanel());
			noColl_Big.add(new JPanel());
			noColl_Big.add(new JPanel());
			noColl_Big.add(new JPanel());
			noColl_Big.add(noColl);
			noColl_Big.add(new JPanel());
			noColl_Big.add(new JPanel());
			noColl_Big.add(new JPanel());
			noColl_Big.add(new JPanel());
			colorPanel.add(noColl_Big);
			noColl.setBackground(getCurrentPrefs().noContraction);

			// choosing of color of items that cannot be expanded
			colorPanel.add(noExpandColor);
			JPanel noExpand_Big = new JPanel(true);
			noExpand_Big.setLayout(new GridLayout(3, 3));
			noExpand_Big.add(new JPanel());
			noExpand_Big.add(new JPanel());
			noExpand_Big.add(new JPanel());
			noExpand_Big.add(new JPanel());
			noExpand_Big.add(noExpand);
			noExpand_Big.add(new JPanel());
			noExpand_Big.add(new JPanel());
			noExpand_Big.add(new JPanel());
			noExpand_Big.add(new JPanel());
			colorPanel.add(noExpand_Big);
			noExpand.setBackground(getCurrentPrefs().noExpansion);

			// ok and cancel
			JPanel okPanel = new JPanel();
			prefOk = new JButton("OK");
			prefCancel = new JButton("Cancel");
			prefOk.addActionListener(this);
			prefCancel.addActionListener(this);
			okPanel.add(prefOk);
			okPanel.add(prefCancel);
			dialogPanel.add(colorPanel, BorderLayout.NORTH);
			dialogPanel.add(restoreDef);
			dialogPanel.add(lafPanel, BorderLayout.SOUTH);
			JPanel entireDialog = new JPanel();
			entireDialog.add(jtb, BorderLayout.NORTH);
			entireDialog.add(okPanel, BorderLayout.SOUTH);
			prefDialog.setContentPane(entireDialog);
			prefDialog.setVisible(true);
		}
	}

	/**
	 * 
	 * Opens a new internal frame.
	 * 
	 * @return The new internal frame.
	 *  
	 */
	public ViewFrame createNewInternalFrame() {
		frameCounter++;

		// drawing panel
		Preferences pref = getCurrentPrefs();
		Preferences newPrefs;
		if (pref != null) {
			newPrefs = new Preferences(pref);
		} else {
			newPrefs = new Preferences(tmpPrefs);
		}

		ViewPanel iPanel = new ViewPanel(newPrefs, this);
		ViewFrame iFrame = new ViewFrame("Graph " + (frameCounter + 1), iPanel,
				togFlag);
		iFrame.setVisible(true);
		iFrame.setSize(500, 400);
		iFrames.add(iFrame);
		iFrame.addInternalFrameListener(this);
		desk.add(iFrame);
		iFrame.setBackground(Color.lightGray);
		desk.getDesktopManager().activateFrame(iFrame);
		iPanel.setBackground(Color.white);
		iPanel.setLayout(new GridLayout(10, 10));
		iFrame.getContentPane().add(iPanel);

		// adds scrollbar
		JScrollPane scroll = new JScrollPane(iPanel,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		iFrame.getContentPane().add(scroll, "Center");

		try {
			iFrame.setMaximum(true);
		} catch (Exception e) {
			System.err.println(e);
		}
		iPanel.setViewport(scroll.getViewport());
		return iFrame;
	}

	/**
	 * 
	 * Gets the current preference setting.
	 * 
	 * @return Current preferences.
	 *  
	 */
	private Preferences getCurrentPrefs() {
		if (getActiveFrame() != null) {
			return getActiveFrame().getPrefs();
		}
		return null;
	}

	/**
	 * 
	 * Shows an error option pane.
	 * 
	 * @param title
	 *            Title of error dialog.
	 * 
	 * @param message
	 *            Message of error dialog.
	 *  
	 */
	public void showError(String title, String message) {
		JOptionPane.showMessageDialog(this, message, title,
				JOptionPane.ERROR_MESSAGE);
	}

	/**
	 * 
	 * Shows an info dialog.
	 * 
	 * @param title
	 *            Title of info dialog.
	 * 
	 * @param message
	 *            Message of info dialog.
	 *  
	 */
	public void showInfo(String title, String message) {
		JOptionPane.showMessageDialog(this, message, title,
				JOptionPane.INFORMATION_MESSAGE);
	}

	/**
	 * 
	 * Shows a dialog for entering text labels.
	 * 
	 * @param mode
	 *            Signals type of element to be labelled.
	 * 
	 * @param oldLabel
	 *            Former label of element.
	 * 
	 * @return Response.
	 *  
	 */
	public String showLabelDialog(String mode, String oldLabel) {
		String response = "";
		if (mode.equals("cluster")) {
			response = JOptionPane.showInputDialog("Enter cluster label: ",
					oldLabel);
		} else if (mode.equals("edge")) {
			response = JOptionPane.showInputDialog("Enter edge label: ",
					oldLabel);
		} else if (mode.equals("leaf")) {
			response = JOptionPane.showInputDialog("Enter leaf label: ",
					oldLabel);
		}
		return response;
	}

	/**
	 * 
	 * Gets the current scrolling speed.
	 * 
	 * @return Current scrolling speed.
	 *  
	 */
	public int getScrollSpeed() {
		return scrollSpeed;
	}

	/**
	 * 
	 * Looks for all frames that display a given basegraph.
	 * 
	 * @param bg
	 *            The given basegraph.
	 * 
	 * @param filename
	 *            The filename.
	 * 
	 *  
	 */
	private void setAllFrameTitles(BaseCompoundGraph bg, String filename) {
		if (filename != "") {
			int count = 1;
			Iterator it = iFrames.iterator();
			while (it.hasNext()) {
				ViewFrame frame = (ViewFrame) it.next();
				if (frame.getViewPanel().getBaseGraph() == bg) {
					frame.setTitle(filename + " - View " + count);
					frame.setFilename(filename);
					frame.setSaved();
					count++;
				}
			}
		}
	}

	/**
	 * 
	 * Copies selected elements.
	 *  
	 */
	private void copy() {
		LinkedList l = getActiveFrame().getSelectedNodes();
		copiedNodes = (LinkedList) l.get(0);
		copiedClusters = (HashMap) l.get(1);
		contracted = (LinkedList) l.get(2);
		geoCoords = (HashMap) l.get(3);
		edges = (HashMap) l.get(4);
		copiedClusDepth = (HashMap) l.get(5);
		if (!copiedNodes.isEmpty() || !copiedClusters.isEmpty()) {
			menuPaste.setEnabled(true);
			tbPaste.setEnabled(true);
		}
	}

	/**
	 * 
	 * Changes editing mode.
	 * 
	 * @param tog
	 *            The new editing mode.
	 *  
	 */
	private void changeEditMode(int tog) {
		this.getActiveFrame().getViewPanel().removeDrawEdge();
		Iterator it = iFrames.iterator();
		while (it.hasNext()) {
			((ViewFrame) it.next()).setToggle(tog);
		}
		disableCutCopy();
		togFlag = tog;
	}

	/**
	 * 
	 * Testing purpose only.
	 * 
	 * @return
	 *  
	 */
	public ViewPanel main_test() {
		return this.getActiveFrame().getViewPanel();
	}

}