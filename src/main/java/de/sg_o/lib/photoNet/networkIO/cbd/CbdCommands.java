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

package de.sg_o.lib.photoNet.networkIO.cbd;

public class CbdCommands {
    public static String SET_NAME = "U100";
    public static String GET_FILES = "M20";
    public static String DELETE_FILE = "M30";
    public static String CLOSE_FILE = "M22";
    public static String SELECT_FILE = "M6032";
    public static String UPLOAD_FILE = "M28";
    public static String UPLOAD_FILE_STOP = "M29";
    public static String READ_FILE_OFFSET = "M3001 I";

    public static String PRINT = "M6030";
    public static String PAUSE = "M25";
    public static String RESUME = "M24";
    public static String STOP = "M33 I5";
    public static String Z_HOME = "G28 Z0";
    public static String Z_MOVE = "G0 Z%.2f F%.2f";
    public static String Z_STOP = "M112";
    public static String Z_ABSOLUTE = "G90";
    public static String Z_RELATIVE = "G91";
    public static String ZERO = "TODO";

    public static String SYSTEM_INFO = "M99999";
    public static String GET_STATUS = "M4000";
    public static String GET_SELECTED_FILE = "M4006";

    public static String setName(String name) {
        return SET_NAME + " '" + name + "'";
    }

    public static String getFiles(String path) {
        return GET_FILES + " '" + path + "'";
    }

    public static String deleteFile(String file) {
        return DELETE_FILE + " " + file;
    }

    public static String closeFile() {
        return CLOSE_FILE;
    }

    public static String selectFile(String file) {
        return SELECT_FILE + " '" + file + "'";
    }

    public static String uploadFile(String file) {
        return UPLOAD_FILE + " " + file;
    }

    public static String uploadFileStop() {
        return UPLOAD_FILE_STOP;
    }

    public static String readFileOffset(long offset) {
        return READ_FILE_OFFSET + offset;
    }

    public static String print(String file) {
        return PRINT + " ':" + file + "'";
    }

    public static String pause() {
        return PAUSE;
    }

    public static String resume() {
        return RESUME;
    }

    public static String stop() {
        return STOP;
    }

    public static String zHome() {
        return Z_HOME;
    }

    public static String zMove(float distance, float speed) {
        return String.format(Z_MOVE, distance, speed);
    }

    public static String zStop() {
        return Z_STOP;
    }

    @SuppressWarnings("unused")
    public static String zAbsolute() {
        return Z_ABSOLUTE;
    }

    public static String zRelative() {
        return Z_RELATIVE;
    }

    @SuppressWarnings("unused")
    public static String setZToZero() {
        return ZERO;
    }

    public static String systemInfo() {
        return SYSTEM_INFO;
    }

    public static String getStatus() {
        return GET_STATUS;
    }

    public static String getSelectedFile() {
        return GET_SELECTED_FILE;
    }

}
