/*
 * $Id: QPlugin.java 491 2004-10-11 11:57:29Z holleis $
 */

package quoggles;

import org.graffiti.plugin.GenericPluginAdapter;
import org.graffiti.plugin.algorithm.Algorithm;


/**
 * Provides a spring embedder algorithm a la KK.
 * 
 * @version $Revision: 491 $
 */
public class QPlugin extends GenericPluginAdapter {

    /** Quoggles algorithm */
    private QAlgorithm alg = new QAlgorithm();

    public QPlugin() {
    	this.algorithms = new Algorithm[1];
	    this.algorithms[0] = alg;
    }

}
