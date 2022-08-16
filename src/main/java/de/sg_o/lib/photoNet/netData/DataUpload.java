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

import de.sg_o.lib.photoNet.networkIO.NetIO;
import de.sg_o.lib.photoNet.networkIO.NetRegularCommand;
import de.sg_o.lib.photoNet.networkIO.NetSendBinary;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;

public class DataUpload extends DataTransfer {
    private static final int blockSize = 1280;
    private final FileListItem file;
    private final InputStream dataStream;
    private final int maxRetries;
    private final NetIO io;
    private int glide = 0;
    private int retries = 0;

    public DataUpload(FileListItem file, InputStream dataStream, int maxRetries, NetIO io) {
        this.file = file;
        this.dataStream = dataStream;
        if (maxRetries < 0) {
            maxRetries = 0;
        }
        this.maxRetries = maxRetries;
        this.io = io;
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
        DataTransferBlock block = new DataTransferBlock(data, offset);
        NetSendBinary send = new NetSendBinary(io, block.generate());
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

    private boolean createFile() throws UnsupportedEncodingException {
        if (file == null) return false;
        if (file.getName() == null) return false;
        if (file.getName().length() < 1) return false;
        NetRegularCommand statusRequest = new NetRegularCommand(io, "M4000");
        while (!statusRequest.isExecuted()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException ignore) {
            }
        }
        if (statusRequest.getResponse() == null) return false;
        Status stat = new Status(statusRequest.getResponse());
        stat.update(statusRequest.getResponse());
        if (stat.getState() != Status.State.IDLE && stat.getState() != Status.State.FINISHED) return false;
        closeFile();
        NetRegularCommand fileRequest = new NetRegularCommand(io, "M28 " + file.getFullPath());
        while (!fileRequest.isExecuted()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException ignore) {
            }
        }
        return true;
    }

    private void endFile() throws UnsupportedEncodingException {
        NetRegularCommand endFile = new NetRegularCommand(io, "M29");
        while (!endFile.isExecuted()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException ignore) {
            }
        }
        closeFile();
    }

    private void closeFile() throws UnsupportedEncodingException {
        NetRegularCommand closeFile = new NetRegularCommand(io, "M22");
        while (!closeFile.isExecuted()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException ignore) {
            }
        }
    }

    @Override
    public boolean isComplete() {
        if (failed.get()) return false;
        return complete.get();
    }

    @Override
    public boolean hasFailed() {
        return failed.get();
    }

    @Override
    public float getProgress() {
        if (file.getSize() < 1L) return 0;
        return (float) glide / (float) file.getSize();
    }

    @Override
    public String getName() {
        if (file == null) return null;
        return file.getName();
    }

    @Override
    public boolean isPaused() {
        return false;
    }

    @Override
    public void setPaused(boolean paused) {
        this.paused.set(paused);
    }

    @Override
    public boolean isRunning() {
        return running.get();
    }

    @Override
    public void abort() {
        this.aborted.set(true);
        end();
    }

    @Override
    public boolean isAborted() {
        return this.aborted.get();
    }

    @Override
    public void end() {
        try {
            if (file != null) file.closeFile();
        } catch (UnsupportedEncodingException ignore) {
        }
    }

    @Override
    public String toString() {
        String sb = "DataUpload{" + "complete=" + complete +
                ", failed=" + failed +
                ", file='" + file + '\'' +
                ", maxRetries=" + maxRetries +
                '}';
        return sb;
    }
}
