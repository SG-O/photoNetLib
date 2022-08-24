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

package de.sg_o.lib.photoNet.netData.act;

import de.sg_o.lib.photoNet.netData.Status;
import de.sg_o.lib.photoNet.networkIO.act.ActCommands;

public class ActStatus extends Status {
    public ActStatus() {
        super();
    }

    @SuppressWarnings("unused")
    public ActStatus(String response) {
        super();
        update(response);
    }

    public void update(String response) {
        if (response == null) return;
        String[] split = response.split(",");
        if (split.length < 3) return;
        if (!split[0].equals(ActCommands.GET_STATUS)) return;
        if (!split[split.length - 1].equals(ActCommands.Values.END.toString())) return;

        State oldState = state;
        float oldProgress = progress;
        int oldTime = time;
        float oldZ = z;
        String oldFile = openedFile;

        if (split[1].equals(ActCommands.Values.STOP.toString())) {
            if (this.state == State.PRINTING || this.state == State.FINISHED) {
                this.state = State.FINISHED;
            } else {
                this.state = State.IDLE;
            }
            setNull();
        }
        if (split[1].equals(ActCommands.Values.FINISH.toString())) {
            this.state = State.FINISHED;
            setNull();
        }
        if (split[1].equals(ActCommands.Values.PAUSE.toString())) {
            this.state = State.PAUSE;
            parseDetailed(split);
        }
        if (split[1].equals(ActCommands.Values.PRINT.toString())) {
            this.state = State.PRINTING;
            parseDetailed(split);
        }

        if (state != oldState) {
            if (oldState == State.OFFLINE) {
                cameOnline = true;
            }
            updated = true;
        }
        if (progress != oldProgress) updated = true;
        if (time != oldTime) updated = true;
        if (z != oldZ) updated = true;
        if (openedFile == null) {
            if (oldFile != null) updated = true;
        } else {
            if (!this.openedFile.equals(oldFile)) updated = true;
        }
    }

    private void parseDetailed(String[] data) {
        if (data.length < 10) return;
        String[] split = data[2].split("/");
        if (split.length == 2) {
            openedFile = split[0];
        }
        progress = (float) Integer.parseInt(data[4]) / 100.0f;
        time = Integer.parseInt(data[7]);
        if (data.length > 11) {
            z = Integer.parseInt(data[5]) * Float.parseFloat(data[11]);
        }
    }

    private void setNull() {
        this.progress = 0;
        this.time = 0;
        this.z = 0;
        this.openedFile = null;
    }

    public void updateOpenedFile(String response) {
    }
}
