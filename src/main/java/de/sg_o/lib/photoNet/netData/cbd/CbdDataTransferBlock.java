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

package de.sg_o.lib.photoNet.netData.cbd;

import java.security.InvalidParameterException;

public class CbdDataTransferBlock {
    private final byte[] data;
    private final long offset;
    private byte check = 0;

    public CbdDataTransferBlock(byte[] data, long offset) {
        this.data = data;
        this.offset = offset;
        for (byte datum : data) {
            check = (byte) (check ^ datum);
        }
        for (int i = 0; i < 4; i++) {
            check = (byte) (check ^ (offset & 0xFF));
            offset = offset >> 8;
        }
    }

    public CbdDataTransferBlock(byte[] raw) {
        if (raw == null) throw new NullPointerException();
        if (raw.length < 7) throw new ArrayIndexOutOfBoundsException();
        if (raw[raw.length - 1] != (byte) 0x83) throw new InvalidParameterException("Invalid identifier");
        this.data = new byte[raw.length - 6];
        System.arraycopy(raw, 0, this.data, 0, this.data.length);
        this.check = raw[raw.length - 2];

        int ch4 = raw[raw.length - 3] & 0xFF;
        int ch3 = raw[raw.length - 4] & 0xFF;
        int ch2 = raw[raw.length - 5] & 0xFF;
        int ch1 = raw[raw.length - 6] & 0xFF;

        this.offset = (((long) ch4 << 24) + ((long) ch3 << 16) + ((long) ch2 << 8) + (long) ch1);
    }

    public byte[] getData() {
        return data;
    }

    public long getOffset() {
        return offset;
    }

    public boolean verifyChecksum() {
        byte tmpCheck = 0;
        for (byte datum : this.data) {
            tmpCheck = (byte) (tmpCheck ^ datum);
        }

        long tmpOffset = this.offset;

        for (int i = 0; i < 4; i++) {
            tmpCheck = (byte) (tmpCheck ^ (tmpOffset & 0xFF));
            tmpOffset = tmpOffset >> 8;
        }

        return tmpCheck == this.check;
    }

    public byte[] generate() {
        byte[] out = new byte[this.data.length + 6];
        System.arraycopy(this.data, 0, out, 0, this.data.length);

        long tmpOffset = this.offset;

        for (int i = out.length - 6; i < out.length - 2; i++) {
            out[i] = (byte) (tmpOffset & 0xFF);
            tmpOffset = tmpOffset >> 8;
        }
        out[out.length - 2] = check;
        out[out.length - 1] = (byte) 0x83;
        return out;
    }
}
