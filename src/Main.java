import edu.princeton.cs.algs4.Picture;

import java.awt.*;
import java.io.File;
import java.util.Arrays;

public class Main {

    public static void main(String[] args) {
	// write your code here
        String url = "F:\\Personal_development\\Algorithms\\Algorithms\\Algorithms2\\Assignments\\week2\\SeamCarving\\test\\";
        String file = "HJoceanSmall.png";
        url += file;
        Picture picture = new Picture(url);
        SeamCarver s = new SeamCarver(picture);

//        int[] verticalSeam = s.findVerticalSeam();
//        picture = energyPicture(s);
//        for (int t =0; t<verticalSeam.length; t++){
//            picture.set(verticalSeam[t], t, Color.red);
//        }
//        picture.show();
//        s.removeVerticalSeam(verticalSeam);
//
        for (int t =0; t<90; t++){
            int[] verticalSeam = s.findVerticalSeam();
            System.out.print("The seam is:");
            System.out.println(Arrays.toString(verticalSeam));
//            picture = energyPicture(s);
//            picture = s.picture();
//            for (int i =0; i<verticalSeam.length; i++){
//                picture.set(verticalSeam[i], i, Color.red);
//            }
//            picture.show();
            s.removeVerticalSeam(verticalSeam);
        }

        picture = s.picture();
        picture.show();



//        int width = picture.width();
//        int heigit = picture.height();
//        for (int i = 0 ; i < heigit; i++){
//            for (int t = 0 ; t < width; t++) {
//                // the picture is immutable;
//                Color color = picture.get(t ,i);
//                color = color.darker();
//                picture.set(t ,i, color);
//            }
//        }
//        picture.show();

    }

    public static Picture energyPicture(SeamCarver s){
        Picture p = new Picture(s.width(), s.height());
        for (int i = 0; i < s.width(); i++){
            for (int t = 0; t < s.height(); t++) {
                float color = (float) Math.min(s.energy(i,t)/255.0, 1.0) ;
                Color c = new Color(color,color,color);
                p.set(i, t, c);
            }
        }
        return p;
    }
}
