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

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Discover implements Runnable {
    private static final Pattern pattern = Pattern.compile("MAC:(?<mac>[0-9a-fA-F]+:[0-9a-fA-F]+:[0-9a-fA-F]+:[0-9a-fA-F]+:[0-9a-fA-F]+:[0-9a-fA-F]+) IP:(?<ip>\\d+.\\d+.\\d+.\\d+) VER:(?<ver>V[0-9.]+) ID:(?<id>[0-9a-fA-F,]+) NAME:(?<name>\\S+)");
    private final int timeout;
    private TreeMap<String, String> discovered;
    private Thread updateThread;


    public Discover(int timeout) {
        this.timeout = timeout;
    }

    private static List<InetAddress> listAllBroadcastAddresses() throws SocketException {
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

    public void run() {
        try {
            discovered = new TreeMap<>();
            List<InetAddress> broadcasts = listAllBroadcastAddresses();
            for (InetAddress address : broadcasts) {
                DatagramSocket socket = new DatagramSocket();
                socket.setBroadcast(true);
                socket.setSoTimeout((timeout / 10) / broadcasts.size());
                String discover = "M99999";
                byte[] buffer = discover.getBytes();
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, 3000);
                try {
                    socket.send(packet);
                } catch (IOException e) {
                    continue;
                }
                long start = System.currentTimeMillis();
                byte[] buf = new byte[1024];
                while (start + (timeout / broadcasts.size()) > System.currentTimeMillis()) {
                    DatagramPacket response = new DatagramPacket(buf, 0, buf.length);
                    try {
                        socket.receive(response);
                    } catch (IOException e) {
                        continue;
                    }
                    if (response.getData() == null) continue;
                    if (response.getData().length < 64) continue;
                    String info = new String(response.getData());
                    if (info.length() < 64) continue;
                    Matcher m = pattern.matcher(info);
                    while (m.find()) {
                        if (m.groupCount() == 5) {
                            discovered.put(response.getAddress().getHostAddress(), m.group("name"));
                        }
                    }
                }
                socket.close();
            }
        } catch (IOException ignored) {
            discovered = null;
        }
    }

    public TreeMap<String, String> getDiscovered() {
        return discovered;
    }
}
