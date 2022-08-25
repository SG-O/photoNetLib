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

import de.sg_o.lib.photoNet.netData.DataDownload;
import de.sg_o.lib.photoNet.netData.FileListItem;
import de.sg_o.lib.photoNet.networkIO.NetIO;
import de.sg_o.lib.photoNet.networkIO.NetRequestBinary;
import de.sg_o.lib.photoNet.networkIO.cbd.CbdCommands;
import de.sg_o.lib.photoNet.networkIO.cbd.CbdNetRequestBinary;

import java.io.OutputStream;

public class CbdDataDownload extends DataDownload {
    private final long startOffset;
    private long glide = 0L;

    public CbdDataDownload(FileListItem file, OutputStream dataStream, int maxRetries, long startOffset, long downloadLength, NetIO io) {
        super(file, dataStream, maxRetries, downloadLength, io);
        if (startOffset < 0) startOffset = 0;
        this.startOffset = startOffset;
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
            CbdDataTransferBlock response;
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

    private CbdDataTransferBlock readFromOffset(long offset) {
        NetRequestBinary data = new CbdNetRequestBinary(io, CbdCommands.readFileOffset(offset), 0);
        while (!data.isExecuted()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException ignore) {
            }
        }
        if (data.getResponse() == null) return null;
        return new CbdDataTransferBlock(data.getResponse());
    }

    @Override
    public float getProgress() {
        if (downloadLength < 1) return 0;
        return (float) glide / (float) downloadLength;
    }
}
