/* *****************************************************************************
 *  Name:   Khoa Nam Pham
 *  Date:   28/08/2019
 *  Description:    Seam Carver implementation using graph processing algorithm
 **************************************************************************** */

import edu.princeton.cs.algs4.Picture;

public class SeamCarver {
    private int[][] pic;  // The picture
    private int width;    // The width of the picture
    private int height;   // The height of the picture

    // create a seam carver object based on the given picture
    public SeamCarver(Picture picture) {
        if (picture == null) {
            throw new IllegalArgumentException("Null picture");
        }

        height = picture.height();
        width = picture.width();

        pic = new int[height][width];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                pic[i][j] = picture.getRGB(j, i);
            }
        }
    }

    // current picture
    public Picture picture() {
        Picture picture = new Picture(width, height);
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                picture.setRGB(j, i, pic[i][j]);
            }
        }

        return picture;
    }

    // width of current picture
    public int width() {
        return width;
    }

    // height of current picture
    public int height() {
        return height;
    }

    // energy of pixel at column x and row y
    public double energy(int x, int y) {
        if (x < 0 || x > width - 1 || y < 0 || y > height - 1) {
            throw new IllegalArgumentException("Pixel position out of bound.");
        }

        double energy;
        // Pixels at the edges of the picture
        if (x == 0 || x == width - 1 || y == 0 || y == height - 1) {
            energy = 1000;
        }
        else {
            int leftRGB = pic[y][x - 1];  // Pixel to the left
            int rightRGB = pic[y][x + 1]; // Pixel to the right
            int upRGB = pic[y - 1][x];    // Upper pixel
            int downRGB = pic[y + 1][x];  // Lower pixel

            double squareGradientX = squareGradient(leftRGB, rightRGB);
            double squareGradientY = squareGradient(upRGB, downRGB);

            energy = Math.sqrt(squareGradientX + squareGradientY);
        }

        return energy;
    }

    // sequence of indices for horizontal seam
    public int[] findHorizontalSeam() {
        int[][] temp
                = pic;   // Save the current pic so that we don't have to transpose back to the original pic
        pic = transpose(pic);  // Transopose the picture
        swapWidthHeight();     // Now width becomes height and height becomes width

        int[] seam = findVerticalSeam(pic);   // Find the vertical seam of the tranposed pic
        // This seam will be the horizontal seam of the original pic

        // Restore the original state
        pic = temp;
        swapWidthHeight();

        return seam;
    }

    // sequence of indices for vertical seam
    public int[] findVerticalSeam() {
        return findVerticalSeam(pic);
    }

    // remove horizontal seam from current picture
    public void removeHorizontalSeam(int[] seam) {
        if (seam == null) {
            throw new IllegalArgumentException("Null seam.");
        }

        if (!validateHorizontalSeam(seam)) {
            throw new IllegalArgumentException("Invalid seam.");
        }

        if (height <= 1) {
            throw new IllegalArgumentException("Picture is too small");
        }

        // Transpose the pic
        pic = transpose(pic);
        swapWidthHeight();

        // Remove the vertical seam of the tranposed pic, that is equivalent to
        // removing the horizontal seam of the original pic
        removeVerticalSeam(seam);

        // Restore the original state
        pic = transpose(pic);
        swapWidthHeight();
    }

    // remove vertical seam from current picture
    public void removeVerticalSeam(int[] seam) {
        if (seam == null) {
            throw new IllegalArgumentException("Null seam.");
        }

        if (!validateVerticalSeam(seam)) {
            throw new IllegalArgumentException("Invalid seam");
        }

        if (width <= 1) {
            throw new IllegalArgumentException("Picture is too small.");
        }

        // Remove the seam
        // Traverse row by row, in each row, left shift the pixels to the right of
        // the seam by one
        for (int i = 0; i < seam.length; i++) {
            int pos = seam[i];
            System.arraycopy(pic[i], pos + 1, pic[i], pos, width - pos - 1);
        }

        width--;
    }

    /**
     * Calculate the square gradient of the current pixel based on the color of the neighbors (up -
     * down, left - right)
     *
     * @param xRGB - one neighbor
     * @param yRGB - the other neighbor
     * @return - the square gradent
     */
    private double squareGradient(int xRGB, int yRGB) {
        int rX = getRed(xRGB);
        int gX = getGreen(xRGB);
        int bX = getBlue(xRGB);

        int rY = getRed(yRGB);
        int gY = getGreen(yRGB);
        int bY = getBlue(yRGB);

        return Math.pow(rY - rX, 2) + Math.pow(gY - gX, 2) + Math.pow(bY - bX, 2);
    }

    /**
     * Get the red component of the current color
     *
     * @param rgb - encoded integer representing the rgb color
     * @return - an integer for the red component (between 0 and 255)
     */
    private int getRed(int rgb) {
        return (rgb >> 16) & 0xff;
    }

    /**
     * Get the green component of the current color
     *
     * @param rgb - encoded integer representing the rgb color
     * @return - an integer for the green component (between 0 and 255)
     */
    private int getGreen(int rgb) {
        return (rgb >> 8) & 0xff;
    }

    /**
     * Get the blue component of the current color
     *
     * @param rgb - encoded integer representing the rgb color
     * @return - an integer for the bue component (between 0 and 255)
     */
    private int getBlue(int rgb) {
        return rgb & 0xff;
    }

    /**
     * Finds the minimum energy vertical seam of a picture based on the pixels of that picture
     *
     * @param picture - A 2D array representing the picture, each elements is a rgb integer
     * @return - An array which contains the position of the pixel belong to the seam
     */
    private int[] findVerticalSeam(int[][] picture) {
        double[][] energy = calEnergy(height, width);  // Calculate the energy of each pixel
        int[][] pathTo = new int[height][width];  // Path to each pixel in the picture
        double[][] energyTo = initEnergyTo(height, width); // energy to each pixel in the picture
        // calculated from the top row

        // Relax each pixel inn topological order
        // Top to bottom, left to right
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                relax(i, j, energy, energyTo, pathTo);
            }
        }

        return findSeam(energyTo, pathTo);
    }

    /**
     * Calculate the energy of each pixel in the picture
     *
     * @param picHeight - Picture height
     * @param picWidth  - Picture width
     * @return - A 2D array containing energy of each pixel
     */
    private double[][] calEnergy(int picHeight, int picWidth) {
        double[][] energy = new double[picHeight][picWidth];
        for (int i = 0; i < picWidth; i++) {
            for (int j = 0; j < picHeight; j++) {
                energy[j][i] = this.energy(i, j);
            }
        }

        return energy;
    }

    /**
     * Initial the first state of the energyTo 2D array. In the first state, the pixels in the top
     * row will have to energyTo equals 0 and all the other pixels equals POSITIVE_INFINITY
     *
     * @param picHeight - Picture height
     * @param picWidth  - Picture width
     * @return - A 2D array  with the initial state
     */
    private double[][] initEnergyTo(int picHeight, int picWidth) {
        double[][] energyTo = new double[picHeight][picWidth];

        for (int i = 0; i < picHeight; i++) {
            for (int j = 0; j < picWidth; j++) {
                if (i == 0) {
                    energyTo[i][j] = 0;
                }
                else {
                    energyTo[i][j] = Double.POSITIVE_INFINITY;
                }
            }
        }

        return energyTo;
    }

    /**
     * Relax a vertex (a pixel)
     *
     * @param row      - Row of the pixel
     * @param col      - Column of the pixel
     * @param energy   - Energy of all the pixels in the picture
     * @param energyTo - Energy to all the pixels (along the seam) in the picture
     * @param pathTo   - Path to each pixel in the picture
     */
    private void relax(int row, int col, double[][] energy, double[][] energyTo, int[][] pathTo) {
        int height = energy.length;
        int width = energy[0].length;
        // If haven'r reached the last row yet
        if (row < height - 1) {
            // Pixels at the most left
            if (col == 0 && col != width - 1) {
                if (energyTo[row + 1][col + 1] > energyTo[row][col] + energy[row + 1][col + 1]) {
                    energyTo[row + 1][col + 1] = energyTo[row][col] + energy[row + 1][col + 1];
                    pathTo[row + 1][col + 1] = col;
                }
            }
            // Pixels at the most right
            else if (col == width - 1 && col != 0) {
                if (energyTo[row + 1][col - 1] > energyTo[row][col] + energy[row + 1][col - 1]) {
                    energyTo[row + 1][col - 1] = energyTo[row][col] + energy[row + 1][col - 1];
                    pathTo[row + 1][col - 1] = col;
                }
            }
            // The other pixels
            else if (col > 0 && col < width - 1) {
                if (energyTo[row + 1][col + 1] > energyTo[row][col] + energy[row + 1][col + 1]) {
                    energyTo[row + 1][col + 1] = energyTo[row][col] + energy[row + 1][col + 1];
                    pathTo[row + 1][col + 1] = col;
                }

                if (energyTo[row + 1][col - 1] > energyTo[row][col] + energy[row + 1][col - 1]) {
                    energyTo[row + 1][col - 1] = energyTo[row][col] + energy[row + 1][col - 1];
                    pathTo[row + 1][col - 1] = col;
                }
            }

            if (energyTo[row + 1][col] > energyTo[row][col] + energy[row + 1][col]) {
                energyTo[row + 1][col] = energyTo[row][col] + energy[row + 1][col];
                pathTo[row + 1][col] = col;
            }
        }
    }

    /**
     * Based on the energyTo and pathTo, find the minimum seam
     *
     * @param energyTo - The energyTo array
     * @param pathTo   - Path to array
     * @return - The seam
     */
    private int[] findSeam(double[][] energyTo, int[][] pathTo) {
        int minPos = findMinSeamEnergyPos(
                energyTo); // The position (in the last row) of the pixel that has the min seam
        return findSeam(pathTo, minPos);
    }

    /**
     * Based on the position of the last pixel in the minimum seam, track back to find all the other
     * pixels in the seam
     *
     * @param pathTo - Path to each seam in the picture
     * @param minPos - The position of the last pixel in the min seam
     * @return - The seam
     */
    private int[] findSeam(int[][] pathTo, int minPos) {
        int height = pathTo.length;
        int[] seam = new int[height];
        int min = minPos;

        for (int i = height; i > 0; i--) {
            seam[i - 1] = min;
            min = pathTo[i - 1][min];
        }

        return seam;
    }

    /**
     * Find the pixel that has the minimum energyTo in the last row
     *
     * @param energyTo - energyTo each pixels
     * @return - the position (column number) of the pixel with minimum seam
     */
    private int findMinSeamEnergyPos(double[][] energyTo) {
        int height = energyTo.length;
        int width = energyTo[0].length;
        double min = energyTo[height - 1][0];
        int pos = 0;

        for (int i = 0; i < width; i++) {
            if (min > energyTo[height - 1][i]) {
                min = energyTo[height - 1][i];
                pos = i;
            }
        }

        return pos;
    }

    /**
     * Transpose the picture
     *
     * @param picture - The picture need to be transposed
     * @return - The transposed picture
     */
    private int[][] transpose(int[][] picture) {
        int[][] newPic = new int[width][height];

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                newPic[j][i] = picture[i][j];
            }
        }

        return newPic;
    }

    /**
     * Check if the given seam is valid or not. The seam is invalid if is has the length less than
     * the height of the picture or it has any items that is out of bound. (< 0 or > width)
     *
     * @param seam - The given seam
     * @return - True if seam is valid otherwise false
     */
    private boolean validateVerticalSeam(int[] seam) {
        boolean isValid = true;
        if (seam.length != height) {
            isValid = false;
        }
        else {
            int i = 0;
            while (isValid && i < seam.length - 1) {
                isValid = (Math.abs(seam[i] - seam[i + 1]) <= 1) && (seam[i] >= 0
                        && seam[i] < width);
                i++;
            }

            if (isValid) {
                isValid = seam[i] >= 0 && seam[i] < width;
            }
        }

        return isValid;
    }

    /**
     * Check if the given seam is valid or not. The seam is invalid if is has the length less than
     * the width of the picture or it has any items that is out of bound. (< 0 or > height)
     *
     * @param seam - The given seam
     * @return - True if seam is valid otherwise false
     */
    private boolean validateHorizontalSeam(int[] seam) {
        boolean isValid = true;
        if (seam.length != width) {
            isValid = false;
        }
        else {
            int i = 0;
            while (isValid && i < seam.length - 1) {
                isValid = (Math.abs(seam[i] - seam[i + 1]) <= 1) && (seam[i] >= 0
                        && seam[i] < height);
                i++;
            }

            if (isValid) {
                isValid = seam[i] >= 0 && seam[i] < height;
            }
        }

        return isValid;
    }

    /**
     * Swap between width and height for transposing purpose
     */
    private void swapWidthHeight() {
        int temp = width;
        width = height;
        height = temp;
    }

    // Unit test
    public static void main(String[] args) {

    }
}
