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

import de.sg_o.lib.photoNet.netData.Status;
import de.sg_o.lib.photoNet.networkIO.NetIO;
import de.sg_o.lib.photoNet.networkIO.NetRegularCommand;

import java.io.UnsupportedEncodingException;
import java.net.SocketException;
import java.net.UnknownHostException;

public class Printer {
    private final NetIO io;
    private final Status status = new Status();
    private final AsyncStatusUpdate statUpdate;
    private final Thread statUpdateThread;
    private final String ip;
    private final RootFolder rootFolder;
    private String name;
    private String ver;
    private String mac;
    private String id;

    public Printer(String ip) throws SocketException, UnknownHostException, UnsupportedEncodingException {
        this.ip = ip;
        io = new NetIO(ip, 3000);
        AsyncPrinterInformation info = new AsyncPrinterInformation(this, io);
        Thread infoThread = new Thread(info);
        infoThread.start();
        statUpdate = new AsyncStatusUpdate(5000, status, io);
        statUpdateThread = new Thread(statUpdate);
        statUpdateThread.start();
        rootFolder = new RootFolder(io);
    }

    void populateInformation(String name, String ver, String mac, String id) {
        this.name = name;
        this.ver = ver;
        this.mac = mac;
        this.id = id;
    }

    public Status getStatus() {
        return status;
    }

    @SuppressWarnings("unused")
    public RootFolder getRootFolder() {
        return rootFolder;
    }

    @SuppressWarnings("unused")
    public void setStatusUpdateInterval(int interval) {
        statUpdate.setUpdateInterval(interval);
    }

    @SuppressWarnings("unused")
    public String getName() {
        return name;
    }

    @SuppressWarnings("unused")
    public void setName(String name) {
        if (name == null) return;
        if (name.length() < 1) return;
        if (name.length() > 15) return;
        this.name = name;
        try {
            NetRegularCommand rename = new NetRegularCommand(io, "U100 '" + name + "'");
        } catch (UnsupportedEncodingException ignore) {
        }
    }

    public String getIp() {
        return ip;
    }

    @SuppressWarnings("unused")
    public String getVer() {
        return ver;
    }

    @SuppressWarnings("unused")
    public String getMac() {
        return mac;
    }

    @SuppressWarnings("unused")
    public String getId() {
        return id;
    }

    public void disconnect() {
        statUpdate.stop();
        while (statUpdateThread.isAlive()) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException ignored) {
            }
        }
        io.stop();
    }

    @Override
    public String toString() {
        return "Printer{" +
                "name='" + name + '\'' +
                ", ip='" + ip + '\'' +
                ", ver='" + ver + '\'' +
                ", mac='" + mac + '\'' +
                ", id='" + id + '\'' +
                '}';
    }
}
