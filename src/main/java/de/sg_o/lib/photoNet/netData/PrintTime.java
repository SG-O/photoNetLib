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

import java.util.Objects;

public class PrintTime {
    private final long time;

    public PrintTime(long time) {
        if (time < 0) time = 0;
        if (time > (Integer.MAX_VALUE * 3600L)) time = Integer.MAX_VALUE * 3600L;
        this.time = time;
    }

    public int getHours() {
        return (int) (this.time / 3600L);
    }

    public int getMinutes() {
        return (int) ((this.time - (getHours() * 3600L)) / 60L);
    }

    public int getSeconds() {
        return (int) ((this.time - (getHours() * 3600L)) - (getMinutes() * 60L));
    }

    public long getTime() {
        return time;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PrintTime printTime = (PrintTime) o;
        return time == printTime.time;
    }

    @Override
    public int hashCode() {
        return Objects.hash(time);
    }

    @Override
    public String toString() {
        return "PrintTime{" +
                "time=" + time + "(" +
                getHours() + "h" +
                getMinutes() + "m" +
                getSeconds() + "s)" +
                '}';
    }
}
