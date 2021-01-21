import edu.princeton.cs.algs4.Picture;
import java.util.Stack;
import java.awt.*;
import java.util.Arrays;

public class SeamCarver {
    private Picture picture;
    private double[][] energyMatrix;
    private int height;
    private int width;
    private vertex[][] vertices;

    private class vertex{
        private final int x;
        private final int y;
        public vertex(int x, int y){ this.x = x; this.y = y; }
        public int X(){return x;}
        public int Y(){return y;}
    }

    private class edge{
        private final vertex v;
        private final vertex w;
        public edge(vertex v, vertex w){ this.v = v; this.w = w;}
        public vertex v(){return v;}
        public vertex w(){return w;}
    }

    // compute the energy of pixel at column x and row y
    private void computeEnergy(int x, int y){
        if ((x == 0 || x == width() -1) || (y == 0 || y == height() -1)) {energyMatrix[x][y] = 1000; return;}
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

        energyMatrix[x][y] = Math.sqrt(Math.pow(Rx, 2) + Math.pow(Gx, 2) + Math.pow(Bx, 2) + Math.pow(Ry, 2) + Math.pow(Gy, 2) + Math.pow(By, 2));
    }

    // create a seam carver object based on the given picture
    public SeamCarver(Picture picture){
        this.picture = picture;
        this.height = picture.height();
        this.width = picture.width();

        // compute the energy for each pixel
        energyMatrix = new double[width()][height()];

        for (int i = 0; i < width(); i++){
            for (int t = 0; t < height(); t++) {
                computeEnergy(i, t);
            }
        }
        // construct vertex reference for methods.
        vertices = new vertex[width][height];
        vertexMap();
    }

    // current picture
    public Picture picture(){
        return this.picture;
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
        return energyMatrix[x][y];
    }

    private void vertexMap(){
        for (int x = 0; x < width(); x++) {
            for (int y = 0; y < height(); y++) {
                vertices[x][y] = new vertex(x, y);
            }
        }
    }

    private void relax(edge e, double[][] distTo, vertex[][] vertexTo){
        vertex v = e.v();
        vertex w = e.w();
        double weight = energyMatrix[w.X()][w.Y()];
        if (distTo[w.X()][w.Y()] > distTo[v.X()][v.Y()] + weight){
            distTo[w.X()][w.Y()] = distTo[v.X()][v.Y()] + weight;
            vertexTo[w.X()][w.Y()] = v;
        }
    }

    // sequence of indices for horizontal seam
    public int[] findHorizontalSeam(){
        return new int[1];
    }

    // sequence of indices for vertical seam
    public int[] findVerticalSeam(){
        // the graph is in topological order if visit each vertex from left to right, up to down.
        // relax edges outgoing from each vertex except from the last row
        double[][] distTo = new double[width()][height()];
        vertex[][] vertexTo = new vertex[width()][height()];

        vertex virtualSource = new vertex(-1, -1);

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
                if(x == 0){
                    // left edge
                    relax(new edge(vertices[x][y], vertices[x][y+1]), distTo, vertexTo);
                    relax(new edge(vertices[x][y], vertices[x+1][y+1]), distTo, vertexTo);
                }
                else  if (x == width() -1){
                    // right edge
                    relax(new edge(vertices[x][y], vertices[x-1][y+1]), distTo, vertexTo);
                    relax(new edge(vertices[x][y], vertices[x][y+1]), distTo, vertexTo);
                }
                else{
                    relax(new edge(vertices[x][y], vertices[x-1][y+1]), distTo, vertexTo);
                    relax(new edge(vertices[x][y], vertices[x][y+1]), distTo, vertexTo);
                    relax(new edge(vertices[x][y], vertices[x+1][y+1]), distTo, vertexTo);
                }
            }
        }

        // the final distance from the source to sink
        double dist = Double.POSITIVE_INFINITY;
        vertex vertex = new vertex(0, height()-1);

        // edges from the last row in the graph to the virtual sink
        for (int i = 0; i < width(); i++) {
            if(dist > distTo[i][height()-1]){
                dist = distTo[i][height()-1];
                vertex = vertices[i][height()-1];
            }
        }

        // reconstruct the path
        int[] seam = new int[height()];
        seam[height()-1] = vertex.X();
        for (int i = 1; i < height(); i++) {
            vertex = vertexTo[vertex.X()][vertex.Y()];
            seam[height()-1 - i] = vertex.X();
        }

        return seam;
    }

    // remove horizontal seam from current picture
    public void removeHorizontalSeam(int[] seam){
    }

    // remove vertical seam from current picture
    public void removeVerticalSeam(int[] seam){
        Picture p = new Picture(width-1, height);
        for (int x = 0; x < width()-1; x++) {
            for (int y = 0; y < height(); y++) {
                int _x = (x>=seam[y]) ? x+1:x;
                p.set(x, y, this.picture.get(_x, y));
            }
        }
        // todo update energy
        this.picture = p;
        this.width = this.width - 1;
    }

    //  unit testing (optional)
    public static void main(String[] args){

    }

}