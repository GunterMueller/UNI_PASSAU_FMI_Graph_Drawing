package quoggles;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.EmptyBorder;

import quoggles.constants.IBoxConstants;
import quoggles.constants.QDialogConstants;
import quoggles.exceptions.LoadFailedException;
import quoggles.exceptions.QueryExecutionException;
import quoggles.representation.BoxRepresentation;

/**
 *
 */
public class QDialog extends JDialog 
    implements ActionListener, QDialogConstants {
    
    /** Used to communicate between the individual parts of the system */
    private QMain qMain;

    /** The panel where all box icons are placed */
    private JPanel iconPanel;

    /** The panel where the query graph is built */
    private JPanel mainPanel;
    
    /** The panel where the different possible InputBoxes are put */
    private JPanel inputPanel;

    /** Move a box representation without snapping to next IO */
    private boolean placeFreely = false;
    


    public QDialog(QMain q) throws HeadlessException {

        qMain = q;

//          setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
//          setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
//          setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                close();
            }
        });

        setResizable(true);
        setSize(WHOLE_WIDTH, WHOLE_HEIGHT);

        Container cPane = getContentPane();
        cPane.setLayout(new BorderLayout());
        ((JComponent)cPane).setPreferredSize(getSize());
        //      cPane.setBackground(Color.BLUE);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));

        // add startButton
        JButton startButton = new JButton(START_BUTTON);
        startButton.addActionListener(this);
        startButton.setActionCommand(START_BUTTON);
        startButton.setMnemonic(KeyEvent.VK_S);
        getRootPane().setDefaultButton(startButton);
        startButton.setSize(200, 30);
        //        startButton.setLocation(10, getHeight() - startButton.getHeight() - 50);
        buttonPanel.add(startButton);

        // add cancel button
        JButton cancelButton = new JButton(CANCEL_BUTTON);
        cancelButton.addActionListener(this);
        cancelButton.setActionCommand(CANCEL_BUTTON);
        cancelButton.setMnemonic(KeyEvent.VK_C);
        cancelButton.setSize(200, 30);
        //        cancelButton.setLocation(
        //            10 + startButton.getWidth() + 10,
        //            getHeight() - startButton.getHeight() - 50);
        buttonPanel.add(cancelButton);
        
        buttonPanel.add(Box.createHorizontalStrut(50));

        // add save button
        JButton saveButton = new JButton(SAVE_BUTTON);
        saveButton.addActionListener(this);
        saveButton.setActionCommand(SAVE_BUTTON);
        saveButton.setMnemonic(KeyEvent.VK_A);
//        saveButton.setSize(200, 30);
        //        startButton.setLocation(10, getHeight() - startButton.getHeight() - 50);
        buttonPanel.add(saveButton);

        // add load button
        JButton loadButton = new JButton(LOAD_BUTTON);
        loadButton.addActionListener(this);
        loadButton.setActionCommand(LOAD_BUTTON);
        loadButton.setMnemonic(KeyEvent.VK_L);
//        saveButton.setSize(200, 30);
        //        startButton.setLocation(10, getHeight() - startButton.getHeight() - 50);
        buttonPanel.add(loadButton);

        buttonPanel.add(Box.createHorizontalStrut(50));

        // add save sub query button
        JButton subSaveButton = new JButton(SUB_SAVE_BUTTON);
        subSaveButton.addActionListener(this);
        subSaveButton.setActionCommand(SUB_SAVE_BUTTON);
        subSaveButton.setMnemonic(KeyEvent.VK_U);
        buttonPanel.add(subSaveButton);

        buttonPanel.add(Box.createHorizontalStrut(50));

        // add clear button
        JButton clearButton = new JButton(CLEAR_BUTTON);
        clearButton.addActionListener(this);
        clearButton.setActionCommand(CLEAR_BUTTON);
        clearButton.setMnemonic(KeyEvent.VK_R);
        clearButton.setSize(200, 30);
        buttonPanel.add(clearButton);
        
        buttonPanel.add(Box.createHorizontalGlue());
        
