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

package de.sg_o.lib.photoNet.printer.act;

import de.sg_o.lib.photoNet.networkIO.NetIO;
import de.sg_o.lib.photoNet.networkIO.NetRegularCommand;
import de.sg_o.lib.photoNet.networkIO.act.ActCommands;
import de.sg_o.lib.photoNet.networkIO.act.ActNetRegularCommand;
import de.sg_o.lib.photoNet.printer.AsyncPrinterInformation;
import de.sg_o.lib.photoNet.printer.Printer;

import java.io.UnsupportedEncodingException;

public class ActAsyncPrinterInformation extends AsyncPrinterInformation {
    Thread infoThread;

    public ActAsyncPrinterInformation(Printer p, NetIO io) {
        super(p, io);
    }

    public void run() {
        NetRegularCommand sysInfoRequest;
        NetRegularCommand nameRequest;
        try {
            String mac = "";
            String ver = null;
            String name = null;
            String id = null;
            sysInfoRequest = new ActNetRegularCommand(io, ActCommands.systemInfo());
            while (!sysInfoRequest.isExecuted()) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ignore) {
                }
            }
            String response = sysInfoRequest.getResponse();
            if (response != null) {
                String[] split = response.split(",");
                if (split.length > 4) {
                    if (split[0].equals(ActCommands.SYSTEM_INFO) && split[split.length - 1].equals(ActCommands.Values.END.toString())) {
                        ver = split[2];
                        id = split[3];
                    }
                }
            }
            nameRequest = new ActNetRegularCommand(io, ActCommands.getName());
            while (!nameRequest.isExecuted()) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ignore) {
                }
            }
            response = nameRequest.getResponse();
            if (response != null) {
                String[] split = response.split(",");
                if (split.length > 2) {
                    if (split[0].equals(ActCommands.GET_NAME) && split[split.length - 1].equals(ActCommands.Values.END.toString())) {
                        name = split[1];
                    }
                }
            }

            p.populateInformation(name, ver, mac, id);
        } catch (UnsupportedEncodingException ignore) {
        }
    }

    public void update() {
        infoThread = new Thread(this);
        infoThread.start();
    }
}
