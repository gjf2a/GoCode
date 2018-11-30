package com.stack.gocode.localData.flagtypes;

import com.stack.gocode.localData.Flag;
import com.stack.gocode.localData.NeuralNetTrainingData;
import com.stack.gocode.sensors.SensedValues;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.ml.ANN_MLP;

import java.util.TreeSet;

/**
 * Created by gabriel on 11/16/18.
 */

public class NeuralNetFlag extends Flag {
    private ANN_MLP network;

    public NeuralNetFlag(String networkName, ANN_MLP network) {
        super(networkName);
        this.network = network;
    }

    @Override
    public void updateCondition(SensedValues sensedValues) {
        if (sensedValues.hasNewImage()) {
            Mat nnInput = NeuralNetTrainingData.image2nnInput(sensedValues.getLastImage());
            Mat output = Mat.zeros(1, 1, CvType.CV_32FC1);
            network.predict(nnInput, output, 0);
            setTrue(output.get(0,0)[0] > 0.5);
        }
    }

    @Override
    public void addSensorsInUse(TreeSet<String> sensorsInUse) {
        // Intentionally left blank
    }

    @Override
    public String toString() {
        return getName() + "; Neural network: " + network.getWeights(0).cols() + " inputs; " + network.getWeights(1).cols() + " hidden";
    }
}
