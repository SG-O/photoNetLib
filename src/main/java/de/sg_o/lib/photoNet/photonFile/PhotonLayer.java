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

import static de.sg_o.lib.photoNet.photonFile.FileRead.readFloat;
import static de.sg_o.lib.photoNet.photonFile.FileRead.readInt;

@SuppressWarnings("unused")
public class PhotonLayer {
    private final float absolutePosition;
    private final float exposureTime;
    private final float offTime;
    private final long imgDataOffset;
    private final int imgDataLength;

    private final int screenWidth;
    private final int screenHeight;
    private byte[] imgData;

    @SuppressWarnings("unused")
    public PhotonLayer(byte[] model, long index, int screenWidth, int screenHeight) throws IOException {
        if (model == null) throw new IOException("File null");
        absolutePosition = readFloat(model, 0);
        exposureTime = readFloat(model, 4);
        offTime = readFloat(model, 8);

        imgDataOffset = ((long) readInt(model, 12)) & 0xFFFFFFFFL;
        imgDataLength = readInt(model, 16);

        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
    }

    private static int[] decodeImage(byte[] data, int width, int height) {
        int max = width * height;
        int[] img = new int[max];
        int count = 0;
        for (byte datum : data) {
            int color = (datum >> 7) & 1;
            int repeat = (datum & 0x7F);
            for (int j = 0; j < repeat; j++) {
                if (count >= max) return img;
                img[count] = (color * 255);
                count++;
            }
        }
        System.out.println(max + ":" + count);
        return img;
    }

    @SuppressWarnings("unused")
    public float getAbsolutePosition() {
        return absolutePosition;
    }

    @SuppressWarnings("unused")
    public float getExposureTime() {
        return exposureTime;
    }

    @SuppressWarnings("unused")
    public float getOffTime() {
        return offTime;
    }

    @SuppressWarnings("unused")
    public long getImgDataOffset() {
        return imgDataOffset;
    }

    @SuppressWarnings("unused")
    public int getImgDataLength() {
        return imgDataLength;
    }

    @SuppressWarnings("unused")
    public void addData(byte[] model) {
        System.arraycopy(model, 0, imgData, 0, imgData.length);
    }

    @SuppressWarnings("unused")
    public int[] getImage() {
        return decodeImage(imgData, screenWidth, screenHeight);
    }

    @Override
    public String toString() {
        return "PhotonLayer{" +
                "absolutePosition=" + absolutePosition +
                ", exposureTime=" + exposureTime +
                ", offTime=" + offTime +
                ", imgDataOffset=" + imgDataOffset +
                ", imgDataLength=" + imgDataLength +
                '}';
    }
}
