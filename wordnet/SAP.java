/* *****************************************************************************
 *  Name:   Khoa Nam Pham
 *  Date:   10/08/2019
 *  Description:    SAP implementation for Algorithms Part II by
 *                  Princeton University on coursera.org
 **************************************************************************** */

import edu.princeton.cs.algs4.BreadthFirstDirectedPaths;
import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

public class SAP {
    private final Digraph graph;

    public SAP(Digraph G) {
        if (G == null) {
            throw new IllegalArgumentException("Null graph");
        }

        this.graph = new Digraph(G);
    }

    // length of shortest ancestral path between v and w; -1 if no such path
    public int length(int v, int w) {
        int length;
        if (v == w) {
            length = 0;
        }
        else {
            BreadthFirstDirectedPaths bfsW = new BreadthFirstDirectedPaths(graph, w);
            BreadthFirstDirectedPaths bfsV = new BreadthFirstDirectedPaths(graph, v);
            length = length(bfsV, bfsW);
        }
        return length;
    }

    // a common ancestor of v and w that participates in a shortest ancestral path; -1 if no such path
    public int ancestor(int v, int w) {
        int anc;  // The ancestor
        if (v == w) {
            anc = v;
        }
        else {
            BreadthFirstDirectedPaths bfsW = new BreadthFirstDirectedPaths(graph, w);
            BreadthFirstDirectedPaths bfsV = new BreadthFirstDirectedPaths(graph, v);
            anc = ancestor(bfsV, bfsW);
        }
        return anc;
    }

    // length of shortest ancestral path between any vertex in v and any vertex in w; -1 if no such path
    public int length(Iterable<Integer> v, Iterable<Integer> w) {
        int length;
        try {
            BreadthFirstDirectedPaths bfsW = new BreadthFirstDirectedPaths(graph, w);
            BreadthFirstDirectedPaths bfsV = new BreadthFirstDirectedPaths(graph, v);
            length = length(bfsV, bfsW);
        }
        catch (NullPointerException e) {
            throw new IllegalArgumentException("Null vertex in Interable.");
        }
        return length;
    }

    // a common ancestor that participates in shortest ancestral path; -1 if no such path
    public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
        int anc;  // The ancestor

        try {
            BreadthFirstDirectedPaths bfsW = new BreadthFirstDirectedPaths(graph, w);
            BreadthFirstDirectedPaths bfsV = new BreadthFirstDirectedPaths(graph, v);
            anc = ancestor(bfsV, bfsW);
        }
        catch (NullPointerException e) {
            throw new IllegalArgumentException("Null vertex in Iterable.");
        }

        return anc;
    }

    /**
     * Computes the ancestor in the shortest ancestral path between two vertices v and w when given
     * the BreadthFirstDirectedPath of those two vertices.
     *
     * @param bfsV - BreadthFirstDirectedPath from v
     * @param bfsW - BreadthFirstDirectedPath from w
     * @return The common ancestor of v and w in the shortest ancestral path
     */
    private int ancestor(BreadthFirstDirectedPaths bfsV, BreadthFirstDirectedPaths bfsW) {
        int anc = -1;
        int length;  // length

        boolean found = false;      // Checks if there is no common ancestor
        int i = 0;
        while (!found && i < graph.V()) {
            found = bfsV.hasPathTo(i) && bfsW.hasPathTo(i);
            i++;
        }

        // If the is an ancestor, keep searching
        if (found) {
            length = bfsV.distTo(i - 1) + bfsW.distTo(i - 1);
            anc = i - 1;
            while (i < graph.V()) {
                if (bfsW.hasPathTo(i) && bfsW.distTo(i) < length) {
                    if (bfsV.hasPathTo(i) && bfsV.distTo(i) < length
                            && bfsV.distTo(i) + bfsW.distTo(i) < length) {
                        length = bfsV.distTo(i) + bfsW.distTo(i);
                        anc = i;
                    }
                }
                i++;
            }
        }

        return anc;
    }

    /**
     * Computes the length of the shortest ancestral path between two vertices v and w when given
     * the BreadthFirstDirectedPath of those two vertices.
     *
     * @param bfsV - BreadthFirstDirectedPath from v
     * @param bfsW - BreadthFirstDirectedPath from w
     * @return The length of the shortest ancestral path of v and w
     */
    private int length(BreadthFirstDirectedPaths bfsV, BreadthFirstDirectedPaths bfsW) {
        int length = -1;  // length

        boolean found = false;      // Checks if there is no common ancestor
        int i = 0;
        while (!found && i < graph.V()) {
            found = bfsV.hasPathTo(i) && bfsW.hasPathTo(i);
            i++;
        }

        // If the is an ancestor, keep searching
        if (found) {
            length = bfsV.distTo(i - 1) + bfsW.distTo(i - 1);
            while (i < graph.V()) {
                if (bfsW.hasPathTo(i) && bfsW.distTo(i) < length) {
                    if (bfsV.hasPathTo(i) && bfsV.distTo(i) < length
                            && bfsV.distTo(i) + bfsW.distTo(i) < length) {
                        length = bfsV.distTo(i) + bfsW.distTo(i);
                    }
                }
                i++;
            }
        }

        return length;
    }

    // do unit testing of this class
    public static void main(String[] args) {
        In in = new In(args[0]);
        Digraph G = new Digraph(in);
        SAP sap = new SAP(G);
        while (!StdIn.isEmpty()) {
            int v = StdIn.readInt();
            int w = StdIn.readInt();
            int length = sap.length(v, w);
            int ancestor = sap.ancestor(v, w);
            StdOut.printf("length = %d, ancestor = %d\n", length, ancestor);
        }
    }
}
