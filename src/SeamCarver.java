import edu.princeton.cs.algs4.Picture;

import java.awt.Color;
import java.util.Arrays;

public class SeamCarver {
    private Picture picture;
    private double[][] energyMatrix;
    private int height;
    private int width;
//    private vertex[][] vertices;

//    private class vertex{
//        private final int x;
//        private final int y;
//        public vertex(int x, int y){ this.x = x; this.y = y; }
//        public int X(){return x;}
//        public int Y(){return y;}
//    }

//    private class edge{
//        private final vertex v;
//        private final vertex w;
//        public edge(vertex v, vertex w){ this.v = v; this.w = w;}
//        public vertex v(){return v;}
//        public vertex w(){return w;}
//    }

    // compute the energy of pixel at column x and row y
    private double computeEnergy(int x, int y, boolean returnValue){
        if ((x == 0 || x == width() -1) || (y == 0 || y == height() -1)) { if(!returnValue){energyMatrix[x][y] = 1000;} return 1000;}
        Color _this = picture.get(x - 1, y);
        Color _that = picture.get(x + 1, y);
        int Rx = _this.getRed() - _that.getRed();
        int Gx = _this.getGreen() - _that.getGreen();
        int Bx = _this.getBlue() - _that.getBlue();

        _this = picture.get(x, y - 1);
        _that = picture.get(x, y + 1);
        int Ry = _this.getRed() - _that.getRed();
        int Gy = _this.getGreen() - _that.getGreen();
        int By = _this.getBlue() - _that.getBlue();

        double temp = Math.sqrt(Math.pow(Rx, 2) + Math.pow(Gx, 2) + Math.pow(Bx, 2) + Math.pow(Ry, 2) + Math.pow(Gy, 2) + Math.pow(By, 2));
        if(!returnValue) energyMatrix[x][y] = temp;
        return temp;
    }

    // create a seam carver object based on the given picture
    public SeamCarver(Picture picture){
        if (picture == null) throw new IllegalArgumentException("Null picture for constructor!");
        this.picture = new Picture(picture);
        this.height = picture.height();
        this.width = picture.width();

        // compute the energy for each pixel
        energyMatrix = new double[width()][height()];

        for (int i = 0; i < width(); i++){
            for (int t = 0; t < height(); t++) {
                computeEnergy(i, t, false);
            }
        }
        // construct vertex reference for methods.
//        vertices = new vertex[width][height];
//        vertexMap();
    }

    // current picture
    public Picture picture(){
        return new Picture(this.picture);
    }

    // width of current picture
    public int width(){
        return this.width;
    }

    // height of current picture
    public int height(){
        return this.height;
    }

    // return the precomputed energy
    public double energy(int x, int y) {
        if (x < 0 || x >= width || y < 0 || y >= height) throw new IllegalArgumentException("x or y not with in range!");
        return energyMatrix[x][y];
    }

//    private void vertexMap(){
//        for (int x = 0; x < width(); x++) {
//            for (int y = 0; y < height(); y++) {
//                vertices[x][y] = new vertex(x, y);
//            }
//        }
//    }

    private void relax(int vx, int vy, int wx, int wy, double[][] distTo, int[][] vertexTo, boolean horizontal){
        double weight = energyMatrix[wx][wy];
        if (distTo[wx][wy] > distTo[vx][vy] + weight){
            distTo[wx][wy] = distTo[vx][vy] + weight;
            vertexTo[wx][wy] = (horizontal) ? vy:vx;
        }
    }

    // sequence of indices for horizontal seam
    public int[] findHorizontalSeam(){
        // the graph is in topological order if visit each vertex from left to right, up to down.
        // relax edges outgoing from each vertex except from the last row
        double[][] distTo = new double[width()][height()];
        int[][] vertexTo = new int[width()][height()];

        int virtualSource = -1;

        // initialize the distance
        for(double[] d : distTo) {
            Arrays.fill(d, Double.POSITIVE_INFINITY);
        }

        // from virtual sink to the first row in the graph.
        for (int i = 0; i < height(); i++) {
            distTo[0][i] = 1000.0;
            vertexTo[0][i] = virtualSource;
        }

        // edges with in the graph except for the last column.
        for (int x = 0; x < width()-1; x++) {
            for (int y = 0; y < height(); y++) {
                if (y != 0) relax(x, y, x+1,y-1, distTo, vertexTo, true);
                relax(x, y, x+1, y, distTo, vertexTo, true);
                if (y != height() -1) relax(x, y, x+1, y+1, distTo, vertexTo, true);
            }
        }

        // the final distance from the source to sink
        double dist = Double.POSITIVE_INFINITY;
        int vertex = 0;

        // edges from the last row in the graph to the virtual sink
        for (int i = 0; i < height(); i++) {
            if(dist > distTo[width()-1][i]){
                dist = distTo[width()-1][i];
                vertex = i;
            }
        }

        // reconstruct the path
        int[] seam = new int[width()];
        seam[width()-1] = vertex;
        for (int i = 1; i < width(); i++) {
            vertex = vertexTo[width() - i][vertex];
            seam[width() - 1 - i] = vertex;
        }

        return seam;

    }

