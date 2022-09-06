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

import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.TreeMap;

public abstract class Discover implements Runnable {
    protected final int timeout;
    protected TreeMap<String, String> discovered;
    protected Thread updateThread;

    protected final NetIO.DeviceType type;

    public Discover(int timeout, NetIO.DeviceType type) {
        this.timeout = timeout;
        this.type = type;
    }

    public void update() {
        if (updateThread != null) {
            if (updateThread.isAlive()) return;
        }
        updateThread = new Thread(this);
        updateThread.start();
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean isValid() {
        if (discovered == null) return false;
        if (updateThread == null) return false;
        return !updateThread.isAlive();
    }

    public boolean isRunning() {
        if (updateThread == null) return false;
        return updateThread.isAlive();
    }

    public TreeMap<String, String> getDiscovered() {
        return discovered;
    }

    public NetIO.DeviceType getType() {
        return type;
    }

    protected static List<InetAddress> listAllBroadcastAddresses() throws SocketException {
        List<InetAddress> broadcastList = new ArrayList<>();
        Enumeration<NetworkInterface> interfaces
                = NetworkInterface.getNetworkInterfaces();
        while (interfaces.hasMoreElements()) {
            NetworkInterface networkInterface = interfaces.nextElement();

            if (networkInterface.isLoopback() || !networkInterface.isUp()) {
                continue;
            }

            for (InterfaceAddress address : networkInterface.getInterfaceAddresses()) {
                if (address == null) continue;
                InetAddress broadcast = address.getBroadcast();
                if (broadcast != null) {
                    broadcastList.add(broadcast);
                }
            }
        }
        return broadcastList;
    }
}
