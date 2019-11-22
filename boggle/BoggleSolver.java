/* *****************************************************************************
 *  Name: Khoa Nam Pham
 *  Date: 22/11/2019
 *  Description: BoggleSolver implementation for Algorithm Part 2 assignment
 **************************************************************************** */

import edu.princeton.cs.algs4.Bag;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.StdOut;

public class BoggleSolver {
    private DictionaryNode root;  // The root of the dictionary trie
    private DictionaryNode checkFreg;  // The root of the trie used to check frequencies

    // The 26-way trie used to represent the dictionary
    private static class DictionaryNode {
        private static final int R = 26;

        private String word;  // If the node is the end of a word, cache that word in the node
        private DictionaryNode[] next;

        public DictionaryNode() {
            word = null;
            next = new DictionaryNode[R];
        }
    }

    // The node in a graph built from the BoggleBoard
    // Each node will store the label (the letter on each dice)
    // as well as the adjacent dices
    private static class GraphNode {
        private final char label;
        private final Bag<GraphNode> adj;
        private boolean visited;

        public GraphNode(char inLabel) {
            label = inLabel;
            adj = new Bag<>();
            visited = false;
        }

        // Add an adjacent dice
        public void addEdge(GraphNode neighbor) {
            adj.add(neighbor);
        }

        // Get all adjacent dices
        public Iterable<GraphNode> adjs() {
            return adj;
        }

        public String toString() {
            StringBuilder str = new StringBuilder(label + " - ");
            for (GraphNode node : adj) {
                str.append(node.label);
                str.append(" ");
            }

            return str.toString();
        }
    }

    // Initializes the data structure using the given array of strings as the dictionary.
    // (You can assume each word in the dictionary contains only the uppercase letters A through Z.)
    public BoggleSolver(String[] words) {
        if (words == null) {
            throw new IllegalArgumentException("Null dictionary");
        }

        for (int i = 0; i < words.length; i++) {
            addWordToDict(words[i]);
        }
    }

    // Returns the set of all valid words in the given Boggle board, as an Iterable.
    public Iterable<String> getAllValidWords(BoggleBoard board) {
        if (board == null) {
            throw new IllegalArgumentException("Null board");
        }
        Queue<String> result = new Queue<>();
        GraphNode[][] graph = buildGraph(board);
        generateWords(graph, result);
        checkFreg = null;
        return result;
    }

    // Returns the score of the given word if it is in the dictionary, zero otherwise.
    // (You can assume the word contains only the uppercase letters A through Z.)
    public int scoreOf(String word) {
        if (word == null) {
            throw new IllegalArgumentException("Null word");
        }

        int score = 0;
        // Base on the length of the word, gives the corresponding score
        if (inDictionary(word)) {
            int length = word.length();
            if (length == 3 || length == 4) {
                score = 1;
            }
            else if (length == 5) {
                score = 2;
            }
            else if (length == 6) {
                score = 3;
            }
            else if (length == 7) {
                score = 5;
            }
            else if (length >= 8) {
                score = 11;
            }
        }

        return score;
    }

    /**
     * Add a word to the dictionary, used in the constructor
     *
     * @param word - The word to be added
     */
    private void addWordToDict(String word) {
        root = addWordRecurse(root, word, 0);
    }

    /**
     * Add a word that already been checked to the frequency list.
     * This list is used to check for any words that already been added to the result
     *
     * @param word - The word to be added
     */
    private void addWordToResult(String word) {
        checkFreg = addWordRecurse(checkFreg, word, 0);
    }

    private DictionaryNode addWordRecurse(DictionaryNode x, String word, int currPos) {
        if (x == null) {
            x = new DictionaryNode();
        }

        if (currPos == word.length()) {
            x.word = word;
        }
        else {
            char c = word.charAt(currPos);
            int nextPos = c - 'A';
            x.next[nextPos] = addWordRecurse(x.next[nextPos], word, currPos + 1);
        }

        return x;
    }

    /**
     * From the previous node in the trie, find the next node base on the given string
     * (continues the search from the previous node).
     *
     * @param prevNode   - The previous prefix node
     * @param nextString - The next string to be searched
     * @return - Returns null if no nodes are found otherwise return the node where the given
     * string ends.
     */
    private DictionaryNode findPrefixNodeFrom(DictionaryNode prevNode, String nextString) {
        DictionaryNode nextNode = prevNode;
        int i = 0;
        int nextPos;

        do {
            nextPos = nextString.charAt(i) - 'A';
            nextNode = nextNode.next[nextPos];
            i++;
        } while (i < nextString.length() && nextNode != null);

        return nextNode;
    }

