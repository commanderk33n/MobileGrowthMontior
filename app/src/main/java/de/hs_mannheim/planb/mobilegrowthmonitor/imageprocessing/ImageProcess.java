package de.hs_mannheim.planb.mobilegrowthmonitor.imageprocessing;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import org.opencv.android.Utils;
import org.opencv.core.CvException;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * Created by eikood on 05.05.2016.
 */
public class ImageProcess {




// this is our prototype function!!
    public void sizeMeasurement(String path) {
        // init
        Mat source = Imgcodecs.imread(path);
        // rotate image
        //  Mat rotate = Imgproc.getRotationMatrix2D(new Point(source.cols()/2, source.rows()/2), 90,-1);
        // Imgproc.warpAffine(source, source, rotate, new Size(source.rows(), source.cols()));
        Mat hierarchy = new Mat();
        Size size = new Size(7, 7);
        List<MatOfPoint> contours = new ArrayList<>();
        int erosion_size = 5;
        int dilation_size = 5;
        Mat element = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(2 * erosion_size + 1, 2 * erosion_size + 1));
        Mat element1 = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(2 * dilation_size + 1, 2 * dilation_size + 1));
        Bitmap bmp = null;
        Rect rect_small;
        Rect rect_large;
        new Rect();
        try {
            Mat destination;
            destination = source;
            Imgproc.cvtColor(source, destination, Imgproc.COLOR_BGR2GRAY);
            Imgproc.GaussianBlur(destination, destination, size, 0);
            Imgproc.Canny(destination, destination, 50, 100);
            Imgproc.dilate(destination, destination, element);
            Imgproc.erode(destination, destination, element1);

            Imgproc.findContours(destination, contours, hierarchy, Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);
            Collections.sort(contours, new Comparator<MatOfPoint>() {
                @Override
                public int compare(MatOfPoint lhs, MatOfPoint rhs) {
                    return Imgproc.boundingRect(lhs).x - Imgproc.boundingRect(rhs).x;
                }
            });
            Imgproc.drawContours(source, contours, 0, new Scalar(255, 255, 255), 5);
            rect_small = Imgproc.boundingRect(contours.get(0));
            Imgproc.rectangle(source, new Point(rect_small.x, rect_small.y), new Point(rect_small.x + rect_small.width, rect_small.y + rect_small.height), new Scalar(255, 255, 255), 5);

            Collections.sort(contours, new Comparator<MatOfPoint>() {
                @Override
                public int compare(MatOfPoint lhs, MatOfPoint rhs) {
                    return (int) (Imgproc.contourArea(rhs) - Imgproc.contourArea(lhs));
                }
            });
            Imgproc.drawContours(source, contours, 0, new Scalar(255, 255, 255), 5);
            rect_large = Imgproc.boundingRect(contours.get(0));
            Imgproc.rectangle(source, new Point(rect_large.x, rect_large.y), new Point(rect_large.x + rect_large.width, rect_large.y + rect_large.height), new Scalar(255, 255, 255), 5);
            bmp = Bitmap.createBitmap(source.cols(), source.rows(), Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(source, bmp);
            double referenceObjectHeight = 17.5;
            double ergebnis = rect_large.height / rect_small.height * referenceObjectHeight;
            System.out.println(ergebnis);
        } catch (CvException e) {
            Log.e("sizeMeasurement(): ", e.getMessage());
        }
        imageWriter(bmp);
    }


    @SuppressLint("SimpleDateFormat")
    private void imageWriter(Bitmap bmp) {
        FileOutputStream out = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
            String currentDateandTime = sdf.format(new Date());
            String fileName = Environment.getExternalStorageDirectory().getPath() +
                    "/growpics/" + currentDateandTime + "_filter.jpg";
            out = new FileOutputStream(fileName, true);
            bmp.compress(Bitmap.CompressFormat.JPEG, 50, out);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
