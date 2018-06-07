import edu.princeton.cs.algs4.Picture;
import edu.princeton.cs.algs4.Bag;

public class SeamCarver {
    private int[][] picture;
    private int height, width;
    
    // create a seam carver object based on the given picture
    public SeamCarver(Picture picture) {
        height = picture.height();
        width = picture.width();
        this.picture = new int[width][height];
        for (int col = 0; col < width; col++) {
            for (int row = 0; row < height; row++) {
                this.picture[col][row] = picture.getRGB(col, row);
            }
        }
    }
    
    // current picture
    public Picture picture() {
        Picture currPicture = new Picture(width, height);
        for (int col = 0; col < width; col++) {
            for (int row = 0; row < height; row++) {
                currPicture.setRGB(col, row, picture[col][row]);
            }
        }
        return currPicture;
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
        if (x == 0 || y == 0 || y == height - 1 || x == width - 1) return 1000.0;
        else {
            double squared = deltaSquared(x + 1, y, x - 1, y) + deltaSquared(x, y + 1, x, y - 1);
            return Math.sqrt(squared);
        }
    }
    
    // helper methods to get a list of RGB numbers
    private int[] rgb(int col, int row) {
        int[] colors = new int[3];
        int rgb = picture[col][row];
        colors[0] = (rgb >> 16) & 0xFF;
        colors[1] = (rgb >>  8) & 0xFF;
        colors[2] = (rgb >>  0) & 0xFF;
        return colors;
    }
    
    // helper methods to compute delta
    private double deltaSquared(int col1, int row1, int col2, int row2) {
        int[] colors1 = rgb(col1, row1);
        int[] colors2 = rgb(col2, row2);
        double sum = 0;
        for (int i = 0; i < 3; i++) {
            sum += (colors2[i] - colors1[i]) * (colors2[i] - colors1[i]);
        }
        return sum;
    }
    
    // sequence of indices for vertical seam
    // the underlying DAG has an implied topological order.
    // start from a virtual pixel above the top row and
    // relax all its adjacent pixels in the topological order
    public int[] findVerticalSeam() {
        double[][] energy;       // 2d array storing energy
        energy = new double[width][height];
        for (int col = 0; col < width; col++) {
            for (int row = 1; row < height; row++)
                energy[col][row] = energy(col, row);
        }
        
        int[] verticalSeam = new int[height];
        int[][] colTo = new int[width][height];
        double[][] energyTo = new double[width][height];
        for (int col = 0; col < width; col++) {
            for (int row = 1; row < height; row++)
                energyTo[col][row] = Double.POSITIVE_INFINITY;
        }
        
        for (int row = 0; row < height - 1; row++) {
            for (int col = 0; col < width; col++) {               
                for (int x : adj(col)) {
                    if (energyTo[col][row] + energy[x][row + 1] < energyTo[x][row + 1]) {
                        energyTo[x][row + 1] = energyTo[col][row] + energy[x][row + 1];
                        colTo[x][row + 1] = col;
                    }
                }
            }
        }
        
        if (height < 3) {
            for (int i = 0; i < height; i++)
                verticalSeam[i] = 0;
        }
        else {
            for (int col = 0; col < width; col++) {
                if (energyTo[col][height - 2] < energyTo[verticalSeam[height - 2]][height - 2])
                    verticalSeam[height - 2] = col;
            }
            verticalSeam[height - 1] = verticalSeam[height - 2];
            for (int i = height - 3; i >= 0; i--)    
                verticalSeam[i] = colTo[verticalSeam[i + 1]][i + 1];
        }
        return verticalSeam;
    }
    
    // column index of pixels adjacant to a pixel
    private Iterable<Integer> adj(int col) {
        Bag<Integer> adj = new Bag<Integer>();
        if (col != width - 1) adj.add(col + 1);
        adj.add(col);
        if (col != 0) adj.add(col - 1);
        return adj;
    }
    
    // sequence of indices for horizontal seam
    // transpose the picture, find the vertial seam and transpose back
    public int[] findHorizontalSeam() {
        transpose();
        int[] horizontalSeam = findVerticalSeam();
        transpose();
        
        return horizontalSeam;
    }
    
    // transpose the picture
    private void transpose() {
        int[][] pictureTrans = new int[height][width];
        for (int col = 0; col < height; col++) {
            for (int row = 0; row < width; row++) {
                pictureTrans[col][row] = picture[row][col];
            }
        }
        
        picture = new int[height][width];
        for (int col = 0; col < height; col++) {
            for (int row = 0; row < width; row++) {
                picture[col][row] = pictureTrans[col][row];
            }
        }
        int temp = width;
        width = height;
        height = temp;
    }
    
    // remove vertical seam from current picture
    public void removeVerticalSeam(int[] seam) {
        int[][] removed = new int[width - 1][height];
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < seam[row]; col++)
                removed[col][row] = picture[col][row];
            for (int col = seam[row]; col < width - 1; col++)
                removed[col][row] = picture[col + 1][row];
        }
        picture = new int[width - 1][height];
        for (int col = 0; col < width - 1; col++)
            System.arraycopy(removed[col], 0, picture[col], 0, height);
        width--;
    }
    
    // remove horizontal seam from current picture
    public void removeHorizontalSeam(int[] seam) {
        transpose();
        removeVerticalSeam(seam);
        transpose();
    }
    
    public static void main(String[] args) {
        Picture inputImg = new Picture(args[0]);
        SeamCarver sc = new SeamCarver(inputImg);
        Picture outputImg = sc.picture();
        inputImg.show();
        outputImg.show();
    }
}