////        // add follow button
////        JToggleButton followButton = new JToggleButton(FOLLOW_BUTTON);
////        followButton.addActionListener(this);
////        followButton.setActionCommand(FOLLOW_BUTTON);
////        followButton.setMnemonic(KeyEvent.VK_F);
////        followButton.setSize(200, 30);
////        followButton.setSelected(false);
////        buttonPanel.add(followButton);
////        
////        buttonPanel.add(Box.createHorizontalGlue());

//        // add addBoxButton
//        JButton addBoxButton = new JButton(ADDBOX_BUTTON);
//        addBoxButton.addActionListener(this);
//        addBoxButton.setActionCommand(ADDBOX_BUTTON);
//        addBoxButton.setMnemonic(KeyEvent.VK_B);
//        addBoxButton.setSize(200, 30);
//        //        startButton.setLocation(10, getHeight() - startButton.getHeight() - 50);
//        buttonPanel.add(addBoxButton);
//
//        buttonPanel.add(Box.createHorizontalGlue());

//        // add showTable button
//        JToggleButton showTableButton = new JToggleButton(SHOWTABLE_BUTTON);
//        showTableButton.addActionListener(this);
//        showTableButton.setActionCommand(SHOWTABLE_BUTTON);
//        showTableButton.setMnemonic(KeyEvent.VK_T);
//        showTableButton.setSize(200, 30);
//        showTableButton.setSelected(showTable);
//        buttonPanel.add(showTableButton);

//        // add seilwob button
//        JToggleButton seilwobButton = new JToggleButton(SEILWOB_BUTTON);
//        seilwobButton.addActionListener(this);
//        seilwobButton.setActionCommand(SEILWOB_BUTTON);
//        seilwobButton.setMnemonic(KeyEvent.VK_W);
//        seilwobButton.setSize(200, 30);
//        seilwobButton.setSelected(seilwob);
//        buttonPanel.add(seilwobButton);

        // add place freely button
        JToggleButton placeFreelyButton = new JToggleButton(PLACEFREELY_BUTTON);
        placeFreelyButton.addActionListener(this);
        placeFreelyButton.setActionCommand(PLACEFREELY_BUTTON);
        placeFreelyButton.setMnemonic(KeyEvent.VK_P);
        //placeFreelyButton.setSize(placeFreelyButton.getPreferredSize());
        placeFreelyButton.setSelected(placeFreely);
        buttonPanel.add(placeFreelyButton);

        mainPanel = new JPanel();
        mainPanel.setBorder(BorderFactory.createMatteBorder(2, 2, 0, 0, Color.GRAY));
        mainPanel.setBackground(IBoxConstants.MAINPANEL_BACKGROUND);
        mainPanel.setLayout(null);
        mainPanel.validate();
        JScrollPane mainScroll = new JScrollPane(mainPanel);
        mainScroll.getVerticalScrollBar().setUnitIncrement(10);
        mainPanel.setPreferredSize(new Dimension(4000, 1000));
        
        iconPanel = new JPanel();
        iconPanel.setBackground(IBoxConstants.ICONPANEL_BACKGROUND);
        iconPanel.setLayout(new BoxLayout(iconPanel, BoxLayout.X_AXIS));
        iconPanel.setBorder(new EmptyBorder(5, 3, 5, 3));
        iconPanel.add(Box.createHorizontalStrut(50));
        JScrollPane iconScroll = new JScrollPane(iconPanel);
        iconScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
//        iconPanel.setPreferredSize(new Dimension(ICONPANEL_WIDTH, ICONPANEL_HEIGHT));
        
        inputPanel = new JPanel();
//        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
        inputPanel.setLayout(null);
        Dimension size = new Dimension(
            IBoxConstants.DEFAULT_INPUTBOX_WIDTH,
            IBoxConstants.DEFAULT_INPUTBOX_HEIGHT);
        inputPanel.setSize(size);
        inputPanel.setPreferredSize(size);
        inputPanel.setBackground(IBoxConstants.INPUTPANEL_BACKGROUND);
