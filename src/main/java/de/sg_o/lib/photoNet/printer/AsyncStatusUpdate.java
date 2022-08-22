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

package de.sg_o.lib.photoNet.printer;

import de.sg_o.lib.photoNet.netData.Status;
import de.sg_o.lib.photoNet.networkIO.NetIO;
import de.sg_o.lib.photoNet.networkIO.NetRegularCommand;

public abstract class AsyncStatusUpdate implements Runnable {
    protected final NetIO io;
    protected final Status status;
    protected NetRegularCommand updateRequest;
    protected NetRegularCommand selectedFileRequest;
    protected int interval;
    protected long lastUpdate = 0;
    protected boolean statusUpdated = false;
    protected boolean fileUpdated = false;
    protected int errorCount = 10;

    public AsyncStatusUpdate(int interval, Status status, NetIO io) {
        this.status = status;
        this.io = io;
        setUpdateInterval(interval);
    }

    public void setUpdateInterval(int interval) {
        if (interval < 1000) interval = 1000;
        if (interval > 600000) interval = 600000;
        this.interval = interval;
    }

    public void stop() {
        this.interval = -1;
    }
}
