import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.BreadthFirstDirectedPaths;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.Queue;

public class SAP {
    private static final int INFINITY = Integer.MAX_VALUE;
    private final Digraph G;  // instance variable for the associated digraph
    
    // constructor takes a digraph (not necessarily a DAG)
    public SAP(Digraph G) {
        this.G = new Digraph(G);         // a deep copy of the digraph
    }
    
    // length of shortest ancestral path between v and w; -1 if no such path
    public int length(int v, int w) {
        return shortest(v, w)[0];
    }

    // a common ancestor of v and w that participates in a shortest ancestral path; -1 if no such path
    public int ancestor(int v, int w) {
        return shortest(v, w)[1];
    }

    // length of shortest ancestral path between any vertex in v and any vertex in w; -1 if no such path
    public int length(Iterable<Integer> v, Iterable<Integer> w) {
        return shortest(v, w)[0];
    }

    // a common ancestor that participates in shortest ancestral path; -1 if no such path
    public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
        return shortest(v, w)[1];
    }

    private int[] shortest(int v, int w) {
        int shortestLength = INFINITY;
        int commonAncestor = -1;
        int[] result = new int[2];
        BreadthFirstDirectedPaths bfsV = new BreadthFirstDirectedPaths(G, v);
        BreadthFirstDirectedPaths bfsW = new BreadthFirstDirectedPaths(G, w);
        for (int i = 0; i < G.V(); i++) {
            if (bfsV.hasPathTo(i) && bfsW.hasPathTo(i)) {
                if ((bfsV.distTo(i) + bfsW.distTo(i)) < shortestLength) {
                    shortestLength = bfsV.distTo(i) + bfsW.distTo(i);
                    commonAncestor = i;
                }
            }
        }
        if (shortestLength == INFINITY) result[0] = -1;
        else                            result[0] = shortestLength;
        result[1] = commonAncestor;
        return result;
    }
    
    private int[] shortest(Iterable<Integer> v, Iterable<Integer> w) {
        int shortestLength = INFINITY;
        int commonAncestor = -1;
        int[] result = new int[2];
        BreadthFirstDirectedPaths bfsV = new BreadthFirstDirectedPaths(G, v);
        BreadthFirstDirectedPaths bfsW = new BreadthFirstDirectedPaths(G, w);
        for (int i = 0; i < G.V(); i++) {
            if (bfsV.hasPathTo(i) && bfsW.hasPathTo(i)) {
                if ((bfsV.distTo(i) + bfsW.distTo(i)) < shortestLength) {
                    shortestLength = bfsV.distTo(i) + bfsW.distTo(i);
                    commonAncestor = i;
                }
            }
        }
        if (shortestLength == INFINITY) result[0] = -1;
        else                            result[0] = shortestLength;
        result[1] = commonAncestor;
        return result;
    }
    
    // do unit testing of this class
    public static void main(String[] args) {
        In in = new In(args[0]);
        Digraph G = new Digraph(in);
        SAP sap = new SAP(G);
        /*
        while (!StdIn.isEmpty()) {
            int v = StdIn.readInt();
            int w = StdIn.readInt();
            int length   = sap.length(v, w);
            int ancestor = sap.ancestor(v, w);
            StdOut.printf("length = %d, ancestor = %d\n", length, ancestor);
        }
        */
        Queue<Integer> v = new Queue<Integer>();
        Queue<Integer> w = new Queue<Integer>();
        w.enqueue(7);
        v.enqueue(2);
        v.enqueue(12); 
        int length   = sap.length(v, w);
        int ancestor = sap.ancestor(v, w);
        StdOut.printf("length = %d, ancestor = %d\n", length, ancestor);
    }
}