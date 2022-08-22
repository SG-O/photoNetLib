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

public abstract class Status {
    protected State state = State.UNKNOWN;
    protected float progress = 0.0f;
    protected int time = 0;
    protected float z = 0.0f;
    protected boolean updated = false;
    protected String openedFile;

    public Status() {
    }

    public abstract void update(String response);

    public abstract void updateOpenedFile(String response);

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
