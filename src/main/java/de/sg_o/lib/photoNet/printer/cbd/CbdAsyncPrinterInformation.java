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

package de.sg_o.lib.photoNet.printer.cbd;

import de.sg_o.lib.photoNet.networkIO.NetIO;
import de.sg_o.lib.photoNet.networkIO.NetRegularCommand;
import de.sg_o.lib.photoNet.networkIO.cbd.CbdNetRegularCommand;
import de.sg_o.lib.photoNet.printer.AsyncPrinterInformation;
import de.sg_o.lib.photoNet.printer.Printer;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CbdAsyncPrinterInformation extends AsyncPrinterInformation {
    private static final Pattern pattern = Pattern.compile("MAC:(?<mac>[0-9a-fA-F]+:[0-9a-fA-F]+:[0-9a-fA-F]+:[0-9a-fA-F]+:[0-9a-fA-F]+:[0-9a-fA-F]+) IP:(?<ip>\\d+.\\d+.\\d+.\\d+) VER:(?<ver>V[0-9.]+) ID:(?<id>[0-9a-fA-F,]+) NAME:(?<name>\\S+)");

    Thread infoThread;

    public CbdAsyncPrinterInformation(Printer p, NetIO io) {
        super(p, io);
    }


    public void run() {
        NetRegularCommand statusRequest;
        statusRequest = new CbdNetRegularCommand(io, "M99999");
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

    public void update() {
        infoThread = new Thread(this);
        infoThread.start();
    }
}