    // sequence of indices for vertical seam
    public int[] findVerticalSeam(){
        // the graph is in topological order if visit each vertex from left to right, up to down.
        // relax edges outgoing from each vertex except from the last row
        double[][] distTo = new double[width()][height()];
        int[][] vertexTo = new int[width()][height()];

        int virtualSource = -1;

        // initialize the distance
        for(double[] d : distTo) {
            Arrays.fill(d, Double.POSITIVE_INFINITY);
        }

        // from virtual sink to the first row in the graph
        for (int i = 0; i < width(); i++) {
            distTo[i][0] = 1000.0;
            vertexTo[i][0] = virtualSource;
        }

        // edges with in the graph except for the last row
        for (int y = 0; y < height()-1; y++) {
            for (int x = 0; x < width(); x++) {
                if(x != 0) relax(x, y, x-1, y+1, distTo, vertexTo, false);
                relax(x, y, x, y+1, distTo, vertexTo, false);
                if (x != width() -1) relax(x, y, x+1, y+1, distTo, vertexTo, false);
            }
        }

        // the final distance from the source to sink
        double dist = Double.POSITIVE_INFINITY;
        int vertex = 0;

        // edges from the last row in the graph to the virtual sink
        for (int i = 0; i < width(); i++) {
            if(dist > distTo[i][height()-1]){
                dist = distTo[i][height()-1];
                vertex = i;
            }
        }

        // reconstruct the path
        int[] seam = new int[height()];
        seam[height()-1] = vertex;
        for (int i = 1; i < height(); i++) {
            vertex = vertexTo[vertex][height() - i];
            seam[height() - 1 - i] = vertex;
        }

        return seam;
    }

    // remove horizontal seam from current picture
    public void removeHorizontalSeam(int[] seam){
        if (height <= 1) throw new IllegalArgumentException("Picture too small.");
        validateHorizontalSeam(seam);
        double[][] energies = new double[width][height-1];
        Picture p = new Picture(width, height-1);
        // update picture first
        for (int x = 0; x < width(); x++) {
            for (int y = 0; y < height()-1; y++) {
                p.set(x, y, this.picture.get(x,(y>=seam[x]) ? y+1:y));
            }
        }
        this.picture = p;
        this.height = this.height - 1;
        // Update the energy
        for (int x = 0; x < width(); x++) {
            for (int y = 0; y < height(); y++) {
                energies[x][y] = (y == seam[x] - 1 || y == seam[x]) ? computeEnergy(x,y, true) : energyMatrix[x][(y>=seam[x]) ? y+1:y];
            }
        }
        this.energyMatrix = energies;
    }

    // remove vertical seam from current picture
    public void removeVerticalSeam(int[] seam){
        if (width <= 1) throw new IllegalArgumentException("Picture too small.");
        validateVerticalSeam(seam);
        double[][] energies = new double[width-1][height];
        Picture p = new Picture(width-1, height);
        for (int x = 0; x < width()-1; x++) {
            for (int y = 0; y < height(); y++) {
                p.set(x, y, this.picture.get((x>=seam[y]) ? x+1:x, y));
            }
        }
        this.picture = p;
        this.width = this.width - 1;
        // Update the energy
        for (int x = 0; x < width(); x++) {
            for (int y = 0; y < height(); y++) {
                energies[x][y] = (x == seam[y] - 1 ||x == seam[y]) ? computeEnergy(x, y,true) : energyMatrix[(x>=seam[y]) ? x+1:x][y];
            }
        }
        this.energyMatrix = energies;

    }

    private void validateVerticalSeam(int[] seam){
        if (seam == null || seam.length != height) throw new IllegalArgumentException("null seam");
        int prev = seam[0];
        for(int pixel : seam){
            if (pixel < 0 || pixel >= width || Math.abs(prev - pixel) > 1) { throw new IllegalArgumentException("Illegal seam");}
            prev = pixel;
        }
    }

    private void validateHorizontalSeam(int[] seam){
        if (seam == null || seam.length != width) throw new IllegalArgumentException("null seam");
        int prev = seam[0];
        for(int pixel : seam){
            if (pixel < 0 || pixel >= height || Math.abs(prev - pixel) > 1) { throw new IllegalArgumentException("Illegal seam");}
            prev = pixel;
        }
    }

    //  unit testing (optional)
    public static void main(String[] args){

    }

}