package com.stack.gocode.sensors;

import android.util.Log;

import com.stack.gocode.primaryFragments.ArduinoRunnerFragment;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by gabriel on 10/25/18.
 */

public class SensedValues {
    private TreeMap<String,Integer> sensor2value = new TreeMap<>();

    private static final int TOTAL_SONARS = 3;
    private static final int TOTAL_ENCODERS = 2;
    private static final int TOTAL_SENSORS = TOTAL_ENCODERS + TOTAL_SONARS;
    private static final int BYTES_PER_SONAR = 2;
    private static final int BYTES_PER_ENCODER = 4;
    private static final int LEFT_ENCODER_START = TOTAL_SONARS * BYTES_PER_SONAR;
    private static final int RIGHT_ENCODER_START = LEFT_ENCODER_START + BYTES_PER_ENCODER;

    private static final String TAG = SensedValues.class.getSimpleName();

    public static SensedValues checkSensors(byte[] received) {
        SensedValues result = new SensedValues();
        for (int i = 0; i < TOTAL_SONARS; i++) {
            Log.i(TAG, "Index: " + i + ", received.length: " + received.length);
            result.sensor2value.put("sonar" + (i + 1), (int)(ByteBuffer.wrap(Arrays.copyOfRange(received, i * BYTES_PER_SONAR, (i + 1) * BYTES_PER_SONAR)).order(ByteOrder.LITTLE_ENDIAN).getShort()));
        }

        result.sensor2value.put("leftEncoder", ByteBuffer.wrap(Arrays.copyOfRange(received, LEFT_ENCODER_START, LEFT_ENCODER_START + BYTES_PER_ENCODER)).order(ByteOrder.LITTLE_ENDIAN).getInt());
        result.sensor2value.put("rightEncoder", ByteBuffer.wrap(Arrays.copyOfRange(received, RIGHT_ENCODER_START, RIGHT_ENCODER_START + BYTES_PER_ENCODER)).order(ByteOrder.LITTLE_ENDIAN).getInt());

        Log.i(TAG, "Sensor values: " + result);
        return result;
    }

    public boolean hasSensor(String sensor) {
        return sensor2value.containsKey(sensor);
    }

    public int getSensedValueFor(String sensor) {
        return sensor2value.get(sensor);
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        for (Map.Entry<String,Integer> entry: sensor2value.entrySet()) {
            result.append(entry.getKey() + ":" + entry.getValue() + ";");
        }
        return result.toString();
    }

    @Override
    public int hashCode() {return toString().hashCode();}

    @Override
    public boolean equals(Object other) {
        return toString().equals(other.toString());
    }
}
