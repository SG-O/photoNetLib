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

import de.sg_o.lib.photoNet.printFile.PrintFileMeta;

import java.io.IOException;

public class PhotonPrintFileMeta extends PrintFileMeta {
    public PhotonPrintFileMeta(byte[] model) throws IOException {
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
        screenWidth = FileRead.readInt(model, 52);
        screenHeight = FileRead.readInt(model, 56);
        previewHeaderOffset = ((long) FileRead.readInt(model, 60)) & 0xFFFFFFFFL;
        layerHeadersOffset = ((long) FileRead.readInt(model, 64)) & 0xFFFFFFFFL;
        nrLayers = FileRead.readInt(model, 68);
        previewThumbnailHeaderOffset = ((long) FileRead.readInt(model, 72)) & 0xFFFFFFFFL;
        lightCuringType = FileRead.readInt(model, 80);
        antiAliasing = FileRead.readInt(model, 92);
    }
}
