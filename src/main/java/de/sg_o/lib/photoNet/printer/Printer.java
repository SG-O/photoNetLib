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

public abstract class Printer {
    protected NetIO io;
    protected Status status;
    protected AsyncStatusUpdate statUpdate;
    protected Thread statUpdateThread;
    private final String ip;
    protected Folder rootFolder;
    private String name;
    private String ver;
    private String mac;
    private String id;

    public Printer(String ip) {
        this.ip = ip;
    }

    public void populateInformation(String name, String ver, String mac, String id) {
        this.name = name;
        this.ver = ver;
        this.mac = mac;
        this.id = id;
    }

    public Status getStatus() {
        return status;
    }

    @SuppressWarnings("unused")
    public Folder getRootFolder() {
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
        NetRegularCommand rename = new NetRegularCommand(io, "U100 '" + name + "'");
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

    public NetIO.DeviceType getDeviceType() {
        return io.getDeviceType();
    }

    public void disconnect() {
        if (statUpdate == null) return;
        statUpdate.stop();
        if (statUpdateThread == null) return;
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
