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

public abstract class Printer {
    protected NetIO io;
    protected Status status;
    protected AsyncStatusUpdate statUpdate;
    protected AsyncPrinterInformation info;
    protected Thread statUpdateThread;
    private final String ip;
    protected Folder rootFolder;
    protected String name;
    private String ver;
    private String mac;
    private String id;
    private boolean infoValid = false;

    public Printer(String ip) {
        this.ip = ip;
    }

    public void populateInformation(String name, String ver, String mac, String id) {
        this.name = name;
        this.ver = ver;
        this.mac = mac;
        this.id = id;
        infoValid = name != null && ver != null && mac != null && id != null;
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
    public abstract void setName(String name);

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

    public NetIO getNetIo() {
        return io;
    }

    public NetIO.DeviceType getDeviceType() {
        return io.getDeviceType();
    }

    @SuppressWarnings("unused")
    public abstract void stop();

    @SuppressWarnings("unused")
    public abstract void pause();

    @SuppressWarnings("unused")
    public abstract void resume();

    @SuppressWarnings("unused")
    public abstract void moveZ(float distance);

    @SuppressWarnings("unused")
    public abstract void homeZ();

    @SuppressWarnings("unused")
    public abstract void stopMove();


    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean isInfoValid() {
        return infoValid;
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

    public void update() {
        if (info == null) return;
        infoValid = false;
        info.update();
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
