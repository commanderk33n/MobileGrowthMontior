package de.hs_mannheim.planb.mobilegrowthmonitor.imageprocessing;

import android.annotation.SuppressLint;

import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import org.opencv.android.Utils;
import org.opencv.core.CvException;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
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

import de.hs_mannheim.planb.mobilegrowthmonitor.database.MeasurementData;
import de.hs_mannheim.planb.mobilegrowthmonitor.pinlock.BaseActivity;

/**
 * <h1>ImageProcess.java OpenCV implementation for
 * segmentation of pictures:</h1>
 * <ul>
 * <li>finding lowest horizontal line</li>
 * <li>contourFind for finding human body</li>
 * <li>rectangle detection</li>
 * <li>measure size of the detected human body</li>
 * </ul>
 */
public class ImageProcess {

    private static final String TAG = "ImageProcess";

    // in case calling ImageProcess without our constructor
    private double REFERENCEOBJECTHEIGHT = 10;
    private final double PERSONPOSITION = 3;

    /**
     * Constructor of ImageProcess
     *
     * @param height Reference object height
     */
    public ImageProcess(double height) {
        REFERENCEOBJECTHEIGHT = height;
    }

    /**
     * MainFunction of ImageProcess - does all the magic
     *
     * @param path path of the ImageFile
     * @return MeasurementData object
     * @throws IllegalArgumentException no contours found
     */
    public MeasurementData sizeMeasurement(String path) throws IllegalArgumentException {
        // init

        MeasurementData measurementData = new MeasurementData();
        Mat source = Imgcodecs.imread(path);
        Mat hierarchy = new Mat();
        Size size = new Size(7, 7);
        List<MatOfPoint> contours = new ArrayList<>();
        Bitmap bmp = null;
        double heightOfPerson = 0;
        double yCoordinateHorizontalLine = 0;
        double heightReferenceObject = 0;

        // Start imageProcessing
        try {
            Mat destination;
            destination = source.clone();
            Imgproc.cvtColor(source, destination, Imgproc.COLOR_BGR2GRAY);
            Imgproc.GaussianBlur(destination, destination, size, 0);
            Imgproc.Canny(destination, destination, 50, 100);
            Imgproc.findContours(destination, contours, hierarchy, Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);
            if (contours.size() == 0) {
                throw new IllegalArgumentException("No Objects found");
            }

            List<MatOfPoint> rectContour = getRectContour(contours);
            // Rectangle detection found no rectangle so try method 2
            if (rectContour == null) {
                Log.i(TAG, "sizeMeasurement method 2");
                measurementData = sizeMeasurement2(destination, source);
                if (measurementData.height < 20 || measurementData.height > 250) {
                    throw new IllegalArgumentException("No reference Object found or another error hihi");
                }
                return measurementData;

            }
            Log.i(TAG, "sizeMeasurement method 1");
            yCoordinateHorizontalLine = getYLowerHorizontalLine(destination);

            rectContour = sortContours(rectContour);

            heightReferenceObject = findReferenceObject(rectContour, source).height;


            //find highest point
            Point highestPoint = getHighestPoint(destination);

            // Draw Line from lowest to highest point
            Imgproc.line(source, new Point(highestPoint.x, yCoordinateHorizontalLine),
                    highestPoint, new Scalar(0, 255, 0), 3);
            // Height of ReferenceObject and SizeMeasurement
            double heightInPixels = yCoordinateHorizontalLine - highestPoint.y;
            heightOfPerson = heightInPixels / heightReferenceObject * REFERENCEOBJECTHEIGHT;
            if (heightOfPerson > 250 || heightOfPerson < 10) {
                throw new IllegalArgumentException("Error, person / reference object not found");
            }
            Imgproc.cvtColor(source, source, Imgproc.COLOR_BGR2RGB);
            bmp = Bitmap.createBitmap(source.cols(), source.rows(), Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(source, bmp);

        } catch (CvException e) {
            Log.e("sizeMeasurement(): ", e.getMessage());
        }
        measurementData.edited = imageWriter(bmp);
        measurementData.height = heightOfPerson;

        return measurementData;
    }

    /**
     * Checks if a given contour is a rectangle
     *
     * @param thisContour the contour to be checked
     * @return true if the contour is rect
     */
    public static boolean isContourRect(MatOfPoint thisContour) {
        MatOfPoint2f approxCurve = new MatOfPoint2f();
        // Convert contours from MatOfPoint to MatOfPoint2f
        MatOfPoint2f contour2f = new MatOfPoint2f(thisContour.toArray());
        // Processing on MatOfPoint which is in type MatOfPoint2f
        double approxDistance = Imgproc.arcLength(contour2f, true) * 0.02;

        if (approxDistance > 5) {

            //Find Polygons
            Imgproc.approxPolyDP(contour2f, approxCurve, approxDistance, true);

            //Convert back to MatOfPoint
            MatOfPoint points = new MatOfPoint(approxCurve.toArray());

            //Rectangle Checks - Points, area, convexity
            if (points.total() == 4 && Math.abs(Imgproc.contourArea(points)) > 1000 && Imgproc.isContourConvex(points)) {

                Rect rect = Imgproc.boundingRect(points);

                if (Math.abs(rect.height - rect.width) < 100) {

                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Function for finding all rectangle contours in contourList
     *
     * @param contours contourList
     * @return List<MatOfPoint> squares
     */
    private static List<MatOfPoint> getRectContour(List<MatOfPoint> contours) {

        List<MatOfPoint> squares = null;

        for (MatOfPoint c : contours) {

            if ((ImageProcess.isContourRect(c))) {

                if (squares == null)
                    squares = new ArrayList<MatOfPoint>();
                squares.add(c);
            }
        }
        return squares;
    }

    /**
     * This function is called when our first size Measurement doesn't wield any results
     *
     * @param destination image mat object
     * @param original    image mat object
     * @return MeasurementData object
     */
    public MeasurementData sizeMeasurement2(Mat destination, Mat original) throws IllegalArgumentException {
        MeasurementData measurementData = new MeasurementData();
        // Mat destination = source.clone();
        Mat hierarchy = new Mat();
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
        double yCoordinateHorizontalLine;
        double heightReferenceObject;

        try {

            yCoordinateHorizontalLine = getYLowerHorizontalLine(destination);
            Imgproc.dilate(destination, destination, element);
            Imgproc.erode(destination, destination, element1);
            Imgproc.findContours(destination, contours, hierarchy, Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);

            contours = sortContours(contours);

            // Find Contour of ReferenceObject in middle of left side of the picture
            rect_small = findReferenceObject(contours, original);

            heightReferenceObject = rect_small.height;
            Imgproc.rectangle(original, new Point(rect_small.x, rect_small.y), new Point(rect_small.x +
                    rect_small.width, rect_small.y + rect_small.height), new Scalar(0, 255, 0), 3);

            Point highestPoint = getHighestPoint(destination);


            Imgproc.line(original, new Point(highestPoint.x, yCoordinateHorizontalLine),
                    highestPoint, new Scalar(0, 255, 0), 3);

            // Height of ReferenceObject and SizeMeasurement
            double heightInPixels = yCoordinateHorizontalLine - highestPoint.y;
            heightOfPerson = heightInPixels / heightReferenceObject * REFERENCEOBJECTHEIGHT;

            Log.i("Size = ", "" + heightOfPerson);

            Imgproc.cvtColor(original, original, Imgproc.COLOR_BGR2RGB);
            bmp = Bitmap.createBitmap(original.cols(), original.rows(), Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(original, bmp);

        } catch (CvException e) {
            Log.e("sizeMeasurement(): ", e.getMessage());
        }
        measurementData.height = heightOfPerson;
        measurementData.edited = imageWriter(bmp);
        return measurementData;
    }

    /**
     * Sort contours from left to right or right to left
     *
     * @param contours the contours to be sorted
     * @return the sorted contours
     */
    public List<MatOfPoint> sortContours(List<MatOfPoint> contours) {
        Collections.sort(contours, new Comparator<MatOfPoint>() {
            @Override
            public int compare(MatOfPoint lhs, MatOfPoint rhs) {
                return (Imgproc.boundingRect(lhs).x - Imgproc.boundingRect(rhs).x);

            }
        });
        return contours;
    }

    /**
     * Used to find the highest Point in the image
     * starting at the top & going until the middle of the image
     * looking in the middle third
     *
     * @param image the image
     * @return a point with x & y coordinates of the point
     * @throws IllegalArgumentException if there is no highest point found
     */
    public Point getHighestPoint(Mat image) throws IllegalArgumentException {

        Point p = new Point();
        for (int j = image.rows() / 50; j < image.rows() / 2; j++) {
            for (int k = (int) (image.cols() / PERSONPOSITION); k < image.cols() * 2 / PERSONPOSITION; k++) {
                if (image.get(j, k)[0] > 0) {
                    p.y = j;
                    p.x = k;
                    return p;
                }
            }
        }
        throw new IllegalArgumentException("No highest Point found");
    }

    /**
     * Finding lower horizontal Line
     *
     * @param img imageFile
     * @return int minimum y-coordinate
     */
    public int getYLowerHorizontalLine(Mat img) {
        // init
        int threshold = 50;
        int minLineLength = 10;
        int maxLineGap = 10;
        Mat lines = new Mat();
        int miny = 0;
        try {
            Mat destination;
            destination = img;
            Imgproc.HoughLinesP(destination, lines, 1, Math.PI / 360, threshold, minLineLength, maxLineGap);
            for (int x = 0; x < lines.rows(); x++) {
                double[] vec = lines.get(x, 0);
                double x1 = vec[0],
                        y1 = vec[1],
                        x2 = vec[2],
                        y2 = vec[3];
                if (Math.abs((Math.atan2(y2 - y1, x2 - x1) * 180 / Math.PI)) < 5) {
                    if ((destination.width() / 3 > x1 || x2 > destination.width() * 2.0 / 3.0) && y1 > miny && y1 < destination.height()) {
                        //   if (y1 > miny && y1 < destination.height()) {
                        miny = (int) y1;
                    }
                }
            }
        } catch (CvException e) {
            Log.e("LowerHorizontalLine:", e.getMessage());
        }
        return miny;
    }

    /**
     * Function for finding referenceObject in upper left corner of Image
     *
     * @param contours contourList
     * @param original image
     * @return rectangle
     * @throws IllegalArgumentException
     */
    public Rect findReferenceObject(List<MatOfPoint> contours, Mat original) throws IllegalArgumentException {
        for (MatOfPoint m : contours) {
            Rect temp = Imgproc.boundingRect(m);

            if (temp.y + temp.height < original.height() / 2 && temp.y + temp.height
                    > original.height() / 10.0 && temp.area()>1000)  {
                Imgproc.rectangle(original, new Point(temp.x, temp.y), new Point(temp.x +
                        temp.width, temp.y + temp.height), new Scalar(0, 255, 0), 3);
                return temp;
            }
        }
        throw new IllegalArgumentException("no reference object found");
    }

    /**
     * Save image to file
     *
     * @param bmp file to save
     * @return String path
     */
    @SuppressLint("SimpleDateFormat")
    public String imageWriter(Bitmap bmp) {
        FileOutputStream out = null;
        String fileName = "";
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
            String timeStamp = sdf.format(new Date());
            fileName = Environment.getExternalStorageDirectory().getPath() +
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
        return fileName;
    }
}
