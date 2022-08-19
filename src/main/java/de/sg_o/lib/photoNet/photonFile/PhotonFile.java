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

package de.sg_o.lib.photoNet.photonFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.LinkedList;

@SuppressWarnings("unused")
public class PhotonFile {
    private final LinkedList<PhotonLayer> layers = new LinkedList<>();
    private final RandomAccessFile input;
    private PhotonFileMeta meta;
    private PhotonFilePreview preview;
    private PhotonFilePreview thumbnail;

    public PhotonFile(File file) throws FileNotFoundException {
        this.input = new RandomAccessFile(file, "r");
    }

    public void parse() {
        try {
            input.seek(0);
            meta = new PhotonFileMeta(readData(96));
            parsePreview();
            parseLayers();
        } catch (Exception ignore) {
        }
    }

    private void parseLayers() {
        if (meta == null) return;
        long[] allLayers = meta.getLayers();
        try {
            for (long layer : allLayers) {
                input.seek(layer);
                layers.add(new PhotonLayer(readData(PhotonLayer.HEADER_SPACING), meta.getScreenWidth(), meta.getScreenHeight()));
            }
        } catch (IOException ignore) {
        }
    }

    private void parsePreview() {
        if (meta == null) return;
        try {
            input.seek(meta.getPreviewHeaderOffset());
            preview = new PhotonFilePreview(readData(16));
            input.seek(preview.getImgDataOffset());
            preview.addData(readData(preview.getImgDataLength()));

            input.seek(meta.getPreviewThumbnailHeaderOffset());
            thumbnail = new PhotonFilePreview(readData(16));
            input.seek(thumbnail.getImgDataOffset());
            thumbnail.addData(readData(thumbnail.getImgDataLength()));
        } catch (IOException ignore) {
        }
    }

    private byte[] readData(long length) throws IOException {
        if (length > (long) Integer.MAX_VALUE) return null;
        byte[] tmp = new byte[(int) length];
        input.readFully(tmp);
        return tmp;
    }

    public PhotonFileMeta getMeta() {
        return meta;
    }

    public PhotonFilePreview getPreview() {
        return preview;
    }

    public PhotonFilePreview getThumbnail() {
        return thumbnail;
    }

    public LinkedList<PhotonLayer> getLayers() {
        return layers;
    }

    public int[] getLayerImage(int layer) {
        try {
            PhotonLayer pl = layers.get(layer);
            if (pl == null) return null;
            input.seek(pl.getImgDataOffset());
            return pl.getImage(readData(pl.getImgDataLength()));
        } catch (Exception ignored) {
        }
        return null;
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
