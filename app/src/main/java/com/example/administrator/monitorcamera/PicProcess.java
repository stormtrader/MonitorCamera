package com.example.administrator.monitorcamera;

import android.util.Log;

import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.opencv.core.Core.absdiff;

/**
 * Created by Administrator on 2017/3/14.
 */

public class PicProcess {

    List<String> pics = new ArrayList<String>();
    int diffSize = 2; //暂时比较2张照片

    public int GetDiffN(String newPicPath) {
        int maxDiffN = 0;
        if (pics.size() < 2)
            pics.add(newPicPath);
        else {
            pics.remove(0);
            pics.add(newPicPath);
        }

        for (int i=1; i<pics.size(); i++){
            int tmpDiffN = checkTwoPicDiff(pics.get(i), pics.get(0));
            if (tmpDiffN > maxDiffN)
                maxDiffN = tmpDiffN;
        }

        return maxDiffN;
    }

    public int  checkTwoPicDiff(String picPath1, String picPath2) {
        Mat snap1 = Imgcodecs.imread(picPath1);
        Mat snap2 = Imgcodecs.imread(picPath2);

        Mat tmp_mat1 = new Mat();
        Mat tmp_mat2 = new Mat();
        Mat tmp_mat3 = new Mat();
        absdiff(snap1, snap2, tmp_mat3);
        Imgproc.cvtColor(tmp_mat3, tmp_mat2, Imgproc.COLOR_RGB2GRAY);
        Imgproc.threshold(tmp_mat2, tmp_mat1, 70,255,Imgproc.THRESH_TOZERO);

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

        Log.i("PicProcess", "n=" + n);

        return n;
    }
}
