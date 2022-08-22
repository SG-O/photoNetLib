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

package de.sg_o.lib.photoNet.printer.cbd;

import de.sg_o.lib.photoNet.netData.FolderList;
import de.sg_o.lib.photoNet.netData.cbd.CbdFolderList;
import de.sg_o.lib.photoNet.networkIO.NetIO;
import de.sg_o.lib.photoNet.networkIO.NetRegularCommand;
import de.sg_o.lib.photoNet.printer.Folder;

import java.io.UnsupportedEncodingException;

@SuppressWarnings("SerializableHasSerializationMethods")
public class CbdFolder extends Folder {
    private static final long serialVersionUID = 8537190192225455645L;

    public CbdFolder(String path, NetIO io) throws UnsupportedEncodingException {
        super(path, io);
    }

    public void update() throws UnsupportedEncodingException {
        updateFolder = new NetRegularCommand(io, "M20 '" + path + "'");
    }

    public FolderList getFolderList() {
        if (updateFolder == null) return null;
        if (!updateFolder.isExecuted()) return null;
        return new CbdFolderList(path, updateFolder.getResponse(), io);
    }
}
