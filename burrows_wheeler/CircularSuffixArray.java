import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

public class CircularSuffixArray {
    
    private final int N;
    private int[] index;
    
    // circular suffix array of s
    public CircularSuffixArray(String s) {
        if (s == null) throw new NullPointerException();
        N = s.length();
        index = new int[N];
        for (int i = 0; i < N; i++)
            index[i] = i;
        sort(s, index, N);
    }
    
    private static void sort(String s, int[] a, int N) {
        int[] aux = new int[N];
        sort(s, a, 0, N-1, 0, aux);
    }

    // return dth character of s, -1 if d = length of string
    private static int charAt(String s, int a, int d) { 
        if (d == s.length()) return -1;
        return s.charAt((d+a) % s.length());
    }

    // sort from a[lo] to a[hi], starting at the dth character
    private static void sort(String s, int[] a, int lo, int hi, int d, int[] aux) {

        // cutoff to insertion sort for small subarrays
        if (hi <= lo + 15) {
            insertion(s, a, lo, hi, d);
            return;
        }

        // compute frequency counts
        int[] count = new int[258];
        for (int i = lo; i <= hi; i++) {
            int c = charAt(s, a[i], d);
            count[c+2]++;
        }

        // transform counts to indicies
        for (int r = 0; r < 257; r++)
            count[r+1] += count[r];

        // distribute
        for (int i = lo; i <= hi; i++) {
            int c = charAt(s, a[i], d);
            aux[count[c+1]++] = a[i];
        }

        // copy back
        for (int i = lo; i <= hi; i++) 
            a[i] = aux[i - lo];

        // recursively sort for each character (excludes sentinel -1)
        for (int r = 0; r < 256; r++) {
            int l = lo + count[r], h = lo + count[r+1] - 1;
            if (l <= h) sort(s, a, l, h, d+1, aux);
        }
    }


    // sort from a[lo] to a[hi], starting at the dth character
    private static void insertion(String s, int[] a, int lo, int hi, int d) {
        for (int i = lo; i <= hi; i++)
            for (int j = i; j > lo && less(s, a[j], a[j-1], d); j--)
                exch(a, j, j-1);
    }

    // exchange a[i] and a[j]
    private static void exch(int[] a, int i, int j) {
        int temp = a[i];
        a[i] = a[j];
        a[j] = temp;
    }
    
    // is v less than w, starting at character d
    private static boolean less(String s, int v, int w, int d) {
        for (int i = d; i < s.length(); i++) {
            if (charAt(s, v, i) < charAt(s, w, i)) return true;
            if (charAt(s, v, i) > charAt(s, w, i)) return false;
        }
        return false;
    }
    
    // length of s
    public int length() {
        return N;
    }
    // returns index of ith sorted suffix
    public int index(int i) {
        if (i < 0 || i >= N) throw new IllegalArgumentException();
        return index[i];
    }
    
    // unit testing of the methods
    public static void main(String[] args) {
        String s = new In(args[0]).readAll();
        int N = s.length();
        CircularSuffixArray csa = new CircularSuffixArray(s);
        for (int i = 0; i < N; i++) {
            int idx = csa.index(i);
            for (int j = idx, cnt = 0; cnt < N; j = (j+1) % N, cnt++)
                StdOut.print(s.charAt(j));
            StdOut.println(" " + idx);
        }
    }
}
