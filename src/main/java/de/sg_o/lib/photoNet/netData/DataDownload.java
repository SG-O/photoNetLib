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

import java.io.IOException;
import java.io.OutputStream;

public abstract class DataDownload extends DataTransfer {
    protected final FileListItem file;
    protected final OutputStream dataStream;
    protected final int maxRetries;

    protected final NetIO io;
    protected int retries = 0;
    protected long downloadLength;

    public DataDownload(FileListItem file, OutputStream dataStream, int maxRetries, long downloadLength, NetIO io) {
        this.file = file;
        this.dataStream = dataStream;
        if (maxRetries < 0) {
            maxRetries = 0;
        }
        this.maxRetries = maxRetries;
        this.downloadLength = downloadLength;
        this.io = io;
    }


    protected void fail() {
        failed.set(true);
        running.set(false);
    }

    protected void close() {
        file.closeFile();
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
    public abstract float getProgress();

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
                ", downloadLength=" + downloadLength +
                '}';
    }
}
