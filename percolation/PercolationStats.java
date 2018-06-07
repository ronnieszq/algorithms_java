import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.StdStats;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

public class PercolationStats {
    private double mean;         // sample mean of percolation threshold
    private double stddev;       // sample standard deviation of percolation threshold
    private int t;               // number of trials
    
    public PercolationStats(int n, int trials) {
        if (n <= 0 || trials <= 0) {
            throw new IllegalArgumentException("invalid input!");
        }
        t = trials;
        double[] results = new double[trials];
        for (int i = 0; i < trials; i++) {
            Percolation perc = new Percolation(n);
            while (!perc.percolates()) {
                int row = StdRandom.uniform(1, n + 1);
                int col = StdRandom.uniform(1, n + 1);
                if (perc.isOpen(row, col)) continue;
                perc.open(row, col);
            }
            results[i] = ((double) perc.numberOfOpenSites()) / (n * n);
            mean = StdStats.mean(results);
            stddev = StdStats.stddev(results);
        }      
    }
    
    public double mean() {        
        return mean;
    }
    
    public double stddev() {        
        return stddev;
    }
    
    public double confidenceLo() {
        return (mean - 1.96 * stddev / Math.sqrt(t));
    }
    
    public double confidenceHi() {
        return (mean + 1.96 * stddev / Math.sqrt(t));
    }

    public static void main(String[] args) {
        int n = StdIn.readInt();
        int trials = StdIn.readInt();
        PercolationStats ps = new PercolationStats(n, trials);
        StdOut.println("mean                    = " + ps.mean());
        StdOut.println("stddev                  = " + ps.stddev());
        StdOut.println("95% confidence interval = ["
                           + ps.confidenceLo() + ", "
                           + ps.confidenceHi() + "]");
    }
}