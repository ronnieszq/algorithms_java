import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;

public class KdTree {
    private Node root;             // root of KdTree
    private int size;              // size of KdTree
    private Queue<Point2D> queue = new Queue<Point2D>();

    private static class Node {
        private Point2D p;         // the point
        private RectHV rect;       // the axis-aligned rectangle corresponding to this node
        private Node lb;           // the left/bottom subtree
        private Node rt;           // the right/top subtree

        public Node(Point2D p, RectHV rect) {
            this.p = p;
            this.rect = rect;
        }
    }
    
    /**
     * Initializes an empty tree.
     */
    public KdTree() {
    }    
    
    /**
     * Returns true if this KdTree is empty.
     * @return true if this KdTree is empty; false otherwise
     */
    public boolean isEmpty() {
        return size() == 0;
    }

    /**
     * Returns the number of points in this KdTree.
     * @return the number of points in this KdTree
     */
    public int size() {
        return size;
    }
    
    /**
     * Inserts the specified point into the KdTree.
     *
     * @param  p the point
     * @throws IllegalArgumentException if the point is null
     */
    public void insert(Point2D p) {
        if (p == null) throw new IllegalArgumentException("calls insert() with a null point");
        // start from the root node; 
        root = put(root, p, new RectHV(0.0, 0.0, 1.0, 1.0), true);      
    }

    /**
     * A helper function that recursively finds the node to insert a new point.
     *
     * @param  n the current node
     * @param  p the point
     * @param  ort the orientation of the current node (vertical or horizontal)
     * @return the new node where the point is inserted
     */
    private Node put(Node x, Point2D p, RectHV rect, boolean ort) {
        // add the node
        if (x == null) {
            queue.enqueue(p);
            size++;                    // increment the size of the KdTree by 1
            return new Node(p, rect);
        }
        if (x.p.equals(p)) return x;   // for repetitive point, return the node
        
        double cmp;
        if (ort) {
            RectHV lRect = new RectHV(x.rect.xmin(), x.rect.ymin(), x.p.x(), x.rect.ymax());
            RectHV rRect = new RectHV(x.p.x(), x.rect.ymin(), x.rect.xmax(), x.rect.ymax());
            cmp = p.x() - x.p.x();     // if vertial, compare x-coordinates
            if (cmp < 0) x.lb = put(x.lb, p, lRect, !ort);
            else         x.rt = put(x.rt, p, rRect, !ort);
        }
        else {
            RectHV bRect = new RectHV(x.rect.xmin(), x.rect.ymin(), x.rect.xmax(), x.p.y());
            RectHV tRect = new RectHV(x.rect.xmin(), x.p.y(), x.rect.xmax(), x.rect.ymax());
            cmp = p.y() - x.p.y();     // if horizontal, compare y-coordinates
            if (cmp < 0) x.lb = put(x.lb, p, bRect, !ort);
            else         x.rt = put(x.rt, p, tRect, !ort);
        }
        return x;
    }
    
    /**
     * Does this KdTree contain the given point?
     *
     * @param  p the point
     * @return true if this KdTree contains the point and
     *         false otherwise
     * @throws IllegalArgumentException if point is null
     */
    public boolean contains(Point2D p) {
        if (p == null) throw new IllegalArgumentException("argument to contains() is null");
        return get(root, p, true) != null;
    }

    /**
     * A helper function that recursively checks if the point is in the KdTree
     *
     * @param  n the current node
     * @param  p the point
     * @param  ort the orientation of the current node (vertical or horizontal)
     * @return the node where the point is found;
     *         null if not found in the KdTree
     */
    private Node get(Node x, Point2D p, boolean ort) {
        if (x == null) return null;
        
        double cmp;
        if (ort) cmp = p.x() - x.p.x();      // if vertial, compare x-coordinates
        else     cmp = p.y() - x.p.y();      // if horizontal, compare y-coordinates
        // flip the orientation everytime
        if (cmp < 0)                    return get(x.lb, p, !ort);
        else if (p.compareTo(x.p) != 0) return get(x.rt, p, !ort);
        else                            return x;
    }
    
