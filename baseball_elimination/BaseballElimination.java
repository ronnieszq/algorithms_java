import edu.princeton.cs.algs4.ST;
import edu.princeton.cs.algs4.Bag;
import edu.princeton.cs.algs4.FlowEdge;
import edu.princeton.cs.algs4.FlowNetwork;
import edu.princeton.cs.algs4.FordFulkerson;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

public class BaseballElimination {
    private static final int INFI_CAPA = Integer.MAX_VALUE;
    private final ST<String, Integer> teams = new ST<String, Integer>();
    private final ST<Integer, String> teamIDs = new ST<Integer, String>();
    private final int n;              // number of teams
    private final int[] w;            // wins for all teams
    private final int[] l;            // losses for all teams
    private final int[] r;            // remaining games for all teams
    private final int[][] g;          // game metrix
    private boolean isEliminated;
    private Bag<String> certificateOfElimination;
    
    // create a baseball division from given filename
    public BaseballElimination(String filename) {
        // read inputs from a file
        In in = new In(filename);
        n = in.readInt();
        w = new int[n];
        l = new int[n];
        r = new int[n];
        g = new int[n][n];
        for (int i = 0; i < n; i++) {
            String s = in.readString();
            teams.put(s, i);
            teamIDs.put(i, s);
            w[i] = in.readInt();
            l[i] = in.readInt();
            r[i] = in.readInt();
            for (int j = 0; j < n; j++)
                g[i][j] = in.readInt();
        }
    }

    private void computeMaxflow(String team) {
        validateTeam(team);
        isEliminated = false;
        certificateOfElimination = new Bag<String>();
        boolean isTrivial = false;                 // is the team trivailly eliminated?
        int V = n * (n - 1) / 2 + 2;               // number of vertices
        int teamID = teams.get(team);
        FlowNetwork fn = new FlowNetwork(V);
        // add edges from source and to target
        int c = 0;                                 // count of team pairs
        for (int i = 0; i < n; i++) {
            // skip the same team
            int iID = i;
            if (i == teamID)     continue;
            else if (i > teamID) iID--;
            // check for trivial elimination
            if (wins(team) + remaining(team) - w[i] < 0) {
                isTrivial = true;
                isEliminated = true;
                certificateOfElimination.add(teamIDs.get(i));
            }
            else if (!isTrivial) {
                // add the edges from team vertics to target
                FlowEdge eSink = new FlowEdge(V - n + iID, V - 1, wins(team) + remaining(team) - w[i]);
                fn.addEdge(eSink);
                for (int j = i + 1; j < n; j++) {
                    int jID = j;
                    if (j == teamID)     continue;
                    else if (j > teamID) jID--;
                    c++;
                    FlowEdge eGame = new FlowEdge(0, c, g[i][j]);
                    fn.addEdge(eGame);
                    FlowEdge eTeam1 = new FlowEdge(c, V - n + iID, INFI_CAPA);
                    FlowEdge eTeam2 = new FlowEdge(c, V - n + jID, INFI_CAPA);
                    fn.addEdge(eTeam1);
                    fn.addEdge(eTeam2);
                }
            }
        }
        
        if (!isTrivial) {
            FordFulkerson ff = new FordFulkerson(fn, 0, V - 1);
            // non-trivial elimination
            for (int v = 1; v <= (n - 1) * (n - 2) / 2; v++)
                if (ff.inCut(v)) isEliminated = true;
            // Certificate Of Elimination
            if (!isEliminated) certificateOfElimination = null;
            else {
                for (int v = 0; v < n - 1; v++) {
                    int vID = v;
                    if (v >= teamID) vID++;
                    if (ff.inCut(n * (n - 1) / 2 + 2 - n + v)) certificateOfElimination.add(teamIDs.get(vID));
                }
            }
        }
    }
    
    private void validateTeam(String team) {
        if (!teams.contains(team))
            throw new IllegalArgumentException("team " + team + " is not valid");
    }
    
    // is given team eliminated?
    public boolean isEliminated(String team) {
        computeMaxflow(team);
        return isEliminated;
    }
    
    // subset R of teams that eliminates given team; null if not eliminated
    public Iterable<String> certificateOfElimination(String team) {
        computeMaxflow(team);
        return certificateOfElimination;
    }
    
    // number of teams
    public int numberOfTeams() { return n; }
    
    // all teams
    public Iterable<String> teams() { return teams.keys(); }
    
    // number of wins for given team
    public int wins(String team) {
        validateTeam(team);
        return w[teams.get(team)];
    }  
    
    // number of losses for given team
    public int losses(String team) {
        validateTeam(team);
        return l[teams.get(team)];
    }
    
    // number of remaining games for given team
    public int remaining(String team) {
        validateTeam(team);
        return r[teams.get(team)]; 
    }
    
    // number of remaining games between team1 and team2
    public int against(String team1, String team2) {
        validateTeam(team1);
        validateTeam(team2);
        return g[teams.get(team1)][teams.get(team2)];
    }
    
    public static void main(String[] args) {
        BaseballElimination division = new BaseballElimination(args[0]);
        for (String team : division.teams()) {
            if (division.isEliminated(team)) {
                StdOut.print(team + " is eliminated by the subset R = { ");
                for (String t : division.certificateOfElimination(team)) {
                    StdOut.print(t + " ");
                }
                StdOut.println("}");
            }
            else {
                StdOut.println(team + " is not eliminated");
            }
        }
    }
}