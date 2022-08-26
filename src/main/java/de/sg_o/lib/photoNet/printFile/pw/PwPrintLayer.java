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

import de.sg_o.lib.photoNet.printFile.PrintLayer;

import java.io.IOException;

import static de.sg_o.lib.photoNet.printFile.FileRead.readFloat;
import static de.sg_o.lib.photoNet.printFile.FileRead.readInt;

public class PwPrintLayer extends PrintLayer {
    public static final long HEADER_SPACING = 32;
    private final PwPrintFileMeta meta;

    public PwPrintLayer(byte[] model, int screenWidth, int screenHeight, PwPrintFileMeta meta) throws IOException {
        if (model == null) throw new IOException("File null");
        imgDataOffset = ((long) readInt(model, 0)) & 0xFFFFFFFFL;
        imgDataLength = readInt(model, 4);

        exposureTime = readFloat(model, 16);

        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.meta = meta;
    }

    private int[] decodeImagePws(byte[] data, int width, int height) {
        int max = width * height;
        int[] img = new int[max];
        int count = 0;
        for (byte datum : data) {
            int color = (datum >> 7) & 1;
            int repeat = (datum & 0x7F);
            for (int j = 0; j < repeat; j++) {
                if (count >= max) return img;
                int x = (width - (count % width)) - 1; //Why is the layer flipped? WHY???
                int y = count / width;
                img[x + (y * width)] = (color * 0xFFFFFFFF) | backgroundColor;
                count++;
            }
        }
        return img;
    }

    private int[] decodeImagePw0(byte[] data, int width, int height) {
        int max = width * height;
        int[] img = new int[max];

        int count = 0;
        int color = 0;
        for (int i = 0; i < data.length; i++) {
            int code = (data[i] >> 4) & 0xF;
            int repeat = data[i] & 0xF;
            if (code == 0x0 || code == 0xF) {
                color = code;
                i++;
                if (i >= data.length) {
                    break;
                }
                repeat = (repeat << 8) + (data[i] & 0xFF);
            } else {
                color = code;
            }
            for (int j = 0; j < repeat; j++) {
                if (count >= max) {
                    if (data.length > (i + 10)) {
                        meta.setUsesPws(true);
                        return decodeImagePws(data, width, height);
                    }
                    return img;
                }
                int x = (width - (count % width)) - 1; //Why is the layer flipped? WHY???
                int y = count / width;
                img[x + (y * width)] = color | color << 4 | color << 8 | color << 12 | color << 16 | color << 20 | color << 24 | color << 28 | backgroundColor;
                count++;
            }
        }
        if (count < max - 256) {
            meta.setUsesPws(true);
            return decodeImagePws(data, width, height);
        }
        return img;
    }

    public int[] getImage() {
        if (imageData == null) return null;
        if (imageData.length < imgDataLength) return null;
        if (meta.usesPws()) {
            return decodeImagePws(imageData, screenWidth, screenHeight);
        }
        return decodeImagePw0(imageData, screenWidth, screenHeight);
    }

    @SuppressWarnings("unused")
    public int[] getImage(byte[] data) {
        if (data == null) return null;
        if (data.length < imgDataLength) return null;
        if (meta.usesPws()) {
            return decodeImagePws(imageData, screenWidth, screenHeight);
        }
        return decodeImagePw0(data, screenWidth, screenHeight);
    }
}
