import edu.princeton.cs.algs4.WeightedQuickUnionUF;
import edu.princeton.cs.algs4.StdOut;

public class Percolation {
    private boolean[] siteopen;       // opensite[i] = 1 if the site is open
    private int gridsize;             // dimention of the grid
    private int num;                  // number of open sites
    private WeightedQuickUnionUF uf1; // unionfind object 1 with top virtual site only
    private WeightedQuickUnionUF uf2; // unionfind object 2 with top and bottom virtual sites
    
    public Percolation(int n) {
        gridsize = n;
        num = 0;
        uf1 = new WeightedQuickUnionUF(n * n + 2);
        uf2 = new WeightedQuickUnionUF(n * n + 2);
        siteopen = new boolean[n * n + 1];    // default to false
        // create a virtual site and connect with every site on the top and bottom rows
        for (int i = 0; i < n; i++) {
            uf1.union(0, i + 1);
            uf2.union(0, i + 1);
            uf2.union(n * n - i, n * n + 1);
        }
    }
    
    public void open(int row, int col) {
        validate(row, col);        
        int p = xyTo1D(row, col);
        siteopen[p] = true;
        num++;
        if (col > 1) {
            if (isOpen(row, col - 1)) {
                uf1.union(p - 1, p);
                uf2.union(p - 1, p);
            }
        }
        if (col < gridsize) {
            if (isOpen(row, col + 1)) {
                uf1.union(p, p + 1);
                uf2.union(p, p + 1);
            }
        }
        if (row > 1) {
            if (isOpen(row - 1, col)) {
                uf1.union(p - gridsize, p);
                uf2.union(p - gridsize, p);
            }
        }
        if (row < gridsize) {
            if (isOpen(row + 1, col)) {
                uf1.union(p, p + gridsize);
                uf2.union(p, p + gridsize);
            }
        }
    }
    
    private int xyTo1D(int row, int col) {
        return (row - 1) * gridsize + col;
    }
    
    private void validate(int row, int col) {
        if (row <= 0 || row > gridsize) {
            throw new IllegalArgumentException("index " + row + " is not between 1 and " + gridsize);
        }
        if (col <= 0 || col > gridsize) {
            throw new IllegalArgumentException("index " + col + " is not between 1 and " + gridsize);
        }
    }
        
    public int numberOfOpenSites() {
        return num;
    }
    
    public boolean isOpen(int row, int col) {
        validate(row, col);
        return siteopen[xyTo1D(row, col)];
    }
    
    public boolean isFull(int row, int col) {
        validate(row, col);
        return (isOpen(row, col) && uf1.connected(0, xyTo1D(row, col)));
    }
    
    public boolean percolates() {
        return uf2.connected(0, gridsize * gridsize + 1);
    }
    
    public static void main(String[] args) {     
        Percolation perc = new Percolation(1);
        StdOut.println(perc.numberOfOpenSites());
        StdOut.println(perc.percolates());
    }
}
        
      