import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.MinPQ;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;


public class Solver {
    private class Node implements Comparable<Node> {
        private int moves;
        private boolean isTwin;
        private Board board;
        private Node predecessor;
        private int manhattan;
        
        public Node(Board board, Node predecessor, int moves, boolean isTwin) {
            this.board = board;
            this.predecessor = predecessor;
            this.moves = moves;
            this.isTwin = isTwin;
            // cache the manhattan distance for a node so we don't need to calculate it every time
            this.manhattan = board.manhattan();
        }
        
        public int compareTo(Node that) {
            // when two nodes have the same priority,
            // break tie by comparing the manhattan distance
            if ((this.manhattan + this.moves) == (that.manhattan + that.moves)) {
                return this.manhattan - that.manhattan;
            }
            // order by priority
            else {
                return this.manhattan + this.moves - that.manhattan - that.moves;
            }
        }
    }
    
    private MinPQ<Node> pq = new MinPQ<Node>();
    private int moves;                               // number of moves since the initial search node
    private boolean isSolvable = false;
    private Stack<Board> solutionStack = new Stack<Board>(); // solution stack (LIFO)
    
    public Solver(Board initial) {
        // first, insert the initial search node and its twin into a priority queue
        pq.insert(new Node(initial, null, 0, false));
        pq.insert(new Node(initial.twin(), null, 0, true));
        
        while (!pq.isEmpty()) {
            // pop the search node with the lowest priority from the queue
            Node current = pq.delMin();
            // StdOut.println(current.board.hamming());
            if (current.board.isGoal()) {
                if (current.isTwin)
                    this.moves = -1;            // the initial board is not solvable
                else {
                    this.isSolvable = true;
                    this.moves = current.moves;
                    reconstructPath(current);  // reonstruct the solution
                }
                break;
            }
            
            for (Board neighbor : current.board.neighbors()) {
                // we don't enqueue a neighbor if its board is the same
                // as the board of the predecessor search node
                if (current.predecessor == null || !neighbor.equals(current.predecessor.board)) {
                    pq.insert(new Node(neighbor, current, current.moves+1, current.isTwin));
                }
            }
        }
    }
    
    private void reconstructPath(Node node) {        
        this.solutionStack.push(node.board);
        while (node.predecessor != null) {
            node = node.predecessor;
            this.solutionStack.push(node.board);
        }
    }
    
    public boolean isSolvable() {
        return this.isSolvable;
    }
    
    public int moves() {
        return this.moves;
    }
    
    public Iterable<Board> solution() {
        if (this.isSolvable) return this.solutionStack;
        else                 return null;
    }
    
    public static void main(String[] args) {

        // create initial board from file
        In in = new In(args[0]);
        int n = in.readInt();
        int[][] blocks = new int[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                blocks[i][j] = in.readInt();
        Board initial = new Board(blocks);

        // solve the puzzle
        Solver solver = new Solver(initial);

        // print solution to standard output
        if (!solver.isSolvable())
            StdOut.println("No solution possible");
        else {
            StdOut.println("Minimum number of moves = " + solver.moves());
            for (Board board : solver.solution())
                StdOut.println(board);
        }
    }
}