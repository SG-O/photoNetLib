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
import de.sg_o.lib.photoNet.networkIO.NetRequestBinary;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

public class DataDownload extends DataTransfer {
    private final FileListItem file;
    private final OutputStream dataStream;
    private final int maxRetries;
    private final long startOffset;
    private final NetIO io;
    private long glide = 0L;
    private int retries = 0;
    private long downloadLength;

    public DataDownload(FileListItem file, OutputStream dataStream, int maxRetries, long startOffset, long downloadLength, NetIO io) {
        this.file = file;
        this.dataStream = dataStream;
        if (maxRetries < 0) {
            maxRetries = 0;
        }
        this.maxRetries = maxRetries;
        if (startOffset < 0) startOffset = 0;
        this.startOffset = startOffset;
        this.downloadLength = downloadLength;
        this.io = io;
    }

    public void run() {
        if (complete.get()) {
            return;
        }
        failed.set(false);
        running.set(true);

        try {
            if (file == null) {
                fail();
                return;
            }
            long size = file.openFile() - startOffset;
            if (size <= 0) {
                fail();
                if (size == 0) {
                    close();
                }
                return;
            }
            if ((downloadLength < size) && (downloadLength > 0)) {
                size = downloadLength;
            } else {
                downloadLength = size;
            }
            DataTransferBlock response;
            long time = System.currentTimeMillis();
            while (glide < size) {
                if (aborted.get()) {
                    fail();
                    close();
                    return;
                }
                while (paused.get()) {
                    try {
                        if (aborted.get()) {
                            fail();
                            close();
                            return;
                        }
                        Thread.sleep(100);
                    } catch (InterruptedException ignore) {
                    }
                }
                response = readFromOffset(startOffset + glide);
                if (response == null) {
                    if (retries < maxRetries) {
                        retries++;
                        continue;
                    } else {
                        fail();
                        close();
                        return;
                    }
                }
                if (!response.verifyChecksum()) {
                    if (retries < maxRetries) {
                        retries++;
                        continue;
                    } else {
                        fail();
                        close();
                        return;
                    }
                }
                int length = response.getData().length;
                if (length > size - glide) {
                    length = (int) (size - glide);
                }
                dataStream.write(response.getData(), 0, length);
                retries = 0;
                glide += length;
                super.transferSpeed = ((float) length) / ((float) (System.currentTimeMillis() - time) / 1000.0f);
                time = System.currentTimeMillis();
            }
            dataStream.close();
            file.closeFile();
            complete.set(true);
            running.set(false);
        } catch (Exception e) {
            fail();
            close();
        }
    }


    private void fail() {
        failed.set(true);
        running.set(false);
    }

    private void close() {
        try {
            file.closeFile();
        } catch (UnsupportedEncodingException ignore) {
        }
    }

    private DataTransferBlock readFromOffset(long offset) {
        NetRequestBinary data = new NetRequestBinary(io, "M3001 I" + offset);
        while (!data.isExecuted()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException ignore) {
            }
        }
        if (data.getResponse() == null) return null;
        return new DataTransferBlock(data.getResponse());
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
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
        if (downloadLength < 1) return 0;
        return (float) glide / (float) downloadLength;
    }

    @Override
    public String getName() {
        if (file == null) return null;
        return file.getName();
    }

    @Override
    public boolean isPaused() {
        return paused.get();
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
            dataStream.close();
        } catch (IOException ignore) {
        }
    }

    @Override
    public String toString() {
        return "DataDownload{" + "complete=" + complete +
                ", failed=" + failed +
                ", file='" + file + '\'' +
                ", maxRetries=" + maxRetries +
                ", startOffset=" + startOffset +
                ", downloadLength=" + downloadLength +
                '}';
    }
}
