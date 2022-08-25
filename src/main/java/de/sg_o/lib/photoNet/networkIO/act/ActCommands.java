/*
 *
 *   Copyright (C) 2022 Joerg Bayer (SG-O)
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */

package de.sg_o.lib.photoNet.networkIO.act;

import java.text.DecimalFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ActCommands {
    public static String GET_NAME = "getname";
    public static String SET_NAME = "setname";
    public static String GET_FILES = "getfile";
    public static String DELETE_FILE = "delfile";
    public static String GET_PREVIEW_1 = "getPreview1";
    public static String GET_PREVIEW_2 = "getPreview2";
    public static String GET_PARAMETERS = "getpara";
    public static String SET_PARAMETERS = "setpara";
    public static String GET_WIFI = "getwifi";
    public static String SET_WIFI = "setwifi";
    public static String GET_HISTORY = "gethistory";
    public static String DELETE_HISTORY = "delhistory";

    public static String PRINT = "goprint";
    public static String PAUSE = "gopause";
    public static String RESUME = "goresume";
    public static String STOP = "gostop";
    public static String STOP_UV = "stopUV";
    public static String Z_HOME = "setZhome";
    public static String Z_MOVE = "setZmove";
    public static String Z_STOP = "setZstop";
    public static String ZERO = "setZero";

    public static String DETECT = "detect";
    public static String SYSTEM_INFO = "sysinfo";
    public static String GET_STATUS = "getstatus";
    public static String GET_MODE = "getmode";
    public static String GET_FIRMWARE = "getFirmware";

    public static String getName() {
        return simpleCommand(GET_NAME);
    }

    public static String setName(String name) {
        return singleParameterCommand(SET_NAME, name);
    }

    public static String getFiles() {
        return simpleCommand(GET_FILES);
    }

    public static String deleteFile(String[] names) {
        if (names == null) return null;
        if (names.length < 1) return null;
        StringBuilder builder = new StringBuilder();
        builder.append(DELETE_FILE);
        builder.append(",");
        builder.append(names.length);
        for (String name : names) {
            builder.append(",");
            builder.append(name);
        }
        builder.append(",");
        return builder.toString();
    }

    @SuppressWarnings("unused")
    public static String getPreview1(String name) {
        return singleParameterCommand(GET_PREVIEW_1, name);
    }

    public static String getPreview2(String name) {
        return singleParameterCommand(GET_PREVIEW_2, name);
    }

    @SuppressWarnings("unused")
    public static String getParameters() {
        return simpleCommand(GET_PARAMETERS);
    }

    @SuppressWarnings("unused")
    public static String setParameters(int bottomLayers, float offTIme, float bottomTime, float normalTime,
                                       float risingHeight, float risingSpeed, float retractSpeed) {
        return SET_PARAMETERS +
                "," +
                new DecimalFormat("0.00").format(bottomLayers) +
                "," +
                new DecimalFormat("0.00").format(offTIme) +
                "," +
                new DecimalFormat("0.00").format(bottomTime) +
                "," +
                new DecimalFormat("0.00").format(normalTime) +
                "," +
                new DecimalFormat("0.00").format(risingHeight) +
                "," +
                new DecimalFormat("0.00").format(risingSpeed) +
                "," +
                new DecimalFormat("0.00").format(retractSpeed) +
                ",";
    }

    @SuppressWarnings("unused")
    public static String getWifi() {
        return simpleCommand(GET_WIFI);
    }

    @SuppressWarnings("unused")
    public static String setWifi(String ssid, String password) {
        return SET_WIFI +
                "," +
                ssid +
                "," +
                password +
                ",";
    }

    @SuppressWarnings("unused")
    public static String getHistory() {
        return simpleCommand(GET_HISTORY);
    }

    @SuppressWarnings("unused")
    public static String deleteHistory(String[] names) {
        if (names == null) return null;
        if (names.length < 1) return null;
        StringBuilder builder = new StringBuilder();
        builder.append(DELETE_HISTORY);
        builder.append(",");
        builder.append(names.length);
        for (String name : names) {
            builder.append(",");
            builder.append(name);
        }
        builder.append(",");
        return builder.toString();
    }

    public static String print(String name) {
        return singleParameterCommand(PRINT, name);
    }

    @SuppressWarnings("unused")
    public static String pause() {
        return simpleCommand(PAUSE);
    }

    @SuppressWarnings("unused")
    public static String resume() {
        return simpleCommand(RESUME);
    }

    @SuppressWarnings("unused")
    public static String stop() {
        return simpleCommand(STOP);
    }

    @SuppressWarnings("unused")
    public static String stopUV() {
        return simpleCommand(STOP_UV);
    }

    @SuppressWarnings("unused")
    public static String zHome() {
        return simpleCommand(Z_HOME);
    }

    @SuppressWarnings("unused")
    public static String zMove(float move) {
        return singleParameterCommand(Z_MOVE, new DecimalFormat("0.00").format(move));
    }

    @SuppressWarnings("unused")
    public static String zStop() {
        return simpleCommand(Z_STOP);
    }

    @SuppressWarnings("unused")
    public static String zero() {
        return simpleCommand(ZERO);
    }

    @SuppressWarnings("unused")
    public static String detect(int i, int j) {
        return DETECT +
                "," +
                new DecimalFormat("0.00").format(i) +
                "," +
                new DecimalFormat("0.00").format(j) +
                ",";
    }

    public static String systemInfo() {
        return simpleCommand(SYSTEM_INFO);
    }

    public static String getStatus() {
        return simpleCommand(GET_STATUS);
    }

    public static String getMode() {
        return simpleCommand(GET_MODE);
    }

    @SuppressWarnings("unused")
    public static String getFirmware() {
        return simpleCommand(GET_FIRMWARE);
    }

    private static String simpleCommand(String command) {
        return command + ",";
    }

    private static String singleParameterCommand(String command, String parameter) {
        return command + "," + parameter + ",";
    }

    public enum Values {
        ERROR("ERROR"),
        ERROR_1("ERROR1"),
        ERROR_2("ERROR2"),
        ERROR_3("ERROR3"),
        ERROR_4("ERROR4"),
        OK("OK"),
        STOP("stop"),
        PRINT("print"),
        PAUSE("pause"),
        FINISH("finish"),
        END("end");

        private static final Map<String, ActCommands.Values> valueMap = Collections.unmodifiableMap(initializeMapping());
        private final String value;

        Values(String value) {
            this.value = value;
        }

        @SuppressWarnings("unused")
        public static ActCommands.Values getFromID(String value) {
            if (valueMap.containsKey(value)) {
                return valueMap.get(value);
            }
            return ERROR;
        }

        private static HashMap<String, ActCommands.Values> initializeMapping() {
            HashMap<String, ActCommands.Values> IdMap = new HashMap<>();
            for (ActCommands.Values s : ActCommands.Values.values()) {
                IdMap.put(s.value, s);
            }
            return IdMap;
        }

        public String toString() {
            return value;
        }
    }
}
