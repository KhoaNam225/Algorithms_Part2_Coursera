/* *****************************************************************************
 *  Name: Khoa Nam Pham
 *  Date: 27/11/2019
 *  Description: Implementaion of move-to-front encoding
 **************************************************************************** */

import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

public class MoveToFront {
    private static final int R = 256;
    private static final int BIT_LENGTH = 8;

    // apply move-to-front encoding, reading from standard input and writing to standard output
    public static void encode() {
        int[] alphabet = new int[R];
        for (int i = 0; i < R; i++) {
            alphabet[i] = i;
        }

        // Keep scanning the alphabet until found the character
        while (!BinaryStdIn.isEmpty()) {
            char c = BinaryStdIn.readChar(BIT_LENGTH);
            int i = 0;
            while (alphabet[i] != c) {
                i++;
            }
            BinaryStdOut.write(i, BIT_LENGTH);  // Write the position of the character
            // Move all the elements at index 0 to i - 1 to the right 1 position
            System.arraycopy(alphabet, 0, alphabet, 1, i);
            alphabet[0] = c; // Brings the character to the front
        }

        BinaryStdOut.close();
    }

    // apply move-to-front decoding, reading from standard input and writing to standard output
    public static void decode() {
        int[] alphabet = new int[R];
        for (int i = 0; i < R; i++) {
            alphabet[i] = i;
        }

        // Do the same thing as encode()
        while (!BinaryStdIn.isEmpty()) {
            int index = BinaryStdIn.readInt(BIT_LENGTH);
            int c = alphabet[index];
            BinaryStdOut.write(c, BIT_LENGTH);
            System.arraycopy(alphabet, 0, alphabet, 1, index);
            alphabet[0] = c;
        }

        BinaryStdOut.close();
    }

    // if args[0] is "-", apply move-to-front encoding
    // if args[0] is "+", apply move-to-front decoding
    public static void main(String[] args) {
        char mode = args[0].charAt(0);
        if (mode == '-') {
            encode();
        }
        else {
            decode();
        }
    }
}
