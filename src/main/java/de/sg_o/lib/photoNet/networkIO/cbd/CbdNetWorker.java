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

package de.sg_o.lib.photoNet.networkIO.cbd;

import de.sg_o.lib.photoNet.networkIO.NetRequestResponse;
import de.sg_o.lib.photoNet.networkIO.NetWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;

public class CbdNetWorker extends NetWorker {

    private static final Logger LOGGER = LoggerFactory.getLogger(CbdNetWorker.class);
    private final DatagramSocket socket;

    private final byte[] buf = new byte[2048];

    public CbdNetWorker(InetAddress address, int port, int timeout) throws SocketException {
        super(address, port);
        socket = new DatagramSocket();
        socket.setSoTimeout(timeout);
    }

    public void run() {
        while (!socket.isClosed()) {
            int jobs;
            synchronized (this) {
                jobs = toDo.size();
            }
            if (jobs < 1) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ignored) {
                }
            } else {
                NetRequestResponse active;
                synchronized (this) {
                    active = toDo.poll();
                }
                if (active == null) continue;
                try {
                    send(active);
                } catch (IOException e) {
                    LOGGER.info(e.getMessage());
                    active.setError(e.getMessage());
                    active.setResponse(null);
                }
                synchronized (this) {
                    done.add(active);
                }
            }
        }
    }

    private void send(NetRequestResponse request) throws IOException {
        if (request.getRequest().length < 64) {
            LOGGER.debug("Request: " + new String(request.getRequest(), StandardCharsets.US_ASCII));
        }
        DatagramPacket packet = new DatagramPacket(request.getRequest(), request.getRequest().length, address, port);
        DatagramPacket response = new DatagramPacket(buf, 0, buf.length);
        socket.send(packet);
        byte[] multiPacket = null;
        while (true) {
            socket.receive(response);
            if ((buf[0] == 0x6F) && (buf[1] == 0x6B)) {
                byte[] data;
                if (multiPacket == null) {
                    data = new byte[response.getLength() - 2];
                    System.arraycopy(buf, 2, data, 0, data.length);
                    LOGGER.debug("Single Packet Response: " + new String(data, StandardCharsets.US_ASCII));
                } else {
                    data = new byte[multiPacket.length + (response.getLength() - 2)];
                    System.arraycopy(multiPacket, 0, data, 0, multiPacket.length);
                    System.arraycopy(buf, 2, data, multiPacket.length, response.getLength() - 2);
                    if (data.length < 128) {
                        LOGGER.debug("Multi Packet Response: " + new String(data, StandardCharsets.US_ASCII));
                    }
                }
                request.setResponse(data);
                return;
            }
            if ((buf[0] == 0x45) && (buf[1] == 0x72) && (buf[2] == 0x72) && (buf[3] == 0x6F) && (buf[4] == 0x72) && ((buf[5] == 0x3A) || (buf[5] == 0x2C))) {
                byte[] data = new byte[response.getLength() - 6];
                LOGGER.debug("Error Response: " + new String(data, StandardCharsets.US_ASCII));
                System.arraycopy(buf, 6, data, 0, data.length);
                request.setError(new String(data));
                request.setResponse(null);
                return;
            }
            if (response.getLength() != 17 && multiPacket == null) {
                if (!new String(buf).trim().startsWith("Begin file list")) {
                    LOGGER.debug("File List Response");
                    byte[] data = new byte[response.getLength()];
                    System.arraycopy(buf, 0, data, 0, data.length);
                    request.setResponse(data);
                    return;
                }
            }
            int destPos = 0;
            if (multiPacket == null) {
                LOGGER.debug("Multi Packet Start");
                multiPacket = new byte[response.getLength()];
            } else {
                LOGGER.debug("Multi Packet Add");
                byte[] tmp = multiPacket;
                destPos = tmp.length;
                multiPacket = new byte[tmp.length + response.getLength()];
                System.arraycopy(tmp, 0, multiPacket, 0, tmp.length);
            }
            System.arraycopy(buf, 0, multiPacket, destPos, response.getLength());
        }
    }

    public void stop() {
        socket.close();
    }
}
