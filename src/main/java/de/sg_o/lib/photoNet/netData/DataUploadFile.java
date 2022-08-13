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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

@SuppressWarnings("unused")
public class DataUploadFile {
    private final RandomAccessFile model;
    private final int blockSize;
    private long offset = 0;

    @SuppressWarnings("unused")
    public DataUploadFile(File modelFile, int blockSize) throws FileNotFoundException {
        this.model = new RandomAccessFile(modelFile, "r");
        this.blockSize = blockSize;
    }

    public DataTransferBlock getBlock(long offset) throws IOException {
        byte[] buffer = new byte[this.blockSize];
        model.seek(offset);
        int lengthRead = model.read(buffer);
        if (lengthRead < buffer.length) {
            byte[] tmp = buffer;
            buffer = new byte[lengthRead];
            System.arraycopy(tmp, 0, buffer, 0, lengthRead);
        }
        return new DataTransferBlock(buffer, offset);
    }

    @SuppressWarnings("unused")
    public DataTransferBlock getBlock() throws IOException {
        DataTransferBlock block = getBlock(offset);
        this.offset = this.offset + this.blockSize;
        return block;
    }

    @SuppressWarnings("unused")
    public long getOffset() {
        return offset;
    }

    @SuppressWarnings("unused")
    public int getBlockSize() {
        return blockSize;
    }

    @SuppressWarnings("unused")
    public void close() throws IOException {
        model.close();
    }
}
