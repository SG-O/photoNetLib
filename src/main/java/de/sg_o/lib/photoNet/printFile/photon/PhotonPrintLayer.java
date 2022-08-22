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

package de.sg_o.lib.photoNet.printFile.photon;

import de.sg_o.lib.photoNet.printFile.PrintLayer;

import java.io.IOException;

import static de.sg_o.lib.photoNet.printFile.photon.FileRead.readFloat;
import static de.sg_o.lib.photoNet.printFile.photon.FileRead.readInt;

public class PhotonPrintLayer extends PrintLayer {
    public static final long HEADER_SPACING = 36;

    public PhotonPrintLayer(byte[] model, int screenWidth, int screenHeight) throws IOException {
        if (model == null) throw new IOException("File null");
        absolutePosition = readFloat(model, 0);
        exposureTime = readFloat(model, 4);
        offTime = readFloat(model, 8);

        imgDataOffset = ((long) readInt(model, 12)) & 0xFFFFFFFFL;
        imgDataLength = readInt(model, 16);

        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
    }

    private int[] decodeImage(byte[] data, int width, int height) {
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

    public int[] getImage() {
        if (imageData == null) return null;
        if (imageData.length < imgDataLength) return null;
        return decodeImage(imageData, screenWidth, screenHeight);
    }

    @SuppressWarnings("unused")
    public int[] getImage(byte[] data) {
        if (data == null) return null;
        if (data.length < imgDataLength) return null;
        return decodeImage(data, screenWidth, screenHeight);
    }
}
