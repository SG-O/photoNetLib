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
import de.sg_o.lib.photoNet.networkIO.act.ActCommands;
import de.sg_o.lib.photoNet.printer.Discover;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.TreeMap;

public class ActDiscover extends Discover {

    public ActDiscover(int timeout) {
        super(timeout, NetIO.DeviceType.ACT);
    }

    public void run() {
        try {
            discovered = new TreeMap<>();
            List<InetAddress> broadcasts = listAllBroadcastAddresses();
            for (InetAddress address : broadcasts) {
                DatagramSocket socket = new DatagramSocket();
                socket.setBroadcast(true);
                socket.setSoTimeout((timeout / 10) / broadcasts.size());
                String discover = ActCommands.BROADCAST_SEARCH_DATA;
                byte[] buffer = discover.getBytes(StandardCharsets.US_ASCII);
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, 48899);
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
                    if (response.getData().length < 20) continue;
                    String info = new String(response.getData());
                    if (info.length() < 20) continue;
                    String[] split = info.split(",");
                    if (split.length < 1) continue;
                    discovered.put(response.getAddress().getHostAddress(), split[0].trim());
                }
                socket.close();
            }
        } catch (IOException ignored) {
            discovered = null;
        }
    }
}
