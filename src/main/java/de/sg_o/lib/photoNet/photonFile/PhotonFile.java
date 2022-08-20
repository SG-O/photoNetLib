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

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;

@SuppressWarnings("unused")
public class PhotonFile {
    private final LinkedList<PhotonLayer> layers = new LinkedList<>();
    private final InputStream input;
    private PhotonFileMeta meta;
    private PhotonFilePreview preview;
    private PhotonFilePreview thumbnail;
    private final long fileSize;
    private long position = 0;

    public PhotonFile(InputStream input, long fileSize) {
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

    public void parse() {
        try {
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
                seek(layer);
                layers.add(new PhotonLayer(readData(PhotonLayer.HEADER_SPACING), meta.getScreenWidth(), meta.getScreenHeight()));
            }
            if (!input.markSupported()) {
                for (PhotonLayer pl : layers) {
                    if (pl == null) continue;
                    seek(pl.getImgDataOffset());
                    pl.setImageData(readData(pl.getImgDataLength()));
                }
            }
        } catch (IOException ignore) {
        }
    }

    private void parsePreview() {
        if (meta == null) return;
        if (meta.getPreviewHeaderOffset() < meta.getPreviewThumbnailHeaderOffset()) {
            readPreview();
            readThumbnail();
        } else {
            readThumbnail();
            readPreview();
        }
    }

    private void readPreview() {
        if (meta == null) return;
        try {
            seek(meta.getPreviewHeaderOffset());
            preview = new PhotonFilePreview(readData(16));
            seek(preview.getImgDataOffset());
            preview.addData(readData(preview.getImgDataLength()));
        } catch (IOException ignore) {
        }
    }

    private void readThumbnail() {
        if (meta == null) return;
        try {
            seek(meta.getPreviewThumbnailHeaderOffset());
            thumbnail = new PhotonFilePreview(readData(16));
            seek(thumbnail.getImgDataOffset());
            thumbnail.addData(readData(thumbnail.getImgDataLength()));
        } catch (IOException ignore) {
        }
    }

    private void seek(long position) throws IOException {
        long toSkip = position - this.position;
        if (toSkip < 0) throw new IOException("Inaccessible");
        if (toSkip == 0) return;
        readData(toSkip);
    }

    private byte[] readData(long length) throws IOException {
        if (length > ((long) Integer.MAX_VALUE)) return null;
        int lengthInt = (int) length;
        byte[] tmp = new byte[(int) length];
        int read = 0;
        while ((read += input.read(tmp, read, lengthInt - read)) != -1) {
            if (read >= lengthInt) break;
        }
        position += read;
        if (read != length) throw new IOException("Read less than expected");
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
        PhotonLayer pl = layers.get(layer);
        if (pl == null) return null;
        if (input.markSupported()) {
            try {
                goToFileStart();
                seek(pl.getImgDataOffset());
                return pl.getImage(readData(pl.getImgDataLength()));
            } catch (Exception ignored) {
            }
        }
        return pl.getImage();
    }

    public void goToFileStart() throws IOException {
        if (!input.markSupported()) return;
        input.reset();
        int fileSizeInt = Integer.MAX_VALUE;
        if (fileSize < ((long) Integer.MAX_VALUE)) {
            fileSizeInt = (int) fileSize;
        }
        input.mark(fileSizeInt);
        position = 0;
    }

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
