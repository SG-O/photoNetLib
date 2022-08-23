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

package de.sg_o.lib.photoNet.printer;

import de.sg_o.lib.photoNet.netData.FolderList;
import de.sg_o.lib.photoNet.networkIO.NetIO;
import de.sg_o.lib.photoNet.networkIO.NetRegularCommand;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;

@SuppressWarnings("SerializableHasSerializationMethods")
public abstract class Folder implements Serializable {
    private static final long serialVersionUID = -8105907103389418019L;
    protected final String path;
    protected final NetIO io;

    protected boolean supportsUpload;

    protected transient NetRegularCommand updateFolder;

    public Folder(String path, NetIO io) {
        this.path = path;
        this.io = io;
    }

    public abstract void update() throws UnsupportedEncodingException;

    @SuppressWarnings("unused")
    public String getPath() {
        return path;
    }

    @SuppressWarnings("unused")
    public abstract FolderList getFolderList();

    @SuppressWarnings({"unused", "BooleanMethodIsAlwaysInverted"})
    public boolean isUpToDate() {
        if (updateFolder == null) return false;
        return updateFolder.isExecuted();
    }

    @SuppressWarnings("unused")
    public boolean supportsUpload() {
        return supportsUpload;
    }
}