    // Check if the word is already in the dictionary
    private boolean inDictionary(String word) {
        DictionaryNode node = get(root, word, 0);
        return node != null && node.word != null;
    }

    // Check if the word is already added to the result (thus already been added to the freq list)
    private boolean added(String word) {
        DictionaryNode node = get(checkFreg, word, 0);
        return node != null && node.word != null;
    }

    private DictionaryNode get(DictionaryNode x, String key, int currPos) {
        DictionaryNode result = null;
        if (x != null) {
            if (currPos == key.length()) result = x;
            else {
                int nextPos = key.charAt(currPos) - 'A';
                result = get(x.next[nextPos], key, currPos + 1);
            }
        }
        return result;
    }

    /**
     * Builds a graph of all the dices on the given board.
     * Each graph is a 2D array of GraphNode object.
     *
     * @param board - The BoggleBoard used to build the graph
     * @return - A 2D array of GraphNodes object
     */
    private GraphNode[][] buildGraph(BoggleBoard board) {
        int rows = board.rows();
        int cols = board.cols();

        // Add each dices into the 2d Array
        GraphNode[][] graph = new GraphNode[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                char label = board.getLetter(i, j);
                graph[i][j] = new GraphNode(label);
            }
        }

        // For each dices, add the corresponding adjacent dices to the adjency list
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                GraphNode node = graph[i][j];
                // Check the surrounding dices (8 in total)
                if (i - 1 >= 0) node.addEdge(graph[i - 1][j]);
                if (i - 1 >= 0 && j + 1 < cols) node.addEdge(graph[i - 1][j + 1]);
                if (j + 1 < cols) node.addEdge(graph[i][j + 1]);
                if (i + 1 < rows && j + 1 < cols) node.addEdge(graph[i + 1][j + 1]);
                if (i + 1 < rows) node.addEdge(graph[i + 1][j]);
                if (i + 1 < rows && j - 1 >= 0) node.addEdge(graph[i + 1][j - 1]);
                if (j - 1 >= 0) node.addEdge(graph[i][j - 1]);
                if (i - 1 >= 0 && j - 1 >= 0) node.addEdge(graph[i - 1][j - 1]);
            }
        }

        return graph;
    }

    // Generate all valid words and add them to the given queue
    private void generateWords(GraphNode[][] graph, Queue<String> result) {
        int rows = graph.length;
        int cols = graph[0].length;

        // For each dice in the board
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                getVallidWordsFrom(graph[i][j], root, result);
            }
        }
    }

    // Generate all the valid words from the given GraphNode (a dice in the BoggleBoard)
    // The search for all the valid words starts from the given previousNode
    private void getVallidWordsFrom(GraphNode gNode, DictionaryNode prevDNode,
                                    Queue<String> result) {
        gNode.visited = true; // Mark the state to avoid being visited twice

        char label = gNode.label;
        String queryStr;
        // Handle the letter 'Qu'
        if (label == 'Q') {
            queryStr = "QU";
        }
        else {
            queryStr = Character.toString(label);
        }

        // Find the next node
        DictionaryNode nextDNode = findPrefixNodeFrom(prevDNode, queryStr);
        // If there is a next node, which means there exist a prefix which is the same as the query string
        if (nextDNode != null) {
            // Checks if there is a word like the queryStr in the dicitonary
            // and the word is not already added to result
            if (nextDNode.word != null && nextDNode.word.length() > 2 && !added(nextDNode.word)) {
                result.enqueue(nextDNode.word);
                addWordToResult(nextDNode.word);
            }

            // Continues searching for all the surrounding dices
            for (GraphNode adj : gNode.adjs()) {
                // Can't visit a same dice twice
                if (!adj.visited) {
                    getVallidWordsFrom(adj, nextDNode, result);
                }
            }
        }
        gNode.visited = false;  // Reset the state to prepair for the next search
    }

    public static void main(String[] args) {
        In in = new In(args[0]);
        String[] dictionary = in.readAllStrings();
        BoggleSolver solver = new BoggleSolver(dictionary);
        BoggleBoard board = new BoggleBoard(args[1]);
        int score = 0;
        StdOut.println(board.toString());
        long startTime = System.currentTimeMillis();
        Iterable<String> result = solver.getAllValidWords(board);
        long endTime = System.currentTimeMillis();
        int count = 0;
        for (String word : result) {
            StdOut.println(word);
            count++;
            score += solver.scoreOf(word);
        }
        StdOut.println("Score = " + score);
        StdOut.println("Time = " + (double) (endTime - startTime) / (double) 1000);
        StdOut.println("Count = " + count);
    }
}
