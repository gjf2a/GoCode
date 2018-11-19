package com.stack.gocode.localData;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;

/**
 * Created by gabriel on 11/19/18.
 */

public class ColorFilter {
    private int red, green, blue, radius;

    public ColorFilter(int red, int green, int blue, int radius) {
        setRed(red);
        setGreen(green);
        setBlue(blue);
        setRadius(radius);
    }

    public void setRed(int red) {this.red = red;}
    public void setGreen(int green) {this.green = green;}
    public void setBlue(int blue) {this.blue = blue;}
    public void setRadius(int radius) {this.radius = radius;}

    public static int boundify(int value) {
        return Math.min(Math.max(0, value), 255);
    }

    public void updateFrame(Mat frame) {
        Core.inRange(frame, new Scalar(boundify(red - radius), boundify(green - radius), boundify(blue - radius), 0), new Scalar(boundify(red + radius), boundify(green + radius), boundify(blue + radius), 255), frame);
    }
}
