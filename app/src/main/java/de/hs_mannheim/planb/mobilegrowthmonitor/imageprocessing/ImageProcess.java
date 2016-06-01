package de.hs_mannheim.planb.mobilegrowthmonitor.imageprocessing;

import android.annotation.SuppressLint;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import org.opencv.android.Utils;
import org.opencv.core.Core;
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
import java.text.DecimalFormat;
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

    private static final String TAG = "ImageProcess";

    private static Context context;

  public ImageProcess(Context  c) {
        context = c;
    }

    public ImageProcess() {
    }

    // this is our prototype function!!
    public double sizeMeasurement(String path) {
        // init
        Mat source = Imgcodecs.imread(path);

        // rotate image
       // Core.transpose(source, source);
       // Core.flip(source, source, 1);


        Mat hierarchy = new Mat();
        Size size = new Size(7, 7);
        List<MatOfPoint> contours = new ArrayList<>();
        int erosion_size = 5;
        int dilation_size = 5;
        Mat element = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(2 * erosion_size + 1,
                2 * erosion_size + 1));
        Mat element1 = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(2 * dilation_size + 1,
                2 * dilation_size + 1));
        Bitmap bmp = null;
        Rect rect_small;
        double heightOfPerson = 0;
        double yCoordinateHorizontalLine =0;
        double heightReferenceObject = 0;
        int yCoordinateHighestPoint = 0;
        boolean breakForLoop = false;
        try {
            Mat destination;
            destination = source.clone();
            Imgproc.cvtColor(source, destination, Imgproc.COLOR_BGR2GRAY);
            Imgproc.GaussianBlur(destination, destination, size, 0);
            Imgproc.Canny(destination, destination, 50, 100);
            yCoordinateHorizontalLine =  getYLowerHorizontalLine(destination);
            Imgproc.dilate(destination, destination, element);
            Imgproc.erode(destination, destination, element1);
            Imgproc.findContours(destination, contours, hierarchy, Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);
            Collections.sort(contours, new Comparator<MatOfPoint>() {
                @Override
                public int compare(MatOfPoint lhs, MatOfPoint rhs) {
                    return Imgproc.boundingRect(lhs).x - Imgproc.boundingRect(rhs).x;
                }
            });

            // Find Contour of ReferenceObject in middle of left side of the picture
            int i = 0;
            for (MatOfPoint m : contours) {

                if (Imgproc.boundingRect(m).y < source.height() * 1.0 / 2.0 && Imgproc.boundingRect(m).y
                        > source.height() / 6.0) {
                    Imgproc.drawContours(source, contours, i, new Scalar(0, 255, 0), 2);
                    heightReferenceObject = Imgproc.boundingRect(m).height;
                    break;
                }
                i++;
            }
            rect_small = Imgproc.boundingRect(contours.get(i));
            Imgproc.rectangle(source, new Point(rect_small.x, rect_small.y), new Point(rect_small.x +
                    rect_small.width, rect_small.y + rect_small.height), new Scalar(0, 255, 0), 3);
            for (int j = destination.rows() / 10; j < destination.rows() * 2 / 3; j++) {
                   for(int k = destination.width()/5;k<destination.width()*4/5;k++){
                if (destination.get(j, destination.width() / 2)[0] > 0) {
                    yCoordinateHighestPoint = j;
                    //    breakForLoop = true;
                    break;
                }

                  }
                  if(breakForLoop){
                      break;
                 }
            }
            Imgproc.line(source, new Point(source.width() / 2.0, yCoordinateHorizontalLine),
                    new Point(source.width() / 2.0, yCoordinateHighestPoint), new Scalar(0, 255, 0), 3);
            // Height of ReferenceObject and SizeMeasurement
            // TODO: change to alertDialog
            double referenceObjectHeight = 14.9;
            double heightInPixels = yCoordinateHorizontalLine - yCoordinateHighestPoint;
            heightOfPerson = heightInPixels / heightReferenceObject * referenceObjectHeight;
            DecimalFormat df = new DecimalFormat("####0.00");
            String resultString = df.format(heightOfPerson);
            Toast.makeText(context, "Height is: " + resultString + " cm", Toast.LENGTH_LONG).show();
            Imgproc.cvtColor(source, source, Imgproc.COLOR_BGR2RGB);
            bmp = Bitmap.createBitmap(source.cols(), source.rows(), Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(source, bmp);

        } catch (CvException e) {
            Log.e("sizeMeasurement(): ", e.getMessage());
        }
        imageWriter(bmp);
        return heightOfPerson;
    }

    // finding lower horizontal Line
    public int getYLowerHorizontalLine(Mat img) {
        // init
        Mat source = img;
        int threshold = 50;
        int minLineLength = 1;
        int maxLineGap = 10;
        Mat lines = new Mat();
        int miny = 0;
        try {
            Mat destination;
            destination = source;

        //
        //    Imgproc.cvtColor(source, destination, Imgproc.COLOR_BGR2GRAY);
            Imgproc.Canny(destination, destination, 50, 100, 3, true);
            Imgproc.HoughLinesP(destination, lines, 1, Math.PI / 360, threshold, minLineLength, maxLineGap);
            for (int x = 0; x < lines.rows(); x++) {
                double[] vec = lines.get(x, 0);
                double x1 = vec[0],
                        y1 = vec[1],
                        x2 = vec[2],
                        y2 = vec[3];
                if ((Math.atan2(y2 - y1, x2 - x1) * 180 / Math.PI == 0)) {
                    if (source.width() / 4.0 < x2 && x2 < source.width() * 3.0 / 4.0 && y1 > miny && y1 < source.height()) {
                        miny = (int) y1;
                    }
                }
            }
        } catch (CvException e) {
            Log.e("backgroundSub():", e.getMessage());
        }
        return miny;
    }

    @SuppressLint("SimpleDateFormat")
    private void imageWriter(Bitmap bmp) {
        FileOutputStream out = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
            String timeStamp = sdf.format(new Date());
            String fileName = Environment.getExternalStorageDirectory().getPath() +
                    "/growpics/" + timeStamp + "_filter.jpg";
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
