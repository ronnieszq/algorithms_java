import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;
import edu.princeton.cs.algs4.StdOut;

public class BurrowsWheeler { 
    // apply Burrows-Wheeler transform, reading from standard input and writing to standard output
    public static void transform() {
        int first = 0;
        StringBuilder t = new StringBuilder();        
        String inStr = BinaryStdIn.readString();
        CircularSuffixArray csa = new CircularSuffixArray(inStr);
        
        int len = csa.length();
        for (int i = 0; i < len; i++) {
            int idx = csa.index(i);
            if (idx == 0) {
                first = i;
                t.append(inStr.charAt(len-1));
            }
            else t.append(inStr.charAt(idx-1));
        }
        
        BinaryStdOut.write(first);
        BinaryStdOut.write(t.toString());
        BinaryStdOut.close();
    }

    // apply Burrows-Wheeler inverse transform, reading from standard input and writing to standard output
    public static void inverseTransform() {
        int first = BinaryStdIn.readInt();
        String s = BinaryStdIn.readString();
        
        int n = s.length();
        int R = 256;
        int[] freq = new int[R+1];
        char[] sorted = new char[n];
        int[] next = new int[n];

        for (int i = 0; i < n; i++)
            freq[s.charAt(i)+1]++;

        for (int r = 0; r < R; r++)
            freq[r+1] += freq[r];

        for (int i = 0; i < n; i++) {
            char temp = s.charAt(i);
            next[freq[temp]++] = i;
        }
        
        int idx = next[first];
        for (int i = 0; i < n; i++) {
            BinaryStdOut.write(s.charAt(idx));
            idx = next[idx];
        }
        
        // BinaryStdOut.write(oriStr.toString());
        BinaryStdOut.close();
    }

    // if args[0] is '-', apply Burrows-Wheeler transform
    // if args[0] is '+', apply Burrows-Wheeler inverse transform
    public static void main(String[] args) {
        if      (args[0].equals("-")) transform();
        else if (args[0].equals("+")) inverseTransform();
        else throw new IllegalArgumentException("Illegal command line argument");
    }
}