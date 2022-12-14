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
import de.sg_o.lib.photoNet.printer.act.ActDiscover;
import de.sg_o.lib.photoNet.printer.act.ActPrinter;
import de.sg_o.lib.photoNet.printer.cbd.CbdDiscover;
import de.sg_o.lib.photoNet.printer.cbd.CbdPrinter;

import java.util.*;

public class Environment {
    private final LinkedList<Discover> available = new LinkedList<>();
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
        available.add(new CbdDiscover(timeout));
        available.add(new ActDiscover(timeout));
        for (Discover d : available) {
            d.update();
        }
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
    public LinkedList<Discover> getDiscover() {
        return available;
    }

    @SuppressWarnings("unused")
    public TreeMap<String, Map.Entry<NetIO.DeviceType, String>> notConnected() {
        TreeMap<String, Map.Entry<NetIO.DeviceType, String>> missing = new TreeMap<>();
        for (Discover d : available) {
            if (!d.isValid()) continue;
            TreeMap<String, String> discovered = d.getDiscovered();
            for (Map.Entry<String, String> e : discovered.entrySet()) {
                missing.put(e.getKey(), new AbstractMap.SimpleImmutableEntry<>(d.getType(), e.getValue()));
            }
        }
        for (Printer p : connected) {
            missing.remove(p.getIp());
        }
        return missing;
    }

    @SuppressWarnings("unused")
    public Printer connect(String ip, NetIO.DeviceType type) {
        if (ip == null) return null;
        for (Discover d : available) {
            if (d.isValid()) {
                TreeMap<String, String> discovered = d.getDiscovered();
                for (Printer p : connected) {
                    if (p.getIp().equals(ip)) return p;
                }
            }
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
