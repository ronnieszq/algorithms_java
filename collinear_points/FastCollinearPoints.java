import java.util.Arrays;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdDraw;


public class FastCollinearPoints {
    private int n;                               // the number of line segments
    private LineSegment[] lineSegments = new LineSegment[0];
    
    public FastCollinearPoints(Point[] points) {
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
        
        // treat each point as origin, sort all the points by the slope they
        // make with the origin
        for (int i = 0; i < points.length; i++) {
            /*
            for (int j = 0; j < points.length; j++) {
                otherPoints[j] = points[j];
            }
            */
            Point[] otherPoints = Arrays.copyOf(points, points.length);
            Arrays.sort(otherPoints, points[i].slopeOrder());
            // check if 4 or more adjacent points have the same slope
            // start from the second point as the first is always itself
            // keep a counter c
            int c = 2;
            for (int k = 1; k < otherPoints.length-1; k++) {
                // check for repeated point
                if (points[i].slopeTo(otherPoints[k]) == Double.NEGATIVE_INFINITY) {
                    throw new IllegalArgumentException("repeated point!");
                }
                double slopeCurr = points[i].slopeTo(otherPoints[k]);
                double slopeNext = points[i].slopeTo(otherPoints[k+1]);
                if (Double.compare(slopeCurr, slopeNext) == 0) {
                    c++;
                    if (k == otherPoints.length-2 && c >= 4) {
                        if (points[i].compareTo(otherPoints[k-c+3]) == -1) {
                            LineSegment ls = new LineSegment(points[i], otherPoints[k+1]);
                            if (n == lineSegments.length) resize(lineSegments.length+1);
                            lineSegments[n++] = ls;
                        }
                    }
                }
                else {
                    // whenever slope differs or reach the end, check if c >= 4
                    if (c >= 4) {
                        if (points[i].compareTo(otherPoints[k-c+2]) == -1) {
                            LineSegment ls = new LineSegment(points[i], otherPoints[k]);
                            if (n == lineSegments.length) resize(lineSegments.length+1);
                            lineSegments[n++] = ls;
                        }
                    }
                    c = 2;
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
        FastCollinearPoints collinear = new FastCollinearPoints(points);
        for (LineSegment segment : collinear.segments()) {
            StdOut.println(segment);
            segment.draw();
        }
        StdDraw.show();
    }
}