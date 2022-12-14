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

public abstract class PrintFilePreview {
    protected int imgWidth;
    protected int imgHeight;
    protected long imgDataOffset;
    protected int imgDataLength;
    protected byte[] imgData;

    @SuppressWarnings("unused")
    public int getImgWidth() {
        return imgWidth;
    }

    @SuppressWarnings("unused")
    public int getImgHeight() {
        return imgHeight;
    }

    public long getImgDataOffset() {
        return imgDataOffset;
    }

    public long getImgDataLength() {
        return imgDataLength;
    }

    public void addData(byte[] model) {
        if (model.length > imgData.length) return;
        System.arraycopy(model, 0, imgData, 0, model.length);
    }

    @SuppressWarnings("unused")
    public abstract int[] getImage();

    @Override
    public String toString() {
        return "PhotonFilePreview{" +
                "imgWidth=" + imgWidth +
                ", imgHeight=" + imgHeight +
                ", imgDataOffset=" + imgDataOffset +
                ", imgDataLength=" + imgDataLength +
                '}';
    }
}
