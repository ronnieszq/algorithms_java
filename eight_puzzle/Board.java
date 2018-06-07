import java.util.Arrays;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

public class Board {
    private final char[] blocks; // immutable character representation of the block of integers
    private final int dim;       // the dimension of the board
    private final int length;    // length of the array representation
    private int emptyRow;        // row index of the blank block
    private int emptyCol;        // column index of the blank block
    
    public Board(int[][] blocks) {
        // use a length n^2 char[] array as a copy of the input n-by-n integer array
        // to reduce the amount of memory and make the data type immutable
        this.dim = blocks.length;
        this.length = this.dim * this.dim;
        this.blocks = new char[this.length];
        // make a copy of the input blocks
        for (int i = 0; i < this.length; i++) {
            this.blocks[i] = (char) blocks[i / this.dim][i % this.dim];
            if (blocks[i / this.dim][i % this.dim] == 0) {
                // get the location of the blank block
                emptyRow = i / this.dim;
                emptyCol = i % this.dim;
            }
        }
    }

    public int dimension() {
        return this.dim;
    }

    public int hamming() {
        int hamming = 0;
        for (int i = 0; i < this.length; i++) {
            // minus one to account for the fact that array starts from 0
            if ((int) this.blocks[i]-1 != i && (int) this.blocks[i] != 0) hamming += 1;
        }
        return hamming;
    }
    
    public int manhattan() {
        int manhattan = 0;
        for (int i = 0; i < this.length; i++) {
            if ((int) this.blocks[i] != 0) {
                // minus one to account for the fact that array starts from 0
                manhattan += Math.abs(((int) this.blocks[i]-1) / this.dim - i / this.dim);
                manhattan += Math.abs(((int) this.blocks[i]-1) % this.dim - i % this.dim);
            }
        }
        return manhattan;
    }
    
    public boolean isGoal() {
        return manhattan() == 0;
    }
    
    public Board twin() {
        char[] twin = Arrays.copyOf(this.blocks, this.length);
        if (emptyRow == 0) exch(twin, this.dim, this.dim+1);
        else               exch(twin, 0, 1);
        return new Board(reshape(twin));
    }
    
    private void exch(char[] a, int i, int j) {
        char t = a[i];
        a[i] = a[j];
        a[j] = t;
    }
    
    @Override
    public boolean equals(Object y) {
        if (y == this) return true;
        if (y == null) return false;
        if (y.getClass() != this.getClass()) return false;
        Board that = (Board) y;
        return Arrays.equals(this.blocks, that.blocks);
    }
    
    // reshape the flattened array to 2d
    private int[][] reshape(char[] blocks1D) {
        int[][] blocks2D = new int[this.dim][this.dim];
        for (int i = 0; i < blocks1D.length; i++) {
            blocks2D[i / this.dim][i % this.dim] = (int) blocks1D[i];
        }
        return blocks2D;
    }
    
    public Iterable<Board> neighbors() {
        Queue<Board> neighbors = new Queue<Board>();
        // make a copy of the array
        char[] neighbor = new char[this.length];
        // Swap with upper block (row-1)
        if (emptyRow > 0) {
            neighbor = Arrays.copyOf(this.blocks, this.length);
            exch(neighbor, emptyRow*this.dim+emptyCol, (emptyRow-1)*this.dim+emptyCol);
            neighbors.enqueue(new Board(reshape(neighbor)));
        }
        // Swap with lower block (row+1)
        if (emptyRow < this.dim-1) {
            neighbor = Arrays.copyOf(this.blocks, this.length);
            exch(neighbor, emptyRow*this.dim+emptyCol, (emptyRow+1)*this.dim+emptyCol);
            neighbors.enqueue(new Board(reshape(neighbor)));
        }
        // Swap with left block (col-1)
        if (emptyCol > 0) {
            neighbor = Arrays.copyOf(this.blocks, this.length);
            exch(neighbor, emptyRow*this.dim+emptyCol, emptyRow*this.dim+emptyCol-1);
            neighbors.enqueue(new Board(reshape(neighbor)));
        }
        // Swap with right block (col+1)
        if (emptyCol < this.dim-1) {
            neighbor = Arrays.copyOf(this.blocks, this.length);
            exch(neighbor, emptyRow*this.dim+emptyCol, emptyRow*this.dim+emptyCol+1);
            neighbors.enqueue(new Board(reshape(neighbor)));
        }
        return neighbors;
    }
    
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append(this.dim + "\n");
        for (int i = 0; i < length; i++) {
            s.append(String.format("%2d ", (int) blocks[i]));
            if ((i+1) % this.dim == 0) s.append("\n");
        }
        return s.toString();
    }

    public static void main(String[] args) {
        // read inputs from a file
        In in = new In(args[0]);
        int n = in.readInt();
        int[][] blocks = new int[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                blocks[i][j] = in.readInt();
            }
        }
        Board board = new Board(blocks);
        StdOut.println(board);
        StdOut.println(board.twin());
        StdOut.println(board.hamming());
        StdOut.println(board.manhattan());
        StdOut.println(board.isGoal());
        for (Board neighbor : board.neighbors()) {
            StdOut.println(neighbor);
        }
    }
}