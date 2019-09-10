/* *****************************************************************************
 *  Name:   Khoa Nam Pham
 *  Date:   10/09/2019
 *  Description:    Baseball Elimination Implementation using maxflow-mincut
 *                  algorithm
 **************************************************************************** */


import edu.princeton.cs.algs4.FlowNetwork;
import edu.princeton.cs.algs4.FordFulkerson;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.Quick;

import java.util.HashMap;

public class BaseballElimination 
{
    private HashMap<String, Team> teams; // All the teams
    private HashMap<String, Integer> vertices; // Vertices corresponding to each team
    private int teamNum;  // Number of teams
    
    // Create a baseball division from the given filename
    public BaseballElimination(String filename) 
    {
        In inputStream = new In(filename);
        teamNum = Integer.parseInt(inputStream.readLine());
        teams = new HashMap<>();
        vertices = new HashMap<>();
        for (int i = 0; i < teamNum; i++)
        {
            String line = inputStream.readLine();
            Team newTeam = processLine(line);
            teams.put(newTeam.getName(), newTeam);
            vertices.put(newTeam.getName(), i);
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
    public int wins(String team)
    {
        if (team == null)
        {
            throw new IllegalArgumentException("Team name is null.");
        }

        return teams.get(team).wins();
    }

    // number of losses for given team
    public int losses(String team)
    {
        if (team == null)
        {
            throw new IllegalArgumentException("Team name is null.");
        }

        return teams.get(team).loss();
    }

    // number of remaining games for given team
    public int remaining(String team)    
    {
        if (team == null)
        {
            throw new IllegalArgumentException("Team name is null.");
        }

        return teams.get(team).loss();
    }
    
    // number of remaining games between team1 and team2
    public int against(String team1, String team2)  
    {
        if (team1 == null)
        {
            throw new IllegalArgumentException("Team1 is null");
        }

        if (team2 == null)
        {
            throw new IllegalArgumentException("Team2 is null");
        }

        int vertex2 = vertices.get(team2);
        Team firstTeam = teams.get(team1);

        return firstTeam.remainAgainst()[vertex2];
    }  

    // is given team eliminated?
    public boolean isEliminated(String team)
    {
        return true;
    }

    // subset R of teams that eliminates given team; null if not eliminated
    public Iterable<String> certificateOfElimination(String team)  
    {
        return null;
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

    public static void main(String[] args) 
    {
        BaseballElimination division = new BaseballElimination(args[0]);
        /*for (String team : division.teams()) 
        {
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
        } */   
    }
}
