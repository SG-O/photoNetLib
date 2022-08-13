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

package de.sg_o.lib.photoNet.netData;

import de.sg_o.lib.photoNet.networkIO.NetIO;
import de.sg_o.lib.photoNet.networkIO.NetRegularCommand;
import de.sg_o.lib.photoNet.networkIO.NetRequestBinary;
import de.sg_o.lib.photoNet.photonFile.PhotonFileMeta;
import de.sg_o.lib.photoNet.photonFile.PhotonFilePreview;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidParameterException;

public class FileListItem {
    private final NetIO io;
    private final String baseDir;
    private final String name;
    private final long size;
    private final boolean folder;

    @SuppressWarnings("unused")
    public FileListItem(String baseDir, String name, long size, boolean folder, NetIO io) {
        this.baseDir = baseDir;
        this.name = name;
        this.size = size;
        this.folder = folder;
        this.io = io;
    }

    public FileListItem(String baseDir, String info, NetIO io) {
        if (baseDir == null) throw new InvalidParameterException("Null string");
        this.baseDir = baseDir;
        if (info == null) throw new InvalidParameterException("Null string");
        if (info.length() < 1) throw new InvalidParameterException("Couldn't parse String");
        if (info.startsWith("->")) {
            this.folder = true;
            this.size = 0;
            this.name = info.substring(2);
        } else {
            this.folder = false;
            String[] split = info.split("\\s+");
            if (split.length < 2) throw new InvalidParameterException("Couldn't parse String");
            this.size = Long.parseLong(split[split.length - 1]);
            this.name = info.substring(0, info.length() - split[split.length - 1].length()).trim();
        }
        this.io = io;
    }

    @SuppressWarnings("unused")
    public String getName() {
        return name;
    }

    @SuppressWarnings("unused")
    public long getSize() {
        return size;
    }

    @SuppressWarnings("unused")
    public boolean isFolder() {
        return folder;
    }

    public String getFullPath() {
        if (baseDir.length() < 1) return name;
        return baseDir + "/" + name;
    }

    @SuppressWarnings("unused")
    public PhotonFileMeta getMeta() throws UnsupportedEncodingException {
        if (folder) return null;

        long size = openFile();
        if (size != this.size) {
            return null;
        }

        DataTransferBlock response = readNext();
        closeFile();
        if (response == null) return null;
        if (!response.verifyChecksum()) return null;
        try {
            return new PhotonFileMeta(response.getData());
        } catch (IOException e) {
            return null;
        }
    }

    @SuppressWarnings("unused")
    public PhotonFilePreview getPreview(long offset) throws UnsupportedEncodingException {
        if (folder) return null;

        long size = openFile();
        if (size != this.size) {
            return null;
        }

        DataTransferBlock response = readFromOffset(offset);
        if (response == null) {
            closeFile();
            return null;
        }
        if (!response.verifyChecksum()) {
            closeFile();
            return null;
        }
        PhotonFilePreview preview;
        try {
            preview = new PhotonFilePreview(response.getData());
        } catch (IOException e) {
            closeFile();
            return null;
        }
        byte[] data = new byte[(int) preview.getImgDataLength()];
        offset = preview.getImgDataOffset();
        int glide = 0;
        while (glide < data.length) {
            response = readFromOffset(offset + glide);
            if (response == null) {
                closeFile();
                return null;
            }
            if (!response.verifyChecksum()) {
                closeFile();
                return null;
            }
            int length = response.getData().length;
            if (length > data.length - glide) {
                length = data.length - glide;
            }
            System.arraycopy(response.getData(), 0, data, glide, length);
            glide += length;
        }
        closeFile();
        preview.addData(data);
        return preview;
    }

    private DataTransferBlock readNext() {
        NetRequestBinary data = new NetRequestBinary(io, "M3000");
        while (!data.isExecuted()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException ignore) {
            }
        }
        if (data.getResponse() == null) return null;
        return new DataTransferBlock(data.getResponse());
    }

    private DataTransferBlock readFromOffset(long offset) {
        NetRequestBinary data = new NetRequestBinary(io, "M3001 I" + offset);
        while (!data.isExecuted()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException ignore) {
            }
        }
        if (data.getResponse() == null) return null;
        return new DataTransferBlock(data.getResponse());
    }

    private long openFile() throws UnsupportedEncodingException {
        NetRegularCommand statusRequest = new NetRegularCommand(io, "M4000");
        while (!statusRequest.isExecuted()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException ignore) {
            }
        }
        if (statusRequest.getResponse() == null) return -1;
        Status stat = new Status();
        stat.update(statusRequest.getResponse());
        if (stat.getState() != Status.State.IDLE && stat.getState() != Status.State.FINISHED) return -1;
        closeFile();
        NetRegularCommand fileRequest = new NetRegularCommand(io, "M6032 '" + name + "'");
        while (!fileRequest.isExecuted()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException ignore) {
            }
        }
        if (fileRequest.getResponse() == null) {
            closeFile();
            return -1;
        }
        return Integer.parseInt(fileRequest.getResponse().split(":")[1]) & 0xFFFFFFFFL;
    }

    private void closeFile() throws UnsupportedEncodingException {
        NetRegularCommand closePrevious = new NetRegularCommand(io, "M22");
        while (!closePrevious.isExecuted()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException ignore) {
            }
        }
    }

    @SuppressWarnings("unused")
    public void delete() throws UnsupportedEncodingException {
        NetRegularCommand delete = new NetRegularCommand(io, "M30 " + getFullPath());
        while (!delete.isExecuted()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException ignore) {
            }
        }
    }

    @SuppressWarnings("unused")
    public void print() throws UnsupportedEncodingException {
        NetRegularCommand delete = new NetRegularCommand(io, "M6030 ':" + getFullPath() + "'");
    }

    @Override
    public String toString() {
        return "FileListItem{" +
                "baseDir='" + baseDir + '\'' +
                ", name='" + name + '\'' +
                ", size=" + size +
                ", folder=" + folder +
                '}';
    }
}
