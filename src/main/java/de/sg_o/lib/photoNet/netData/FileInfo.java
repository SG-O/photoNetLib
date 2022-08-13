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

package de.sg_o.lib.photoNet.netData;

import java.security.InvalidParameterException;

@SuppressWarnings("unused")
public class FileInfo {
    private final long dataRead;
    private final long totalData;
    private final boolean pauseFlag;
    private final long time;

    @SuppressWarnings("unused")
    public FileInfo(long dataRead, long totalData, boolean pauseFlag, long time) {
        this.dataRead = dataRead;
        this.totalData = totalData;
        this.pauseFlag = pauseFlag;
        this.time = time;
    }

    @SuppressWarnings("unused")
    public FileInfo(String dString, String tString) {
        if (dString == null || tString == null) throw new InvalidParameterException("Null string");
        dString = dString.trim();
        tString = tString.trim();
        if (dString.length() < 5 || tString.length() < 3) throw new InvalidParameterException("Empty String");
        dString = dString.substring(2);
        tString = tString.substring(2);
        String[] split = dString.split("/");
        if (split.length != 3) throw new InvalidParameterException("Couldn't parse String");
        this.dataRead = Long.parseLong(split[0]);
        this.totalData = Long.parseLong(split[1]);
        pauseFlag = (split[2].equals("1"));
        time = Long.parseLong(tString);
    }

    @SuppressWarnings("unused")
    public long getDataRead() {
        return dataRead;
    }

    @SuppressWarnings("unused")
    public long getTotalData() {
        return totalData;
    }

    @SuppressWarnings("unused")
    public boolean isPaused() {
        if (totalData < 1) return false;
        return pauseFlag;
    }

    @SuppressWarnings("unused")
    public long getTime() {
        return time;
    }

    @Override
    public String toString() {
        return "D:" + dataRead + "/" + totalData + "/" + (pauseFlag ? "1" : "0") + " T:" + time;
    }
}
