/* *****************************************************************************
 *  Name:   Khoa Nam Pham
 *  Date:   10/09/2019
 *  Description:    Baseball Elimination Implementation using maxflow-mincut
 *                  algorithm
 **************************************************************************** */


import edu.princeton.cs.algs4.FlowEdge;
import edu.princeton.cs.algs4.FlowNetwork;
import edu.princeton.cs.algs4.FordFulkerson;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Queue;

import java.util.HashMap;

public class BaseballElimination 
{
    private HashMap<String, Team> teams; // All the teams
    private HashMap<String, Integer> vertices; // Vertices corresponding to each team
    private HashMap<Integer, String> names;  // This is used to convert from vertex to the corresponding team name
    private int teamNum;  // Number of teams
    
    // Create a baseball division from the given filename
    public BaseballElimination(String filename) 
    {
        In inputStream = new In(filename);
        teamNum = Integer.parseInt(inputStream.readLine());
        teams = new HashMap<>();
        vertices = new HashMap<>();
        names = new HashMap<>();
        // Process each line and save data
        for (int i = 0; i < teamNum; i++)
        {
            String line = inputStream.readLine();
            Team newTeam = processLine(line.trim());
            teams.put(newTeam.getName(), newTeam);  // Convert the team name to the Team object
            vertices.put(newTeam.getName(), i);  // Convert the team to it's vertex representation in the flow network
            names.put(i, newTeam.getName());  // Convert from vertex to team name
        }
    }

    // number of teams
    public int numberOfTeams()
    {
        return teamNum;
    }

    // all teams
    public Iterable<String> teams()
    {
        return teams.keySet();
    }

    // number of wins for given team
    public int wins(String teamName)
    {
        if (teamName == null)
        {
            throw new IllegalArgumentException("Team name is null.");
        }

        Team team = teams.get(teamName);

        if (team == null)
        {
            throw new IllegalArgumentException("Cannot find your team in division.");
        }

        return team.wins();
    }

    // number of losses for given team
    public int losses(String teamName)
    {
        if (teamName == null)
        {
            throw new IllegalArgumentException("Team name is null.");
        }

        Team team = teams.get(teamName);

        if (team == null)
        {
            throw new IllegalArgumentException("Cannot find your team in division.");
        }

        return team.loss();
    }

    // number of remaining games for given team
    public int remaining(String teamName)    
    {
        if (teamName == null)
        {
            throw new IllegalArgumentException("Team name is null.");
        }

        Team team = teams.get(teamName);

        if (team == null)
        {
            throw new IllegalArgumentException("Cannot find your team in division.");
        }

        return team.remain();
    }
    
    /**
     * Return the number of remaining games between team1 and team2
     * @param team1 - The first team
     * @param team2 - The second team
     * @return - The number of remaining games between 2 teams
     */
    public int against(String team1, String team2)  
    {
        if (team1 == null || teams.get(team1) == null)
        {
            throw new IllegalArgumentException("Team1 is null");
        }

        if (team2 == null || teams.get(team2) == null)
        {
            throw new IllegalArgumentException("Team2 is null");
        }

        // The number of remaining games of team1 and team2 is 
        // the element at the vertex'th position of either one of them
        // inside the other's games array
        int vertex2 = vertices.get(team2);
        Team firstTeam = teams.get(team1);

        return firstTeam.remainAgainst()[vertex2];
    }  

    /**
     * Checks if the given team is mathematically eliminated or not
     * @param team - The given team
     * @return - true if the team can be mathematically eliminated or false otherwise
     */
    public boolean isEliminated(String team)
    {
        if (teams.get(team) == null)
        {
            throw new IllegalArgumentException("Team not in division");
        }

        boolean eliminated = false;  // Is the team eliminated?
        // If the team is trivially eliminated
        if (isTrivialEliminated(team))
        {
            eliminated = true;
        }
        else
        {
            FlowNetwork graph = constructNetwork(team);
            int teamVertex = vertices.get(team);
            eliminated = getCertificate(graph, teamVertex) != null;
        }

        return eliminated;
    }

    // subset R of teams that eliminates given team; null if not eliminated

