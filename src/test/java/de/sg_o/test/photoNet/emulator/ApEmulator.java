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

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ApEmulator implements Runnable {
    private static final int PREVIEW_WIDTH = 224;
    private static final int PREVIEW_HEIGHT = 168;

    private final ServerSocket serverSocket;

    String printerName = "SomeName";
    private final String[] fileList = {"Model1.pwmb", "Model2.pwmb", "JustSomeModel.photon"};
    Socket clientSocket;
    DataOutputStream out;
    DataInputStream in;
    //private final String[] fileList = {};
    private String printingFile = null;
    private boolean paused = false;

    public ApEmulator(int port, int timeout) throws IOException {
        serverSocket = new ServerSocket(port);
        serverSocket.setSoTimeout(timeout);
    }

    public static void main(String[] args) {
        try {
            ApEmulator emulator = new ApEmulator(6000, 10000);
            Thread runner = new Thread(emulator);
            runner.start();
            ApEmulatorDiscovery discovery = new ApEmulatorDiscovery(48899, 10000);
            Thread disco = new Thread(discovery);
            disco.start();
            while (runner.isAlive()) {
                //noinspection BusyWait
                Thread.sleep(100);
            }
            discovery.stop();
            while (disco.isAlive()) {
                //noinspection BusyWait
                Thread.sleep(100);
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean acceptClient() {
        if (in != null || out != null) return false;
        try {
            clientSocket = serverSocket.accept();
            out = new DataOutputStream(clientSocket.getOutputStream());
            in = new DataInputStream(clientSocket.getInputStream());
            return true;
        } catch (IOException ignore) {
            return false;
        }
    }

    public void close() throws IOException {
        if (clientSocket != null) {
            clientSocket.close();
        }
        in = null;
        out = null;
    }

    public byte[] receive() throws IOException {
        if (in == null || out == null) return null;

        byte[] buf = new byte[4096];
        int read = in.read(buf);
        if (read > 0) {
            byte[] ret = new byte[read];
            System.arraycopy(buf, 0, ret, 0, ret.length);
            return ret;
        }
        if (read < 0) throw new IOException("Closed");
        return null;
    }

    public boolean isConnected() {
        return !clientSocket.isClosed();
    }

    public String[] extractData(String[] raw, int offset, int length) {
        String[] tmp = new String[length];
        System.arraycopy(raw, offset, tmp, 0, length);
        return tmp;
    }

    private void sendData(String data) {
        System.out.println("Sending: " + data);
        try {
            out.write(data.getBytes("GBK"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendData(byte[] data) {
        if (data == null) return;
        System.out.println("Sending binary");
        try {
            int i = 0;
            while (i < data.length) {
                int maxBuffer = 256;
                if (maxBuffer > data.length - i) {
                    maxBuffer = data.length - i;
                }
                byte[] buffer = new byte[maxBuffer];
                System.arraycopy(data, i, buffer, 0, maxBuffer);
                out.write(buffer);
                i += maxBuffer;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String generateFileList() {
        StringBuilder out = new StringBuilder();
        if (fileList == null) return "ERROR1,"; //No Usb Stick
        if (fileList.length < 1) return "ERROR2,"; //NoFiles
        for (int i = 0; i < fileList.length; i++) {
            String file = generateFileWithAlias(fileList[i], i);
            if (file == null) continue;
            out.append(file);
            out.append(",");
        }
        return out.toString();
    }

    private String generateHistory() {
        //,40/Orbiter v2.0-Mount-Duet_MFM_w_Dumbboard/4.pwms/9204986/213/1269/13/45.0/3.0,41/Orbiter v2.0-Mount-Duet_MFM_w_Dumbboard/4.pwms/9204986/194/1269/13/45.0/2.19,42/v1.2_Orbiter v2.0-Mount-Duet_MFM_w_Dumb/17.pwms/9346725/194/1269/13/45.0/2.19,end
        StringBuilder out = new StringBuilder();
        if (fileList == null) return "ERROR1,"; //No Usb Stick
        if (fileList.length < 1) return "ERROR2,"; //NoFiles
        for (int i = 0; i < fileList.length; i++) {
            String file = generateFileWithAlias(fileList[i], i);
            if (file == null) continue;
            out.append(i);
            out.append("/");
            out.append(file);
            out.append("/");
            out.append(9204986); // File Size bytes
            out.append("/");
            out.append(213); //Time min
            out.append("/");
            out.append(1269); //Layers
            out.append("/");
            out.append(13); //Resin ml
            out.append("/");
            out.append(45.0f); //Bottom Exposure
            out.append("/");
            out.append(3.0f); //Layer Exposure
            out.append(",");
        }
        return out.toString();
    }

    private String generateFileWithAlias(String file, int number) {
        StringBuilder out = new StringBuilder();
        String[] split = file.split("\\.");
        if (split.length < 2) return null;
        out.append(file);
        out.append("/");
        out.append(number).append(".").append(split[1]);
        return out.toString();
    }

    private String getFileFromAlias(String alias) {
        int i = getIndexFromAlias(alias);
        if (i < 0 || i >= fileList.length) return null;
        return generateFileWithAlias(fileList[i], i);
    }

    private int getIndexFromAlias(String alias) {
        String[] split = alias.split("\\.");
        if (split.length < 2) return -1;
        try {
            return Integer.parseInt(split[0]);
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException ignore) {
            return -1;
        }
    }

    private byte[] generatePreview(String alias) {
        byte[] buffer = new byte[(PREVIEW_WIDTH * PREVIEW_HEIGHT * 2)];
        BufferedImage image = new BufferedImage(PREVIEW_WIDTH, PREVIEW_HEIGHT, BufferedImage.TYPE_USHORT_565_RGB);
        Graphics2D graphics = image.createGraphics();
        graphics.setBackground(Color.DARK_GRAY);
        graphics.setPaint(Color.CYAN);
        graphics.setFont(new Font(Font.SERIF, Font.BOLD, 16));
        int index = getIndexFromAlias(alias);
        if (index >= 0 && index < fileList.length) {
            graphics.drawString(fileList[index], 10, 80);
        }
        graphics.dispose();
        DataBuffer dataBuffer = image.getData().getDataBuffer();
        for (int i = 0; i < (PREVIEW_HEIGHT * PREVIEW_WIDTH * 2); i += 2) {
            buffer[i] = (byte) (dataBuffer.getElem(i / 2) >> 8 & 0xFF);
            buffer[i + 1] = (byte) (dataBuffer.getElem(i / 2) & 0xFF);
        }
        return buffer;
    }

    public int commandNeedsData(String command) {
        if (command.equals("goprint")) {
            return 1;
        }
        if (command.equals("delfile")) {
            return 2;
        }
        if (command.equals("getPreview2")) {
            return 1;
        }
        if (command.equals("getPreview1")) {
            return 1;
        }
        if (command.equals("setname")) {
            return 1;
        }
        return 0;
    }

    public void parseCommand(String command, String[] parameter) throws IOException {
        if (command == null || parameter == null) return;
        if (command.equals("sysinfo")) {
            sendData("sysinfo,Photon Mono X,v0.1,0000000000000001,SomeSSID,end");
        }
        if (command.equals("getmode")) {
            sendData("getmode,0,end");
        }
        if (command.equals("getstatus")) {
            if (printingFile == null) {
                sendData("getstatus,stop,end");
            } else {
                if (paused) {
                    sendData("getstatus,pause," +
                            getFileFromAlias(printingFile) +
                            ",2338,88,2062,51744,6844,~178mL,UV,39.38,0.05,0,end");
                } else {
                    sendData("getstatus,print," +
                            getFileFromAlias(printingFile) +
                            ",2338,88,2062,51744,6844,~178mL,UV,39.38,0.05,0,end");
                }
            }
        }
        if (command.equals("getfile")) {
            sendData("getfile," + generateFileList() + "end");
        }
        if (command.equals("goprint")) {
            if (parameter.length < 1 || printingFile != null) {
                sendData("goprint,ERROR1,end");
            } else {
                printingFile = parameter[0];
                sendData("goprint,OK,end");
            }
        }
        if (command.equals("delfile")) {
            sendData("delfile,OK,end");
        }
        if (command.equals("getPreview2")) {
            if (parameter.length < 1) {
                sendData("getPreview2,ERROR1,end");
            } else {
                sendData(generatePreview(parameter[0]));
            }
        }
        if (command.equals("getPreview1")) {
            sendData("getPreview1,OK,end");
        }
        if (command.equals("gethistory")) {
            sendData("gethistory," + generateHistory() + "end");
        }
        if (command.equals("delhistory")) {
            sendData("delhistory,OK,end");
        }
        if (command.equals("getpara")) {
            sendData("getpara,OK,end"); //Unknown at the moment
        }
        if (command.equals("getwifi")) {
            sendData("getwifi,OK,end"); //Unknown at the moment
        }
        if (command.equals("gopause")) {
            if (printingFile != null) {
                paused = true;
            }
            sendData("gopause,OK,end"); //Unknown at the moment
        }
        if (command.equals("gostop")) {
            printingFile = null;
            sendData("gostop,OK,end"); //Unknown at the moment
        }
        if (command.equals("goresume")) {
            if (printingFile != null) {
                paused = false;
            }
            sendData("goresume,OK,end"); //Unknown at the moment
        }
        if (command.equals("setname")) {
            if (parameter.length < 1) {
                sendData("setname,ERROR1,end");
            } else {
                printerName = parameter[0].trim();
                sendData("setname,OK,end");
            }
        }
        if (command.equals("getname")) {
            sendData("getname," + printerName + ",end"); //Unknown at the moment
        }
        if (command.equals("setpara")) {
            sendData("setpara,OK,end"); //Unknown at the moment
        }
        if (command.equals("setwifi")) {
            sendData("setwifi,OK,end"); //Unknown at the moment
        }
        if (command.equals("setZero")) {
            sendData("setZero,OK,end"); //Unknown at the moment
        }
        if (command.equals("setZhome")) {
            sendData("setZhome,OK,end"); //Unknown at the moment
        }
        if (command.equals("setZmove")) {
            sendData("setZmove,OK,end"); //Unknown at the moment
        }
        if (command.equals("setZstop")) {
            sendData("setZstop,OK,end"); //Unknown at the moment
        }
        if (command.equals("stopUV")) {
            sendData("stopUV,OK,end"); //Unknown at the moment
        }
    }

    @Override
    public void run() {
        //noinspection StatementWithEmptyBody
        while (!acceptClient()) {
        }
        while (isConnected()) {
            try {
                byte[] received = receive();
                if (received == null) {
                    System.out.println("null");
                    continue;
                }
                if (received.length < 1) continue;
                String command = new String(received, "GBK").trim();
                System.out.println(command);
                String[] commands = command.split(",");
                if (commands.length < 1) continue;
                for (int i = 0; i < commands.length; i++) {
                    if (commandNeedsData(commands[i]) > 0) {
                        if ((i + commandNeedsData(commands[i])) < commands.length) {
                            parseCommand(commands[i], extractData(commands, i + 1, commandNeedsData(commands[i])));
                        }
                    } else {
                        parseCommand(commands[i], new String[0]);
                    }
                }
            } catch (IOException e) {
                System.out.println(e.getMessage());
                try {
                    close();
                } catch (IOException ignore) {
                }
                while (!acceptClient()) {
                }
            }
        }
        try {
            close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
