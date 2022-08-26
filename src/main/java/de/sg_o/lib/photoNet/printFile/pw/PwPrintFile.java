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

package de.sg_o.lib.photoNet.printFile.pw;

import de.sg_o.lib.photoNet.printFile.FileRead;
import de.sg_o.lib.photoNet.printFile.PrintFile;
import de.sg_o.lib.photoNet.printFile.PrintLayer;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class PwPrintFile extends PrintFile {
    public static final int SECTION_HEADER_LENGTH = 12;
    private long position = 0;

    public PwPrintFile(InputStream input, long fileSize) {
        super(input, fileSize);
    }

    public void parse() {
        try {
            meta = new PwPrintFileMeta(this);
            readPreview();
            parseLayers();
        } catch (Exception e) {
        }
    }

    private void parseLayers() throws IOException {
        if (this.meta == null) return;
        PwPrintFileMeta meta = (PwPrintFileMeta) this.meta;
        seek(meta.getLayerHeadersOffset());
        if (!new String(readData(PwPrintFile.SECTION_HEADER_LENGTH), StandardCharsets.US_ASCII)
                .trim().equals("LAYERDEF")) throw new IOException("Invalid Header");
        int layerDefLength = readInt();
        meta.setNrLayers(readInt());

        long[] allLayers = meta.getLayers();
        try {
            for (long layer : allLayers) {
                seek(layer);
                layers.add(new PwPrintLayer(readData(PwPrintLayer.HEADER_SPACING), meta.getScreenWidth(), meta.getScreenHeight(), meta));
            }
            if (!input.markSupported()) {
                for (PrintLayer pl : layers) {
                    if (pl == null) continue;
                    seek(pl.getImgDataOffset());
                    pl.setImageData(readData(pl.getImgDataLength()));
                }
            }
        } catch (IOException ignore) {
        }
    }

    private void readPreview() {
        if (meta == null) return;
        try {
            long offset = meta.getPreviewHeaderOffset();
            seek(offset);
            preview = new PwPrintFilePreview(this, offset, meta.getLayerHeadersOffset());
            seek(preview.getImgDataOffset());
            preview.addData(readData(preview.getImgDataLength()));
        } catch (IOException ignore) {
        }
    }

    public void seek(long position) throws IOException {
        long toSkip = position - this.position;
        if (toSkip < 0) throw new IOException("Inaccessible");
        if (toSkip == 0) return;
        readData(toSkip);
    }

    public byte[] readData(long length) throws IOException {
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

    public int readInt() throws IOException {
        return FileRead.readInt(readData(4), 0);
    }

    public float readFloat() throws IOException {
        return FileRead.readFloat(readData(4), 0);
    }

    public int[] getLayerImage(int layer) {
        PrintLayer pl = layers.get(layer);
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

    private void goToFileStart() throws IOException {
        if (!input.markSupported()) return;
        input.reset();
        int fileSizeInt = Integer.MAX_VALUE;
        if (fileSize < ((long) Integer.MAX_VALUE)) {
            fileSizeInt = (int) fileSize;
        }
        input.mark(fileSizeInt);
        position = 0;
    }
}
