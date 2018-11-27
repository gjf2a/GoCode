package com.stack.gocode.localData;

import com.stack.gocode.localData.factory.WrappedLabel;

import org.opencv.core.CvType;
import org.opencv.core.Mat;

import java.util.ArrayList;

/**
 * Created by gabriel on 11/15/18.
 */

public class NeuralNetTrainingData {
    private WrappedLabel targetLabel;
    private Mat train, trainLabels, tests, testLabels;

    public NeuralNetTrainingData(ArrayList<Duple<WrappedLabel,Mat>> labeledTrain, ArrayList<Duple<WrappedLabel,Mat>> labeledTest, WrappedLabel targetLabel) {
        this.targetLabel = targetLabel;
        Duple<Mat,Mat> trainers = convertToMats(labeledTrain);
        train = trainers.getFirst();
        trainLabels = trainers.getSecond();
        Duple<Mat,Mat> testers = convertToMats(labeledTest);
        tests = testers.getFirst();
        testLabels = testers.getSecond();
    }

    public static Mat image2nnInput(Mat image) {
        Mat reshaped = image.reshape(1, 1);
        reshaped.convertTo(reshaped, CvType.CV_32F);
        return reshaped;
    }

    public Mat getTrainingExamples() {return train;}
    public Mat getTrainingLabels() {return trainLabels;}
    public Mat getTestingExamples() {return tests;}
    public Mat getTestingLabels() {return testLabels;}

    public Duple<Mat,Mat> convertToMats(ArrayList<Duple<WrappedLabel,Mat>> labeled) {
        Mat inputs = new Mat();
        Mat outputs = Mat.zeros(labeled.size(), 1, CvType.CV_32FC1);
        for (int i = 0; i < labeled.size(); i++) {
            inputs.push_back(image2nnInput(labeled.get(i).getSecond()));
            outputs.put(i, 0, labeled.get(i).getFirst().equals(targetLabel) ? 1.0 : 0.0);
        }
        return new Duple<>(inputs, outputs);
    }
}
