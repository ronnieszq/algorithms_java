/******************************************************************************
 *  Compilation:  javac Point.java
 *  Execution:    java Point
 *  Dependencies: none
 *  
 *  An immutable data type for points in the plane.
 *  For use on Coursera, Algorithms Part I programming assignment.
 *
 ******************************************************************************/

import java.util.Comparator;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;
import java.util.Arrays;

public class Point implements Comparable<Point> {

    private final int x;     // x-coordinate of this point
    private final int y;     // y-coordinate of this point

    /**
     * Initializes a new point.
     *
     * @param  x the <em>x</em>-coordinate of the point
     * @param  y the <em>y</em>-coordinate of the point
     */
    public Point(int x, int y) {
        /* DO NOT MODIFY */
        this.x = x;
        this.y = y;
    }

    /**
     * Draws this point to standard draw.
     */
    public void draw() {
        /* DO NOT MODIFY */
        StdDraw.point(x, y);
    }

    /**
     * Draws the line segment between this point and the specified point
     * to standard draw.
     *
     * @param that the other point
     */
    public void drawTo(Point that) {
        /* DO NOT MODIFY */
        StdDraw.line(this.x, this.y, that.x, that.y);
    }

    /**
     * Returns the slope between this point and the specified point.
     * Formally, if the two points are (x0, y0) and (x1, y1), then the slope
     * is (y1 - y0) / (x1 - x0). For completeness, the slope is defined to be
     * +0.0 if the line segment connecting the two points is horizontal;
     * Double.POSITIVE_INFINITY if the line segment is vertical;
     * and Double.NEGATIVE_INFINITY if (x0, y0) and (x1, y1) are equal.
     *
     * @param  that the other point
     * @return the slope between this point and the specified point
     */
    public double slopeTo(Point that) {
        /* YOUR CODE HERE */
        double slope;
        if (this.y == that.y) {
            if (this.x == that.x) slope = Double.NEGATIVE_INFINITY;
            else                  slope = +0.0;
        }
        else {
            if (this.x == that.x) slope = Double.POSITIVE_INFINITY;
            else                  slope = (double) (that.y - this.y) / (that.x - this.x);
        }
        return slope;
    }

    /**
     * Compares two points by y-coordinate, breaking ties by x-coordinate.
     * Formally, the invoking point (x0, y0) is less than the argument point
     * (x1, y1) if and only if either y0 < y1 or if y0 = y1 and x0 < x1.
     *
     * @param  that the other point
     * @return the value <tt>0</tt> if this point is equal to the argument
     *         point (x0 = x1 and y0 = y1);
     *         a negative integer if this point is less than the argument
     *         point; and a positive integer if this point is greater than the
     *         argument point
     */
    public int compareTo(Point that) {
        /* YOUR CODE HERE */
        if (this.y < that.y) return -1;
        if (this.y > that.y) return 1;
        if (this.x < that.x) return -1;
        if (this.x > that.x) return 1;
        return 0;
    }

    /**
     * Compares two points by the slope they make with this point.
     * The slope is defined as in the slopeTo() method.
     *
     * @return the Comparator that defines this ordering on points
     */
    public Comparator<Point> slopeOrder() {
        /* YOUR CODE HERE */
        Comparator<Point> SLOPE_ORDER = new SlopeOrder();
        return SLOPE_ORDER;
    }
    
    /**
     * Private class needed to implements the Comparator<Point> interface.
     * The compare method is overriden by compare(q1, q2) that compares 
     * the slopes that q1 and q2 make with the invoking object p
     * 
     * @ return the ordering on points
     */
    private class SlopeOrder implements Comparator<Point> {
        // override the compare method
        public int compare(Point q1, Point q2) {
            double slope1 = Point.this.slopeTo(q1);
            double slope2 = Point.this.slopeTo(q2);
            
            if      (Double.compare(slope1, slope2) == -1) return -1;
            else if (Double.compare(slope1, slope2) == 1)  return 1;
            else     return 0;
        }
    }

    /**
     * Returns a string representation of this point.
     * This method is provide for debugging;
     * your program should not rely on the format of the string representation.
     *
     * @return a string representation of this point
     */
    public String toString() {
        /* DO NOT MODIFY */
        return "(" + x + ", " + y + ")";
    }

    /**
     * Unit tests the Point data type.
     */
    public static void main(String[] args) {
        /* YOUR CODE HERE */
        Point p = new Point(4096, 22016);
        Point q1 = new Point(4096, 25088);
        Point q2 = new Point(4096, 20992);
        Point q3 = new Point(4096, 23040);
        Point q4 = new Point(4096, 24064);
        /*
        Point[] points = new Point[4];
        points[0] = q1;
        points[1] = q2;
        points[2] = q3;
        points[3] = q4;
        Arrays.sort(points);
        */
        Point.SlopeOrder so = p.new SlopeOrder();
        StdOut.println(p.slopeTo(q4));
        StdOut.println(p.slopeTo(q3));
        StdOut.println(p.slopeTo(q2));
        StdOut.println(Double.compare(p.slopeTo(q4), p.slopeTo(q3)));
        StdOut.println(q2.compareTo(q1));
        StdOut.println(so.compare(q1, q3));
        /*
        for (Point point : points) {
            StdOut.println(point.x + " " + point.y);
        }
        */
    }
}
