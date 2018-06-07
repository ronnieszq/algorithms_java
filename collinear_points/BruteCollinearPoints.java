import java.util.Arrays;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdDraw;

public class BruteCollinearPoints {
    private int n;                               // the number of line segments
    private LineSegment[] lineSegments = new LineSegment[0];
    
    public BruteCollinearPoints(Point[] points) {
        // check for corner cases
        if (points == null) {
            throw new IllegalArgumentException("input is empty!");
        }
        for (Point point : points) {
            if (point == null) throw new IllegalArgumentException("invalid point!");
        }

        // sort the array by coordinates first so that the start and end point 
        // of the line segments can be easily identified later
        Arrays.sort(points);
        // loop through all the points
        for (int i = 0; i < points.length; i++) {
            for (int j = i+1; j < points.length; j++) {
                double slope1 = points[i].slopeTo(points[j]);
                if (slope1 == Double.NEGATIVE_INFINITY) {
                    throw new IllegalArgumentException("repeated point!");
                }
                for (int k = j+1; k < points.length; k++) {
                    double slope2 = points[j].slopeTo(points[k]);
                    if (slope2 == Double.NEGATIVE_INFINITY) {
                        throw new IllegalArgumentException("repeated point!");
                    }
                    if (Double.compare(slope1, slope2) == 0) {
                        for (int m = k+1; m < points.length; m++) {
                            double slope3 = points[k].slopeTo(points[m]);
                            if (slope3 == Double.NEGATIVE_INFINITY) {
                                throw new IllegalArgumentException("repeated point!");
                            }
                            if (Double.compare(slope1, slope3) == 0) {
                                LineSegment ls = new LineSegment(points[i], points[m]);
                                if (n == lineSegments.length) resize(lineSegments.length+1);
                                lineSegments[n++] = ls;
                            }
                        }
                    }
                }
            }
        }
    }
    // resize the array holding the linesegments
    private void resize(int capacity) {
        LineSegment[] temp = new LineSegment[capacity];
        for (int i = 0; i < n; i++) {
            temp[i] = lineSegments[i];
        }
        lineSegments = temp;
    }
    
    public int numberOfSegments() {
        return n;
    }
    
    public LineSegment[] segments() {
        return Arrays.copyOf(lineSegments, numberOfSegments());
    }
    
    // test client
    public static void main(String[] args) {

        // read the n points from a file
        In in = new In(args[0]);
        int n = in.readInt();
        Point[] points = new Point[n];
        for (int i = 0; i < n; i++) {
            int x = in.readInt();
            int y = in.readInt();
            points[i] = new Point(x, y);
        }

        // draw the points
        StdDraw.enableDoubleBuffering();
        StdDraw.setXscale(0, 32768);
        StdDraw.setYscale(0, 32768);
        for (Point p : points) {
            p.draw();
        }
        StdDraw.show();

        // print and draw the line segments
        BruteCollinearPoints collinear = new BruteCollinearPoints(points);
        for (LineSegment segment : collinear.segments()) {
            StdOut.println(segment);
            segment.draw();
        }
        StdDraw.show();
    }
}