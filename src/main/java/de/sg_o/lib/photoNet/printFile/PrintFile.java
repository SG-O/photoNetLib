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

package de.sg_o.lib.photoNet.printFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;

@SuppressWarnings("unused")
public abstract class PrintFile {
    protected final LinkedList<PrintLayer> layers = new LinkedList<>();
    protected final InputStream input;
    protected final long fileSize;
    protected PrintFileMeta meta;
    protected PrintFilePreview preview;
    protected PrintFilePreview thumbnail;

    public PrintFile(InputStream input, long fileSize) {
        this.input = input;
        this.fileSize = fileSize;
        int fileSizeInt = Integer.MAX_VALUE;
        if (fileSize < ((long) Integer.MAX_VALUE)) {
            fileSizeInt = (int) fileSize;
        }
        if (input.markSupported()) {
            input.mark(fileSizeInt);
        }
    }

    public abstract void parse();


    public PrintFileMeta getMeta() {
        return meta;
    }

    public PrintFilePreview getPreview() {
        return preview;
    }

    public PrintFilePreview getThumbnail() {
        return thumbnail;
    }

    public LinkedList<PrintLayer> getLayers() {
        return layers;
    }

    public abstract int[] getLayerImage(int layer);

    public void close() {
        try {
            input.close();
        } catch (IOException ignore) {
        }
    }

    @Override
    public String toString() {
        return "PhotonFile{" + "meta=" + meta +
                ", preview=" + preview +
                ", thumbnail=" + thumbnail +
                ", layers=" + layers +
                '}';
    }
}
