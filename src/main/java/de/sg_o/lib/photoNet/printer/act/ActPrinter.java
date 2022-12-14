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

import de.sg_o.lib.photoNet.netData.act.ActStatus;
import de.sg_o.lib.photoNet.networkIO.act.ActCommands;
import de.sg_o.lib.photoNet.networkIO.act.ActNetIO;
import de.sg_o.lib.photoNet.networkIO.act.ActNetRegularCommand;
import de.sg_o.lib.photoNet.printer.Printer;

import java.io.UnsupportedEncodingException;
import java.net.SocketException;
import java.net.UnknownHostException;

public class ActPrinter extends Printer {

    public ActPrinter(String ip, int timeout) throws SocketException, UnknownHostException {
        super(ip);
        ActNetIO io = new ActNetIO(ip, 6000, timeout);
        ActStatus status = new ActStatus();
        this.io = io;
        this.status = status;
        info = new ActAsyncPrinterInformation(this, io);
        info.update();
        this.statUpdate = new ActAsyncStatusUpdate(5000, status, io);
        this.statUpdateThread = new Thread(statUpdate);
        this.statUpdateThread.start();
        this.rootFolder = new ActRootFolder(io);
    }

    public void setName(String name) {
        if (name == null) return;
        if (name.length() < 1) return;
        if (name.length() > 15) return;
        this.name = name;
        try {
            new ActNetRegularCommand(io, ActCommands.setName(name));
        } catch (UnsupportedEncodingException ignore) {
        }
    }

    @Override
    public void stop() {
        try {
            new ActNetRegularCommand(io, ActCommands.stop());
        } catch (UnsupportedEncodingException ignore) {
        }
    }

    @Override
    public void pause() {
        try {
            new ActNetRegularCommand(io, ActCommands.pause());
        } catch (UnsupportedEncodingException ignore) {
        }
    }

    @Override
    public void resume() {
        try {
            new ActNetRegularCommand(io, ActCommands.resume());
        } catch (UnsupportedEncodingException ignore) {
        }
    }

    @Override
    public void moveZ(float distance) {
        try {
            new ActNetRegularCommand(io, ActCommands.zMove(distance));
        } catch (UnsupportedEncodingException ignore) {
        }
    }

    @Override
    public void homeZ() {
        try {
            new ActNetRegularCommand(io, ActCommands.zHome());
        } catch (UnsupportedEncodingException ignore) {
        }
    }

    @Override
    public void stopMove() {
        try {
            new ActNetRegularCommand(io, ActCommands.zStop());
        } catch (UnsupportedEncodingException ignore) {
        }
    }
}
