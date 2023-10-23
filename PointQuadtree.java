import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * A point quadtree: stores an element at a 2D position,
 * with children at the subdivided quadrants.
 *
 * @author Chris Bailey-Kellogg, Dartmouth CS 10, Spring 2015.
 * @author CBK, Spring 2016, explicit rectangle.
 * @author CBK, Fall 2016, generic with Point2D interface.
 * @author Mason Childers, 23W, creating methods for the PointQuadtree
 */
public class PointQuadtree<E extends Point2D> {
	private E point;							// the point anchoring this node
	private int x1, y1;							// upper-left corner of the region
	private int x2, y2;							// bottom-right corner of the region
	private PointQuadtree<E> c1, c2, c3, c4;	// children

	/**
	 * Initializes a leaf quadtree, holding the point in the rectangle
	 */
	public PointQuadtree(E point, int x1, int y1, int x2, int y2) {
		this.point = point;
		this.x1 = x1; this.y1 = y1; this.x2 = x2; this.y2 = y2;
	}

	// Getters

	public E getPoint() {
		return point;
	}

	public int getX1() {
		return x1;
	}

	public int getY1() {
		return y1;
	}

	public int getX2() {
		return x2;
	}

	public int getY2() {
		return y2;
	}

	/**
	 * Returns the child (if any) at the given quadrant, 1-4
	 * @param quadrant	1 through 4
	 */
	public PointQuadtree<E> getChild(int quadrant) {
		if (quadrant==1) return c1;
		if (quadrant==2) return c2;
		if (quadrant==3) return c3;
		if (quadrant==4) return c4;
		return null;
	}

	/**
	 * Returns whether or not there is a child at the given quadrant, 1-4
	 * @param quadrant	1 through 4
	 */
	public boolean hasChild(int quadrant) {
		return (quadrant==1 && c1!=null) || (quadrant==2 && c2!=null) || (quadrant==3 && c3!=null) || (quadrant==4 && c4!=null);
	}

	/**
	 * Inserts the point into the tree
	 */
	public void insert(E p2) {
		// TODO: YOUR CODE HERE

		// Figure out the quadrant that p2 is in using a method findQuadrant I created in the helper functions section
		int quadrant = findQuadrant(p2);

		// If the quadrant has a child, recursively insert the point in that child
		if (hasChild(quadrant)) getChild(quadrant).insert(p2);

		// If the quadrant has no children, insert point as a child into the quadrant
		else {
			if (quadrant == 1) this.c1 = new PointQuadtree<>(p2, (int)point.getX(), y1, x2, (int)point.getY());
			else if (quadrant == 2) this.c2 = new PointQuadtree<>(p2, x1, y1, (int)point.getX(), (int)point.getY());
			else if (quadrant == 3) this.c3 = new PointQuadtree<>(p2, x1, (int)point.getY(), (int)point.getX(), y2);
			else this.c4 = new PointQuadtree<>(p2, (int)point.getX(), (int)point.getY(), x2, y2);
		}
	}

	/**
	 * Finds the number of points in the quadtree (including its descendants)
	 */
	public int size() {
		// TODO: YOUR CODE HERE

		// size starts at 1
		int num = 1;

		// if there's a child in quadrant 1, 2, 3 or 4 recursively call size on that child
		if (hasChild(1)) num += c1.size();

		if (hasChild(2)) num += c2.size();

		if (hasChild(3)) num += c3.size();

		if (hasChild(4)) num += c4.size();

		return num;
	}

	/**
	 * Builds a list of all the points in the quadtree (including its descendants)
	 */
	public List<E> allPoints() {
		// TODO: YOUR CODE HERE
		List<E> allPoints = new ArrayList<>();
		allPointsHelper(allPoints);
		return allPoints;
	}

	/**
	 * Uses the quadtree to find all points within the circle
	 * @param cx	circle center x
	 * @param cy  	circle center y
	 * @param cr  	circle radius
	 * @return    	the points in the circle (and the qt's rectangle)
	 */
	public List<E> findInCircle(double cx, double cy, double cr) {
		// TODO: YOUR CODE HERE
		List<E> circlePoints = new ArrayList<>();
		findInCircleHelper(circlePoints, cx, cy, cr);
		return circlePoints;
	}

	// TODO: YOUR CODE HERE for any helper methods.

	// Given a point, find what quadrant it's in
	public int findQuadrant(E p2) {
		// Note: if the point is equal to an x or y value, I set it to register as to the right and above respectively

		// Quadrant 1
		if (p2.getX() >= point.getX() && p2.getY() <= point.getY()) return 1;
		// Quadrant 2
		else if (p2.getX() < point.getX() && p2.getY() <= point.getY()) return 2;
		// Quadrant 3
		else if (p2.getX() < point.getX() && p2.getY() > point.getY()) return 3;
		// Else, in quadrant 4
		else return 4;
	}

	public void allPointsHelper(List<E> pointsList) {
		// adds point of the node this was called on
		pointsList.add(point);

		// checks every quadrant for children and calls the same method recursively if there is
		if (hasChild(1)) c1.allPointsHelper(pointsList);

		if (hasChild(2)) c2.allPointsHelper(pointsList);

		if (hasChild(3)) c3.allPointsHelper(pointsList);

		if (hasChild(4)) c4.allPointsHelper(pointsList);
	}

	public void findInCircleHelper(List<E> circlePoints, double cx, double cy, double cr) {
		double rectX1 = getX1();
		double rectX2 = getX2();
		double rectY1 = getY1();
		double rectY2 = getY2();

		double pointX = point.getX();
		double pointY = point.getY();

		// check if circle intersects rectangle
		if (Geometry.circleIntersectsRectangle(cx, cy, cr, rectX1, rectY1, rectX2, rectY2)) {
			// if so, check if the circle intersects the tree's point
			if (Geometry.pointInCircle(pointX, pointY, cx, cy, cr)) {
				// if so, add the point to list of hits
				circlePoints.add(point);
			}
			// recursively call on each child if it exists
			if (hasChild(1)) c1.findInCircleHelper(circlePoints, cx, cy, cr);

			if (hasChild(2)) c2.findInCircleHelper(circlePoints, cx, cy, cr);

			if (hasChild(3)) c3.findInCircleHelper(circlePoints, cx, cy, cr);

			if (hasChild(4)) c4.findInCircleHelper(circlePoints, cx, cy, cr);
		}
	}
}
