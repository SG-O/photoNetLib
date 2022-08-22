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

@SuppressWarnings("unused")
public abstract class PrintLayer {
    protected float absolutePosition;
    protected float exposureTime;
    protected float offTime;
    protected long imgDataOffset;
    protected int imgDataLength;

    protected int screenWidth;
    protected int screenHeight;

    protected byte[] imageData = null;
    protected int backgroundColor = 0x00000000;

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

    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public void setImageData(byte[] data) {
        this.imageData = data;
    }

    public abstract int[] getImage();

    @SuppressWarnings("unused")
    public abstract int[] getImage(byte[] data);

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
