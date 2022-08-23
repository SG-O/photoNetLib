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

package de.sg_o.lib.photoNet.manager;

import de.sg_o.lib.photoNet.networkIO.NetIO;
import de.sg_o.lib.photoNet.printer.Discover;
import de.sg_o.lib.photoNet.printer.Printer;
import de.sg_o.lib.photoNet.printer.act.ActPrinter;
import de.sg_o.lib.photoNet.printer.cbd.CbdDiscover;
import de.sg_o.lib.photoNet.printer.cbd.CbdPrinter;

import java.util.ArrayList;
import java.util.TreeMap;

public class Environment {
    private final Discover available;
    private final ArrayList<Printer> connected;
    private final int timeout;

    public Environment(String connected, int timeout) {
        this.connected = new ArrayList<>();
        this.timeout = timeout;
        if (connected != null) {
            String[] printers = connected.split(";");
            if (printers.length > 0) {
                for (String p : printers) {
                    NetIO.DeviceType type = NetIO.DeviceType.CBD;
                    if (p.contains("=")) {
                        String[] split = p.split("=");
                        if (split.length == 2) {
                            try {
                                int t = Integer.parseInt(split[0]);
                                type = NetIO.DeviceType.getFromID(t);
                                p = split[1];
                            } catch (NumberFormatException ignore) {
                            }
                        }
                    }
                    if (p.length() < 1) continue;
                    addPrinter(p, timeout, type);
                }
            }
        }
        available = new CbdDiscover(timeout);
        available.update();
    }

    private Printer addPrinter(String ip, int timeout, NetIO.DeviceType type) {
        try {
            Printer pr;
            switch (type) {
                case CBD:
                    pr = new CbdPrinter(ip, timeout);
                    break;
                case ACT:
                    pr = new ActPrinter(ip, timeout);
                    break;
                default:
                    return null;
            }
            this.connected.add(pr);
            return pr;
        } catch (Exception ignored) {
        }
        return null;
    }

    @SuppressWarnings("unused")
    public Discover getDiscover() {
        return available;
    }

    @SuppressWarnings("unused")
    public TreeMap<String, String> notConnected() {
        TreeMap<String, String> missing = new TreeMap<>();
        if (!available.isValid()) return missing;
        TreeMap<String, String> discovered = available.getDiscovered();
        missing.putAll(discovered);
        for (Printer p : connected) {
            missing.remove(p.getIp());
        }
        return missing;
    }

    @SuppressWarnings("unused")
    public Printer connect(String ip, NetIO.DeviceType type) {
        if (ip == null) return null;
        if (!available.isValid()) return null;
        TreeMap<String, String> discovered = available.getDiscovered();
        for (Printer p : connected) {
            if (p.getIp().equals(ip)) return p;
        }
        return addPrinter(ip, timeout, type);
    }

    public ArrayList<Printer> getConnected() {
        return connected;
    }

    @SuppressWarnings("unused")
    public void disconnect(String ip) {
        if (ip == null) return;
        Printer toDelete = null;
        for (Printer p : connected) {
            if (p.getIp().equals(ip)) {
                toDelete = p;
                break;
            }
        }
        if (toDelete != null) {
            toDelete.disconnect();
            connected.remove(toDelete);
        }
    }

    @SuppressWarnings("unused")
    public String save() {
        StringBuilder builder = new StringBuilder();
        boolean first = true;
        for (Printer p : connected) {
            if (first) {
                first = false;
            } else {
                builder.append(";");
            }
            builder.append(p.getDeviceType().getID());
            builder.append("=");
            builder.append(p.getIp());
        }
        return builder.toString();
    }

}
