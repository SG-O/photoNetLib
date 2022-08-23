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
import de.sg_o.lib.photoNet.networkIO.NetRegularCommand;

import java.io.UnsupportedEncodingException;

public class ActNetRegularCommand extends NetRegularCommand {
    public ActNetRegularCommand(NetIO device, String command, boolean important) throws UnsupportedEncodingException {
        byte[] com = command.trim().getBytes("GBK");
        if (important) {
            response = device.sendImmediately(com, 0);
        } else {
            response = device.send(com, 0);
        }
    }

    public ActNetRegularCommand(NetIO device, String command) throws UnsupportedEncodingException {
        this(device, command, false);
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
