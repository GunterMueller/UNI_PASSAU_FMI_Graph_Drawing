package crossing;
import java.lang.Math;


/**
 * Stores line segments. Consists of startpoint, endpoint and a calculated
 * gradient. A Segment starts always lies above its endpoint. In a case where
 * this order is not given, the boolean inverted is set to true.
 *
 * @author Daniel Hanisch
 */
public class Segment implements Comparable<Segment>{
	
	private Coordinate start;		
	private Coordinate end;			
	private double gradient;
    private boolean inverted;
	
	/** Accuracy of round-method (decimal place count) */
	public static final double factor = 100000.0;    
	
	/**
	 * Constructor. Creates a new Segment starting at s1, ending at s2.
	 * Also calculates gradient of segment. Sets inverted status.
	 * @param s1 startpoint
	 * @param s2 endpoint
	 */
	public Segment(Coordinate s1, Coordinate s2, boolean inv) {
		this.updateCoordinate(s1);
		this.setEnd(s2);	
        this.setInverted(inv);
		this.setGradient();
		}	

	/**
     * Returns startpoint of this Segment
	 * @return startpoint
	 */
	public Coordinate getCoordinate() {
		return start;
	}
	
	/**
     * Returns a key for navigation in TreeNode  
	 * @return TreeKey consisting of startpoint and gradient
	 */
	public TreeKey getKey() {
		return new TreeKey(start, gradient);
	}
	
	/**
	 * Calculates the gradient of the segment using 
	 * 2-point normal form.
	 * Horizontal segments have a gradient of 0.
	 * Vertical segments have a gradient of -infinity
	 */
	public void setGradient() {
		double xS = start.getXCoord();
		double yS = start.getYCoord();
		double xE = end.getXCoord();
		double yE = end.getYCoord();
		double a = yE-yS;
		double b = xE-xS;
		gradient = a/b;
		
		if (Math.abs(b) < 10/Segment.factor) {
			gradient = Double.NEGATIVE_INFINITY;		
		}
		if (Math.abs(a) < 10/Segment.factor) {
			gradient = 0;
		}						
	}
	
	/**
	 * Calculates intersection coordinate of two Segments.
	 * If intersection occurs outside of segment length, or if both gradients
	 * are equal, null is returned. 
	 * @param seg 
	 * @return coordinate intersection 
	 */
	public Coordinate calculateIntersection(Segment seg) {
		
		// Start- and Endpoint of first segment
		Coordinate A1 = this.getCoordinate();
		Coordinate B1 = this.getEnd();
		Coordinate A2 = seg.getCoordinate();
		Coordinate B2 = seg.getEnd();

		double xA1 = A1.getXCoord();		
		double yA1 = A1.getYCoord();
		double xB1 = B1.getXCoord();
		double yB1 = B1.getYCoord();
		
		// factors for y1=m1*x+t1 of first segement 
		double m1 = (yB1 - yA1) / (xB1 - xA1); 
		double t1 = -(xA1 * m1) + yA1;
		
		//Start- and Endpoint of second segment
		double xA2 = A2.getXCoord();
		double yA2 = A2.getYCoord();
		double xB2 = B2.getXCoord();
		double yB2 = B2.getYCoord(); 
		
		// factors for y2=m2*x+t2 of second segement  	
		double m2 = (yB2 - yA2) / (xB2 - xA2);
		double t2 = -(xA2 * m2) + yA2;
		
		// equation y1=y2 resolved to x (rounded)
		double x = Math.round(((t2-t1)/(m1-m2))*factor)/factor;
		double y = Math.round((m1*x+t1)*factor)/factor;
		
		Double temp1 = new Double(m1);
		Double temp2 = new Double(m2);
		
		double minY;
		double maxY;
		
		Coordinate candidate = null;
		Coordinate boundary = getMinMax(yA1,yB1,yA2,yB2);
		
		// check if one or both gradients are infinite
		if (temp1.isInfinite()) {
			if (temp2.isInfinite()) return null;
			else { 			
				candidate = new 
                    Coordinate(xA1, Math.round((m2*xA1 + t2)*factor)/factor);
				if (checkBoundary(candidate,boundary))
					return  candidate;
				else return null;
			}
		} 
		if (temp2.isInfinite()) {
			candidate = new 
                Coordinate (xA2, Math.round((m1*xA2+t1)*factor)/factor);
			if (checkBoundary(candidate,boundary))
				return candidate;
			else return null;
		} else {
			candidate = new Coordinate(x,y);
			if (checkBoundary(candidate,boundary))
				return candidate;
			else return null;				
		}
	}
	

