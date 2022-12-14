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

import java.util.TreeMap;

public abstract class FolderList {
    protected final TreeMap<String, FileListItem> items = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    protected final String path;
    protected final NetIO io;

    protected FolderList(String path, NetIO io) {
        this.io = io;
        this.path = path;
    }

    @SuppressWarnings("unused")
    public String getPath() {
        return path;
    }

    @SuppressWarnings("unused")
    public TreeMap<String, FileListItem> getItems() {
        return items;
    }

    @SuppressWarnings("unused")
    public abstract FileListItem newFile(String fileName, long size);

    @Override
    public String toString() {
        return "FolderList{" +
                "path='" + path + '\'' +
                ", items=" + items +
                '}';
    }
}
