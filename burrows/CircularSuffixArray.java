/* *****************************************************************************
 *  Name: Khoa Nam Pham
 *  Date: 27/11/2019
 *  Description: Implementation of circular suffix array data structure
 **************************************************************************** */

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

// The CircularSuffixArray data structure
// A string s of length n will have n circular suffixes
// The ith suffix will start at the ith character in the original string
public class CircularSuffixArray {
    private final int[] offsets;   // The offset (the start position of each suffix
    private final String str;   // The original string
    private final int length;   // The length of the original string

    // circular suffix array of s
    public CircularSuffixArray(String s) {
        if (s == null) {
            throw new IllegalArgumentException("Null string passed to CircularSuffixArray");
        }

        str = s;
        length = s.length();
        offsets = new int[length];

        for (int i = 0; i < length; i++) {
            offsets[i] = i;
        }

        sortSuffixes(offsets, 0, length - 1);  // Sort all the suffixes
    }

    // length of s
    public int length() {
        return offsets.length;
    }

    // returns index of ith sorted suffix
    public int index(int i) {
        if (i < 0 || i >= length()) {
            throw new IllegalArgumentException("Index out of bound");
        }

        return offsets[i];
    }

    // return the character at the given index of the given suffixes (specified by the offset)
    private int charAt(int offset, int index) {
        int realIndex = (offset + index) % offsets.length;
        return str.charAt(realIndex);
    }

    // Swap 2 elements in an arrray of integer
    private void swap(int[] arr, int i, int j) {
        int temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }

    // Compare 2 suffixes, returns -1 if suffix i is less than j
    private int compare(int i, int j) {
        int result = 0;
        int k = 0;

        // Keep comparing each character in the string
        while (k < length && result == 0) {
            int iChar = charAt(i, k);
            int jChar = charAt(j, k);
            if (iChar < jChar) {
                result = -1;
            }
            else if (iChar > jChar) {
                result = 1;
            }

            k++;
        }

        return result;
    }

    // Sort all the suffixes using the 3-way quicksort algorithm
    private void sortSuffixes(int[] suffixes, int low, int high) {
        if (low <= high) {
            int lessThan = low;
            int greaterThan = high;
            int i = lessThan + 1;
            int val = suffixes[low];

            while (i <= greaterThan) {
                int comp = compare(val, suffixes[i]);
                if (comp < 0) {
                    swap(suffixes, greaterThan, i);
                    greaterThan--;
                }
                else if (comp > 0) {
                    swap(suffixes, lessThan, i);
                    lessThan++;
                    i++;
                }
                else
                    i++;
            }

            sortSuffixes(suffixes, low, lessThan - 1);
            sortSuffixes(suffixes, greaterThan + 1, high);
        }
    }

    // unit testing (required)
    public static void main(String[] args) {
        In in = new In(args[0]);
        String s = in.readAll();
        CircularSuffixArray csa = new CircularSuffixArray(s);
        int length = csa.length();
        StdOut.println(length);
        for (int i = 0; i < length; i++) {
            StdOut.println(csa.index(i));
        }
    }
}