	/**
     * Calculates x-value of this Segment at a given y-value (parameter value).
     * Used to determine in which order the sweep line passes segments. 
	 * @param value y-value for which x-value is calculated
	 * @return calculated x-value
	 */
	public double calculatePosition (double value) {
		return (value - getCoordinate().getYCoord() + 
                getGradient()*getCoordinate().getXCoord())/getGradient();
	}

	/**
	 * Calculates upper and lower y-axis boundary in which an intersection
	 * between two segments could occur.
	 * @param start1 y-value of startpoint of first segment 
	 * @param end1	  y-value of endpoint of first segment	
	 * @param start2 y-value of startpoint of second segment
	 * @param end2   y-value of endpoint of first segment
	 * @return Boundary with max/min y-value an intersection could have
	 */
	public Coordinate getMinMax(double start1, double end1, double start2, 
            double end2) {
		if (start1 <= start2) {
			if (end1 >= end2) return new Coordinate(start1,end1);
			else return new Coordinate(start1, end2);
		} else if (end1 >= end2) return new Coordinate(start2, end1);
			else return new Coordinate(start2, end2);
	}
	
	/**
	 * checks if calculated intersection occurs within the two segments
	 * @param cand  possible intersection
	 * @param bound  boundary in which an intersection could occur
	 * @return true, if intersection is within segment length
	 */
	public boolean checkBoundary(Coordinate cand, Coordinate bound) {
		if ((cand.getYCoord() <= bound.getXCoord()) & 
			(cand.getYCoord() >= bound.getYCoord()))
			return true;
		else return false;	
	}
	
	/**
	 * change start coordinate when at an intersection
	 * @param c new start coordinate
	 */
	public void updateCoordinate(Coordinate c) {
		start = c;	
	}

	/**
	 * Returns endpoint of segment
	 * @return endpoint
	 */
	public Coordinate getEnd() {
		return end;
	}


	/**
	 * Sets endpoint of segment
	 * @param coordinate
	 */
	public void setEnd(Coordinate coordinate) {
		end = coordinate;
	}

	/**
	 * Returns gradient of segment
	 * @return gradient
	 */
	public double getGradient() {
		return gradient;
	}

	
	/**
	 * Compares to Segments (at a specific EventPoint).
	 * A Segment is considered bigger, if the sweep line passes this segment
	 * prior to the compared segment o.
	 * 
	 * @param o - compared Segment 
	 * @return -1, if sweep line passes this segement first
	 * 			 1, if sweep line passes segment o first
	 * 			 0, if both have same gradient
	 */
	public int compareTo(Segment o) {
		if (this.getGradient() <= 0.0 & o.getGradient() <= 0.0) {
			if (this.getGradient() < o.getGradient()) return 1;
			if (this.getGradient() > o.getGradient()) return -1;
			else return 0;			
		} 
		if (this.getGradient() > 0 & o.getGradient() > 0) {
			if (this.getGradient() < o.getGradient()) return 1;
			if (this.getGradient() > o.getGradient()) return -1;
			else return 0;			
		} else {
			if (this.getGradient() == 0.0) return -1;
			if (o.getGradient() == 0.0) return 1;
			if (this.getGradient() < o.getGradient()) return -1;
			if (this.getGradient() > o.getGradient()) return 1;
			else return 0;
		}	
	}	
    /**
     * Returns direction of the segment. If a Segments' startpoint lies below
     * its endpoint, the segment is inverted.
     * @return true, if the segment is inverted
     */
    public boolean isInverted() {
        return inverted;
    }
    /**
     * Sets the direction of the segment
     * @param inverted - Set the inverted status of the segment.
     */
    public void setInverted(boolean inverted) {
        this.inverted = inverted;
    }
    
    /**
     * Overrides java.lang.Object#hashCode()
     * @return hash of concatenated string consisting of start- and endpoint
     */
    public int hashCode() {
        String hash = ""+start.hashCode()+end.hashCode();
        return hash.hashCode();
    }
    
    /** 
     * Compares two Segments
     * Overrides java.lang.Object#equals(java.lang.Object)
     * @param obj compared segment
     * @return true, if both segments have the same hashcode
     */
    public boolean equals(Object obj) {
        try {
            Segment tmp = (Segment) obj;
            return (this.hashCode()==tmp.hashCode());
        } catch (Exception e) {
            return false;
        }
    }
}
