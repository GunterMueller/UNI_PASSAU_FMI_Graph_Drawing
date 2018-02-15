package quoggles.auxiliary;

import java.io.File;

import javax.swing.filechooser.FileFilter;

public class GMLFileFilter extends FileFilter {

    /**
     * @see javax.swing.filechooser.FileFilter#accept(java.io.File)
     */
    public boolean accept(File f) {
        if (f.isDirectory()) {
            return true;
        }
        return f.getName().toLowerCase().endsWith(".gml");
    }

    /**
     * @see javax.swing.filechooser.FileFilter#getDescription()
     */
    public String getDescription() {
        return "GML files";
    }
}
