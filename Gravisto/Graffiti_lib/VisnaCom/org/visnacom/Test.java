package org.visnacom;

import java.util.Iterator;

import org.visnacom.controller.ViewPanel;
import org.visnacom.model.Node;
import org.visnacom.model.View;
import org.visnacom.sugiyama.AffinLinearAnimation;
import org.visnacom.sugiyama.SugiyamaDrawingStyle;
import org.visnacom.sugiyama.eval.RunTests;
import org.visnacom.sugiyama.eval.RandomGraphs.CreationError;
import org.visnacom.sugiyama.test.TestGraph1;
import org.visnacom.sugiyama.test.TestGraphPresentation;
import org.visnacom.view.Geometry;


/**
 * 
 * 
 *  
 */

public class Test {

	//~ Methods

	// ================================================================

	/**
	 * 
	 * 
	 * 
	 * 
	 * 
	 * @param arg
	 * 
	 * DOCUMENT ME!
	 *  
	 */

	public static void main(String[] arg) throws CreationError {

		ViewPanel viewPanel = Visnacom.main_test();

		View c = viewPanel.getView();

		Geometry geo = viewPanel.getGeometry();

		new TestGraphPresentation().fillCompoundGraph(c);

		//        Geometry geo = new Geometry(null);

		//        View c = geo.getView();

		//        geo.setDrawingStyle(new DummyDrawingStyle(geo));

		//        RandomGraphs.constructRandomGraph(c, 20, 0.04, 3.0, 0.7, 1033096058);

		for (Iterator it = geo.getView().getChildrenIterator(

		geo.getView().getRoot()); it.hasNext();) {

			RunTests.contractAll(geo.getView(), (Node) it.next());

		}

		geo.setDrawingStyle(new SugiyamaDrawingStyle(geo));

		viewPanel.setAnimationStyle(new AffinLinearAnimation(geo, viewPanel));
		viewPanel.getPrefs().animation = "linear";
		viewPanel.getPrefs().curveType = "polyline";

		c.expand((Node) c.getAllNodes().get(4));
		c.expand((Node) c.getAllNodes().get(8));
		
	}
}