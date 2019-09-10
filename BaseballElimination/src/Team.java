public class Team implements Comparable<Team>
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

    public int compareTo(Team otherTeam)
    {
        int comp;
        if (this.wins < otherTeam.wins)
        {
            comp = -1;
        }
        else if (this.wins > otherTeam.wins)
        {
            comp = 1;
        }
        else 
        {
            comp = 0;
        }

        return comp;
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

    public int maxWin()
    {
        return wins + remain;
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