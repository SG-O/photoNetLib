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

package de.sg_o.lib.photoNet.printer;

import de.sg_o.lib.photoNet.networkIO.NetIO;
import de.sg_o.lib.photoNet.networkIO.NetRegularCommand;

import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AsyncPrinterInformation implements Runnable {
    private static final Pattern pattern = Pattern.compile("MAC:(?<mac>[0-9a-fA-F]+:[0-9a-fA-F]+:[0-9a-fA-F]+:[0-9a-fA-F]+:[0-9a-fA-F]+:[0-9a-fA-F]+) IP:(?<ip>\\d+.\\d+.\\d+.\\d+) VER:(?<ver>V[0-9.]+) ID:(?<id>[0-9a-fA-F,]+) NAME:(?<name>\\S+)");
    private final NetIO io;
    private final Printer p;


    public AsyncPrinterInformation(Printer p, NetIO io) {
        this.p = p;
        this.io = io;
    }

    public void run() {
        NetRegularCommand statusRequest;
        try {
            statusRequest = new NetRegularCommand(io, "M99999");
        } catch (UnsupportedEncodingException ignore) {
            return;
        }
        while (!statusRequest.isExecuted()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException ignore) {
            }
        }
        String response = statusRequest.getResponse();
        if (response == null) return;
        Matcher m = pattern.matcher(response);
        while (m.find()) {
            if (m.groupCount() == 5) {
                String mac = m.group("mac");
                String ver = m.group("ver");
                String id = m.group("id");
                String name = m.group("name");
                p.populateInformation(name, ver, mac, id);
            }
        }
    }
}
