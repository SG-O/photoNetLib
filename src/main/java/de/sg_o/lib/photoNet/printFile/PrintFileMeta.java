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

public abstract class PrintFileMeta {
    protected int fileVersion;

    protected float plateX;
    protected float plateY;
    protected float plateZ;
    protected float layerThickness;
    protected float normalExposureTime;
    protected float bottomExposureTime;
    protected float offTime;
    protected int bottomLayers;
    protected int screenHeight;
    protected int screenWidth;
    protected int lightCuringType;
    protected int nrLayers;
    protected long layerHeadersOffset;
    protected long previewHeaderOffset;
    protected long previewThumbnailHeaderOffset;
    protected int antiAliasing;


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

    public int getAntiAliasing() {
        return antiAliasing;
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