    /**
     * Draw all points to standard draw
     */
    public void draw() {
        /**
         * PenColor and PenRadius are set in the test client
         * StdDraw.setPenColor(StdDraw.BLACK);
         * StdDraw.setPenRadius(0.01);
         */
        for (Point2D pt : queue) {
            pt.draw();
        }
    }
    

    /**
     * Returns all points in the KdTree inside the rectangle (or on the boundary),
     * as an Iterable.
     *
     * @param  rect the rectangle against which points are checked
     * @return all points in the KdTree inside the rectangle (or on the boundary)
     * @throws IllegalArgumentException if the rectangle is null
     */
    public Iterable<Point2D> range(RectHV rect) {
        if (rect == null) throw new IllegalArgumentException("the range is null");

        Queue<Point2D> ptQueue = new Queue<Point2D>();
        range(root, ptQueue, rect);
        return ptQueue;
    } 

    private void range(Node x, Queue<Point2D> ptQueue, RectHV rectRange) { 
        if (x == null) return;
        // explore the node iif the range intersects with the node rectangle
        if (x.rect.intersects(rectRange)) {
            // check if the point is in the range
            if (rectRange.contains(x.p)) ptQueue.enqueue(x.p);
            range(x.lb, ptQueue, rectRange);
            range(x.rt, ptQueue, rectRange);
        }
        else return;
    }

    /**
     * Find the nearest neighbor in the KdTree to point p
     * Return null if the KdTree is empty
     * @param  p the point against which all points in the KdTree are checked
     * @return a point in the KdTree that is the nearest neighbor of point p
     */
    public Point2D nearest(Point2D p) {
        if (p == null) throw new IllegalArgumentException("the point is null");
        if (root == null) return null;
        else return nearest(root, p, root.p);
    }

    private Point2D nearest(Node x, Point2D p, Point2D np) { 
        if (x == null) return np;
        double nDist = np.distanceSquaredTo(p);      // distance from the nearest point to the query point
        double rDist = x.rect.distanceSquaredTo(p);  // distance from this rectangle to the query point
        double pDist = x.p.distanceSquaredTo(p);     // distance from this point to the query point
        // explore this node iif the rectangel is nearer than the nearest point
        if (rDist < nDist) {
            Point2D nstPt = np;
            // compare this point with the nearest point so far
            if (pDist < nDist) nstPt = x.p;
            // check both subtrees
            if (x.lb != null && x.rt != null) {
                double lbDist = x.lb.rect.distanceSquaredTo(p);   // distance from the left subtree
                double rtDist = x.rt.rect.distanceSquaredTo(p);   // distance from the right subtree
                if (lbDist < rtDist) return nearest(x.rt, p, nearest(x.lb, p, nstPt));
                else                 return nearest(x.lb, p, nearest(x.rt, p, nstPt));
            }
            else if (x.lb == null && x.rt == null) return nstPt;
            else if (x.lb == null)                 return nearest(x.rt, p, nstPt);
            else                                   return nearest(x.lb, p, nstPt);
        }
        else return np;
    }
    
    /**
     * Unit tests the KdTree data type.
     */
    public static void main(String[] args) {
        KdTree tree = new KdTree();
        Point2D p1 = new Point2D(0.0, 1.0);
        tree.insert(p1);
        StdOut.println(tree.size());
        Point2D p2 = new Point2D(0.0, 0.0);
        tree.insert(p2);
        StdOut.println(tree.size());
        Point2D p3 = new Point2D(0.0, 0.0);
        tree.insert(p3);
        StdOut.println(tree.size());
        
        StdOut.println(tree.isEmpty());
        StdOut.println(tree.contains(p1));
        StdDraw.setPenRadius(0.02);
        tree.draw();
    }
}