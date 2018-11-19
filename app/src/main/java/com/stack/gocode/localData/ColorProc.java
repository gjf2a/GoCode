package com.stack.gocode.localData;

import android.util.Log;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;

/**
 * Created by gabriel on 11/19/18.
 */

public class ColorProc implements Named {
    private String name;
    private int red, green, blue, radius;

    public final static String COLOR_CENTER_PREFIX = "color_where_";
    public final static String COLOR_AMOUNT_PREFIX = "color_amt_";

    public final static String TAG = ColorProc.class.getSimpleName();

    public ColorProc(String name, int red, int green, int blue, int radius) {
        setName(name);
        setRed(red);
        setGreen(green);
        setBlue(blue);
        setRadius(radius);
    }

    public void setName(String name) {this.name = name;}
    public void setRed(int red) {this.red = red;}
    public void setGreen(int green) {this.green = green;}
    public void setBlue(int blue) {this.blue = blue;}
    public void setRadius(int radius) {this.radius = radius;}

    public String getName() {return name;}
    public int getRed() {return red;}
    public int getGreen() {return green;}
    public int getBlue() {return blue;}
    public int getRadius() {return radius;}

    public static int boundify(int value) {
        return Math.min(Math.max(0, value), 255);
    }

    public Mat thresholded(Mat frame) {
        Mat thresh = new Mat();
        Core.inRange(frame, new Scalar(boundify(red - radius), boundify(green - radius), boundify(blue - radius), 0), new Scalar(boundify(red + radius), boundify(green + radius), boundify(blue + radius), 255), thresh);
        return thresh;
    }

    public int getValueUsing(String prefix, Mat frame) {
        switch (prefix) {
            case COLOR_CENTER_PREFIX: return xCentroidPercent(frame);
            case COLOR_AMOUNT_PREFIX: return pixelCountWithin(frame);
            default: throw new IllegalArgumentException(prefix + " is undefined");
        }
    }

    public int pixelCountWithin(Mat frame) {
        if (frame == null) {
            Log.i(TAG, "No image available");
            return 0;
        }
        Mat thresh = thresholded(frame);
        int result = Core.countNonZero(thresh);
        thresh.release();
        return result;
    }

    // Ideas from https://www.programcreek.com/java-api-examples/index.php?api=org.opencv.imgproc.Moments
    public int xCentroidPercent(Mat frame) {
        if (frame == null) {
            Log.i(TAG, "No image available");
            return 50;
        }
        Mat thresh = thresholded(frame);
        Moments moments = Imgproc.moments(thresh);
        thresh.release();
        double xCentroid = moments.get_m10() / moments.get_m00();
        return (int)(100 * xCentroid / frame.width());
    }

    public static String removePrefix(String name) {
        return name.split("_", 3)[2];
    }

    public static String retainPrefix(String name) {
        String[] parts = name.split("_");
        return parts[0] + "_" + parts[1] + "_";
    }
}
