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

package de.sg_o.lib.photoNet.printer.cbd;

import de.sg_o.lib.photoNet.netData.cbd.CbdStatus;
import de.sg_o.lib.photoNet.networkIO.cbd.CbdCommands;
import de.sg_o.lib.photoNet.networkIO.cbd.CbdNetIO;
import de.sg_o.lib.photoNet.networkIO.cbd.CbdNetRegularCommand;
import de.sg_o.lib.photoNet.printer.AsyncStatusUpdate;

public class CbdAsyncStatusUpdate extends AsyncStatusUpdate {
    public CbdAsyncStatusUpdate(int interval, CbdStatus status, CbdNetIO io) {
        super(interval, status, io);
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
                updateRequest = new CbdNetRegularCommand(io, CbdCommands.getStatus());
                selectedFileRequest = new CbdNetRegularCommand(io, CbdCommands.getSelectedFile());
                statusUpdated = false;
                fileUpdated = false;
                lastUpdate = System.currentTimeMillis();
            }
        }
    }
}
