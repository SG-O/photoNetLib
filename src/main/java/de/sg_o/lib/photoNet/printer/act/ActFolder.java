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

package de.sg_o.lib.photoNet.printer.act;

import de.sg_o.lib.photoNet.netData.FolderList;
import de.sg_o.lib.photoNet.netData.act.ActFolderList;
import de.sg_o.lib.photoNet.networkIO.NetIO;
import de.sg_o.lib.photoNet.networkIO.act.ActCommands;
import de.sg_o.lib.photoNet.networkIO.act.ActNetRegularCommand;
import de.sg_o.lib.photoNet.printer.Folder;

import java.io.UnsupportedEncodingException;

@SuppressWarnings("SerializableHasSerializationMethods")
public class ActFolder extends Folder {
    private static final long serialVersionUID = -5410887332147328347L;

    public ActFolder(String path, NetIO io) {
        super(path, io);
        supportsUpload = false;
    }

    public void update() throws UnsupportedEncodingException {
        updateFolder = new ActNetRegularCommand(io, ActCommands.getFiles());
    }

    public FolderList getFolderList() {
        if (updateFolder == null) return null;
        if (!updateFolder.isExecuted()) return null;
        return new ActFolderList(path, updateFolder.getResponse(), io);
    }
}
