package crossing;


/**
 * Key of a segment used for navigation and construction of TreeNode.
 * Consists of startpoint and gradient of the segement.
 * The order in which the sweep line passes the segments is calculated by
 * the relative position of the segment compared to other segments.
 * Segments with lower keys are passed first.
 *
 * @author Daniel Hanisch
 */
public class TreeKey implements Comparable<TreeKey> {
    
	Coordinate keyC;
	double keyGradient;

	/**
	 * Constructor. Creates a new TreeKey from a startpoint and gradient
	 * @param c startpoint of segment
	 * @param a gradient of segment
	 */
	public TreeKey(Coordinate c, double a) {
		keyC = c;
		keyGradient = a;
	}

		
	/**
	 * Compares x-values of two segments on y-level of current EventPoint.
	 * The x-value of second (key) segment is calculated with the value 
	 * checkPoint.
	 * If the checkPoint lies to the left of the current point, the sweep line
	 * passes the second (key) segment first.
	 * @param key compared TreeKey
	 * @return -1: sweepline passes current segment before key segment
	 * 			 1: sweepline passes current segment after key segment
	 * 			 0: both keys/segments are equal
	 */
	public int compareTo(TreeKey key) {
		double xOld = keyC.getXCoord();
		double yOld = keyC.getYCoord();
		double xNew = key.getCoordinate().getXCoord();
		double yNew = key.getCoordinate().getYCoord();
		double checkPoint = 0.0;
		boolean delete = false;
		switch (keyC.compareTo(key.keyC)) {
        // compared coordinate lies below (or on the same level and to the 
        // right) of current coordinate
		case -1:            
			if (keyGradient != Double.NEGATIVE_INFINITY) {
				checkPoint = (yNew - yOld + keyGradient * xOld) / keyGradient;				
			} else {				
				// vertical Segment
				if (this.keyC.getXCoord() < key.keyC.getXCoord())
					return 1;
				else return -1;
			}
			break;
		// compared coordinate lies above current coordinate
		// only the case if a segment is deleted
		case 1:
			if (key.keyGradient != Double.NEGATIVE_INFINITY) {
				checkPoint = (yOld - yNew + key.keyGradient * xNew)/
				                key.keyGradient;
				xNew = keyC.getXCoord();
				delete = true;
			} else {
				if (this.keyC.getXCoord() < key.keyC.getXCoord())
					return 1;
				else return -1;				
			}
			break;
		}
		// if both coordinates are the same,their gradients have to be compared
		if (this.keyC == key.keyC) {
			if (keyGradient * key.keyGradient > 0) {
				if (keyGradient < key.keyGradient)
					return 1;
				if (keyGradient > key.keyGradient)
					return -1;
				else
					return 0;
			}
			if (keyGradient * key.keyGradient < 0) {
				if (keyGradient < 0)
					return -1;
				if (keyGradient > 0)
					return 1;
				else
					return 0;
            // horizontal segments
			} else {
				if (keyGradient == 0.0 & key.keyGradient == 0.0)
					return 0;
				if (keyGradient == 0.0)
					return -1;
				else
					return 1;
			}
		}

		if (!delete) {
			if (checkPoint < xNew)
				return 1;
			if (checkPoint > xNew)
				return -1;
		} else {
			if (checkPoint < xNew)
				return -1;
			if (checkPoint > xNew)
				return 1;
		}

		// horizontal Segments
		if (this.keyGradient == 0.0)
			return -1;
		if (key.keyGradient == 0.0)
			return 1;

		// should not be reached
		System.out.println("RETURNED -2");
		return -2;
	}

	/**
	 * Returns the keyGradient
	 * @return keyGradient
	 */
	public double getGradient() {
		return keyGradient;
	}

	/**
	 * Sets the keyGradient
	 * @param keyGradient
	 *            
	 */
	public void setGradient(double keyGradient) {
		this.keyGradient = keyGradient;
	}

	/**
	 * Returns the keyCoordinate
	 * @return keyC
	 */
	public Coordinate getCoordinate() {
		return keyC;
	}

	/**
	 * Sets the keyCoordinate
	 * @param keyC           
	 */
	public void setCoordinate(Coordinate keyC) {
		this.keyC = keyC;
	}
}
