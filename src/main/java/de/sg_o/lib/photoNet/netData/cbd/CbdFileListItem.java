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

package de.sg_o.lib.photoNet.netData.cbd;

import de.sg_o.lib.photoNet.netData.DataDownload;
import de.sg_o.lib.photoNet.netData.DataUpload;
import de.sg_o.lib.photoNet.netData.FileListItem;
import de.sg_o.lib.photoNet.netData.Status;
import de.sg_o.lib.photoNet.networkIO.NetIO;
import de.sg_o.lib.photoNet.networkIO.NetRegularCommand;
import de.sg_o.lib.photoNet.printFile.PrintFileMeta;
import de.sg_o.lib.photoNet.printFile.PrintFilePreview;
import de.sg_o.lib.photoNet.printFile.photon.PhotonPrintFileMeta;
import de.sg_o.lib.photoNet.printFile.photon.PhotonPrintFilePreview;
import de.sg_o.lib.photoNet.printer.Folder;
import de.sg_o.lib.photoNet.printer.cbd.CbdFolder;

import java.io.*;
import java.security.InvalidParameterException;

public class CbdFileListItem extends FileListItem {
    public CbdFileListItem(String baseDir, String name, long size, boolean folder, NetIO io) {
        super(baseDir, name, size, folder, io);
    }

    public CbdFileListItem(String baseDir, String info, NetIO io) {
        super(baseDir, io);
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
    }

    public Folder getFolder() {
        if (!isFolder()) return null;
        try {
            if (name.equals(FOLDER_UP)) {
                String[] split = baseDir.split("/");
                if (split.length < 2) {
                    return new CbdFolder("", io);
                }
                StringBuilder parent = new StringBuilder();
                for (int i = 0; i < (split.length - 1); i++) {
                    if (i != 0) parent.append("/");
                    parent.append(split[i]);
                }
                return new CbdFolder(parent.toString(), io);
            }
            return new CbdFolder(getFullPath(), io);
        } catch (UnsupportedEncodingException ignore) {
            return null;
        }
    }

    public PrintFileMeta getMeta() {
        if (folder) return null;
        if (this.photonFileMeta != null) return this.photonFileMeta;

        ByteArrayOutputStream meta = new ByteArrayOutputStream();
        DataDownload metaDownload = new CbdDataDownload(this, meta, 2, 0, 96, io);
        metaDownload.run();
        if (!metaDownload.isComplete()) {
            return null;
        }
        if (meta.size() < 96) {
            return null;
        }

        try {
            this.photonFileMeta = new PhotonPrintFileMeta(meta.toByteArray());
            return this.photonFileMeta;
        } catch (IOException e) {
            return null;
        }
    }

    public PrintFilePreview getPreview(long offset) {
        if (folder) return null;
        if (this.photonFilePreview != null) return this.photonFilePreview;
        ByteArrayOutputStream previewHeader = new ByteArrayOutputStream();
        DataDownload previewHeaderDownload = new CbdDataDownload(this, previewHeader, 2, offset, 16, io);
        previewHeaderDownload.run();
        if (!previewHeaderDownload.isComplete()) {
            return null;
        }
        if (previewHeader.size() < 16) {
            return null;
        }
        PrintFilePreview preview;
        try {
            preview = new PhotonPrintFilePreview(previewHeader.toByteArray());
        } catch (IOException e) {
            return null;
        }

        ByteArrayOutputStream previewData = new ByteArrayOutputStream();
        DataDownload previewDataDownload = new CbdDataDownload(this, previewData, 2, preview.getImgDataOffset(), preview.getImgDataLength(), io);
        previewDataDownload.run();
        if (!previewHeaderDownload.isComplete()) {
            return null;
        }
        preview.addData(previewData.toByteArray());
        this.photonFilePreview = preview;
        return preview;
    }

    public long openFile() {
        NetRegularCommand statusRequest = new NetRegularCommand(io, "M4000");
        while (!statusRequest.isExecuted()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException ignore) {
            }
        }
        if (statusRequest.getResponse() == null) return -1;
        Status stat = new CbdStatus(statusRequest.getResponse());
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

    public void closeFile() {
        NetRegularCommand closePrevious = new NetRegularCommand(io, "M22");
        while (!closePrevious.isExecuted()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException ignore) {
            }
        }
    }

    public DataDownload getDownload(OutputStream dataStream, int maxRetries) {
        return new CbdDataDownload(this, dataStream, maxRetries, 0, 0, io);
    }

    public DataUpload getUpload(InputStream dataStream, int maxRetries) {
        return new CbdDataUpload(this, dataStream, maxRetries, io);
    }

    public void delete() {
        NetRegularCommand delete = new NetRegularCommand(io, "M30 " + getFullPath());
        while (!delete.isExecuted()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException ignore) {
            }
        }
    }

    public void print() {
        NetRegularCommand print = new NetRegularCommand(io, "M6030 ':" + getFullPath() + "'");
    }
}
