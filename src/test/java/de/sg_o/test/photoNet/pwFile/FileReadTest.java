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

package de.sg_o.test.photoNet.pwFile;

import de.sg_o.lib.photoNet.printFile.FileRead;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FileReadTest {

    @Test
    void readInt() {
        assertEquals(0, FileRead.readInt(new byte[]{0,0,0,0}, 0));
        assertEquals(0, FileRead.readInt(new byte[]{1,2,3,4,0,0,0,0}, 4));

        assertEquals(123456789, FileRead.readInt(new byte[]{(byte)0x15,(byte)0xCD,(byte)0x5B,(byte)0x07}, 0));
        assertEquals(-1, FileRead.readInt(new byte[]{(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF}, 0));
        assertEquals(-2147483648, FileRead.readInt(new byte[]{(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x80}, 0));
        assertEquals(2147483647, FileRead.readInt(new byte[]{(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0x7F}, 0));
    }

    @Test
    void readLong() {
        assertEquals(0, FileRead.readLong(new byte[]{0,0,0,0,0,0,0,0}, 0));
        assertEquals(0, FileRead.readLong(new byte[]{1,2,3,4,5,6,7,8,0,0,0,0,0,0,0,0}, 8));

        assertEquals(1234567890123456789L, FileRead.readLong(new byte[]{(byte)0x15,(byte)0x81,(byte)0xE9,(byte)0x7D,(byte)0xF4,(byte)0x10,(byte)0x22,(byte)0x11}, 0));
        assertEquals(-1, FileRead.readLong(new byte[]{(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF}, 0));
        assertEquals(Long.MIN_VALUE, FileRead.readLong(new byte[]{(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x80}, 0));
        assertEquals(Long.MAX_VALUE, FileRead.readLong(new byte[]{(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0x7F}, 0));
    }

    @Test
    void readFloat() {
        assertEquals(0.0f, FileRead.readFloat(new byte[]{0,0,0,0}, 0));
        assertEquals(0.0f, FileRead.readFloat(new byte[]{1,2,3,4,0,0,0,0}, 4));

        assertEquals(123.123f, FileRead.readFloat(new byte[]{(byte)0xf9,(byte)0x3e,(byte)0xf6,(byte)0x42}, 0),0.0001);
        assertEquals(Float.NaN, FileRead.readFloat(new byte[]{(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF}, 0));

        assertEquals(3.4028234664e38f, FileRead.readFloat(new byte[]{(byte)0xFF,(byte)0xFF,(byte)0x7F,(byte)0x7F}, 0));
        assertEquals(-3.4028234664e38f, FileRead.readFloat(new byte[]{(byte)0xFF,(byte)0xFF,(byte)0x7F,(byte)0xFF}, 0));
    }

    @Test
    void readDouble() {
        assertEquals(0.0d, FileRead.readDouble(new byte[]{0,0,0,0,0,0,0,0}, 0));
        assertEquals(0.0d, FileRead.readDouble(new byte[]{1,2,3,4,5,6,7,8,0,0,0,0,0,0,0,0}, 8));

        assertEquals(123.123d, FileRead.readDouble(new byte[]{(byte)0x1d,(byte)0x5a,(byte)0x64,(byte)0x3b,(byte)0xdf,(byte)0xc7,(byte)0x5e,(byte)0x40}, 0),0.0001);
        assertEquals(Float.NaN, FileRead.readDouble(new byte[]{(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF}, 0));

        assertEquals(1.7976931348623157e308, FileRead.readDouble(new byte[]{(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xEF,(byte)0x7F}, 0));
        assertEquals(-1.7976931348623157e308, FileRead.readDouble(new byte[]{(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xEF,(byte)0xFF}, 0));
    }
}