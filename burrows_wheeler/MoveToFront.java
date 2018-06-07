import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

public class MoveToFront {  
    // apply move-to-front encoding, reading from standard input and writing to standard output
    public static void encode() {
        int R = 256;   
        String inStr = BinaryStdIn.readString();
        
        int len = inStr.length();
        int[] seq = new int[R];
        for (int r = 0; r < R; r++)
            seq[r] = r;
        
        for (int i = 0; i < len; i++) {
            int head = inStr.charAt(i);
            BinaryStdOut.write(seq[head], 8);
            
            for (int r = 0; r < R; r++) {
                if (seq[r] < seq[head]) seq[r]++;
            }
            seq[head] = 0;
        }
        
        BinaryStdOut.close();
    }
    
    // apply move-to-front decoding, reading from standard input and writing to standard output
    public static void decode() {
        int R = 256;   
        int[] seq = new int[R];
        for (int r = 0; r < R; r++)
            seq[r] = r;
        
        while (!BinaryStdIn.isEmpty()) {
            int head = BinaryStdIn.readInt(8);
            int idx = 0;
            for (int r = 0; r < R; r++) {
                if (seq[r] == head) {
                    idx = r;
                    BinaryStdOut.write(r, 8);
                }
                if (seq[r] < head) seq[r]++;
            }
            seq[idx] = 0;
        }
        
        BinaryStdOut.close();
    }

    // if args[0] is '-', apply move-to-front encoding
    // if args[0] is '+', apply move-to-front decoding
    public static void main(String[] args) {
        if      (args[0].equals("-")) encode();
        else if (args[0].equals("+")) decode();
        else throw new IllegalArgumentException("Illegal command line argument");
    }
}