//        inputPanel.add(Box.createVerticalGlue());
        
        cPane.add(inputPanel, BorderLayout.WEST);
        cPane.add(iconScroll, BorderLayout.NORTH);
        cPane.add(mainScroll, BorderLayout.CENTER);
        cPane.add(buttonPanel, BorderLayout.SOUTH);

//        pack();
    }


    public void adjustIconPanelSize() {
        iconPanel.setPreferredSize(new Dimension(
            iconPanel.getPreferredSize().width,
            ICONPANEL_HEIGHT));
        validate();
    }

    /**
     * Used to close the main window.
     */
    private void close() {
        qMain.close();
    }
    
    /**
     * Returns the panel where the icons are displayed.
     * 
     * @return the panel where the icons are displayed
     */
    public JPanel getIconPanel() {
        return iconPanel;
    }
    
    /**
     * Returns the panel where the query graph is displayed.
     * 
     * @return the panel where the query graph is displayed.
     */
    public JPanel getMainPanel() {
        return mainPanel;
    }
    
    public void addInputBox(BoxRepresentation boxRep) {
        if (boxRep == null) return;
        inputPanel.add(boxRep);
        validate();
    }
    
    public void removeInputBox(BoxRepresentation boxRep) {
        inputPanel.remove(boxRep);
        inputPanel.validate();
        inputPanel.repaint();
    }
    
    public void removeBoxRep(BoxRepresentation boxRep) {
        mainPanel.remove(boxRep);
        mainPanel.validate();
    }
    
    public void reset() {
        mainPanel.removeAll();      
        inputPanel.removeAll();
        validate();
        repaint();
    }

    /**
     * Reacts when the user pressed one of the buttons.
     * 
     * @param e the event to process
     */
    public void actionPerformed(ActionEvent e) {
        String aCommand = e.getActionCommand();
        if (START_BUTTON.equals(aCommand)) {
            try {
                qMain.runQuery();
            } catch (QueryExecutionException qe) {
                JOptionPane.showMessageDialog(this, qe, "Error:",
                    JOptionPane.ERROR_MESSAGE);
            }
            return;
        }
        if (CANCEL_BUTTON.equals(aCommand)) {
            qMain.close();
            return;
        }
        
        if (ADDBOX_BUTTON.equals(aCommand))  {
            qMain.addBox();
            return;
        }
        
        if (SAVE_BUTTON.equals(aCommand)) {
            try {
                qMain.saveQuery();
            } catch (IOException ioe) {
                JOptionPane.showMessageDialog(this, ioe, "Error:",
                    JOptionPane.ERROR_MESSAGE);
            }
            return;
        }
        
        if (LOAD_BUTTON.equals(aCommand)) {
            try {
                qMain.loadQuery();
            } catch (LoadFailedException lfe) {
                JOptionPane.showMessageDialog(this, lfe, "Error:",
                    JOptionPane.ERROR_MESSAGE);
            }
            return;
        }
        
        if (SUB_SAVE_BUTTON.equals(aCommand)) {
            try {
                qMain.saveSubQuery();
            } catch (IOException ioe) {
                JOptionPane.showMessageDialog(this, ioe, "Error:",
                    JOptionPane.ERROR_MESSAGE);
            } catch (QueryExecutionException qee) {
                JOptionPane.showMessageDialog(this, qee, "Error:",
                    JOptionPane.ERROR_MESSAGE);
            }
            return;
        }
        
        if (CLEAR_BUTTON.equals(aCommand)) {
            qMain.clearQuery();
            return;
        }
        
        if (FOLLOW_BUTTON.equals(aCommand)) {
            qMain.setFollowMode(
                ((JToggleButton)e.getSource()).isSelected());
            return;
        }

        if (PLACEFREELY_BUTTON.equals(aCommand)) {
            qMain.setPlaceFreely(
                ((JToggleButton)e.getSource()).isSelected());
            return;
        }
    }
    
    public void addBoxRep(BoxRepresentation boxRep) {
        mainPanel.add(boxRep);
        mainPanel.validate();
    }
}
