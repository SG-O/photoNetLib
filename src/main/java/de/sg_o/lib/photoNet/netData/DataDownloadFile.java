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
public class DataDownloadFile {
    private final RandomAccessFile model;

    @SuppressWarnings("unused")
    public DataDownloadFile(File modelFile) throws FileNotFoundException {
        if (modelFile == null) throw new FileNotFoundException("File null");
        if (modelFile.isDirectory()) throw new FileNotFoundException("File is directory");
        File parent = modelFile.getParentFile();
        if (!parent.exists() && !parent.mkdirs()) {
            throw new IllegalStateException("Couldn't create dir: " + parent);
        } else {
            //noinspection ResultOfMethodCallIgnored
            modelFile.delete();
        }
        this.model = new RandomAccessFile(modelFile, "rw");
    }

    public boolean writeBlock(DataTransferBlock block) throws IOException {
        this.model.seek(block.getOffset());
        this.model.write(block.getData());
        return true;
    }

    @SuppressWarnings("unused")
    public boolean writeBlock(byte[] raw) throws IOException {
        DataTransferBlock block = new DataTransferBlock(raw);
        if (!block.verifyChecksum()) return false;
        return writeBlock(block);
    }

    @SuppressWarnings("unused")
    public void close() throws IOException {
        this.model.close();
    }
}
