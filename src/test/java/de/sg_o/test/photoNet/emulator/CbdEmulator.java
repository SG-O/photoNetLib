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

import de.sg_o.lib.photoNet.netData.cbd.CbdDataTransferBlock;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;

public class CbdEmulator implements Runnable {
    private final DatagramSocket serverSocket;
    private final byte[] buf = new byte[2048];
    private boolean running;
    private String printerName = "SomeName";
    private String selectedFile = "";
    private long fileSize = 0;
    private long time = 0;
    private boolean paused = false;

    public CbdEmulator(int port, int timeout) throws IOException {
        serverSocket = new DatagramSocket(port);
        serverSocket.setSoTimeout(timeout);
    }

    public static void main(String[] args) {
        try {
            CbdEmulator emulator = new CbdEmulator(3000, 10000);
            Thread runner = new Thread(emulator);
            runner.start();
            while (runner.isAlive()) {
                //noinspection BusyWait
                Thread.sleep(100);
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void close() {
        running = false;
    }

    @Override
    public void run() {
        running = true;

        while (running) {
            try {
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                serverSocket.receive(packet);
                if (packet.getLength() < 1) continue;
                byte[] pack = new byte[packet.getLength()];
                System.arraycopy(buf, 0, pack, 0, pack.length);
                parsePacket(pack, packet.getAddress(), packet.getPort());
            } catch (IOException ignore) {
            }
        }
        serverSocket.close();
    }

    private void parsePacket(byte[] packet, InetAddress address, int port) throws IOException {
        String received = new String(packet, 0, packet.length);
        System.out.println("Received: " + received);
        if (received.length() < 1) return;
        if (received.startsWith("M99999")) {
            sendResponse(true, "MAC:00:e0:4c:00:00:00 IP:" +
                    "127.0.0.1" +
                    " VER:V1.4.1 ID:00,00,00,00,00,00,00,01 NAME:" +
                    printerName, address, port);
        }
        if (received.startsWith("M4000")) {
            sendResponse(true, "B:184/0 E1:185/0 E2:192/0 X:0.000 Y:0.000 Z:0.000 F:" +
                    (((time > 0) ? "255" : "0")) + "/256 D:0/" +
                    fileSize + "/" + (paused ? "1" : "0") +
                    " T:" + time, address, port);
        }
        if (received.startsWith("M4006")) {
            sendResponse(true, "'" + selectedFile + "'", address, port);
        }
        if (received.startsWith("M20")) {
            sendResponse(false, "Begin file list", address, port);
            sendResponse(false, "testFile.photon 15076415", address, port);
            sendResponse(false, "End file list", address, port);
            sendResponse(true, "L:1", address, port);
        }
        if (received.startsWith("M6030")) {
            String[] split = received.split("'");
            if (split.length > 1) {
                this.selectedFile = split[1].trim();
                if (this.selectedFile.startsWith(":")) this.selectedFile = this.selectedFile.substring(1);
                File file = getFile(this.selectedFile);
                if (file != null) {
                    this.paused = false;
                    this.time = 20;
                    this.fileSize = file.length();
                    sendResponse(true, "", address, port);
                } else {
                    sendResponse(false, "Error:Can't start print", address, port);
                }
            } else {
                sendResponse(false, "Error:Can't start print", address, port);
            }
        }
        if (received.startsWith("M33")) {
            this.time = 0;
            this.fileSize = 0;
            this.selectedFile = "";
            sendResponse(true, "", address, port);
        }

        if (received.startsWith("M25")) {
            this.paused = true;
            sendResponse(true, "", address, port);
        }

        if (received.startsWith("M24")) {
            if (paused) {
                this.paused = false;
                sendResponse(true, "", address, port);
            } else if (!this.selectedFile.equals("")) {
                File file = getFile(this.selectedFile);
                if (file != null) {
                    this.paused = false;
                    this.time = 20;
                    this.fileSize = file.length();
                    sendResponse(true, "", address, port);
                } else {
                    sendResponse(false, "Error:Can't start print", address, port);
                }
            } else {
                sendResponse(false, "Error:Can't start print", address, port);
            }
        }

        if (received.startsWith("U100")) {
            String[] split = received.split("'");
            if (split.length > 1) {
                this.printerName = split[1];
                sendResponse(true, "", address, port);
            } else {
                sendResponse(false, "Error:", address, port);
            }
        }

        if (received.startsWith("M6032")) {
            String[] split = received.split("'");
            if (split.length > 1) {
                this.selectedFile = split[1].trim();
                if (this.selectedFile.startsWith(":")) this.selectedFile = this.selectedFile.substring(1);
                File file = getFile(this.selectedFile);
                if (file != null) {
                    this.fileSize = file.length();
                    sendResponse(true, "L:" + this.fileSize, address, port);
                } else {
                    sendResponse(false, "Error:Can't open file", address, port);
                }
            } else {
                sendResponse(false, "Error:Can't open file", address, port);
            }
        }

        if (received.startsWith("M3001")) {
            String[] split = received.split("I");
            if (this.selectedFile.equals("")) {
                sendResponse(false, "Error:Can't open file", address, port);
            } else if (split.length > 1) {
                File file = getFile(this.selectedFile);
                if (file != null) {
                    this.fileSize = file.length();
                    FileInputStream fis = new FileInputStream(file);
                    long offset = Long.parseLong(split[1]);
                    if (fis.skip(offset) != offset) {
                        sendResponse(false, "Error:Can't open file", address, port);
                    } else {
                        byte[] data = new byte[1280];
                        int read = fis.read(data);
                        if (read != data.length) {
                            byte[] tmp = new byte[read];
                            System.arraycopy(data, 0, tmp, 0, read);
                            data = tmp;
                        }
                        fis.close();
                        CbdDataTransferBlock transferBlock = new CbdDataTransferBlock(data, offset);
                        byte[] generated = transferBlock.generate();
                        DatagramPacket resp = new DatagramPacket(generated, generated.length, address, port);
                        serverSocket.send(resp);
                    }
                } else {
                    sendResponse(false, "Error:Can't open file", address, port);
                }
            }
        }

        if (received.startsWith("M22")) {
            this.selectedFile = "";
            sendResponse(true, "", address, port);

        }
    }

    private File getFile(String name) {
        URL testFileURL = this.getClass().getResource("/" + name);
        if (testFileURL == null) return null;
        try {
            return new File(testFileURL.toURI());
        } catch (URISyntaxException e) {
            return null;
        }
    }

    private void sendResponse(boolean ok, String text, InetAddress address, int port) throws IOException {
        String out = ((ok ? "ok " : "") + text + "\r\n");
        System.out.print("Sending: " + out);
        byte[] response = out.getBytes(StandardCharsets.US_ASCII);
        DatagramPacket packet = new DatagramPacket(response, response.length, address, port);
        serverSocket.send(packet);
    }
}
