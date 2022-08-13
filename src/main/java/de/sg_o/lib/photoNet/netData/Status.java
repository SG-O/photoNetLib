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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Status {
    private static final Pattern pattern = Pattern.compile("B:\\d+/\\d+ E1:\\d+/\\d+ E2:\\d+/\\d+ X:\\d+.\\d+ Y:\\d+.\\d+ Z:(?<z>\\d+.\\d+) F:\\d+/\\d+ D:(?<current>\\d+)/(?<total>\\d+)/\\d+ T:(?<time>\\d+)");
    private static final Pattern filePattern = Pattern.compile("'(?<name>[^']+)'");
    private State state = State.UNKNOWN;
    private float progress = 0.0f;
    private int time = 0;
    private float z = 0.0f;
    private boolean updated = false;
    private String openedFile;

    public Status() {
    }

    @SuppressWarnings("unused")
    public Status(String response) {
        update(response);
    }

    public void update(String response) {
        if (response == null) return;
        State oldState = state;
        float oldProgress = progress;
        int oldTime = time;
        float oldZ = z;
        Matcher m = pattern.matcher(response);
        while (m.find()) {
            if (m.groupCount() == 4) {
                this.z = Float.parseFloat(m.group("z"));
                float total = Float.parseFloat(m.group("total"));
                if (total > 0.0f) {
                    this.progress = Float.parseFloat(m.group("current")) / total;
                } else {
                    this.progress = 0.0f;
                }
                this.time = Integer.parseInt(m.group("time"));
                if (this.time > 0) {
                    this.state = State.PRINTING;
                } else if (this.state == State.PRINTING || this.state == State.FINISHED) {
                    this.state = State.FINISHED;
                } else {
                    this.state = State.IDLE;
                }
            }
        }
        if (state != oldState) updated = true;
        if (progress != oldProgress) updated = true;
        if (time != oldTime) updated = true;
        if (z != oldZ) updated = true;
    }

    public void updateOpenedFile(String response) {
        if (response == null) {
            openedFile = null;
            return;
        }
        String oldFile = openedFile;
        Matcher m = filePattern.matcher(response);
        while (m.find()) {
            if (m.groupCount() == 1) {
                this.openedFile = m.group("name");
            }
        }
        if (openedFile == null) {
            if (oldFile != null) updated = true;
        } else {
            if (!this.openedFile.equals(oldFile)) updated = true;
        }
    }

    public void setDisconnected() {
        this.state = State.OFFLINE;
        this.z = Float.NaN;
        this.progress = Float.NaN;
        this.time = 0;
        updated = true;
    }

    @SuppressWarnings("unused")
    public boolean isUpdated() {
        boolean old = updated;
        updated = false;
        return old;
    }

    @SuppressWarnings("unused")
    public void removePrint() {
        if (this.state == State.FINISHED) this.state = State.IDLE;
    }

    public State getState() {
        return state;
    }

    @SuppressWarnings("unused")
    public float getProgress() {
        return progress;
    }

    @SuppressWarnings("unused")
    public int getTime() {
        return time;
    }

    @SuppressWarnings("unused")
    public float getZ() {
        return z;
    }

    @SuppressWarnings("unused")
    public String getOpenedFile() {
        return openedFile;
    }

    @Override
    public String toString() {
        return "Status{" +
                "state=" + state +
                ", progress=" + progress +
                ", time=" + time +
                ", z=" + z +
                '}';
    }

    public enum State {
        IDLE,
        PRINTING,
        FINISHED,
        OFFLINE,
        UNKNOWN
    }
}
