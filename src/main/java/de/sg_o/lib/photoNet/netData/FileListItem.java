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
import de.sg_o.lib.photoNet.printFile.PrintFileMeta;
import de.sg_o.lib.photoNet.printFile.PrintFilePreview;
import de.sg_o.lib.photoNet.printer.Folder;

import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidParameterException;

public abstract class FileListItem {
    public static final String FOLDER_UP = "../";
    protected final NetIO io;
    protected final String baseDir;
    protected String name;
    protected long size;
    protected boolean folder;

    protected PrintFilePreview photonFilePreview;
    protected PrintFileMeta photonFileMeta;

    public FileListItem(String baseDir, NetIO io) {
        if (baseDir == null) throw new InvalidParameterException("Null string");
        this.baseDir = baseDir;
        this.io = io;
    }

    @SuppressWarnings("unused")
    public FileListItem(String baseDir, String name, long size, boolean folder, NetIO io) {
        if (baseDir == null) throw new InvalidParameterException("Null string");
        this.baseDir = baseDir;
        this.name = name;
        this.size = size;
        this.folder = folder;
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
    public abstract Folder getFolder();

    public String getFullPath() {
        if (baseDir.length() < 1) return name;
        return baseDir + "/" + name;
    }

    @SuppressWarnings("unused")
    public abstract PrintFileMeta getMeta();

    @SuppressWarnings("unused")
    public abstract PrintFilePreview getPreview(long offset);

    public abstract long openFile();

    public abstract void closeFile();

    @SuppressWarnings("unused")
    public abstract DataDownload getDownload(OutputStream dataStream, int maxRetries);

    @SuppressWarnings("unused")
    public abstract DataUpload getUpload(InputStream dataStream, int maxRetries);

    @SuppressWarnings("unused")
    public abstract void delete();

    @SuppressWarnings("unused")
    public abstract void print();

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
