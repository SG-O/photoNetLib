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

public class FolderList {
    private final TreeMap<String, FileListItem> items = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    private final String path;
    private final NetIO io;

    public FolderList(String path, String response, NetIO io) {
        this.io = io;
        this.path = path;
        if (response == null) return;
        String[] lines = response.split("\\r?\\n");
        FileListItem tmp;
        if (path != null) {
            if (!path.equals("")) {
                tmp = new FileListItem(path, FileListItem.FOLDER_UP, 0, true, io);
                items.put("-" + tmp.getName(), tmp);
            }
        }
        for (int i = 1; i < (lines.length - 2); i++) {
            try {
                tmp = new FileListItem(path, lines[i], io);
                if (tmp.isFolder()) {
                    items.put("-" + tmp.getName(), tmp);
                } else {
                    items.put(tmp.getName(), tmp);
                }
            } catch (Exception ignored) {
            }
        }
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
    public FileListItem newFile(String fileName, long size) {
        if (fileName == null) return null;
        if (fileName.length() < 1) return null;

        if (items.containsKey(fileName)) return null;

        return new FileListItem(path, fileName, size, false, io);
    }

    @Override
    public String toString() {
        return "FolderList{" +
                "path='" + path + '\'' +
                ", items=" + items +
                '}';
    }
}
