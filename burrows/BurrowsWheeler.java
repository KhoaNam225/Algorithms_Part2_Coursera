/* *****************************************************************************
 *  Name: Khoa Nam Pham
 *  Date: 27/11/2019
 *  Description: Implementation of Burrows-Wheeler transformation
 **************************************************************************** */

import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

public class BurrowsWheeler {
    private static final int BIT_LENGTH = 8;
    private static final int R = 256;

    // apply Burrows-Wheeler transform,
    // reading from standard input and writing to standard output
    public static void transform() {
        String s = BinaryStdIn.readString();
        CircularSuffixArray suffixArray = new CircularSuffixArray(s);
        int first = 0;
        int length = suffixArray.length();

        // Find the first
        while (suffixArray.index(first) != 0) {
            first++;
        }

        // Write the Burrows-Wheeler sequence
        BinaryStdOut.write(first);
        for (int i = 0; i < length; i++) {
            int index = (suffixArray.index(i) + length - 1) % length;
            BinaryStdOut.write(s.charAt(index), BIT_LENGTH);
        }
        BinaryStdOut.close();
    }

    // apply Burrows-Wheeler inverse transform,
    // reading from standard input and writing to standard output
    public static void inverseTransform() {
        int first = BinaryStdIn.readInt();
        String s = BinaryStdIn.readString();
        int length = s.length();
        int[] next = new int[length];
        int[] count = new int[R + 1];

        // Use counting sort to construct the next array
        // next[j] = i means the next suffix after the jth suffix in the
        // original suffix array will be the ith suffix in the
        // sorted array
        for (int i = 0; i < length; i++) {
            count[s.charAt(i) + 1]++;
        }

        for (int i = 0; i < R; i++) {
            count[i + 1] += count[i];
        }

        for (int i = 0; i < length; i++) {
            int c = s.charAt(i);
            int index = count[c];
            next[index] = i;
            count[c]++;
        }

        // inverse the Burrows-Wheeler sequence
        int index = first;
        for (int i = 0; i < length; i++) {
            index = next[index];
            BinaryStdOut.write(s.charAt(index), BIT_LENGTH);
        }

        BinaryStdOut.close();
    }

    // if args[0] is "-", apply Burrows-Wheeler transform
    // if args[0] is "+", apply Burrows-Wheeler inverse transform
    public static void main(String[] args) {
        char mode = args[0].charAt(0);
        if (mode == '-') {
            transform();
        }
        else if (mode == '+') {
            inverseTransform();
        }
    }
}
