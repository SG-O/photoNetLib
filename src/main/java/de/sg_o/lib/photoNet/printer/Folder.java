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

import java.io.UnsupportedEncodingException;

public class Folder {
    private final String path;
    private final NetIO io;

    private NetRegularCommand updateRootFolder;

    public Folder(String path, NetIO io) throws UnsupportedEncodingException {
        this.path = path;
        this.io = io;
    }

    public void update() throws UnsupportedEncodingException {
        updateRootFolder = new NetRegularCommand(io, "M20 '" + path + "'");
    }

    @SuppressWarnings("unused")
    public FolderList getFolderList() {
        if (updateRootFolder == null) return null;
        if (!updateRootFolder.isExecuted()) return null;
        System.out.println(updateRootFolder.getError());
        return new FolderList(path, updateRootFolder.getResponse(), io);
    }

    @SuppressWarnings("unused")
    public boolean isUpToDate() {
        if (updateRootFolder == null) return false;
        return updateRootFolder.isExecuted();
    }
}
