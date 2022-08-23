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

package de.sg_o.lib.photoNet.networkIO.cbd;

import de.sg_o.lib.photoNet.networkIO.NetIO;
import de.sg_o.lib.photoNet.networkIO.NetRequestBinary;

import java.nio.charset.StandardCharsets;

public class CbdNetRequestBinary extends NetRequestBinary {
    public CbdNetRequestBinary(NetIO device, String command, int expectedLength, boolean important) {
        byte[] com = command.trim().getBytes(StandardCharsets.US_ASCII);
        if (important) {
            response = device.sendImmediately(com, expectedLength);
        } else {
            response = device.send(com, expectedLength);
        }
    }

    public CbdNetRequestBinary(NetIO device, String command, int expectedLength) {
        this(device, command, expectedLength, false);
    }
}
