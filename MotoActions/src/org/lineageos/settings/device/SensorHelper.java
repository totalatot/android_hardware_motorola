/*
 * Copyright (c) 2015 The CyanogenMod Project
 * Copyright (c) 2017-2022 The LineageOS Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.lineageos.settings.device;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;

public class SensorHelper {
    private static final String TAG = "MotoActions";

    private static final int SENSOR_TYPE_MMI_CHOP_CHOP = 65546;
    private static final int SENSOR_TYPE_MMI_FLAT_UP = 65537;
    private static final int SENSOR_TYPE_MMI_FLAT_DOWN = 65538;
    private static final int SENSOR_TYPE_MMI_STOW = 65539;
    private static final int SENSOR_TYPE_MMI_IR_GESTURE = 65541;
    private static final int SENSOR_TYPE_MMI_IR_OBJECT = 65543;

    private static final int BATCH_LATENCY_IN_MS = 100;

    private final Context mContext;
    private final SensorManager mSensorManager;

    public SensorHelper(Context context) {
        mContext = context;
        mSensorManager = (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);
        dumpSensorsList();
    }

    private void dumpSensorsList() {
        try {
            FileOutputStream out = mContext.openFileOutput("sensors.txt", Context.MODE_PRIVATE);
            OutputStreamWriter writer = new OutputStreamWriter(out);

            List<Sensor> sensorList = mSensorManager.getSensorList(Sensor.TYPE_ALL);
            for (Sensor sensor : sensorList) {
                writer.write("sensor " + sensor.getType() + " = " + sensor.getName()
                        + " max batch: " + sensor.getFifoMaxEventCount() + " isWakeUp: "
                        + sensor.isWakeUpSensor() + "\n");
            }
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Sensor getChopChopSensor() {
        return mSensorManager.getDefaultSensor(SENSOR_TYPE_MMI_CHOP_CHOP, true);
    }

    public Sensor getFlatUpSensor() {
        return mSensorManager.getDefaultSensor(SENSOR_TYPE_MMI_FLAT_UP, true);
    }

    public Sensor getFlatDownSensor() {
        return mSensorManager.getDefaultSensor(SENSOR_TYPE_MMI_FLAT_DOWN, true);
    }

    public Sensor getIrGestureSensor() {
        return mSensorManager.getDefaultSensor(SENSOR_TYPE_MMI_IR_GESTURE, true);
    }

    public Sensor getProximitySensor() {
        return mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY, true);
    }

    public Sensor getStowSensor() {
        return mSensorManager.getDefaultSensor(SENSOR_TYPE_MMI_STOW, true);
    }

    public void registerListener(Sensor sensor, SensorEventListener listener) {
        if (!mSensorManager.registerListener(listener, sensor,
                SensorManager.SENSOR_DELAY_NORMAL, BATCH_LATENCY_IN_MS * 1000)) {
            Log.e(TAG, "Failed to registerListener for sensor " + sensor);
        }
    }

    public void unregisterListener(SensorEventListener listener) {
        mSensorManager.unregisterListener(listener);
    }
}
