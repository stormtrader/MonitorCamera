package com.example;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;

import static org.opencv.core.Core.absdiff;

public class MyClass {
    public static void  main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        //Mat mat = Mat.eye(3, 3, CvType.CV_8UC1);
        //Mat blurredImage = new Mat();
        //Mat hsvImage = new Mat();
        //Imgproc.blur(mat, blurredImage, new Size(7,7));

        Mat snap1 = Imgcodecs.imread("d:/android/testpic/1.jpg");
        Mat snap2 = Imgcodecs.imread("d:/android/testpic/3.jpg");

        //Mat snap1 = Imgcodecs.imread("d:/android/testpic/1.jpg");
        //Mat snap2 = Imgcodecs.imread("d:/android/testpic/3.jpg");

        Mat tmp_mat1 = new Mat();
        Mat tmp_mat2 = new Mat();
        Mat tmp_mat3 = new Mat();
        //Imgproc.cvtColor(snap1, tmp_mat1, Imgproc.COLOR_RGB2GRAY);
        //Imgproc.cvtColor(snap2, tmp_mat2, Imgproc.COLOR_RGB2GRAY);
        absdiff(snap1, snap2, tmp_mat3);
        Imgproc.cvtColor(tmp_mat3, tmp_mat2, Imgproc.COLOR_RGB2GRAY);
        Imgproc.threshold(tmp_mat2, tmp_mat1, 70,255,Imgproc.THRESH_TOZERO);

        /*
        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        Imgproc.findContours(outerBox, contours, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);
        double maxArea = -1;
        int maxAreaIdx = -1;
        for (int idx = 0; idx < contours.size(); idx++) {
            Mat contour = contours.get(idx);
            double contourarea = Imgproc.contourArea(contour);
            if (contourarea > maxArea) {
                maxArea = contourarea;
                maxAreaIdx = idx;
            }
        }
        */


        int n =0;
        for (int y = 0; y < tmp_mat1.height(); y++) {
        //for (int y = 0; y < 10; y++) {
            for (int x = 0; x < tmp_mat1.width(); x++) {
                //for (int x = 0; x < 10; x++) {
                //得到该行像素点的值
                double[] data = tmp_mat1.get(y, x);
                //System.out.println("length="+data.length);
                for (int i1 = 0; i1 < data.length; i1++) {
                    //System.out.print(data[i1] + " ");
                    if (data[i1] != 0x0)
                        n++;
                }
            }
        }
        System.out.println("n=" + n);


        ImageViewer imageViewer = new ImageViewer(tmp_mat2, "First Pic");
        imageViewer.imshow();

        /*
        Mat element = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(3, 3));
        Imgproc.erode(tmp_mat1, tmp_mat2, element);
        */

    }

}

