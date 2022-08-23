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

package de.sg_o.lib.photoNet.networkIO.act;

import de.sg_o.lib.photoNet.networkIO.NetRequestResponse;
import de.sg_o.lib.photoNet.networkIO.NetWorker;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

public class ActNetWorker extends NetWorker {
    private final int timeout;
    private final byte[] buf = new byte[2048];
    private Socket clientSocket;
    private DataOutputStream out;
    private DataInputStream in;
    private boolean alive = true;
    private boolean isConnected = false;

    public ActNetWorker(InetAddress address, int port, int timeout) {
        super(address, port);
        this.timeout = timeout;
    }

    public void run() {
        while (alive) {
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
                if (!connect()) {
                    active.setError("Not Connected");
                    active.setResponse(null);
                    continue;
                }
                try {
                    send(active);
                } catch (IOException e) {
                    active.setError(e.getMessage());
                    active.setResponse(null);
                    if (!e.getMessage().equals("Read timed out")) {
                        isConnected = false;
                    }
                }
                synchronized (this) {
                    done.add(active);
                }
            }
        }
    }

    private boolean connect() {
        if (isConnected) return true;
        try {
            clientSocket = new Socket();
            InetSocketAddress inetSocketAddress = new InetSocketAddress(address, port);
            clientSocket.connect(inetSocketAddress, timeout);
            clientSocket.setSoTimeout(timeout);
            clientSocket.sendUrgentData(255);
            in = new DataInputStream(clientSocket.getInputStream());
            out = new DataOutputStream(clientSocket.getOutputStream());
            if (clientSocket.isConnected()) {
                out.write("111\n".getBytes("GBK"));
                out.flush();
                out.write("getmode,".getBytes("GBK"));
                out.flush();
                byte[] tmp = new byte[16];
                while (in.read(tmp) > 0) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException ignore) {
                    }
                }
                isConnected = true;
                return true;
            }
        } catch (IOException e) {
            if (e.getMessage().equals("Read timed out")) {
                isConnected = true;
                return true;
            }

        }
        return false;
    }

    private void send(NetRequestResponse request) throws IOException {
        int expectedLength = request.getExpectedLength();
        out.write(request.getRequest());
        out.flush();
        long start = System.currentTimeMillis();
        int glide = 0;
        byte[] multiPacket = null;
        int multiPacketOffset = 0;
        if (expectedLength > 0) {
            multiPacket = new byte[expectedLength];
        }
        int fail = 0;
        while (true) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException ignore) {
            }
            if ((System.currentTimeMillis() > (start + timeout)) && timeout > 0)
                throw new IOException("Read timed out");
            int read = in.read(buf, glide, buf.length - glide);
            if (read < 1) {
                if (fail > 50) {
                    throw new IOException("Read timed out");
                }
                fail++;
                continue;
            }
            fail = 0;
            glide += read;
            if (expectedLength < 1) {
                String result = new String(buf, 0, glide, "GBK");
                if (result.contains(",end")) {
                    byte[] out = new byte[glide];
                    System.arraycopy(buf, 0, out, 0, glide);
                    request.setResponse(out);
                    return;
                }
            } else {
                start = System.currentTimeMillis();
                System.arraycopy(buf, 0, multiPacket, multiPacketOffset, Math.min(glide, multiPacket.length - multiPacketOffset));
                multiPacketOffset += glide;
                glide = 0;
                if (multiPacketOffset >= expectedLength) {
                    request.setResponse(multiPacket);
                    return;
                }
            }
        }
    }

    public void stop() {
        alive = false;
        try {
            in.close();
            out.close();
            clientSocket.close();
        } catch (IOException ignore) {
        }
    }
}
