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

/**
 * Helper methods to extract data from little endian encoded byte arrays.
 */
public class FileRead {
    public static int readInt(byte[] file, int offset) {
        int ch1 = file[offset] & 0xFF;
        int ch2 = file[offset + 1] & 0xFF;
        int ch3 = file[offset + 2] & 0xFF;
        int ch4 = file[offset + 3] & 0xFF;
        return ((ch4 << 24) + (ch3 << 16) + (ch2 << 8) + ch1);
    }

    public static long readLong(byte[] file, int offset) {
        return ((long) (readInt(file, offset + 4)) << 32) | (readInt(file, offset) & 0xFFFFFFFFL);
    }

    public static float readFloat(byte[] file, int offset) {
        return Float.intBitsToFloat(readInt(file, offset));
    }

    @SuppressWarnings("unused")
    public static double readDouble(byte[] file, int offset) {
        return Double.longBitsToDouble(readLong(file, offset));
    }
}
