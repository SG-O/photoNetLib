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

package de.sg_o.lib.photoNet.networkIO.act;

import de.sg_o.lib.photoNet.networkIO.NetIO;
import de.sg_o.lib.photoNet.networkIO.NetSendBinary;

import java.io.UnsupportedEncodingException;

@SuppressWarnings("unused")
public class ActNetSendBinary extends NetSendBinary {
    public ActNetSendBinary(NetIO device, byte[] data, boolean important) {
        super(device, data, important);
    }

    public ActNetSendBinary(NetIO device, byte[] data) {
        super(device, data);
    }

    public String getResponse() {
        if (!isExecuted()) return null;
        if (isError()) return null;
        if (response == null) return null;
        byte[] data = response.getResponse();
        try {
            return new String(data, "GBK").trim();
        } catch (UnsupportedEncodingException ignore) {
        }
        return null;
    }
}
