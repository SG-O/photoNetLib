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
import de.sg_o.lib.photoNet.photonFile.PhotonFileMeta;
import de.sg_o.lib.photoNet.photonFile.PhotonFilePreview;
import de.sg_o.lib.photoNet.printer.Folder;

import java.io.*;
import java.security.InvalidParameterException;

public class FileListItem {
    public static final String FOLDER_UP = "../";
    private final NetIO io;
    private final String baseDir;
    private final String name;
    private final long size;
    private final boolean folder;

    private PhotonFilePreview photonFilePreview;
    private PhotonFileMeta photonFileMeta;

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

    @SuppressWarnings("unused")
    public Folder getFolder() {
        if (!isFolder()) return null;
        try {
            if (name.equals(FOLDER_UP)) {
                String[] split = baseDir.split("/");
                if (split.length < 2) {
                    return new Folder("", io);
                }
                StringBuilder parent = new StringBuilder();
                for (int i = 0; i < (split.length - 1); i++) {
                    if (i != 0) parent.append("/");
                    parent.append(split[i]);
                }
                return new Folder(parent.toString(), io);
            }
            return new Folder(getFullPath(), io);
        } catch (UnsupportedEncodingException ignore) {
            return null;
        }
    }

    public String getFullPath() {
        if (baseDir.length() < 1) return name;
        return baseDir + "/" + name;
    }

    @SuppressWarnings("unused")
    public PhotonFileMeta getMeta() {
        if (folder) return null;
        if (this.photonFileMeta != null) return this.photonFileMeta;

        ByteArrayOutputStream meta = new ByteArrayOutputStream();
        DataDownload metaDownload = new DataDownload(this, meta, 2, 0, 96, io);
        metaDownload.run();
        if (!metaDownload.isComplete()) {
            return null;
        }
        if (meta.size() < 96) {
            return null;
        }

        try {
            this.photonFileMeta = new PhotonFileMeta(meta.toByteArray());
            return this.photonFileMeta;
        } catch (IOException e) {
            return null;
        }
    }

    @SuppressWarnings("unused")
    public PhotonFilePreview getPreview(long offset) {
        if (folder) return null;
        if (this.photonFilePreview != null) return this.photonFilePreview;
        ByteArrayOutputStream previewHeader = new ByteArrayOutputStream();
        DataDownload previewHeaderDownload = new DataDownload(this, previewHeader, 2, offset, 16, io);
        previewHeaderDownload.run();
        if (!previewHeaderDownload.isComplete()) {
            return null;
        }
        if (previewHeader.size() < 16) {
            return null;
        }
        PhotonFilePreview preview;
        try {
            preview = new PhotonFilePreview(previewHeader.toByteArray());
        } catch (IOException e) {
            return null;
        }

        ByteArrayOutputStream previewData = new ByteArrayOutputStream();
        DataDownload previewDataDownload = new DataDownload(this, previewData, 2, preview.getImgDataOffset(), preview.getImgDataLength(), io);
        previewDataDownload.run();
        if (!previewHeaderDownload.isComplete()) {
            return null;
        }
        preview.addData(previewData.toByteArray());
        this.photonFilePreview = preview;
        return preview;
    }

    public long openFile() throws UnsupportedEncodingException {
        NetRegularCommand statusRequest = new NetRegularCommand(io, "M4000");
        while (!statusRequest.isExecuted()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException ignore) {
            }
        }
        if (statusRequest.getResponse() == null) return -1;
        Status stat = new Status(statusRequest.getResponse());
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

    public void closeFile() throws UnsupportedEncodingException {
        NetRegularCommand closePrevious = new NetRegularCommand(io, "M22");
        while (!closePrevious.isExecuted()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException ignore) {
            }
        }
    }

    @SuppressWarnings("unused")
    public DataDownload getDownload(OutputStream dataStream, int maxRetries) {
        return new DataDownload(this, dataStream, maxRetries, 0, 0, io);
    }

    @SuppressWarnings("unused")
    public DataUpload getUpload(InputStream dataStream, int maxRetries) {
        return new DataUpload(this, dataStream, maxRetries, io);
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
        NetRegularCommand print = new NetRegularCommand(io, "M6030 ':" + getFullPath() + "'");
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
