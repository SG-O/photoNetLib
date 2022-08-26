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

package de.sg_o.lib.photoNet.netData.act;

import de.sg_o.lib.photoNet.netData.DataDownload;
import de.sg_o.lib.photoNet.netData.DataUpload;
import de.sg_o.lib.photoNet.netData.FileListItem;
import de.sg_o.lib.photoNet.networkIO.NetIO;
import de.sg_o.lib.photoNet.networkIO.NetRegularCommand;
import de.sg_o.lib.photoNet.networkIO.NetRequestBinary;
import de.sg_o.lib.photoNet.networkIO.act.ActCommands;
import de.sg_o.lib.photoNet.networkIO.act.ActNetRegularCommand;
import de.sg_o.lib.photoNet.networkIO.act.ActNetRequestBinary;
import de.sg_o.lib.photoNet.printFile.PrintFileMeta;
import de.sg_o.lib.photoNet.printFile.PrintFilePreview;
import de.sg_o.lib.photoNet.printFile.pw.PwPrintFileMeta;
import de.sg_o.lib.photoNet.printFile.pw.PwPrintFilePreview;
import de.sg_o.lib.photoNet.printer.Folder;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.InvalidParameterException;

public class ActFileListItem extends FileListItem {
    private final String ref;

    public ActFileListItem(String baseDir, String name, long size, boolean folder, NetIO io) {
        super(baseDir, name, size, folder, io);
        this.ref = null;

        supportsDownload = false;
    }

    public ActFileListItem(String baseDir, String info, NetIO io) {
        super(baseDir, io);
        if (info == null) throw new InvalidParameterException("Null string");
        if (info.length() < 1) throw new InvalidParameterException("Couldn't parse String");
        String[] split = info.split("/");
        if (split.length != 2) throw new InvalidParameterException("Couldn't parse String");
        this.folder = false;
        this.size = 0;
        this.name = split[0];
        this.ref = split[1];

        supportsDownload = false;
    }

    public Folder getFolder() {
        return null;
    }

    public PrintFileMeta getMeta() {
        return new PwPrintFileMeta();
    }

    public PrintFilePreview getPreview(long offset) {
        try {
            NetRequestBinary print = new ActNetRequestBinary(io, ActCommands.getPreview2(ref), 224 * 168 * 2);
            while (!print.isExecuted()) {
                Thread.sleep(100);
            }
            if (print.isError()) return null;
            byte[] data = print.getResponse();
            if (data == null) return null;
            PwPrintFilePreview preview = new PwPrintFilePreview();
            preview.addData(data);
            return preview;
        } catch (UnsupportedEncodingException | InterruptedException ignore) {
        }
        return null;
    }

    public long openFile() {
        return -1;
    }

    public void closeFile() {
    }

    public DataDownload getDownload(OutputStream dataStream, int maxRetries) {
        return null;
    }

    public DataUpload getUpload(InputStream dataStream, int maxRetries) {
        return null;
    }

    public void delete() {
        if (ref == null) return;
        try {
            NetRegularCommand delete = new ActNetRegularCommand(io, ActCommands.deleteFile(new String[]{ref}));
            while (!delete.isExecuted()) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ignore) {
                }
            }
        } catch (UnsupportedEncodingException ignore) {
        }
    }

    public void print() {
        if (ref == null) return;
        try {
            new ActNetRegularCommand(io, ActCommands.print(ref));
        } catch (UnsupportedEncodingException ignore) {
        }
    }
}