    /**
     * Returns the certificate of elimination of a certain team.
     * If the the team cannot be mathematically eliminated, returns NULL
     * @param team - The subject team
     * @return - A set containing teams that we can use to mathematically eliminate the subject team
     */
    public Iterable<String> certificateOfElimination(String teamName)  
    {
        if (teams.get(teamName) == null)
        {
            throw new IllegalArgumentException("Team not in division");
        }

        // The collection of teams in the certificate of elimination
        Queue<String> superTeams = null;

        // If the team is trivial eliminated
        // It will surely be eliminated by the best team so far
        // So we just returns that best team
        if (isTrivialEliminated(teamName))
        {
            Team best = getBestTeam();
            superTeams = new Queue<>();
            superTeams.enqueue(best.getName());
        }
        else 
        {
            FlowNetwork graph = constructNetwork(teamName);  
            int teamVertex = vertices.get(teamName);

            // Get the certificate of the team
            superTeams = getCertificate(graph, teamVertex);
        }

        return superTeams;
    }

    /**
     * Check if a team is eliminated (trivially or mathematically).
     * If the team is eliminated, return the certificate of elimination otherwise return null.
     * @param graph - The FlowNetwork built base on the team in the division
     * @param subjectTeam - The subject team that needs checking
     * @return - The certificate of elimination of the team if it is eliminated or null otherwise
     */
    private Queue<String> getCertificate(FlowNetwork graph, int subjectTeam)
    {
        boolean eliminated = false;
        int source = graph.V() - 1;
        int sink = graph.V() - 2;
        FordFulkerson fordFulkerson = new FordFulkerson(graph, source, sink);
        Queue<String> certificate = null;
        // The team is eliminated if the value of the flow less than the capacity of all edges from the source
        // i.e There is an edge from the source that is not full
        eliminated = fordFulkerson.value() < capacFromSource(graph, source);

        if (eliminated)
        {
            certificate = new Queue<String>();
            for (int i = 0; i < teamNum; i++)
            {
                if (i != subjectTeam && fordFulkerson.inCut(i))
                {
                    String teamName = names.get(i);
                    certificate.enqueue(teamName);
                }
            }
        }

        return certificate;
    }

    /**
     * Process each line the input file and constructs a Team object based on that line
     * @param line - A line in the input file
     * @return - The Team object 
     */
    private Team processLine(String line)
    {
        String[] info = line.split(" +");
        String name = info[0];   // The team name
        int wins = Integer.parseInt(info[1]);  // The number of games won
        int loss = Integer.parseInt(info[2]);  // The number of games loss
        int remain = Integer.parseInt(info[3]);  // The number of remaining games
        int[] remainAgainst = new int[info.length - 4];  // The number of remaining games against the other team in the division
        for (int i = 4; i < info.length; i++)
        {
            remainAgainst[i - 4] = Integer.parseInt(info[i]);
        }

        return new Team(name, wins, loss, remain, remainAgainst);
    }

    private double capacFromSource(FlowNetwork network, int source)
    {
        Iterable<FlowEdge> edges = network.adj(source);
        double capac = 0;
        for (FlowEdge edge : edges)
        {
            capac += edge.capacity();
        }

        return capac;
    }

    /**
     * Computes the total number of vertices in the FlowNetwork.
     * This number is the sum of:
     *  + The number of teams
     *  + The number of games between the teams: Some vertices will not be used since each game will be repeated twice (like in a matrix)
     *                                          This will waste a little of memory but not much
     *  + 2 vertices for source and sink
     * @return The number of vertices
     */
    private int getTotalVertexNum()
    {
        return teamNum + teamNum * teamNum + 2;
    }

