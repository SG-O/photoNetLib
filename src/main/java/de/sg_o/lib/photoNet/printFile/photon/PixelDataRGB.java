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

public class PixelDataRGB {
    private static final int[] VALUE_MAP = {0, 8, 16, 25, 33, 41, 49, 58, 66, 74, 82, 90, 99, 107, 115, 123, 132, 140, 148, 156, 165, 173, 181, 189, 197, 206, 214, 222, 230, 239, 247, 255};

    private final int red;
    private final int green;
    private final int blue;
    private final boolean multiple;

    public PixelDataRGB(byte low, byte high) {
        int RGB = (((int) high & 0xFF) << 8) | ((int) low & 0xFF);

        this.blue = RGB & 0x1f;
        this.multiple = (RGB & 0x20) > 0;
        this.green = (RGB >> 6) & 0x1f;
        this.red = (RGB >> 11) & 0x1f;
    }

    public int getRed() {
        return VALUE_MAP[red];
    }

    public int getGreen() {
        return VALUE_MAP[green];
    }

    public int getBlue() {
        return VALUE_MAP[blue];
    }

    public int getRGB() {
        return (0xff) << 24 | (getRed() & 0xff) << 16 | (getGreen() & 0xff) << 8 | (getBlue() & 0xff);
    }

    public boolean isMultiple() {
        return multiple;
    }

    @Override
    public String toString() {
        return "PixelDataRGB{" +
                "red=" + red +
                ", green=" + green +
                ", blue=" + blue +
                '}';
    }
}
