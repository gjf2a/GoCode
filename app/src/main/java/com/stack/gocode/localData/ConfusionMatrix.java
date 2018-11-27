package com.stack.gocode.localData;

import org.opencv.core.Mat;

/**
 * Created by gabriel on 11/19/18.
 */

public class ConfusionMatrix {
    private int truePos, falsePos, trueNeg, falseNeg;

    public void count(Mat output, Mat target) {
        double outVal = output.get(0, 0)[0];
        double targVal = target.get(0, 0)[0];
        boolean pos = outVal >= 0.5;
        boolean tru = targVal >= 0.5;

        if (pos == tru) {
            if (pos) {
                truePos += 1;
            } else {
                trueNeg += 1;
            }
        } else {
            if (pos) {
                falsePos += 1;
            } else {
                falseNeg += 1;
            }
        }
    }

    public int getTruePositives() {return truePos;}
    public int getTrueNegatives() {return trueNeg;}
    public int getFalsePositives() {return falsePos;}
    public int getFalseNegatives() {return falseNeg;}
}
