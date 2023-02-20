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

package de.sg_o.test.photoNet.emulator;

import de.sg_o.lib.photoNet.networkIO.act.ActCommands;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.concurrent.atomic.AtomicBoolean;

public class ActEmulatorDiscovery implements Runnable {

    private final DatagramSocket serverSocket;
    private final byte[] buffer = new byte[256];

    private final AtomicBoolean running = new AtomicBoolean(false);

    public ActEmulatorDiscovery(int port, int timeout) throws IOException {
        serverSocket = new DatagramSocket(port);
        serverSocket.setSoTimeout(timeout);
    }

    private DatagramPacket receive() throws IOException {
        DatagramPacket request = new DatagramPacket(buffer, buffer.length);
        serverSocket.receive(request);
        byte[] input = request.getData();
        if (input != null) {
            System.out.println(new String(input).trim());
        }
        return request;
    }

    @SuppressWarnings("SameParameterValue")
    private void sendResponse(InetAddress address, int port, String data) throws IOException {
        System.out.println(data);
        byte[] resp = data.getBytes();
        DatagramPacket response = new DatagramPacket(resp, resp.length, address, port);
        serverSocket.send(response);
    }

    @Override
    public void run() {
        running.set(true);
        while (running.get()) {
            try {
                DatagramPacket received = receive();
                byte[] input = received.getData();
                if (input == null) continue;
                if (input.length < 1) continue;
                String data = new String(input);
                if (data.length() < 1) continue;
                data = data.trim();
                if (data.equals(ActCommands.BROADCAST_SEARCH_DATA)) {
                    sendResponse(received.getAddress(), received.getPort(), "192.10.10.10, 020000000000, USR - C322, 01.01.10");
                }
            } catch (IOException ignore) {
            }
        }
        serverSocket.close();
    }

    @SuppressWarnings("unused")
    public boolean isRunning() {
        return running.get();
    }

    public void stop() {
        running.set(false);
    }
}
