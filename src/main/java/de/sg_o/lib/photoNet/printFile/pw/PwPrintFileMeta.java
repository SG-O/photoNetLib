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

import de.sg_o.lib.photoNet.printFile.PrintFileMeta;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class PwPrintFileMeta extends PrintFileMeta {
    private float pixelSize;
    private float liftHeight;
    private float liftSpeed;
    private float retractSpeed;
    private float requiredMaterial;
    private float weight;
    private float price;
    private int resinType;
    private boolean usesPws = false;

    public PwPrintFileMeta() {
    }

    public PwPrintFileMeta(PwPrintFile input) throws IOException {
        if (!new String(input.readData(PwPrintFile.SECTION_HEADER_LENGTH), StandardCharsets.US_ASCII)
                .trim().equals("ANYCUBIC")) throw new IOException("Invalid Header");
        super.fileVersion = input.readInt();
        //noinspection unused
        int areaNumber = input.readInt();
        long headerOffset = input.readInt() & 0xFFFFFFFFL;
        input.readInt();
        super.previewHeaderOffset = input.readInt() & 0xFFFFFFFFL;
        input.readInt();
        super.layerHeadersOffset = input.readInt() & 0xFFFFFFFFL;
        //noinspection unused
        long extraOffset = input.readInt() & 0xFFFFFFFFL;

        input.seek(headerOffset);
        if (!new String(input.readData(PwPrintFile.SECTION_HEADER_LENGTH), StandardCharsets.US_ASCII)
                .trim().equals("HEADER")) throw new IOException("Invalid Header");
        //noinspection unused
        int headerLength = input.readInt();
        pixelSize = input.readFloat();
        super.layerThickness = input.readFloat();
        super.normalExposureTime = input.readFloat();
        super.offTime = input.readFloat();
        super.bottomExposureTime = input.readFloat();
        super.bottomLayers = Math.round(input.readFloat());
        liftHeight = input.readFloat();
        liftSpeed = input.readFloat();
        retractSpeed = input.readFloat();
        requiredMaterial = input.readFloat();
        super.antiAliasing = input.readInt();
        super.screenWidth = input.readInt();
        super.screenHeight = input.readInt();
        weight = input.readFloat();
        price = input.readFloat();
        resinType = input.readInt();
    }

    public void setNrLayers(int nrLayers) {
        super.nrLayers = nrLayers;
    }

    public long[] getLayers() {
        if (nrLayers < 1) return null;
        long[] allLayers = new long[nrLayers];
        for (int i = 0; i < allLayers.length; i++) {
            allLayers[i] = layerHeadersOffset + PwPrintFile.SECTION_HEADER_LENGTH + 8 + (i * PwPrintLayer.HEADER_SPACING);
        }
        return allLayers;
    }

    public boolean usesPws() {
        return usesPws;
    }

    public void setUsesPws(boolean usesPws) {
        this.usesPws = usesPws;
    }

    @Override
    public String toString() {
        return "PwPrintFileMeta{" + "fileVersion=" + fileVersion +
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
                ", pixelSize=" + pixelSize +
                ", liftHeight=" + liftHeight +
                ", liftSpeed=" + liftSpeed +
                ", retractSpeed=" + retractSpeed +
                ", requiredMaterial=" + requiredMaterial +
                ", weight=" + weight +
                ", price=" + price +
                ", resinType=" + resinType +
                '}';
    }
}
