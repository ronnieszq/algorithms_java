import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.SET;
import edu.princeton.cs.algs4.Bag;

public class BoggleSolver {
    private static final int A = 'A';       // "A" as integer
    private static final char Q = 'Q'; 
    private static final char U = 'U';
    private static final int R = 26;        // Uppercase alphabet
    
    private int size;
    private Node root;                      // root of trie
    private SET<String> words;
    private Bag<Integer>[] adj;
    private boolean[] marked;
    private char[] letters;
    private StringBuilder sb;               // constant time concat
    
    // Initializes the data structure using the given array of strings as the dictionary.
    // (You can assume each word in the dictionary contains only the uppercase letters A through Z.)
    public BoggleSolver(String[] dictionary) {
        root = new Node();
        for (int i = 0; i < dictionary.length; i++)
            add(dictionary[i]);
        size = 16;
        marked = new boolean[size];
        adj = (Bag<Integer>[]) new Bag[size];
        letters = new char[size];
        sb = new StringBuilder();
    }
    
    // R-way trie node
    private class Node {
        private Node[] next = new Node[R];
        private boolean isWord;
    }
    
    private boolean contains(String key) {
        if (key == null) throw new IllegalArgumentException("argument to contains() is null");
        Node x = get(root, key, 0);
        if (x == null) return false;
        return x.isWord;
    }
    
    private Node get(Node x, String key, int d) {
        if (x == null) return null;
        if (d == key.length()) return x;
        char c = key.charAt(d);
        return get(x.next[c-A], key, d+1);
    }

    private void add(String key) {
        if (key == null) throw new IllegalArgumentException("argument to add() is null");
        root = add(root, key, 0);
    }
    
    private Node add(Node x, String key, int d) {
        if (x == null) x = new Node();
        if (d == key.length()) {
            x.isWord = true;
        }
        else {
            char c = key.charAt(d);
            x.next[c-A] = add(x.next[c-A], key, d+1);
        }
        return x;
    }
            
    // Returns the set of all valid words in the given Boggle board, as an Iterable.
    public Iterable<String> getAllValidWords(BoggleBoard board) {
        words = new SET<String>();
        int r = board.rows();
        int c = board.cols();
        if (size < r * c) {
            size = r * c;
            marked = new boolean[size]; 
            adj = (Bag<Integer>[]) new Bag[size]; 
            letters = new char[size];
        }
        adj = adjacencyMatrix(r, c);
        letters = new char[r * c];
        for (int d = 0; d < r * c; d++)
            letters[d] = board.getLetter(d / c, d % c);
        
        for (int d = 0; d < r * c; d++) {
            Node node = root.next[letters[d]-A];
            if (!marked[d] && node != null) dfs(node, d);
        }        
        return words;
    }

    // recursive depth-first search
    private void dfs(Node node, int p) { 
        char ltr = letters[p];
        if (ltr == Q) node = node.next[U-A]; 
        if (node == null) return;
        sb.append(ltr); 
        if (ltr == Q) sb.append(U); 
        marked[p] = true; 
         
        if (node.isWord && sb.length() > 2)
            words.add(sb.toString()); 
         
        for (int q : adj[p]) { 
            Node next = node.next[letters[q]-A]; 
            if (!marked[q] && next != null)  
                dfs(next, q); 
        } 
         
        marked[p] = false; 
        sb.deleteCharAt(sb.length()-1); 
        if (sb.length() > 0 && sb.charAt(sb.length()-1) == Q)  
            sb.deleteCharAt(sb.length()-1); 
    }
    
    // helper method to get adjacent dice
    private Bag<Integer>[] adjacencyMatrix(int r, int c) {
        for (int d = 0; d < r * c; d++) {
            adj[d] = new Bag<Integer>();
        }
        
        for (int d = 0; d < r * c; d++) {
            // find the position of the dice
            int row = d / c;
            int col = d % c;
            if (row != 0)                     adj[d].add(d - c);      // top dice
            if (row != r - 1)                 adj[d].add(d + c);      // bottom dice
            if (col != 0)                     adj[d].add(d - 1);      // left dice
            if (col != c - 1)                 adj[d].add(d + 1);      // right dice            
            if (row != 0 && col != 0)         adj[d].add(d - c - 1);  // top left dice
            if (row != 0 && col != c - 1)     adj[d].add(d - c + 1);  // top right dice
            if (row != r - 1 && col != 0)     adj[d].add(d + c - 1);  // bottom left dice
            if (row != r - 1 && col != c - 1) adj[d].add(d + c + 1);  // top right dice
        }
        return adj;
    }

    // Returns the score of the given word if it is in the dictionary, zero otherwise.
    // (You can assume the word contains only the uppercase letters A through Z.)
    public int scoreOf(String word) {
        int score = 0;
        int len = word.length();
        if (!contains(word)) score = 0;
        else {
            if (len == 3 || len == 4) score = 1;
            if (len == 5) score = 2;
            if (len == 6) score = 3;
            if (len == 7) score = 5;
            if (len >= 8) score = 11;
        }
        return score;
    }
    
    // Unit test of the data structure
    public static void main(String[] args) {
        In in = new In(args[0]);
        String[] dictionary = in.readAllStrings();
        BoggleSolver solver = new BoggleSolver(dictionary);
        BoggleBoard board = new BoggleBoard(args[1]);
        int score = 0;
        for (String word : solver.getAllValidWords(board)) {
            StdOut.println(word);
            score += solver.scoreOf(word);
        }
        StdOut.println("Score = " + score);
    }
}