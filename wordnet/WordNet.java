import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.SET;
import edu.princeton.cs.algs4.Bag;
import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.StdOut;
import java.util.ArrayList;

public class WordNet {
    private final SET<Noun> nounSet;
    private final SAP sap;
    private final ArrayList<String> synsets;
    private final Digraph G;        // the WordNet digraph
 
    private class Noun implements Comparable<Noun> {
        
        private String noun;        // synset associate with the noun
        private Bag<Integer> id;    // collection of id's
        
        public Noun(String noun) {
            this.noun = noun;
            id = new Bag<Integer>();
        }
        
        @Override  
        public int compareTo(Noun that) {  
            return this.noun.compareTo(that.noun);  
        }
        
        public Iterable<Integer> getId() {
            return this.id;
        }
        
        public void addId(int id) {
            this.id.add(id);
        }
    }
    
    // constructor takes the name of the two input files
    public WordNet(String synsets, String hypernyms) {
        this.nounSet = new SET<Noun>();
        this.synsets = new ArrayList<String>();
        this.G = new Digraph(renderSynset(synsets));
        buildDigraph(hypernyms);
        validateGraph(this.G);
        this.sap = new SAP(this.G);
    }
 
    // Read synsets in the input and store the id-synset pair in a symbol table.
    // Record the number of synsets for later use.
    private int renderSynset(String s) {
        int ns = 0;
        In in = new In(s);
        while (!in.isEmpty()) {
            String[] elements = in.readLine().split(",");
            for (String noun : elements[1].split(" ")) {
                Noun n = new Noun(noun);
                if (nounSet.contains(n)) n = nounSet.ceiling(n);
                else                     nounSet.add(n);
                n.addId(Integer.parseInt(elements[0]));   
            }        
            ns++;
            synsets.add(elements[1]);
        }
        return ns;
    }
 
    private void buildDigraph(String h) {
        In in = new In(h);
        while (!in.isEmpty()) {
            String[] elements = in.readLine().split(",");
            // convert hypernyms id into integer
            for (int i = 1; i < elements.length; i++)
                G.addEdge(Integer.parseInt(elements[0]), Integer.parseInt(elements[i]));
        }
    }

    private void validateGraph(Digraph G) {
        // check if the digraph has cycles
        DirectedCycle dc = new DirectedCycle(G);
        if (dc.hasCycle()) {
            throw new IllegalArgumentException("the input does not correspond to a rooted DAG");
        }
        // check if the digraph is single rooted
        int count = 0;       // number of roots
        for (int v = 0; v < G.V(); v++)
            if (G.outdegree(v) == 0) count++;
        if (count != 1) {
            throw new IllegalArgumentException("the input does not correspond to a rooted DAG");
        }
    }
    
    // use dfs to find a circle
    private class DirectedCycle
    {
        private boolean[] marked;
        private int[] edgeTo;
        private Stack<Integer> cycle;   // vertices on a cycle (if one exists)
        private boolean[] onStack;      // vertices on recursive call stack
        
        public DirectedCycle(Digraph G) {
            onStack = new boolean[G.V()];
            edgeTo  = new int[G.V()];
            marked  = new boolean[G.V()];
            for (int v = 0; v < G.V(); v++)
                if (!marked[v]) dfs(G, v);
        }
        private void dfs(Digraph G, int v)
        {
            onStack[v] = true;
            marked[v] = true;
            for (int w : G.adj(v))
                if (this.hasCycle()) return;
            else if (!marked[w]) {
                edgeTo[w] = v;
                dfs(G, w);
            }
            else if (onStack[w]) {
                cycle = new Stack<Integer>();
                for (int x = v; x != w; x = edgeTo[x])
                    cycle.push(x);
                cycle.push(w);
                cycle.push(v);
            }
            onStack[v] = false;
        }
        
        public boolean hasCycle() {
            return cycle != null;
        }
    }
 
    // returns all WordNet nouns
    public Iterable<String> nouns() {
        Bag<String> nouns = new Bag<String>();
        for (Noun n : nounSet) nouns.add(n.noun);
        return nouns;
    }

    // is the word a WordNet noun?
    public boolean isNoun(String word) {
        Noun n = new Noun(word);
        return nounSet.contains(n);
    }

    // distance between nounA and nounB (defined below)
    public int distance(String nounA, String nounB) {
        Noun a = nounSet.ceiling(new Noun(nounA));  
        Noun b = nounSet.ceiling(new Noun(nounB)); 
        return sap.length(a.getId(), b.getId());
    }

    // a synset (second field of synsets.txt) that is the common ancestor of nounA and nounB
    // in a shortest ancestral path (defined below)
    public String sap(String nounA, String nounB) {
        Noun a = nounSet.ceiling(new Noun(nounA));  
        Noun b = nounSet.ceiling(new Noun(nounB));
        for (int id : a.getId())
            StdOut.println(id);
        for (int id : b.getId())
            StdOut.println(id);
        return synsets.get(sap.ancestor(a.getId(), b.getId()));
    }

    // do unit testing of this class
    public static void main(String[] args) {
        WordNet wn = new WordNet("synsets.txt", "hypernyms.txt");
        StdOut.println(wn.G.V());
        StdOut.println(wn.G.E());
        StdOut.println(wn.distance("Brown_Swiss", "barrel_roll"));
    }
}