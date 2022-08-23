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

package de.sg_o.lib.photoNet.netData.cbd;

import de.sg_o.lib.photoNet.netData.DataUpload;
import de.sg_o.lib.photoNet.netData.FileListItem;
import de.sg_o.lib.photoNet.netData.Status;
import de.sg_o.lib.photoNet.networkIO.NetIO;
import de.sg_o.lib.photoNet.networkIO.NetRegularCommand;
import de.sg_o.lib.photoNet.networkIO.NetSendBinary;
import de.sg_o.lib.photoNet.networkIO.cbd.CbdNetRegularCommand;
import de.sg_o.lib.photoNet.networkIO.cbd.CbdNetSendBinary;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;

public class CbdDataUpload extends DataUpload {
    private static final int blockSize = 1280;

    private int glide = 0;

    public CbdDataUpload(FileListItem file, InputStream dataStream, int maxRetries, NetIO io) {
        super(file, dataStream, maxRetries, io);
    }

    @Override
    public void run() {
        if (complete.get()) return;

        failed.set(false);
        running.set(true);

        try {
            if (!createFile()) {
                fail();
                return;
            }
            if (dataStream == null) {
                fail();
                return;
            }
            byte[] buffer = new byte[blockSize];
            long time = System.currentTimeMillis();
            int bytesRead;
            while ((bytesRead = dataStream.read(buffer, 0, buffer.length)) != -1) {
                if (aborted.get()) {
                    fail();
                    return;
                }
                while (paused.get()) {
                    try {
                        if (aborted.get()) {
                            fail();
                            return;
                        }
                        Thread.sleep(100);
                    } catch (InterruptedException ignore) {
                    }
                }
                byte[] toSend = buffer;
                if (bytesRead < buffer.length) {
                    toSend = new byte[bytesRead];
                    System.arraycopy(buffer, 0, toSend, 0, bytesRead);
                }
                if (!sendData(toSend, glide)) {
                    if (retries < maxRetries) {
                        retries++;
                        continue;
                    } else {
                        fail();
                        return;
                    }
                }
                retries = 0;
                glide += bytesRead;
                super.transferSpeed = ((float) bytesRead) / ((float) (System.currentTimeMillis() - time) / 1000.0f);
                time = System.currentTimeMillis();
            }
            dataStream.close();
            endFile();
            complete.set(true);
            running.set(false);
        } catch (Exception e) {
            fail();
        }
    }

    private boolean sendData(byte[] data, int offset) {
        CbdDataTransferBlock block = new CbdDataTransferBlock(data, offset);
        NetSendBinary send = new CbdNetSendBinary(io, block.generate());
        while (!send.isExecuted()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException ignore) {
            }
        }
        return !send.isError();
    }

    private void fail() {
        failed.set(true);
        running.set(false);
        try {
            endFile();
        } catch (UnsupportedEncodingException ignore) {
        }
    }

    private void endFile() throws UnsupportedEncodingException {
        NetRegularCommand endFile = new CbdNetRegularCommand(io, "M29");
        while (!endFile.isExecuted()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException ignore) {
            }
        }
        closeFile();
    }

    private void closeFile() {
        NetRegularCommand closeFile = new CbdNetRegularCommand(io, "M22");
        while (!closeFile.isExecuted()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException ignore) {
            }
        }
    }

    private boolean createFile() {
        if (file == null) return false;
        if (file.getName() == null) return false;
        if (file.getName().length() < 1) return false;
        NetRegularCommand statusRequest = new CbdNetRegularCommand(io, "M4000");
        while (!statusRequest.isExecuted()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException ignore) {
            }
        }
        if (statusRequest.getResponse() == null) return false;
        Status stat = new CbdStatus(statusRequest.getResponse());
        stat.update(statusRequest.getResponse());
        if (stat.getState() != Status.State.IDLE && stat.getState() != Status.State.FINISHED) return false;
        closeFile();
        NetRegularCommand fileRequest = new CbdNetRegularCommand(io, "M28 " + file.getFullPath());
        while (!fileRequest.isExecuted()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException ignore) {
            }
        }
        return true;
    }

    @Override
    public float getProgress() {
        if (file.getSize() < 1L) return 0;
        return (float) glide / (float) file.getSize();
    }
}
