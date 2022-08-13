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

public class PhotonFileMeta {

    private final int fileVersion;

    private final float plateX;
    private final float plateY;
    private final float plateZ;
    private final float layerThickness;
    private final float normalExposureTime;
    private final float bottomExposureTime;
    private final float offTime;
    private final int bottomLayers;
    private final int screenHeight;
    private final int screenWidth;
    private final int lightCuringType;
    private final int nrLayers;
    private final long layerHeadersOffset;
    private final long previewHeaderOffset;
    private final long previewThumbnailHeaderOffset;
    private final int antiAliasing;

    public PhotonFileMeta(byte[] model) throws IOException {
        if (model == null) throw new IOException("Input null");
        if (model.length < 96) throw new IOException("To Short");
        int header = FileRead.readInt(model, 0);
        if (header != 0x12FD0019 && header != 0x1900FD12) {
            throw new IOException("Invalid Header");
        }
        fileVersion = FileRead.readInt(model, 4);
        plateX = FileRead.readFloat(model, 8);
        plateY = FileRead.readFloat(model, 12);
        plateZ = FileRead.readFloat(model, 16);
        layerThickness = FileRead.readFloat(model, 32);
        normalExposureTime = FileRead.readFloat(model, 36);
        bottomExposureTime = FileRead.readFloat(model, 40);
        offTime = FileRead.readFloat(model, 44);
        bottomLayers = FileRead.readInt(model, 48);
        screenHeight = FileRead.readInt(model, 52);
        screenWidth = FileRead.readInt(model, 56);
        previewHeaderOffset = ((long) FileRead.readInt(model, 60)) & 0xFFFFFFFFL;
        layerHeadersOffset = ((long) FileRead.readInt(model, 64)) & 0xFFFFFFFFL;
        nrLayers = FileRead.readInt(model, 68);
        previewThumbnailHeaderOffset = ((long) FileRead.readInt(model, 72)) & 0xFFFFFFFFL;
        lightCuringType = FileRead.readInt(model, 80);
        antiAliasing = FileRead.readInt(model, 92);
    }

    @SuppressWarnings("unused")
    public int getFileVersion() {
        return fileVersion;
    }

    @SuppressWarnings("unused")
    public float getPlateX() {
        return plateX;
    }

    @SuppressWarnings("unused")
    public float getPlateY() {
        return plateY;
    }

    @SuppressWarnings("unused")
    public float getPlateZ() {
        return plateZ;
    }

    @SuppressWarnings("unused")
    public float getLayerThickness() {
        return layerThickness;
    }

    @SuppressWarnings("unused")
    public float getNormalExposureTime() {
        return normalExposureTime;
    }

    @SuppressWarnings("unused")
    public float getBottomExposureTime() {
        return bottomExposureTime;
    }

    @SuppressWarnings("unused")
    public float getOffTime() {
        return offTime;
    }

    @SuppressWarnings("unused")
    public int getBottomLayers() {
        return bottomLayers;
    }

    @SuppressWarnings("unused")
    public int getScreenHeight() {
        return screenHeight;
    }

    @SuppressWarnings("unused")
    public int getScreenWidth() {
        return screenWidth;
    }

    @SuppressWarnings("unused")
    public int getLightCuringType() {
        return lightCuringType;
    }

    @SuppressWarnings("unused")
    public int getNrLayers() {
        return nrLayers;
    }

    @SuppressWarnings("unused")
    public long getLayerHeadersOffset() {
        return layerHeadersOffset;
    }

    @SuppressWarnings("unused")
    public long getPreviewHeaderOffset() {
        return previewHeaderOffset;
    }

    @SuppressWarnings("unused")
    public long getPreviewThumbnailHeaderOffset() {
        return previewThumbnailHeaderOffset;
    }

    @SuppressWarnings("unused")
    public long[] getLayers() {
        long[] allLayers = new long[nrLayers];
        for (int i = 0; i < allLayers.length; i++) {
            allLayers[i] = layerHeadersOffset + (i * 36L);
        }
        return allLayers;
    }

    @Override
    public String toString() {
        return "PhotonFileMeta{" +
                "fileVersion=" + fileVersion +
                ", plateX=" + plateX +
                ", plateY=" + plateY +
                ", plateZ=" + plateZ +
                ", layerThickness=" + layerThickness +
                ", normalExposureTime=" + normalExposureTime +
                ", bottomExposureTime=" + bottomExposureTime +
                ", offTime=" + offTime +
                ", bottomLayers=" + bottomLayers +
                ", screenHeight=" + screenHeight +
                ", screenWidth=" + screenWidth +
                ", lightCuringType=" + lightCuringType +
                ", nrLayers=" + nrLayers +
                ", layerHeadersOffset=" + layerHeadersOffset +
                ", previewHeaderOffset=" + previewHeaderOffset +
                ", previewThumbnailHeaderOffset=" + previewThumbnailHeaderOffset +
                ", antiAliasing=" + antiAliasing +
                '}';
    }
}