    private FlowNetwork constructNetwork(String teamName)
    {
        int verticesNum = getTotalVertexNum();    // The total number of vertices the network could have
        int teamIndex = vertices.get(teamName);     // The vertex of the subject team
        Team subjectTeam = teams.get(teamName);  // The subject team
        int wins = subjectTeam.wins();          // The number of wins that the subject team has
        int remains = subjectTeam.remain();     // The remaining games of the subject team
        int source = verticesNum - 1;           // The source vertex
        int sink = verticesNum - 2;             // The sink vertex

        // Creates an empty network gradually add edges to the network
        FlowNetwork network = new FlowNetwork(verticesNum);   
        
        // For each team in the division
        for (Team team : teams.values())
        {
            // Get the vertex of each team
            int index = vertices.get(team.getName());
            
            // We dont need to connect the first team in the network because it will be connected 
            // when we consider the other vertices
            // We also ignore the vertex of the subject team
            if (index != teamIndex)
            {
                int[] games = team.remainAgainst(); // Get the remaining games
                
                // Compute the vertex base on the reaming games of the team
                // We only need to consider games from 0 to index - 1 to avoid duplicate of the game
                // (Check the games matrix in the input file to know more in detail, we only use the lower triangle in the matrix)
                for (int i = 0; i < index; i++)
                {
                    // We ignore the subject team
                    if (i != teamIndex)
                    {
                        int gameIndex = getGameVertex(index, i);  // The vertex of the game between 2 teams
                        
                        // Connect the source with the game vertex, the capacity is the number of games between 2 teams
                        network.addEdge(new FlowEdge(source, gameIndex, games[i]));     

                        // Connect the game vertex with the vertex of each team participate in the game
                        network.addEdge(new FlowEdge(gameIndex, i, Double.POSITIVE_INFINITY));
                        network.addEdge(new FlowEdge(gameIndex, index, Double.POSITIVE_INFINITY));
                    }
                }

                // Connect the team vertex with sink
                // The capacity of the edge is the total posible winning that team can have minus the number of wins of the subject team
                int capacToSink =  wins + remains - team.wins();
                if (capacToSink < 0)
                {
                    capacToSink = 0;
                }
                network.addEdge(new FlowEdge(index, sink, capacToSink));  
            }
        }

        return network;
    }

    /**
     * Get the vertex of the game between 2 teams
     * @param team1 - The first team
     * @param team2 - The second team
     * @return - The vertex represent the game between 2 teams
     */
    private int getGameVertex(int team1, int team2)
    {
        return team1 * teamNum + team2;
    }

    /**
     * Checks if the given is trivial eliminated or not
     * @param teamName - The given team that need to be checked
     * @return - true if the team is trivially eliminated or false otherwise
     */
    private boolean isTrivialEliminated(String teamName)
    {
        Team team = teams.get(teamName);  // The current team that needs checking
        int maxWins = getBestTeam().wins();   // The maximum number of wins so far

        return team.wins() + team.remain() < maxWins;
    }

    /**
     * Returns the best team with the most wins so far
     * @return - The best team object
     */
    private Team getBestTeam()
    {
        int maxWins = 0;  // The number of maximum wins known
        Team bestTeam = teams.get(names.get(0)); // Let the first team be the best team at first
        Iterable<Team> allTeams = teams.values();

        // For each team in the division
        for (Team team : allTeams)
        {
            if (team.wins() >  maxWins)
            {
                maxWins = team.wins();
                bestTeam = team;
            }
        }

        return bestTeam;
    }

    /**
     * Private Class representing a Team in the division
     */
    private class Team
    {
        private String name; // Name of the team
        private int wins;    // The nubmer of games the team won
        private int loss;    // The number of games the team loss
        private int remain;  // The number of remaining games
        private int[] remainAgainst; // The number of remaining games with the other teams in the division

        public Team(String name, int wins, int loss, int remain, int[] remainAgainst)
        {
            this.name = name;
            this.wins = wins;
            this.loss = loss;
            this.remain = remain;
            this.remainAgainst = remainAgainst;
        }


        public String getName()
        {
            return name;
        }

        public int wins()
        {
            return wins;
        }

        public int loss()
        {
            return loss;
        }

        public int remain()
        {
            return remain;
        }

        public int[] remainAgainst()
        {
            return remainAgainst;
        }

        public String toString()
        {
            String str = this.name + " " + this.wins + " " + this.loss + " " + this.remain + " ";
            for (int i = 0; i < remainAgainst.length; i++)
            {
                str += remainAgainst[i] + " ";
            }

            return str.trim();
        }

        public int hashCode()
        {
            return this.name.hashCode();
        }
    }
    public static void main(String[] args) 
    {
        BaseballElimination division = new BaseballElimination(args[0]);
        for (String team : division.teams()) 
        {
            StdOut.print(division.remaining(team));
            if (division.isEliminated(team)) 
            {
                StdOut.print(team + " is eliminated by the subset R = { ");
                for (String t : division.certificateOfElimination(team)) 
                {
                    StdOut.print(t + " ");
                }
                StdOut.println("}");
            }
            else 
            {
                StdOut.println(team + " is not eliminated");
            }
        }   
    }
}
