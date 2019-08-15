/* *****************************************************************************
 *  Name:   Khoa Nam Pham
 *  Date:   13/08/2019
 *  Description:    WordNet implementation for WordNet assignment on Algorithms
 *                  Part II by Princeton University (on coursera.org)
 **************************************************************************** */

import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.Topological;

import java.util.HashMap;

public class WordNet {
    private final SAP sap;      // The SAP to calculate the shortest ancestral path
    private final HashMap<String, Queue<Integer>> nounsToID;
    // Convert from a noun to its corresponding ID
    private final HashMap<Integer, Queue<String>> idToNouns;
    // Convert from an ID to its corresponding noun
    private final int nounsCount;   // The number of nouns
    private final int verticesCount;    // The number of ID

    // constructor takes the name of the two input files
    public WordNet(String synsets, String hypernyms) {
        try {
            idToNouns = new HashMap<Integer, Queue<String>>();
            nounsToID = new HashMap<String, Queue<Integer>>();
            processSynset(synsets, idToNouns, nounsToID);

            nounsCount = nounsToID.size();
            verticesCount = idToNouns.size();

            Digraph graph = new Digraph(verticesCount);
            processHypernym(hypernyms, graph);

            if (hasCycle(graph)) {
                throw new IllegalArgumentException("Graph is not a DAG.");
            }

            if (hasManyRoots(graph)) {
                throw new IllegalArgumentException("Graph has more than 1 root.");
            }

            sap = new SAP(graph);
        }
        catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    // returns all WordNet nouns
    public Iterable<String> nouns() {
        return nounsToID.keySet();
    }

    // is the word a WordNet noun?
    public boolean isNoun(String word) {
        if (word == null) {
            throw new IllegalArgumentException("Checking isNoun() with null arguments]");
        }
        return nounsToID.containsKey(word);
    }

    // distance between nounA and nounB (defined below)
    public int distance(String nounA, String nounB) {
        int length;
        if (isNoun(nounA) && isNoun(nounB)) {
            Queue<Integer> vertexA = nounsToID.get(nounA);
            Queue<Integer> vertexB = nounsToID.get(nounB);
            length = sap.length(vertexA, vertexB);
        }
        else {
            throw new IllegalArgumentException("Noun not exist in synsets.");
        }

        return length;
    }

    // a synset (second field of synsets.txt) that is the common ancestor of nounA and nounB
    // in a shortest ancestral path (defined below)
    public String sap(String nounA, String nounB) {
        String anc = "";
        if (isNoun(nounA) && isNoun(nounB)) {
            Queue<Integer> vertexA = nounsToID.get(nounA);
            Queue<Integer> vertexB = nounsToID.get(nounB);
            int vertexAnc = sap.ancestor(vertexA, vertexB);
            Queue<String> strings = idToNouns.get(vertexAnc);

            for (String string : strings) {
                anc += string + " ";
            }
            anc = anc.trim();
        }
        else {
            throw new IllegalArgumentException("Noun not exist in synsets.");
        }

        return anc;
    }

    /**
     * Reads a file, parses the data and store each noun and its corresponding ID to two hash table
     *
     * @param synsetsFile - The name of the file containing all the synsets
     * @param ids         - The hash table that takes ID as key and the corresponding strings as
     *                    values
     * @param nouns       - The hash table that takes strings as key and the corresponding ids as
     *                    values
     */
    private void processSynset(String synsetsFile, HashMap<Integer, Queue<String>> ids,
                               HashMap<String, Queue<Integer>> nouns) {
        In inputStream = new In(synsetsFile);
        while (!inputStream.isEmpty()) {
            String line = inputStream.readLine();
            String[] data = line.split(",");

            int id = Integer.parseInt(data[0]);
            String[] noun = data[1].split(" ");

            Queue<String> stringQueue = new Queue<String>();

            for (int i = 0; i < noun.length; i++) {
                stringQueue.enqueue(noun[i]);

                Queue<Integer> currentIDs = nouns.get(noun[i]);
                if (currentIDs == null) {
                    currentIDs = new Queue<Integer>();
                    currentIDs.enqueue(id);
                }
                else {
                    currentIDs.enqueue(id);
                }
                nouns.put(noun[i], currentIDs);
            }

            ids.put(id, stringQueue);
        }
    }

    /**
     * Reads a file, parses the data and construct the Digraph
     *
     * @param hypernymsFile - The name of the file containing the hypernyms
     * @param digraph       - The graph which needs to be constructed
     */
    private void processHypernym(String hypernymsFile, Digraph digraph) {
        In inputStream = new In(hypernymsFile);
        while (!inputStream.isEmpty()) {
            String line = inputStream.readLine();
            String[] data = line.split(",");
            if (data.length >= 2) {
                int source = Integer.parseInt(data[0]);
                for (int i = 1; i < data.length; i++) {
                    digraph.addEdge(source, Integer.parseInt(data[i]));
                }
            }
        }

    }

    /**
     * Checks if the digraph is a DAG or not (or equivalently has a cycle or not)
     *
     * @param graph - The graph that needs to be checked
     * @return true if it has cycle, else false.
     */
    private boolean hasCycle(Digraph graph) {
        Topological topo = new Topological(graph);
        return !topo.hasOrder();
    }

    /**
     * Checks if the digraph has more than one root
     *
     * @param graph - The graph that needs to be checked
     * @return true if the graph has more than 1 root or false otherwise
     */
    private boolean hasManyRoots(Digraph graph) {
        int rootCount = 0;
        int v = 0;
        while (v < graph.V() && rootCount <= 1) {
            if (graph.outdegree(v) == 0)
                rootCount++;

            v++;
        }

        return rootCount > 1;
    }

    public static void main(String[] args) {
        WordNet wordNet = new WordNet(args[0], args[1]);
        StdOut.println(wordNet.nounsCount);
        StdOut.println(wordNet.verticesCount);
        // StdOut.println(wordNet.graph.E());
        // StdOut.println(wordNet.idToNouns.toString());
        // StdOut.println(wordNet.nounsToID.toString());
        while (!StdIn.isEmpty()) {
            String nounA = StdIn.readLine();
            String nounB = StdIn.readLine();
            // Queue<Integer> idA = wordNet.nounsToID.get(nounA);
            // Queue<Integer> idB = wordNet.nounsToID.get(nounB);
            // StdOut.println(idA.toString() + " - " + idB.toString());
            // StdOut.printf("SAP distance: %d\n", wordNet.sap.length(idA, idB));
            StdOut.printf("Distance: %d\n", wordNet.distance(nounA, nounB));
            StdOut.printf("Ancestor: %s\n", wordNet.sap(nounA, nounB));
        }
    }
}
