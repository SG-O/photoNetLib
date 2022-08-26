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

import de.sg_o.lib.photoNet.printFile.PrintFilePreview;

import java.io.IOException;

import static de.sg_o.lib.photoNet.printFile.FileRead.readInt;

public class PhotonPrintFilePreview extends PrintFilePreview {
    public PhotonPrintFilePreview(byte[] model) throws IOException {
        if (model == null) throw new IOException("Input null");
        if (model.length < 16) throw new IOException("Input to short");
        imgWidth = readInt(model, 0);
        imgHeight = readInt(model, 4);
        imgDataOffset = ((long) readInt(model, 8)) & 0xFFFFFFFFL;
        imgDataLength = readInt(model, 12);
        imgData = new byte[imgDataLength];
    }

    private static int[] decodeImage(byte[] data, int width, int height) {
        int max = width * height;
        int[] img = new int[max];
        int count = 0;
        for (int i = 0; i < (data.length - 1); i += 2) {
            PixelDataRGB pixel = new PixelDataRGB(data[i], data[i + 1]);
            int repeat = 1;
            if (pixel.isMultiple()) {
                i += 2;
                if (i < (data.length - 1)) {
                    repeat += ((((int) data[i + 1] & 0xFF) << 8) | ((int) data[i] & 0xFF)) & 0xFFF;
                }
            }
            for (int j = 0; j < repeat; j++) {
                if (count >= max) return img;
                img[count] = pixel.getRGB();
                count++;
            }
        }
        return img;
    }

    public int[] getImage() {
        return decodeImage(imgData, imgWidth, imgHeight);
    }
}
