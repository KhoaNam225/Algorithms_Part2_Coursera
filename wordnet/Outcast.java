/* *****************************************************************************
 *  Name:   Khoa Nam Pham
 *  Date:   15/08/2019
 *  Description:    Outcast implementation, finding the least related nouns in
 *                  meaning in the given set of WordNet nouns
 **************************************************************************** */

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

public class Outcast {

    private final WordNet wordNet;

    public Outcast(WordNet wordnet)         // constructor takes a WordNet object
    {
        if (wordnet == null) {
            throw new IllegalArgumentException("Null import wordnet");
        }

        this.wordNet = wordnet;
    }

    /**
     * Given an array of WordNet nouns, calculate the least related (in meaning) nouns in the array
     * (the outcast)
     *
     * @param nouns - The array to calculate
     * @return The outast
     */
    public String outcast(String[] nouns)   // given an array of WordNet nouns, return an outcast
    {
        if (nouns == null) {
            throw new IllegalArgumentException("Null import array of Strings");
        }

        int maxLength = 0;  // distance of the current outcast
        int outcastPos = 0; // position of the current outcast
        int[] distances = new int[nouns.length];    // cache the length of each nouns
        // each element in the array store the distance from
        //  that noun to all the nouns before it the set
        for (int i = 0; i < nouns.length; i++)
            distances[i] = 0;   // Init all the distance first

        // Caculate the distance from a noun to all other nouns in the given array
        for (int i = 0; i < nouns.length; i++) {
            int totalLength = distances[i];
            for (int j = i + 1; j < nouns.length; j++) {
                int currentLength = wordNet.distance(nouns[i], nouns[j]);
                totalLength
                        += currentLength;   // calculate the distance from the current noun to every other nouns
                distances[j] += currentLength;  // cache the distance
            }

            if (totalLength > maxLength) {
                maxLength = totalLength;
                outcastPos = i;
            }
        }

        return nouns[outcastPos];
    }

    public static void main(String[] args) {
        WordNet wordnet = new WordNet(args[0], args[1]);
        Outcast outcast = new Outcast(wordnet);
        for (int t = 2; t < args.length; t++) {
            In in = new In(args[t]);
            String[] nouns = in.readAllStrings();
            StdOut.println(args[t] + ": " + outcast.outcast(nouns));
        }
    }
}
