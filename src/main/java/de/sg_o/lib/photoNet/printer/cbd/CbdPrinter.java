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

import de.sg_o.lib.photoNet.netData.cbd.CbdStatus;
import de.sg_o.lib.photoNet.networkIO.cbd.CbdCommands;
import de.sg_o.lib.photoNet.networkIO.cbd.CbdNetIO;
import de.sg_o.lib.photoNet.networkIO.cbd.CbdNetRegularCommand;
import de.sg_o.lib.photoNet.printer.Printer;

import java.io.UnsupportedEncodingException;
import java.net.SocketException;
import java.net.UnknownHostException;

public class CbdPrinter extends Printer {
    public CbdPrinter(String ip, int timeout) throws SocketException, UnsupportedEncodingException, UnknownHostException {
        super(ip);
        CbdNetIO io = new CbdNetIO(ip, 3000, timeout);
        CbdStatus status = new CbdStatus();
        this.io = io;
        this.status = status;
        info = new CbdAsyncPrinterInformation(this, io);
        info.update();
        this.statUpdate = new CbdAsyncStatusUpdate(5000, status, io);
        this.statUpdateThread = new Thread(statUpdate);
        this.statUpdateThread.start();
        this.rootFolder = new CbdRootFolder(io);
    }

    public void setName(String name) {
        if (name == null) return;
        if (name.length() < 1) return;
        if (name.length() > 15) return;
        this.name = name;
        new CbdNetRegularCommand(io, CbdCommands.setName(name));
    }

    @Override
    public void stop() {
        new CbdNetRegularCommand(io, CbdCommands.stop());
    }

    @Override
    public void pause() {
        new CbdNetRegularCommand(io, CbdCommands.pause());
    }

    @Override
    public void resume() {
        new CbdNetRegularCommand(io, CbdCommands.resume());
    }

    @Override
    public void moveZ(float distance) {
        new CbdNetRegularCommand(io, CbdCommands.zRelative());
        new CbdNetRegularCommand(io, CbdCommands.zMove(distance, 600.0f));
    }

    @Override
    public void homeZ() {
        new CbdNetRegularCommand(io, CbdCommands.zHome());
    }

    @Override
    public void stopMove() {
        new CbdNetRegularCommand(io, CbdCommands.zStop());
    }
}
