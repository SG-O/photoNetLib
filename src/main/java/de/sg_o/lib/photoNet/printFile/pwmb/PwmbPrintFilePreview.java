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

package de.sg_o.lib.photoNet.printFile.pwmb;

import de.sg_o.lib.photoNet.printFile.PrintFilePreview;

public class PwmbPrintFilePreview extends PrintFilePreview {
    public PwmbPrintFilePreview() {
        imgWidth = 224;
        imgHeight = 168;
        imgDataLength = imgWidth * imgHeight * 2;
        imgData = new byte[imgDataLength];
    }

    private static int[] decodeImage(byte[] data, int width, int height) {
        int max = width * height;
        if (data.length < (max * 2)) return null;
        int[] img = new int[max];
        for (int i = 0; i < (data.length - 1); i += 2) {
            int tmp = (data[i + 1] + (data[i] << 8)) & 0xFFFF;
            img[i >> 1] = ((tmp & 0xF800) << 8) | ((tmp & 0x7E0) << 5) | ((tmp & 0x1F) << 3) | 0xFF000000;
        }
        return img;
    }

    public int[] getImage() {
        return decodeImage(imgData, imgWidth, imgHeight);
    }
}
