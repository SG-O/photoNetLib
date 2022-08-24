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

import de.sg_o.lib.photoNet.netData.FileListItem;
import de.sg_o.lib.photoNet.netData.FolderList;
import de.sg_o.lib.photoNet.networkIO.NetIO;
import de.sg_o.lib.photoNet.networkIO.act.ActCommands;

public class ActFolderList extends FolderList {
    public ActFolderList(String path, String response, NetIO io) {
        super(path, io);
        if (response == null) return;
        String[] files = response.split(",");
        if (files.length < 2) return;
        if (!files[0].equals(ActCommands.GET_FILES)) return;
        if (!files[files.length - 1].equals(ActCommands.Values.END.toString())) return;
        FileListItem tmp;
        for (int i = 1; i < (files.length - 1); i++) {
            try {
                tmp = new ActFileListItem(path, files[i], io);
                if (tmp.isFolder()) {
                    items.put("-" + tmp.getName(), tmp);
                } else {
                    items.put(tmp.getName(), tmp);
                }
            } catch (Exception ignored) {
            }
        }
    }

    public FileListItem newFile(String fileName, long size) {
        if (fileName == null) return null;
        if (fileName.length() < 1) return null;

        if (items.containsKey(fileName)) return null;

        return new ActFileListItem(path, fileName, size, false, io);
    }
}
