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

public class AsyncStatusUpdate implements Runnable {
    private final NetIO io;
    private final Status status;
    private NetRegularCommand updateRequest;
    private NetRegularCommand selectedFileRequest;
    private int interval;
    private long lastUpdate = 0;
    private boolean statusUpdated = false;
    private boolean fileUpdated = false;
    private int errorCount = 10;

    public AsyncStatusUpdate(int interval, Status status, NetIO io) {
        this.status = status;
        this.io = io;
        setUpdateInterval(interval);
    }

    public void run() {
        while (interval > 1) {
            if (updateRequest != null) {
                if (!updateRequest.isExecuted()) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException ignored) {
                    }
                    continue;
                }
                if (!statusUpdated) {
                    if (updateRequest.isError()) {
                        errorCount++;
                        if (errorCount > 10) {
                            this.status.setDisconnected();
                            errorCount = 10;
                        }
                    } else {
                        errorCount = 0;
                        this.status.update(updateRequest.getResponse());
                        this.statusUpdated = true;
                    }
                }
            }
            if (selectedFileRequest != null) {
                if (!selectedFileRequest.isExecuted()) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException ignored) {
                    }
                    continue;
                }
                if (!fileUpdated) {
                    if (!selectedFileRequest.isError()) {
                        this.status.updateOpenedFile(selectedFileRequest.getResponse());
                        this.fileUpdated = true;
                    }
                }
            }
            if (lastUpdate + interval < System.currentTimeMillis()) {
                updateRequest = new NetRegularCommand(io, "M4000");
                selectedFileRequest = new NetRegularCommand(io, "M4006");
                statusUpdated = false;
                fileUpdated = false;
                lastUpdate = System.currentTimeMillis();
            }
        }
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
